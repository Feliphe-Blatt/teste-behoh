package com.behoh.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "eventos")
public class Evento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String nome;
    
    @Column(columnDefinition = "TEXT")
    private String descricao;
    
    @Column(nullable = false)
    private Integer vagas;
    
    @Column(name = "vagas_disponiveis", nullable = false)
    private Integer vagasDisponiveis;
    
    @Column(name = "data_inicio", nullable = false)
    private LocalDateTime dataInicio;
    
    @Column(name = "data_fim", nullable = false)
    private LocalDateTime dataFim;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (vagasDisponiveis == null) {
            vagasDisponiveis = vagas;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Construtores
    public Evento() {}
    
    public Evento(String nome, String descricao, Integer vagas, LocalDateTime dataInicio, LocalDateTime dataFim) {
        this.nome = nome;
        this.descricao = descricao;
        this.vagas = vagas;
        this.vagasDisponiveis = vagas;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getDescricao() {
        return descricao;
    }
    
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    public Integer getVagas() {
        return vagas;
    }
    
    public void setVagas(Integer vagas) {
        this.vagas = vagas;
    }
    
    public Integer getVagasDisponiveis() {
        return vagasDisponiveis;
    }
    
    public void setVagasDisponiveis(Integer vagasDisponiveis) {
        this.vagasDisponiveis = vagasDisponiveis;
    }
    
    public LocalDateTime getDataInicio() {
        return dataInicio;
    }
    
    public void setDataInicio(LocalDateTime dataInicio) {
        this.dataInicio = dataInicio;
    }
    
    public LocalDateTime getDataFim() {
        return dataFim;
    }
    
    public void setDataFim(LocalDateTime dataFim) {
        this.dataFim = dataFim;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

