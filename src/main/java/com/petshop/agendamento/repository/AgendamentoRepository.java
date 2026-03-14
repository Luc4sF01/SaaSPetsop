package com.petshop.agendamento.repository;

import com.petshop.agendamento.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.Optional;

public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
    Optional<Agendamento> findByHorario(LocalDateTime horario);
}
