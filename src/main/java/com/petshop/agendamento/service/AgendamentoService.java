package com.petshop.agendamento.service;

import com.petshop.agendamento.model.*;
import com.petshop.agendamento.repository.AgendamentoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AgendamentoService {

    private final AgendamentoRepository repository;
    private final TelegramService telegramService;

    public AgendamentoService(AgendamentoRepository repository, TelegramService telegramService) {
        this.repository = repository;
        this.telegramService = telegramService;
    }

    public Agendamento criarAgendamento(Agendamento agendamento) {

        int duracao = calcularDuracao(
                agendamento.getServico(),
                agendamento.getPorte()
        );

        LocalDateTime inicio = agendamento.getHorario();
        LocalDateTime fim = inicio.plusMinutes(duracao);

        List<Agendamento> existentes = repository.findAll();

        for (Agendamento a : existentes) {

            if(a.getStatus() == StatusAgendamento.CANCELADO){
                continue;
            }

            int duracaoExistente = calcularDuracao(
                    a.getServico(),
                    a.getPorte()
            );

            LocalDateTime inicioExistente = a.getHorario();
            LocalDateTime fimExistente = inicioExistente.plusMinutes(duracaoExistente);

            boolean conflito =
                    inicio.isBefore(fimExistente) &&
                            fim.isAfter(inicioExistente);

            if (conflito) {
                throw new RuntimeException("Horário já ocupado nesse intervalo");
            }
        }

        agendamento.setStatus(StatusAgendamento.PENDENTE);

        Agendamento salvo = repository.save(agendamento);
        telegramService.notificarNovoAgendamento(salvo);
        return salvo;
    }

    public List<Agendamento> listar() {
        return repository.findAll();
    }

    public Agendamento confirmarAgendamento(Long id){

        Agendamento agendamento = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));

        agendamento.setStatus(StatusAgendamento.CONFIRMADO);

        return repository.save(agendamento);
    }

    public Agendamento cancelarAgendamento(Long id){

        Agendamento agendamento = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));

        agendamento.setStatus(StatusAgendamento.CANCELADO);

        return repository.save(agendamento);
    }

    public List<LocalDateTime> horariosDisponiveis(LocalDate data, TipoServico servico, PortePet porte) {

        int duracaoServico = calcularDuracao(servico, porte);

        List<Agendamento> agendamentos = repository.findAll();
        List<LocalDateTime> horarios = new ArrayList<>();

        LocalDateTime inicio = data.atTime(9,0);
        LocalDateTime fim = data.atTime(18,0);

        while(inicio.plusMinutes(duracaoServico).isBefore(fim)){

            LocalDateTime inicioProposto = inicio;
            LocalDateTime fimProposto = inicio.plusMinutes(duracaoServico);

            boolean conflito = agendamentos.stream().anyMatch(a -> {

                if(a.getStatus() == StatusAgendamento.CANCELADO){
                    return false;
                }

                int duracaoExistente = calcularDuracao(
                        a.getServico(),
                        a.getPorte()
                );

                LocalDateTime inicioExistente = a.getHorario();
                LocalDateTime fimExistente = inicioExistente.plusMinutes(duracaoExistente);

                return inicioProposto.isBefore(fimExistente) &&
                        fimProposto.isAfter(inicioExistente);

            });

            if(!conflito){
                horarios.add(inicioProposto);
            }

            inicio = inicio.plusMinutes(duracaoServico);
        }

        return horarios;
    }

    private int calcularDuracao(TipoServico servico, PortePet porte) {

        if (servico == TipoServico.BANHO) {

            switch (porte) {
                case MINI: return 40;
                case PEQUENO: return 50;
                case MEDIO: return 60;
                case GRANDE: return 80;
                case GIGANTE: return 100;
            }

        }

        if (servico == TipoServico.TOSA_HIGIENICA) {

            switch (porte) {
                case MINI: return 90;
                case PEQUENO: return 100;
                case MEDIO: return 120;
                case GRANDE: return 140;
                case GIGANTE: return 160;
            }

        }

        return 60;
    }

    public List<Agendamento> agendaDoDia(LocalDate data){

        return repository.findAll()
                .stream()
                .filter(a -> a.getHorario().toLocalDate().equals(data))
                .toList();

    }
}