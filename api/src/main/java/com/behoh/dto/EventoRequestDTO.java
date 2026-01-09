package com.behoh.dto;

import java.time.LocalDateTime;

public record EventoRequestDTO(
    String nome,
    String descricao,
    Integer vagas,
    LocalDateTime dataInicio,
    LocalDateTime dataFim
) {}

