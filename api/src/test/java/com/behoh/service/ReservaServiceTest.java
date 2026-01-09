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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EventoRepository eventoRepository;

    @Mock
    private InscricaoRepository inscricaoRepository;

    @InjectMocks
    private ReservaService reservaService;

    private Usuario usuario;
    private Evento evento;
    private Reserva reserva;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("João Silva");
        usuario.setEmail("joao@email.com");

        evento = new Evento();
        evento.setId(1L);
        evento.setNome("Workshop Spring Boot");
        evento.setVagas(50);
        evento.setVagasDisponiveis(0);
        evento.setDataInicio(LocalDateTime.now().plusDays(5));
        evento.setDataFim(LocalDateTime.now().plusDays(5).plusHours(8));

        reserva = new Reserva(usuario, evento);
        reserva.setId(1L);
    }

    @Test
    @DisplayName("Deve listar todas as reservas")
    void deveListarTodasReservas() {
        // Given
        when(reservaRepository.findAll()).thenReturn(Arrays.asList(reserva));

        // When
        List<ReservaResponseDTO> resultado = reservaService.listarTodas();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(reservaRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Deve listar reservas por usuário")
    void deveListarReservasPorUsuario() {
        // Given
        when(reservaRepository.findByUsuarioId(1L)).thenReturn(Arrays.asList(reserva));

        // When
        List<ReservaResponseDTO> resultado = reservaService.listarPorUsuario(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(reservaRepository, times(1)).findByUsuarioId(1L);
    }

    @Test
    @DisplayName("Deve criar reserva com sucesso")
    void deveCriarReservaComSucesso() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(evento));
        when(inscricaoRepository.existsByUsuarioIdAndEventoId(1L, 1L)).thenReturn(false);
        when(reservaRepository.existsByUsuarioIdAndEventoId(1L, 1L)).thenReturn(false);
        when(reservaRepository.save(any(Reserva.class))).thenReturn(reserva);

        // When
        ReservaResponseDTO resultado = reservaService.criar(1L, 1L);

        // Then
        assertNotNull(resultado);
        verify(reservaRepository, times(1)).save(any(Reserva.class));
    }

    @Test
    @DisplayName("Não deve criar reserva se usuário não existe")
    void naoDeveCriarReservaSeusuarioNaoExiste() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            reservaService.criar(1L, 1L);
        });

        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    @DisplayName("Não deve criar reserva se evento não existe")
    void naoDeveCriarReservaSeEventoNaoExiste() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(eventoRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            reservaService.criar(1L, 1L);
        });

        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    @DisplayName("Não deve criar reserva se usuário já está inscrito")
    void naoDeveCriarReservaSeUsuarioJaInscrito() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(evento));
        when(inscricaoRepository.existsByUsuarioIdAndEventoId(1L, 1L)).thenReturn(true);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            reservaService.criar(1L, 1L);
        });

        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    @DisplayName("Não deve criar reserva se usuário já possui reserva")
    void naoDeveCriarReservaSeUsuarioJaPossuiReserva() {
        // Given
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(evento));
        when(inscricaoRepository.existsByUsuarioIdAndEventoId(1L, 1L)).thenReturn(false);
        when(reservaRepository.existsByUsuarioIdAndEventoId(1L, 1L)).thenReturn(true);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            reservaService.criar(1L, 1L);
        });

        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    @DisplayName("Não deve criar reserva se evento já começou")
    void naoDeveCriarReservaSeEventoJaComecou() {
        // Given
        evento.setDataInicio(LocalDateTime.now().minusDays(1));
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(evento));
        when(inscricaoRepository.existsByUsuarioIdAndEventoId(1L, 1L)).thenReturn(false);
        when(reservaRepository.existsByUsuarioIdAndEventoId(1L, 1L)).thenReturn(false);

        // When & Then
        assertThrows(BusinessException.class, () -> {
            reservaService.criar(1L, 1L);
        });

        verify(reservaRepository, never()).save(any(Reserva.class));
    }

    @Test
    @DisplayName("Deve cancelar reserva com sucesso")
    void deveCancelarReservaComSucesso() {
        // Given
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        // When
        reservaService.cancelar(1L);

        // Then
        verify(reservaRepository, times(1)).delete(reserva);
    }

    @Test
    @DisplayName("Não deve cancelar reserva que não existe")
    void naoDeveCancelarReservaQueNaoExiste() {
        // Given
        when(reservaRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> {
            reservaService.cancelar(1L);
        });

        verify(reservaRepository, never()).delete(any(Reserva.class));
    }

    @Test
    @DisplayName("Deve converter reserva em inscrição com sucesso")
    void deveConverterReservaEmInscricaoComSucesso() {
        // Given
        evento.setVagasDisponiveis(1);
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(inscricaoRepository.existsByUsuarioIdAndEventoId(1L, 1L)).thenReturn(false);
        when(inscricaoRepository.save(any(Inscricao.class))).thenReturn(new Inscricao(usuario, evento));
        when(eventoRepository.save(any(Evento.class))).thenReturn(evento);

        // When
        InscricaoResponseDTO resultado = reservaService.converterReservaEmInscricao(1L);

        // Then
        assertNotNull(resultado);
        verify(inscricaoRepository, times(1)).save(any(Inscricao.class));
        verify(reservaRepository, times(1)).delete(reserva);
        verify(eventoRepository, times(1)).save(evento);
    }

    @Test
    @DisplayName("Não deve converter reserva expirada")
    void naoDeveConverterReservaExpirada() {
        // Given
        reserva.setDataExpiracao(LocalDateTime.now().minusMinutes(1));
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));

        // When & Then
        assertThrows(BusinessException.class, () -> {
            reservaService.converterReservaEmInscricao(1L);
        });

        verify(inscricaoRepository, never()).save(any(Inscricao.class));
        verify(reservaRepository, times(1)).delete(reserva);
    }

    @Test
    @DisplayName("Deve limpar reservas expiradas")
    void deveLimparReservasExpiradas() {
        // Given
        Reserva reservaExpirada = new Reserva(usuario, evento);
        reservaExpirada.setDataExpiracao(LocalDateTime.now().minusMinutes(1));
        when(reservaRepository.findByDataExpiracaoBefore(any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(reservaExpirada));

        // When
        int resultado = reservaService.limparReservasExpiradas();

        // Then
        assertEquals(1, resultado);
        verify(reservaRepository, times(1)).deleteAll(anyList());
    }

    @Test
    @DisplayName("Deve retornar zero quando não há reservas expiradas")
    void deveRetornarZeroQuandoNaoHaReservasExpiradas() {
        // Given
        when(reservaRepository.findByDataExpiracaoBefore(any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        // When
        int resultado = reservaService.limparReservasExpiradas();

        // Then
        assertEquals(0, resultado);
        verify(reservaRepository, never()).deleteAll(anyList());
    }

    @Test
    @DisplayName("Deve tentar converter próxima reserva com sucesso")
    void deveTentarConverterProximaReservaComSucesso() {
        // Given
        evento.setVagasDisponiveis(1);
        when(reservaRepository.findReservasAtivasByEventoId(any(Long.class), any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(reserva));
        when(reservaRepository.findById(1L)).thenReturn(Optional.of(reserva));
        when(inscricaoRepository.existsByUsuarioIdAndEventoId(1L, 1L)).thenReturn(false);
        when(inscricaoRepository.save(any(Inscricao.class))).thenReturn(new Inscricao(usuario, evento));
        when(eventoRepository.save(any(Evento.class))).thenReturn(evento);

        // When
        boolean resultado = reservaService.tentarConverterProximaReserva(1L);

        // Then
        assertTrue(resultado);
        verify(inscricaoRepository, times(1)).save(any(Inscricao.class));
        verify(reservaRepository, times(1)).delete(reserva);
    }

    @Test
    @DisplayName("Deve retornar false quando não há reservas ativas")
    void deveRetornarFalseQuandoNaoHaReservasAtivas() {
        // Given
        when(reservaRepository.findReservasAtivasByEventoId(any(Long.class), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        // When
        boolean resultado = reservaService.tentarConverterProximaReserva(1L);

        // Then
        assertFalse(resultado);
        verify(inscricaoRepository, never()).save(any(Inscricao.class));
    }
}

