package com.behoh.dto;

import com.behoh.model.Inscricao;
import java.time.LocalDateTime;

public record InscricaoResponseDTO(
    Long id,
    Long usuarioId,
    String usuarioNome,
    Long eventoId,
    String eventoNome,
    LocalDateTime dataInscricao,
    String status
) {
    public InscricaoResponseDTO(Inscricao inscricao) {
        this(
            inscricao.getId(),
            inscricao.getUsuario().getId(),
            inscricao.getUsuario().getNome(),
            inscricao.getEvento().getId(),
            inscricao.getEvento().getNome(),
            inscricao.getDataInscricao(),
            inscricao.getStatus()
        );
    }
}

