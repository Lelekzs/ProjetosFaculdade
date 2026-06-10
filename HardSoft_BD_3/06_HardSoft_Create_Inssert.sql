

Observação: as tabelas são criadas automaticamente pela aplicação Spring Boot
(Hibernate). Os comandos CREATE TABLE abaixo representam a estrutura
equivalente gerada no SQL Server, apresentados para fins de documentação.

------------------------------------------------------------------------
-- 1) CRIAÇÃO DAS TABELAS (CREATE TABLE)
------------------------------------------------------------------------

-- Tabela base de usuários (herança JOINED)
CREATE TABLE tb_usuarios (
    id             BIGINT IDENTITY(1,1) PRIMARY KEY,
    nome           VARCHAR(100) NOT NULL,
    email          VARCHAR(100),
    senha          VARCHAR(60),
    telefone       VARCHAR(20),
    endereco       VARCHAR(200),
    data_cadastro  DATE NOT NULL
);

-- Administradores (herda de tb_usuarios)
CREATE TABLE tb_admins (
    id     BIGINT PRIMARY KEY,
    cargo  VARCHAR(50),
    CONSTRAINT FK_admin_usuario FOREIGN KEY (id) REFERENCES tb_usuarios(id)
);

-- Clientes (herda de tb_usuarios)
CREATE TABLE tb_clientes (
    id              BIGINT PRIMARY KEY,
    cpf_cnpj        VARCHAR(14) NOT NULL UNIQUE,
    rg              VARCHAR(20),
    status_cliente  VARCHAR(10) NOT NULL DEFAULT 'ATIVO',
    CONSTRAINT FK_cliente_usuario FOREIGN KEY (id) REFERENCES tb_usuarios(id)
);

-- E-mails do cliente (1:N)
CREATE TABLE tb_cliente_emails (
    id_email    BIGINT IDENTITY(1,1) PRIMARY KEY,
    email       VARCHAR(100) NOT NULL,
    principal   BIT NOT NULL DEFAULT 0,
    id_cliente  BIGINT NOT NULL,
    CONSTRAINT FK_email_cliente FOREIGN KEY (id_cliente) REFERENCES tb_clientes(id)
);

-- Telefones do cliente (1:N)
CREATE TABLE tb_cliente_telefones (
    id_telefone  BIGINT IDENTITY(1,1) PRIMARY KEY,
    telefone     VARCHAR(20) NOT NULL,
    tipo         VARCHAR(20) NOT NULL,   -- CELULAR, FIXO, COMERCIAL
    principal    BIT NOT NULL DEFAULT 0,
    id_cliente   BIGINT NOT NULL,
    CONSTRAINT FK_tel_cliente FOREIGN KEY (id_cliente) REFERENCES tb_clientes(id)
);

-- Equipamentos do cliente (setups)
CREATE TABLE tb_setups (
    id_setup        BIGINT IDENTITY(1,1) PRIMARY KEY,
    marca           VARCHAR(50),
    modelo          VARCHAR(50),
    processador     VARCHAR(100),
    memoria         VARCHAR(50),
    placa_de_video  VARCHAR(100),
    armazenamento   VARCHAR(100),
    id_cliente      BIGINT NOT NULL,
    CONSTRAINT FK_setup_cliente FOREIGN KEY (id_cliente) REFERENCES tb_clientes(id)
);

-- Peças em estoque
CREATE TABLE tb_pecas (
    id_peca      BIGINT IDENTITY(1,1) PRIMARY KEY,
    nome         VARCHAR(100) NOT NULL,
    descricao    VARCHAR(200),
    marca        VARCHAR(50),
    condicao     VARCHAR(20),
    preco_custo  DECIMAL(10,2),
    preco_venda  DECIMAL(10,2) NOT NULL,
    qnt_estoque  INT NOT NULL DEFAULT 0
);

