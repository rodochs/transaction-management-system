-- Schema do banco de dados para PostgreSQL
-- Executado automaticamente na inicialização do container

CREATE TABLE IF NOT EXISTS beneficio (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(255),
    valor NUMERIC(15,2) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT true,
    version BIGINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS cliente (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS conta_beneficio (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    beneficio_id BIGINT NOT NULL,
    saldo NUMERIC(15,2) NOT NULL DEFAULT 0,
    version BIGINT DEFAULT 0,
    CONSTRAINT fk_conta_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id),
    CONSTRAINT fk_conta_beneficio FOREIGN KEY (beneficio_id) REFERENCES beneficio(id),
    CONSTRAINT uk_conta_cliente_beneficio UNIQUE (cliente_id, beneficio_id)
);

CREATE TABLE IF NOT EXISTS transacao_beneficio (
    id BIGSERIAL PRIMARY KEY,
    conta_origem_id BIGINT NOT NULL,
    conta_destino_id BIGINT,
    valor NUMERIC(15,2) NOT NULL,
    tipo VARCHAR(20) NOT NULL CHECK (tipo IN ('CREDITO', 'DEBITO', 'TRANSFERENCIA')),
    data_hora TIMESTAMP NOT NULL,
    CONSTRAINT fk_transacao_origem FOREIGN KEY (conta_origem_id) REFERENCES conta_beneficio(id),
    CONSTRAINT fk_transacao_destino FOREIGN KEY (conta_destino_id) REFERENCES conta_beneficio(id)
);

-- Índices para performance
CREATE INDEX IF NOT EXISTS idx_transacao_origem ON transacao_beneficio(conta_origem_id);
CREATE INDEX IF NOT EXISTS idx_transacao_destino ON transacao_beneficio(conta_destino_id);
CREATE INDEX IF NOT EXISTS idx_transacao_data ON transacao_beneficio(data_hora);
CREATE INDEX IF NOT EXISTS idx_conta_cliente ON conta_beneficio(cliente_id);
CREATE INDEX IF NOT EXISTS idx_conta_beneficio ON conta_beneficio(beneficio_id);
