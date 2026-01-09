package com.behoh.dto;

import com.behoh.model.Usuario;

public record UsuarioResponseDTO(
    Long id,
    String nome,
    String email
) {
    public UsuarioResponseDTO(Usuario usuario) {
        this(usuario.getId(), usuario.getNome(), usuario.getEmail());
    }
}

