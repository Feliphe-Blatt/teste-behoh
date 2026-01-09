package com.behoh.controller;

import com.behoh.dto.InscricaoResponseDTO;
import com.behoh.service.InscricaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inscricoes")
public class InscricaoController {
    
    private final InscricaoService inscricaoService;
    
    public InscricaoController(InscricaoService inscricaoService) {
        this.inscricaoService = inscricaoService;
    }
    
    @GetMapping
    public ResponseEntity<List<InscricaoResponseDTO>> listarTodas() {
        List<InscricaoResponseDTO> inscricoes = inscricaoService.listarTodas();
        return ResponseEntity.ok(inscricoes);
    }
    
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<InscricaoResponseDTO>> listarPorUsuario(@PathVariable Long usuarioId) {
        List<InscricaoResponseDTO> inscricoes = inscricaoService.listarPorUsuario(usuarioId);
        return ResponseEntity.ok(inscricoes);
    }
    
    @GetMapping("/evento/{eventoId}")
    public ResponseEntity<List<InscricaoResponseDTO>> listarPorEvento(@PathVariable Long eventoId) {
        List<InscricaoResponseDTO> inscricoes = inscricaoService.listarPorEvento(eventoId);
        return ResponseEntity.ok(inscricoes);
    }
    
    @PostMapping
    public ResponseEntity<?> criar(@RequestBody Map<String, Long> body) {
        Long usuarioId = body.get("usuarioId");
        Long eventoId = body.get("eventoId");
        Object resultado = inscricaoService.criar(usuarioId, eventoId);
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        inscricaoService.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}

