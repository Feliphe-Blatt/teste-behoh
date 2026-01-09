package com.behoh.service;

import com.behoh.dto.InscricaoResponseDTO;
import com.behoh.dto.ReservaResponseDTO;
import com.behoh.exception.BusinessException;
import com.behoh.exception.ResourceNotFoundException;
import com.behoh.model.Evento;
import com.behoh.model.Inscricao;
import com.behoh.model.Reserva;
import com.behoh.model.Usuario;
import com.behoh.repository.EventoRepository;
import com.behoh.repository.InscricaoRepository;
import com.behoh.repository.ReservaRepository;
import com.behoh.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InscricaoService {
    
    private final InscricaoRepository inscricaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final EventoRepository eventoRepository;
    private final ReservaRepository reservaRepository;
    
    public InscricaoService(InscricaoRepository inscricaoRepository,
                           UsuarioRepository usuarioRepository,
                           EventoRepository eventoRepository,
                           ReservaRepository reservaRepository) {
        this.inscricaoRepository = inscricaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.eventoRepository = eventoRepository;
        this.reservaRepository = reservaRepository;
    }
    
    @Transactional(readOnly = true)
    public List<InscricaoResponseDTO> listarTodas() {
        return inscricaoRepository.findAll().stream()
                .map(InscricaoResponseDTO::new)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<InscricaoResponseDTO> listarPorUsuario(Long usuarioId) {
        return inscricaoRepository.findByUsuarioId(usuarioId).stream()
                .map(InscricaoResponseDTO::new)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<InscricaoResponseDTO> listarPorEvento(Long eventoId) {
        return inscricaoRepository.findByEventoId(eventoId).stream()
                .map(InscricaoResponseDTO::new)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public Object criar(Long usuarioId, Long eventoId) {
        // Verificar se usuário existe
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com ID: " + usuarioId));
        
        // Verificar se evento existe
        Evento evento = eventoRepository.findById(eventoId)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado com ID: " + eventoId));
        
        // Verificar se já existe inscrição
        if (inscricaoRepository.existsByUsuarioIdAndEventoId(usuarioId, eventoId)) {
            throw new BusinessException("Usuário já está inscrito neste evento");
        }
        
        // Verificar se já existe reserva
        if (reservaRepository.existsByUsuarioIdAndEventoId(usuarioId, eventoId)) {
            throw new BusinessException("Usuário já possui uma reserva para este evento");
        }
        
        // Verificar se o evento já começou
        if (LocalDateTime.now().isAfter(evento.getDataInicio())) {
            throw new BusinessException("Não é possível se inscrever em evento que já começou");
        }
        
        // Verificar se há vagas disponíveis
        if (evento.getVagasDisponiveis() <= 0) {
            // Não há vagas - criar reserva automaticamente
            Reserva reserva = new Reserva(usuario, evento);
            Reserva reservaSalva = reservaRepository.save(reserva);
            return new ReservaResponseDTO(reservaSalva);
        }
        
        // Criar inscrição
        Inscricao inscricao = new Inscricao(usuario, evento);
        Inscricao salva = inscricaoRepository.save(inscricao);
        
        // Atualizar vagas disponíveis
        evento.setVagasDisponiveis(evento.getVagasDisponiveis() - 1);
        eventoRepository.save(evento);
        
        return new InscricaoResponseDTO(salva);
    }
    
    @Transactional
    public void cancelar(Long id) {
        Inscricao inscricao = inscricaoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inscrição não encontrada com ID: " + id));
        
        Evento evento = inscricao.getEvento();
        
        // Verificar se o evento já começou
        if (LocalDateTime.now().isAfter(evento.getDataInicio())) {
            throw new BusinessException("Não é possível cancelar inscrição de evento que já começou");
        }
        
        // Remover inscrição
        inscricaoRepository.delete(inscricao);
        
        // Tentar converter a próxima reserva em inscrição
        LocalDateTime now = LocalDateTime.now();
        List<Reserva> reservasAtivas = reservaRepository.findReservasAtivasByEventoId(evento.getId(), now);
        
        if (!reservasAtivas.isEmpty()) {
            // Pegar a primeira reserva (FIFO)
            Reserva primeiraReserva = reservasAtivas.get(0);
            
            try {
                // Criar inscrição a partir da reserva
                Inscricao novaInscricao = new Inscricao(primeiraReserva.getUsuario(), evento);
                inscricaoRepository.save(novaInscricao);
                
                // Remover a reserva
                reservaRepository.delete(primeiraReserva);
                
                // Não altera vagas disponíveis pois a reserva foi convertida em inscrição
            } catch (Exception e) {
                // Se falhar, apenas libera a vaga
                evento.setVagasDisponiveis(evento.getVagasDisponiveis() + 1);
                eventoRepository.save(evento);
            }
        } else {
            // Sem reservas, apenas libera a vaga
            evento.setVagasDisponiveis(evento.getVagasDisponiveis() + 1);
            eventoRepository.save(evento);
        }
    }
}

