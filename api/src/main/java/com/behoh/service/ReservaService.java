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
public class ReservaService {
    
    private final ReservaRepository reservaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EventoRepository eventoRepository;
    private final InscricaoRepository inscricaoRepository;
    
    public ReservaService(ReservaRepository reservaRepository,
                         UsuarioRepository usuarioRepository,
                         EventoRepository eventoRepository,
                         InscricaoRepository inscricaoRepository) {
        this.reservaRepository = reservaRepository;
        this.usuarioRepository = usuarioRepository;
        this.eventoRepository = eventoRepository;
        this.inscricaoRepository = inscricaoRepository;
    }
    
    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> listarTodas() {
        return reservaRepository.findAll().stream()
                .map(ReservaResponseDTO::new)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> listarPorUsuario(Long usuarioId) {
        return reservaRepository.findByUsuarioId(usuarioId).stream()
                .map(ReservaResponseDTO::new)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ReservaResponseDTO> listarReservasAtivasPorEvento(Long eventoId) {
        LocalDateTime now = LocalDateTime.now();
        return reservaRepository.findReservasAtivasByEventoId(eventoId, now).stream()
                .map(ReservaResponseDTO::new)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public ReservaResponseDTO criar(Long usuarioId, Long eventoId) {
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
            throw new BusinessException("Não é possível fazer reserva para evento que já começou");
        }
        
        // Criar reserva
        Reserva reserva = new Reserva(usuario, evento);
        Reserva salva = reservaRepository.save(reserva);
        
        return new ReservaResponseDTO(salva);
    }
    
    @Transactional
    public void cancelar(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva não encontrada com ID: " + id));
        
        // Remover reserva
        reservaRepository.delete(reserva);
    }
    
    @Transactional
    public InscricaoResponseDTO converterReservaEmInscricao(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva não encontrada com ID: " + reservaId));
        
        // Verificar se reserva está expirada
        if (reserva.isExpirada()) {
            reservaRepository.delete(reserva);
            throw new BusinessException("Reserva expirada e foi removida");
        }
        
        // Verificar se já existe inscrição
        if (inscricaoRepository.existsByUsuarioIdAndEventoId(
                reserva.getUsuario().getId(),
                reserva.getEvento().getId())) {
            throw new BusinessException("Usuário já está inscrito neste evento");
        }
        
        Evento evento = reserva.getEvento();
        
        // Verificar se há vagas disponíveis
        if (evento.getVagasDisponiveis() <= 0) {
            throw new BusinessException("Não há vagas disponíveis para converter a reserva");
        }
        
        // Criar inscrição
        Inscricao inscricao = new Inscricao(reserva.getUsuario(), evento);
        Inscricao salva = inscricaoRepository.save(inscricao);
        
        // Atualizar vagas disponíveis
        evento.setVagasDisponiveis(evento.getVagasDisponiveis() - 1);
        eventoRepository.save(evento);
        
        // Remover reserva
        reservaRepository.delete(reserva);
        
        return new InscricaoResponseDTO(salva);
    }
    
    /**
     * Tenta converter automaticamente a primeira reserva ativa em inscrição
     * quando uma vaga é liberada. Usado internamente pelo InscricaoService.
     *
     * @param eventoId ID do evento que teve vaga liberada
     * @return true se uma reserva foi convertida, false caso contrário
     */
    @Transactional
    public boolean tentarConverterProximaReserva(Long eventoId) {
        LocalDateTime now = LocalDateTime.now();
        List<Reserva> reservasAtivas = reservaRepository.findReservasAtivasByEventoId(eventoId, now);
        
        if (reservasAtivas.isEmpty()) {
            return false;
        }
        
        // Pegar a primeira reserva (FIFO)
        Reserva primeiraReserva = reservasAtivas.get(0);
        
        try {
            converterReservaEmInscricao(primeiraReserva.getId());
            return true;
        } catch (BusinessException e) {
            // Se falhar, remove a reserva e tenta a próxima
            reservaRepository.delete(primeiraReserva);
            if (reservasAtivas.size() > 1) {
                return tentarConverterProximaReserva(eventoId);
            }
            return false;
        }
    }
    
    /**
     * Remove todas as reservas expiradas do sistema.
     * Deve ser chamado periodicamente por um scheduler.
     *
     * @return número de reservas removidas
     */
    @Transactional
    public int limparReservasExpiradas() {
        LocalDateTime now = LocalDateTime.now();
        List<Reserva> reservasExpiradas = reservaRepository.findByDataExpiracaoBefore(now);
        int quantidade = reservasExpiradas.size();
        
        if (!reservasExpiradas.isEmpty()) {
            reservaRepository.deleteAll(reservasExpiradas);
        }
        
        return quantidade;
    }
}