-- Catálogo de tipos de serviço
CREATE TABLE tb_tipos_servico (
    id_tipo_servico  BIGINT IDENTITY(1,1) PRIMARY KEY,
    nome             VARCHAR(100) NOT NULL UNIQUE,
    descricao        VARCHAR(255),
    valor_base       DECIMAL(10,2) NOT NULL
);

-- Ordens de serviço (entidade central)
CREATE TABLE tb_ordens_servico (
    id_ordem_servico  BIGINT IDENTITY(1,1) PRIMARY KEY,
    data_entrada      DATETIME NOT NULL,
    data_saida        DATETIME,
    status            VARCHAR(30) NOT NULL,
    defeito           TEXT,
    solucao           TEXT,
    valor_total       DECIMAL(10,2) DEFAULT 0,
    id_cliente        BIGINT NOT NULL,
    id_setup          BIGINT,
    id_admin          BIGINT,
    CONSTRAINT FK_os_cliente FOREIGN KEY (id_cliente) REFERENCES tb_clientes(id),
    CONSTRAINT FK_os_setup   FOREIGN KEY (id_setup)   REFERENCES tb_setups(id_setup),
    CONSTRAINT FK_os_admin   FOREIGN KEY (id_admin)   REFERENCES tb_admins(id)
);

-- Serviços executados em cada OS
CREATE TABLE tb_servicos_executados (
    id_servico         BIGINT IDENTITY(1,1) PRIMARY KEY,
    descricao_servico  VARCHAR(500),
    preco_mao_de_obra  DECIMAL(10,2) NOT NULL,
    id_ordem_servico   BIGINT NOT NULL,
    id_tipo_servico    BIGINT NOT NULL,
    CONSTRAINT FK_serv_os   FOREIGN KEY (id_ordem_servico) REFERENCES tb_ordens_servico(id_ordem_servico),
    CONSTRAINT FK_serv_tipo FOREIGN KEY (id_tipo_servico)  REFERENCES tb_tipos_servico(id_tipo_servico)
);

-- Peças utilizadas em cada serviço (preço congelado)
CREATE TABLE tb_pecas_servicos (
    id_peca_servico     BIGINT IDENTITY(1,1) PRIMARY KEY,
    qnt_vendida         INT NOT NULL,
    vlr_unitario_venda  DECIMAL(10,2) NOT NULL,
    id_servico          BIGINT NOT NULL,
    id_peca             BIGINT NOT NULL,
    CONSTRAINT FK_ps_servico FOREIGN KEY (id_servico) REFERENCES tb_servicos_executados(id_servico),
    CONSTRAINT FK_ps_peca    FOREIGN KEY (id_peca)    REFERENCES tb_pecas(id_peca)
);
GO
------------------------------------------------------------------------
-- 2) INJEÇÃO DE DADOS (INSERTs — no mínimo 10 por tabela)
------------------------------------------------------------------------

-- ===== tb_usuarios (10 admins + 12 clientes = 22 registros) =====
-- Admins (id 1 a 10)
INSERT INTO tb_usuarios (nome, email, senha, telefone, endereco, data_cadastro) VALUES
('Matheus Vilela',   'matheus@hardsoft.com', 'senha123', '17999990001', 'Rua da Oficina, 50',  GETDATE()),
('Ana Souza',        'ana@hardsoft.com',     'senha456', '17999990002', 'Rua da Oficina, 50',  GETDATE()),
('Leandro Belati',   'leandro@hardsoft.com', 'senha789', '17999990003', 'Rua da Oficina, 50',  GETDATE()),
('Davi Vieira',      'davi@hardsoft.com',    'senha101', '17999990004', 'Rua da Oficina, 50',  GETDATE()),
('Carlos Pereira',   'carlos@hardsoft.com',  'senha112', '17999990005', 'Rua da Oficina, 50',  GETDATE()),
('Fernanda Lima',    'fernanda@hardsoft.com','senha131', '17999990006', 'Rua da Oficina, 50',  GETDATE()),
('Roberto Alves',    'roberto@hardsoft.com', 'senha415', '17999990007', 'Rua da Oficina, 50',  GETDATE()),
('Juliana Castro',   'juliana@hardsoft.com', 'senha161', '17999990008', 'Rua da Oficina, 50',  GETDATE()),
('Paulo Mendes',     'paulo@hardsoft.com',   'senha718', '17999990009', 'Rua da Oficina, 50',  GETDATE()),
('Beatriz Rocha',    'beatriz@hardsoft.com', 'senha192', '17999990010', 'Rua da Oficina, 50',  GETDATE());

