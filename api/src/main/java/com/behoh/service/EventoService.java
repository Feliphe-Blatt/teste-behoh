package com.behoh.service;

import com.behoh.dto.EventoRequestDTO;
import com.behoh.dto.EventoResponseDTO;
import com.behoh.exception.ResourceNotFoundException;
import com.behoh.model.Evento;
import com.behoh.repository.EventoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EventoService {
    
    private final EventoRepository eventoRepository;
    
    public EventoService(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }
    
    @Transactional(readOnly = true)
    public List<EventoResponseDTO> listarTodos() {
        return eventoRepository.findAll().stream()
                .map(EventoResponseDTO::new)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public EventoResponseDTO buscarPorId(Long id) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado com ID: " + id));
        return new EventoResponseDTO(evento);
    }
    
    @Transactional
    public EventoResponseDTO criar(EventoRequestDTO dto) {
        Evento evento = new Evento(
                dto.nome(),
                dto.descricao(),
                dto.vagas(),
                dto.dataInicio(),
                dto.dataFim()
        );
        Evento salvo = eventoRepository.save(evento);
        return new EventoResponseDTO(salvo);
    }
    
    @Transactional
    public EventoResponseDTO atualizar(Long id, EventoRequestDTO dto) {
        Evento evento = eventoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado com ID: " + id));
        
        evento.setNome(dto.nome());
        evento.setDescricao(dto.descricao());
        evento.setVagas(dto.vagas());
        evento.setDataInicio(dto.dataInicio());
        evento.setDataFim(dto.dataFim());
        
        Evento atualizado = eventoRepository.save(evento);
        return new EventoResponseDTO(atualizado);
    }
    
    @Transactional
    public void deletar(Long id) {
        if (!eventoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Evento não encontrado com ID: " + id);
        }
        eventoRepository.deleteById(id);
    }
}

