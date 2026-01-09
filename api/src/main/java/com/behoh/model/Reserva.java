package com.behoh.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservas",
       uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "evento_id"}),
       indexes = {
           @Index(name = "idx_reserva_usuario", columnList = "usuario_id"),
           @Index(name = "idx_reserva_evento", columnList = "evento_id"),
           @Index(name = "idx_reserva_expiracao", columnList = "data_expiracao")
       })
public class Reserva {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;
    
    @Column(name = "data_reserva", nullable = false)
    private LocalDateTime dataReserva;
    
    @Column(name = "data_expiracao", nullable = false)
    private LocalDateTime dataExpiracao;
    
    @PrePersist
    protected void onCreate() {
        dataReserva = LocalDateTime.now();
        if (dataExpiracao == null) {
            dataExpiracao = dataReserva.plusMinutes(15); // Reserva expira em 15 minutos
        }
    }
    
    // Construtores
    public Reserva() {}
    
    public Reserva(Usuario usuario, Evento evento) {
        this.usuario = usuario;
        this.evento = evento;
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Usuario getUsuario() {
        return usuario;
    }
    
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
    public Evento getEvento() {
        return evento;
    }
    
    public void setEvento(Evento evento) {
        this.evento = evento;
    }
    
    public LocalDateTime getDataReserva() {
        return dataReserva;
    }
    
    public void setDataReserva(LocalDateTime dataReserva) {
        this.dataReserva = dataReserva;
    }
    
    public LocalDateTime getDataExpiracao() {
        return dataExpiracao;
    }
    
    public void setDataExpiracao(LocalDateTime dataExpiracao) {
        this.dataExpiracao = dataExpiracao;
    }
    
    // Método auxiliar
    public boolean isExpirada() {
        return LocalDateTime.now().isAfter(dataExpiracao);
    }
}