-- Clientes (id 11 a 22) - email/senha NULL pois nao logam
INSERT INTO tb_usuarios (nome, email, senha, telefone, endereco, data_cadastro) VALUES
('João da Silva',       NULL, NULL, '17988880001', 'Rua A, 100',          GETDATE()),
('Maria Oliveira',      NULL, NULL, '17988880002', 'Rua B, 200',          GETDATE()),
('Tech Solutions LTDA', NULL, NULL, '17988880003', 'Av. Comercial, 1500', GETDATE()),
('Pedro Santos',        NULL, NULL, '17988880004', 'Rua C, 300',          GETDATE()),
('Carla Ferreira',      NULL, NULL, '17988880005', 'Rua D, 400',          GETDATE()),
('Lucas Martins',       NULL, NULL, '17988880006', 'Rua E, 500',          GETDATE()),
('Amanda Costa',        NULL, NULL, '17988880007', 'Rua F, 600',          GETDATE()),
('InfoShop ME',         NULL, NULL, '17988880008', 'Av. Brasil, 2000',    GETDATE()),
('Rafael Gomes',        NULL, NULL, '17988880009', 'Rua G, 700',          GETDATE()),
('Patrícia Dias',       NULL, NULL, '17988880010', 'Rua H, 800',          GETDATE()),
('Bruno Azevedo',       NULL, NULL, '17988880011', 'Rua I, 900',          GETDATE()),
('Escola Saber LTDA',   NULL, NULL, '17988880012', 'Av. Educacao, 350',   GETDATE());

-- ===== tb_admins (10 registros) =====
INSERT INTO tb_admins (id, cargo) VALUES
(1,'Gerente'),(2,'Tecnico'),(3,'Tecnico'),(4,'Tecnico'),(5,'Atendente'),
(6,'Tecnico'),(7,'Atendente'),(8,'Tecnico'),(9,'Gerente'),(10,'Tecnico');

-- ===== tb_clientes (12 registros) =====
INSERT INTO tb_clientes (id, cpf_cnpj, rg, status_cliente) VALUES
(11,'11111111111','SP100001','ATIVO'),
(12,'22222222222','SP100002','ATIVO'),
(13,'33333333000199',NULL,'ATIVO'),
(14,'44444444444','SP100004','ATIVO'),
(15,'55555555555','SP100005','ATIVO'),
(16,'66666666666','SP100006','ATIVO'),
(17,'77777777777','SP100007','ATIVO'),
(18,'88888888000188',NULL,'ATIVO'),
(19,'99999999999','SP100009','ATIVO'),
(20,'10101010101','SP100010','ATIVO'),
(21,'12121212121','SP100011','ATIVO'),
(22,'13131313000177',NULL,'ATIVO');

-- ===== tb_cliente_emails (12 registros) =====
INSERT INTO tb_cliente_emails (email, principal, id_cliente) VALUES
('joao@email.com',1,11),('maria@email.com',1,12),('contato@techsolutions.com',1,13),
('pedro@email.com',1,14),('carla@email.com',1,15),('lucas@email.com',1,16),
('amanda@email.com',1,17),('vendas@infoshop.com',1,18),('rafael@email.com',1,19),
('patricia@email.com',1,20),('bruno@email.com',1,21),('contato@escolasaber.com',1,22);

