package com.petshop.agendamento.controller;


import com.petshop.agendamento.model.Agendamento;
import com.petshop.agendamento.service.AgendamentoService;
import org.springframework.web.bind.annotation.*;
import com.petshop.agendamento.model.TipoServico;
import com.petshop.agendamento.model.PortePet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/agendamentos")
public class AgendamentoController {
    private final AgendamentoService service;
    public AgendamentoController(AgendamentoService service) {
        this.service = service;
    }

    @PostMapping
    public Agendamento criar(@RequestBody Agendamento agendamento) {
        return service.criarAgendamento(agendamento);
    }

    @GetMapping
    public List<Agendamento> listar() {
        return service.listar();
    }

    @GetMapping("/disponiveis")
    public List<LocalDateTime> horariosDisponiveis(@RequestParam String data , @RequestParam TipoServico servico , @RequestParam PortePet porte) {
        LocalDate dia = LocalDate.parse(data);

        return service.horariosDisponiveis(dia , servico, porte);
    }

    @PostMapping("/{id}/confirmar")
    public Agendamento confirmar(@PathVariable Long id) {
        return service.confirmarAgendamento(id);
    }

    @PostMapping("/{id}/cancelar")
    public Agendamento cancelar(@PathVariable Long id) {
        return service.cancelarAgendamento(id);
    }
    @GetMapping("/agenda")
    public List<Agendamento> agendaDoDia(@RequestParam LocalDate data){
        return service.agendaDoDia(data);
    }

}
