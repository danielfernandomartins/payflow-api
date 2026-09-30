-- Inserção de Usuários de Teste (Usuários comuns e Lojistas)
INSERT INTO users (id, full_name, document, email, password, user_type, created_at)
VALUES 
    (1, 'Alice Silva', '12345678901', 'alice@payflow.com', '$2a$10$7vj41H8cZ3gR5z5n5Y4o9eXv7oN0jX4.5r2oN0jX45r2oN0jX45r2', 'COMMON', CURRENT_TIMESTAMP),
    (2, 'Bob Santos', '98765432100', 'bob@payflow.com', '$2a$10$7vj41H8cZ3gR5z5n5Y4o9eXv7oN0jX4.5r2oN0jX45r2oN0jX45r2', 'COMMON', CURRENT_TIMESTAMP),
    (3, 'Tech Store Eletronicos LTDA', '12345678000199', 'contato@techstore.com', '$2a$10$7vj41H8cZ3gR5z5n5Y4o9eXv7oN0jX4.5r2oN0jX45r2oN0jX45r2', 'MERCHANT', CURRENT_TIMESTAMP);

-- Inserção de Carteiras com Saldo Inicial
INSERT INTO wallets (id, user_id, balance, version, updated_at)
VALUES 
    (1, 1, 1500.0000, 0, CURRENT_TIMESTAMP),
    (2, 2, 500.0000, 0, CURRENT_TIMESTAMP),
    (3, 3, 0.0000, 0, CURRENT_TIMESTAMP);