-- ===== tb_cliente_telefones (12 registros) =====
INSERT INTO tb_cliente_telefones (telefone, tipo, principal, id_cliente) VALUES
('17988880001','CELULAR',1,11),('17988880002','CELULAR',1,12),('1733330003','COMERCIAL',1,13),
('17988880004','CELULAR',1,14),('17988880005','CELULAR',1,15),('17988880006','CELULAR',1,16),
('17988880007','CELULAR',1,17),('1733330008','COMERCIAL',1,18),('17988880009','CELULAR',1,19),
('17988880010','FIXO',1,20),('17988880011','CELULAR',1,21),('1733330012','COMERCIAL',1,22);

-- ===== tb_setups (12 registros) =====
INSERT INTO tb_setups (marca, modelo, processador, memoria, placa_de_video, armazenamento, id_cliente) VALUES
('Dell','Inspiron 15','Intel i5','8GB DDR4','Integrada','SSD 256GB',11),
('Apple','MacBook Air','Apple M1','8GB','Integrada','SSD 256GB',12),
('HP','ProDesk 400','Intel i7','16GB DDR4','Integrada','SSD 512GB',13),
('Acer','Aspire 5','Ryzen 5','8GB DDR4','Integrada','HD 1TB',14),
('Lenovo','IdeaPad 3','Intel i3','4GB DDR4','Integrada','HD 500GB',15),
('Asus','VivoBook','Ryzen 7','16GB DDR4','GTX 1650','SSD 512GB',16),
('Dell','XPS 13','Intel i7','16GB DDR4','Integrada','SSD 1TB',17),
('HP','EliteBook','Intel i5','8GB DDR4','Integrada','SSD 256GB',18),
('Samsung','Book','Intel i5','8GB DDR4','Integrada','SSD 256GB',19),
('Positivo','Motion','Intel Celeron','4GB DDR4','Integrada','HD 500GB',20),
('Acer','Nitro 5','Intel i7','16GB DDR4','RTX 3050','SSD 512GB',21),
('Dell','OptiPlex','Intel i5','8GB DDR4','Integrada','SSD 256GB',22);

-- ===== tb_pecas (12 registros) =====
INSERT INTO tb_pecas (nome, descricao, marca, condicao, preco_custo, preco_venda, qnt_estoque) VALUES
('SSD 480GB','SSD SATA 2.5','Kingston','NOVO',180.00,280.00,30),
('Memória 8GB DDR4','RAM 2666MHz','Corsair','NOVO',120.00,200.00,25),
('Fonte 500W','Fonte ATX 80 Plus','Corsair','NOVO',200.00,320.00,15),
('HD 1TB','HD 7200rpm','Seagate','NOVO',180.00,260.00,20),
('Cooler CPU','Air Cooler','Cooler Master','NOVO',90.00,150.00,18),
('Pasta Térmica','Seringa 4g','Arctic','NOVO',20.00,40.00,50),
('Teclado USB','Teclado ABNT2','Logitech','NOVO',45.00,80.00,40),
('Mouse USB','Mouse óptico','Logitech','NOVO',30.00,60.00,45),
('Placa de Vídeo GTX 1650','GPU 4GB','MSI','NOVO',900.00,1300.00,8),
('Bateria Notebook','Bateria 6 células','Genérica','NOVO',150.00,250.00,12),
('Tela 15.6','LCD Full HD','AUO','NOVO',300.00,480.00,10),
('Cabo SATA','Cabo de dados','Genérica','NOVO',8.00,18.00,60);

-- ===== tb_tipos_servico (10 registros) =====
INSERT INTO tb_tipos_servico (nome, descricao, valor_base) VALUES
('Formatação','Formatação e reinstalação do sistema',120.00),
('Limpeza Interna','Limpeza física e troca de pasta térmica',80.00),
('Troca de Peça','Substituição de componente de hardware',60.00),
('Remoção de Vírus','Remoção de malware e otimização',100.00),
('Upgrade de Hardware','Instalação de novos componentes',90.00),
('Backup de Dados','Cópia de segurança dos arquivos',70.00),
('Instalação de Programas','Instalação de softwares diversos',50.00),
('Diagnóstico','Análise técnica do equipamento',40.00),
('Reparo de Placa','Reparo em placa-mãe/circuitos',250.00),
('Recuperação de Dados','Recuperação de arquivos perdidos',300.00);

