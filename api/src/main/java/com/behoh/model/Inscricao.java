package com.behoh.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inscricoes", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"usuario_id", "evento_id"})
})
public class Inscricao {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evento_id", nullable = false)
    private Evento evento;
    
    @Column(name = "data_inscricao", nullable = false)
    private LocalDateTime dataInscricao;
    
    @Column(nullable = false)
    private String status = "CONFIRMADA";
    
    @PrePersist
    protected void onCreate() {
        dataInscricao = LocalDateTime.now();
    }
    
    // Construtores
    public Inscricao() {}
    
    public Inscricao(Usuario usuario, Evento evento) {
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
    
    public LocalDateTime getDataInscricao() {
        return dataInscricao;
    }
    
    public void setDataInscricao(LocalDateTime dataInscricao) {
        this.dataInscricao = dataInscricao;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}

