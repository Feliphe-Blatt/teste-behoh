package com.behoh.dto;

import jakarta.validation.constraints.NotNull;

public record ReservaRequestDTO(
    @NotNull(message = "ID do usuário é obrigatório")
    Long usuarioId,
    
    @NotNull(message = "ID do evento é obrigatório")
    Long eventoId
) {}