-- ===== tb_ordens_servico (12 registros) =====
INSERT INTO tb_ordens_servico (data_entrada, data_saida, status, defeito, solucao, valor_total, id_cliente, id_setup, id_admin) VALUES
('2026-01-05',NULL,'ABERTA','PC não liga',NULL,0,11,1,2),
('2026-01-06','2026-01-08','CONCLUIDA','Lentidão','Formatação e SSD',560.00,12,2,2),
('2026-01-07','2026-01-09','ENTREGUE','Tela quebrada','Troca de tela',480.00,13,3,3),
('2026-01-10',NULL,'EM_ANDAMENTO','Não dá vídeo',NULL,0,14,4,4),
('2026-01-11',NULL,'AGUARDANDO_PECA','Sem som','Aguardando placa',0,15,5,2),
('2026-01-12','2026-01-13','CONCLUIDA','Vírus','Remoção de vírus',100.00,16,6,6),
('2026-01-14','2026-01-16','ENTREGUE','Upgrade','Instalação de RAM',290.00,17,7,4),
('2026-01-15',NULL,'ABERTA','Superaquecimento',NULL,0,18,8,2),
('2026-01-16','2026-01-18','CONCLUIDA','Backup','Backup realizado',70.00,19,9,6),
('2026-01-17',NULL,'CANCELADA','Orçamento alto','Cliente desistiu',0,20,10,5),
('2026-01-18','2026-01-20','ENTREGUE','HD com defeito','Troca de HD',320.00,21,11,3),
('2026-01-19',NULL,'EM_ANDAMENTO','Não conecta wifi',NULL,0,22,12,4);

-- ===== tb_servicos_executados (12 registros) =====
INSERT INTO tb_servicos_executados (descricao_servico, preco_mao_de_obra, id_ordem_servico, id_tipo_servico) VALUES
('Formatação completa',120.00,2,1),
('Instalação de SSD',60.00,2,3),
('Troca da tela LCD',60.00,3,3),
('Remoção de malwares',100.00,6,4),
('Instalação de pente de RAM',90.00,7,5),
('Backup dos documentos',70.00,9,6),
('Troca do HD por novo',60.00,11,3),
('Limpeza interna completa',80.00,2,2),
('Diagnóstico inicial',40.00,3,8),
('Instalação de programas',50.00,7,7),
('Otimização do sistema',40.00,6,8),
('Configuração pós-troca',60.00,11,3);

-- ===== tb_pecas_servicos (12 registros) =====
INSERT INTO tb_pecas_servicos (qnt_vendida, vlr_unitario_venda, id_servico, id_peca) VALUES
(1,280.00,2,1),   -- SSD na OS 2
(1,480.00,3,11),  -- Tela na OS 3
(1,200.00,5,2),   -- RAM no upgrade
(1,260.00,7,4),   -- HD novo
(1,40.00,8,6),    -- Pasta termica na limpeza
(1,280.00,7,1),   -- SSD adicional
(2,18.00,7,12),   -- Cabos SATA
(1,150.00,5,5),   -- Cooler
(1,80.00,10,7),   -- Teclado
(1,60.00,10,8),   -- Mouse
(1,250.00,4,10),  -- Bateria
(1,320.00,1,3);   -- Fonte
GO


------------------------------------------------------------------------
-- 3) EXEMPLOS DE PROCEDURES, TRIGGERS, VIEWS E FUNCTIONS
------------------------------------------------------------------------

