package com.behoh.repository;

import com.behoh.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByDataExpiracaoBefore(LocalDateTime data);
    List<Reserva> findByUsuarioId(Long usuarioId);
    
    @Query("SELECT r FROM Reserva r WHERE r.evento.id = :eventoId AND r.dataExpiracao > :now ORDER BY r.dataReserva ASC")
    List<Reserva> findReservasAtivasByEventoId(Long eventoId, LocalDateTime now);
    
    boolean existsByUsuarioIdAndEventoId(Long usuarioId, Long eventoId);
    Optional<Reserva> findByUsuarioIdAndEventoId(Long usuarioId, Long eventoId);
    
    void deleteByDataExpiracaoBefore(LocalDateTime data);
}

