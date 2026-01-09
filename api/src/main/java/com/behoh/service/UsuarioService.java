package com.behoh.service;

import com.behoh.dto.UsuarioRequestDTO;
import com.behoh.dto.UsuarioResponseDTO;
import com.behoh.exception.BusinessException;
import com.behoh.exception.ResourceNotFoundException;
import com.behoh.model.Usuario;
import com.behoh.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {
    
    private final UsuarioRepository usuarioRepository;
    
    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }
    
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(UsuarioResponseDTO::new)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + id));
        return new UsuarioResponseDTO(usuario);
    }
    
    @Transactional
    public UsuarioResponseDTO criar(UsuarioRequestDTO dto) {
        if (usuarioRepository.existsByEmail(dto.email())) {
            throw new BusinessException("Já existe um usuário com o email: " + dto.email());
        }
        
        Usuario usuario = new Usuario(dto.nome(), dto.email());
        Usuario salvo = usuarioRepository.save(usuario);
        return new UsuarioResponseDTO(salvo);
    }
    
    @Transactional
    public void deletar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            throw new ResourceNotFoundException("Usuário não encontrado com ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }
}