-- ---------- PROCEDURE: cadastrar cliente completo (transação) ----------
CREATE PROCEDURE sp_CadCliente
(
    @nome varchar(100), @cpfCnpj varchar(14), @endereco varchar(200),
    @email varchar(100), @telefone varchar(20),
    @tipoTelefone varchar(20) = 'CELULAR', @rg varchar(20) = NULL
)
AS
BEGIN
    BEGIN TRY
        BEGIN TRAN
            INSERT INTO tb_usuarios (nome, endereco, data_cadastro)
            VALUES (@nome, @endereco, CAST(GETDATE() AS DATE));

            DECLARE @idCliente bigint = SCOPE_IDENTITY();

            INSERT INTO tb_clientes (id, cpf_cnpj, rg, status_cliente)
            VALUES (@idCliente, @cpfCnpj, @rg, 'ATIVO');

            INSERT INTO tb_cliente_emails (id_cliente, email, principal)
            VALUES (@idCliente, @email, 1);

            INSERT INTO tb_cliente_telefones (id_cliente, telefone, tipo, principal)
            VALUES (@idCliente, @telefone, UPPER(@tipoTelefone), 1);
        COMMIT
    END TRY
    BEGIN CATCH
        ROLLBACK
        PRINT 'Erro ao cadastrar cliente: ' + ERROR_MESSAGE();
    END CATCH
END
GO

-- ---------- TRIGGER: baixa automática de estoque ----------
CREATE TRIGGER tg_BaixaEstoque
ON tb_pecas_servicos
AFTER INSERT
AS
BEGIN
    UPDATE P
    SET P.qnt_estoque = P.qnt_estoque - I.qnt_vendida
    FROM tb_pecas AS P
    INNER JOIN inserted AS I ON I.id_peca = P.id_peca;
END
GO

-- ---------- TRIGGER: soft delete de cliente (INSTEAD OF DELETE) ----------
CREATE TRIGGER tg_SoftDeleteCliente
ON tb_clientes
INSTEAD OF DELETE
AS
BEGIN
    -- em vez de apagar, marca como INATIVO (preserva histórico)
    UPDATE tb_clientes
    SET status_cliente = 'INATIVO'
    WHERE id IN (SELECT id FROM deleted);
END
GO

-- ---------- VIEW: ordens de serviço com dados relacionados ----------
CREATE VIEW v_OS AS
SELECT
    os.id_ordem_servico                 AS Numero_OS,
    u.nome                              AS Cliente,
    ua.nome                             AS Administrador,
    os.status                           AS Status,
    os.data_entrada                     AS Entrada,
    os.valor_total                      AS Total
FROM tb_ordens_servico os
INNER JOIN tb_clientes c  ON c.id  = os.id_cliente
INNER JOIN tb_usuarios u  ON u.id  = c.id
LEFT  JOIN tb_admins  a   ON a.id  = os.id_admin
LEFT  JOIN tb_usuarios ua ON ua.id = a.id;
GO

-- ---------- FUNCTION: histórico de OS de um cliente (retorna tabela) ----------
CREATE FUNCTION fc_OSsPorCliente (@idCliente bigint)
RETURNS TABLE
AS
RETURN
(
    SELECT id_ordem_servico, data_entrada, status, valor_total
    FROM tb_ordens_servico
    WHERE id_cliente = @idCliente
);
GO

-- Exemplos de uso:
-- EXEC sp_CadCliente 'Novo Cliente','12345678900','Rua X, 1','novo@email.com','17999998888','CELULAR','SP999';
-- SELECT * FROM v_OS;
-- SELECT * FROM fc_OSsPorCliente(11);




------------------------------------------------------------------------
-- 4) Back- up e Restore
------------------------------------------------------------------------

BACKUP DATABASE HardSoftDB
TO DISK = 'C:\Backup\HardSoftDB.bak'
WITH FORMAT,
     NAME = 'Backup completo - HardSoftDB';


     USE master;
GO

RESTORE DATABASE HardSoftDB
FROM DISK = 'C:\Backup\HardSoftDB.bak'
WITH REPLACE;