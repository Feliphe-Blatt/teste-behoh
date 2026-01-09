package com.behoh.dto;

import com.behoh.model.Evento;
import java.time.LocalDateTime;

public record EventoResponseDTO(
    Long id,
    String nome,
    String descricao,
    Integer vagas,
    Integer vagasDisponiveis,
    LocalDateTime dataInicio,
    LocalDateTime dataFim
) {
    public EventoResponseDTO(Evento evento) {
        this(
            evento.getId(),
            evento.getNome(),
            evento.getDescricao(),
            evento.getVagas(),
            evento.getVagasDisponiveis(),
            evento.getDataInicio(),
            evento.getDataFim()
        );
    }
}

