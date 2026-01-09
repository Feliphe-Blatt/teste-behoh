package com.behoh.dto;

import com.behoh.model.Reserva;
import java.time.LocalDateTime;

public record ReservaResponseDTO(
    Long id,
    Long usuarioId,
    String usuarioNome,
    String usuarioEmail,
    Long eventoId,
    String eventoNome,
    LocalDateTime dataReserva,
    LocalDateTime dataExpiracao,
    boolean expirada
) {
    public ReservaResponseDTO(Reserva reserva) {
        this(
            reserva.getId(),
            reserva.getUsuario().getId(),
            reserva.getUsuario().getNome(),
            reserva.getUsuario().getEmail(),
            reserva.getEvento().getId(),
            reserva.getEvento().getNome(),
            reserva.getDataReserva(),
            reserva.getDataExpiracao(),
            reserva.isExpirada()
        );
    }
}

