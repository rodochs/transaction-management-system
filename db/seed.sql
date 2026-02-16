-- Benefícios corporativos
INSERT INTO BENEFICIO (NOME, DESCRICAO, VALOR, ATIVO) VALUES
('Vale Alimentação', 'Benefício para compras em supermercados e estabelecimentos alimentícios', 800.00, TRUE),
('Vale Refeição', 'Benefício para refeições em restaurantes e lanchonetes', 600.00, TRUE),
('Vale Transporte', 'Auxílio para deslocamento casa-trabalho', 300.00, TRUE),
('Plano de Saúde', 'Cobertura médica e hospitalar completa', 1200.00, TRUE),
('Auxílio Home Office', 'Ajuda de custo para trabalho remoto', 150.00, TRUE);

-- Colaboradores
INSERT INTO CLIENTE (NOME, EMAIL) VALUES
('Ana Carolina Silva', 'ana.silva@empresa.com'),
('Bruno Oliveira Santos', 'bruno.santos@empresa.com'),
('Carla Fernandes Lima', 'carla.lima@empresa.com'),
('Diego Martins Costa', 'diego.costa@empresa.com'),
('Elena Rodrigues Souza', 'elena.souza@empresa.com');

-- Contas de Benefício (cada colaborador com diferentes benefícios)
INSERT INTO CONTA_BENEFICIO (CLIENTE_ID, BENEFICIO_ID, SALDO) VALUES
(1, 1, 1250.00),  -- Ana - Vale Alimentação
(1, 2, 890.50),   -- Ana - Vale Refeição
(1, 3, 180.00),   -- Ana - Vale Transporte
(2, 1, 2100.00),  -- Bruno - Vale Alimentação
(2, 2, 1450.75),  -- Bruno - Vale Refeição
(2, 4, 1200.00),  -- Bruno - Plano de Saúde
(3, 1, 650.25),   -- Carla - Vale Alimentação
(3, 2, 420.00),   -- Carla - Vale Refeição
(3, 5, 300.00),   -- Carla - Auxílio Home Office
(4, 1, 1800.00),  -- Diego - Vale Alimentação
(4, 3, 275.50),   -- Diego - Vale Transporte
(4, 4, 1200.00),  -- Diego - Plano de Saúde
(5, 2, 980.00),   -- Elena - Vale Refeição
(5, 5, 450.00);   -- Elena - Auxílio Home Office

-- Histórico de transações
INSERT INTO TRANSACAO_BENEFICIO (CONTA_ORIGEM_ID, CONTA_DESTINO_ID, VALOR, TIPO, DATA_HORA) VALUES
(1, 4, 150.00, 'TRANSFERENCIA', CURRENT_TIMESTAMP),
(2, 5, 200.00, 'TRANSFERENCIA', CURRENT_TIMESTAMP),
(3, NULL, 45.00, 'DEBITO', CURRENT_TIMESTAMP),
(4, NULL, 120.00, 'DEBITO', CURRENT_TIMESTAMP),
(6, 8, 100.00, 'TRANSFERENCIA', CURRENT_TIMESTAMP),
(7, NULL, 85.50, 'DEBITO', CURRENT_TIMESTAMP),
(9, NULL, 50.00, 'CREDITO', CURRENT_TIMESTAMP),
(10, 11, 75.00, 'TRANSFERENCIA', CURRENT_TIMESTAMP);
