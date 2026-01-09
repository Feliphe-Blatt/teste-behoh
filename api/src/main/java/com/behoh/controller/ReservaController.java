package com.behoh.controller;

import com.behoh.dto.InscricaoResponseDTO;
import com.behoh.dto.ReservaRequestDTO;
import com.behoh.dto.ReservaResponseDTO;
import com.behoh.service.ReservaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservas")
public class ReservaController {
    
    private final ReservaService reservaService;
    
    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }
    
    @GetMapping
    public ResponseEntity<List<ReservaResponseDTO>> listarTodas() {
        List<ReservaResponseDTO> reservas = reservaService.listarTodas();
        return ResponseEntity.ok(reservas);
    }
    
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<ReservaResponseDTO>> listarPorUsuario(@PathVariable Long usuarioId) {
        List<ReservaResponseDTO> reservas = reservaService.listarPorUsuario(usuarioId);
        return ResponseEntity.ok(reservas);
    }
    
    @GetMapping("/evento/{eventoId}/ativas")
    public ResponseEntity<List<ReservaResponseDTO>> listarReservasAtivasPorEvento(@PathVariable Long eventoId) {
        List<ReservaResponseDTO> reservas = reservaService.listarReservasAtivasPorEvento(eventoId);
        return ResponseEntity.ok(reservas);
    }
    
    @PostMapping
    public ResponseEntity<ReservaResponseDTO> criar(@Valid @RequestBody ReservaRequestDTO request) {
        ReservaResponseDTO reserva = reservaService.criar(request.usuarioId(), request.eventoId());
        return ResponseEntity.status(HttpStatus.CREATED).body(reserva);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        reservaService.cancelar(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/converter")
    public ResponseEntity<InscricaoResponseDTO> converterEmInscricao(@PathVariable Long id) {
        InscricaoResponseDTO inscricao = reservaService.converterReservaEmInscricao(id);
        return ResponseEntity.ok(inscricao);
    }
}

