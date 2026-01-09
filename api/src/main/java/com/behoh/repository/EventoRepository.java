package com.behoh.repository;

import com.behoh.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventoRepository extends JpaRepository<Evento, Long> {
    List<Evento> findByDataInicioAfter(LocalDateTime data);
    List<Evento> findByVagasDisponiveisGreaterThan(Integer vagas);
}

