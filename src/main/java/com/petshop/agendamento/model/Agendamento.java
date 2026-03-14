package com.petshop.agendamento.model;
import com.petshop.agendamento.model.StatusAgendamento;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nomeCliente;

    private String telefone;

    @Enumerated(EnumType.STRING)
    private TipoServico servico;

    @Enumerated(EnumType.STRING)
    private PortePet porte;

    @Enumerated(EnumType.STRING)
    private StatusAgendamento status;


    private LocalDateTime horario;

    public Agendamento() {}

    public Long getId() {
        return id;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public TipoServico getServico() {
        return servico;
    }

    public void setServico(TipoServico servico) {
        this.servico = servico;
    }

    public PortePet getPorte() {
        return porte;
    }

    public void setPorte(PortePet porte) {
        this.porte = porte;
    }

    public LocalDateTime getHorario() {
        return horario;
    }

    public void setHorario(LocalDateTime horario) {
        this.horario = horario;
    }

    public StatusAgendamento getStatus() {
        return status;
    }

    public void setStatus(StatusAgendamento status) {
        this.status = status;
    }
}