-- Tabela de Usuários
CREATE TABLE usuarios (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Tabela de Eventos
CREATE TABLE eventos (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    descricao TEXT,
    vagas INTEGER NOT NULL,
    vagas_disponiveis INTEGER NOT NULL,
    data_inicio TIMESTAMP NOT NULL,
    data_fim TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT vagas_positivas CHECK (vagas > 0),
    CONSTRAINT vagas_disponiveis_validas CHECK (vagas_disponiveis >= 0 AND vagas_disponiveis <= vagas)
);

-- Tabela de Inscrições
CREATE TABLE inscricoes (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    evento_id BIGINT NOT NULL,
    data_inscricao TIMESTAMP NOT NULL DEFAULT NOW(),
    status VARCHAR(50) NOT NULL DEFAULT 'CONFIRMADA',
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (evento_id) REFERENCES eventos(id) ON DELETE CASCADE,
    UNIQUE (usuario_id, evento_id)
);

-- Tabela de Reservas (temporárias)
CREATE TABLE reservas (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    evento_id BIGINT NOT NULL,
    data_reserva TIMESTAMP NOT NULL DEFAULT NOW(),
    data_expiracao TIMESTAMP NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (evento_id) REFERENCES eventos(id) ON DELETE CASCADE
);

-- Índices para melhorar performance
CREATE INDEX idx_inscricoes_usuario ON inscricoes(usuario_id);
CREATE INDEX idx_inscricoes_evento ON inscricoes(evento_id);
CREATE INDEX idx_reservas_expiracao ON reservas(data_expiracao);

