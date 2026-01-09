package com.behoh.controller;

import com.behoh.dto.EventoRequestDTO;
import com.behoh.dto.EventoResponseDTO;
import com.behoh.service.EventoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/eventos")
public class EventoController {
    
    private final EventoService eventoService;
    
    public EventoController(EventoService eventoService) {
        this.eventoService = eventoService;
    }
    
    @GetMapping
    public ResponseEntity<List<EventoResponseDTO>> listarTodos() {
        List<EventoResponseDTO> eventos = eventoService.listarTodos();
        return ResponseEntity.ok(eventos);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<EventoResponseDTO> buscarPorId(@PathVariable Long id) {
        EventoResponseDTO evento = eventoService.buscarPorId(id);
        return ResponseEntity.ok(evento);
    }
    
    @PostMapping
    public ResponseEntity<EventoResponseDTO> criar(@RequestBody EventoRequestDTO dto) {
        EventoResponseDTO evento = eventoService.criar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(evento);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<EventoResponseDTO> atualizar(@PathVariable Long id, @RequestBody EventoRequestDTO dto) {
        EventoResponseDTO evento = eventoService.atualizar(id, dto);
        return ResponseEntity.ok(evento);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        eventoService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}

