---------------------------------------------------------------------------
-- HardSoft - Sistema de Ordem de Servico
-- SCRIPT 01: STORED PROCEDURES
---------------------------------------------------------------------------

use HardSoftDB
go

---------------------------------------------------------------------------
-- 1) Cadastrar um Admin (operador do sistema, faz login)
---------------------------------------------------------------------------
create procedure sp_CadAdmin
(
    @nome       varchar(100),   @email      varchar(100),
    @senha      varchar(60),    @cargo      varchar(50) = NULL,
    @telefone   varchar(20) = NULL,
    @endereco   varchar(200) = NULL
)
as
begin
    begin try
        begin tran
            insert into tb_usuarios (nome, email, senha, telefone, endereco, data_cadastro)
            values (@nome, @email, @senha, @telefone, @endereco, CAST(GETDATE() AS DATE))

            declare @idAdmin bigint
            set @idAdmin = SCOPE_IDENTITY()

            insert into tb_admins (id, cargo)
            values (@idAdmin, @cargo)
            commit
    end try
    begin catch
        rollback
        print 'Erro ao cadastrar admin: ' + ERROR_MESSAGE()
    end catch
end
go

-- executar - cadastrar 2 admins
exec sp_CadAdmin 'Matheus Tecnico', 'matheus@hardsoft.com', 'senha123',
    'Tecnico', '17999990001', 'Rua da Oficina, 50'
go

exec sp_CadAdmin 'Ana Gerente', 'ana@hardsoft.com', 'senha456',
    'Gerente', '17999990002', 'Rua da Oficina, 50'
go

---------------------------------------------------------------------------
-- 2) Cadastrar Cliente (com email + telefone iniciais)
-- Cliente nao loga, mas precisa ter pelo menos 1 email e 1 telefone
---------------------------------------------------------------------------
create procedure sp_CadCliente
(
    @nome           varchar(100),   @cpfCnpj        varchar(14),
    @endereco       varchar(200),   @email          varchar(100),
    @telefone       varchar(20),    @tipoTelefone   varchar(20) = 'CELULAR',
    @rg             varchar(20) = NULL
)
as
begin
    begin try
        begin tran
            -- email e senha em tb_usuarios ficam NULL (cliente nao loga)
            insert into tb_usuarios (nome, endereco, data_cadastro)
            values (@nome, @endereco, CAST(GETDATE() AS DATE))

            declare @idCliente bigint
            set @idCliente = SCOPE_IDENTITY()

            insert into tb_clientes (id, cpf_cnpj, rg, status_cliente)
            values (@idCliente, @cpfCnpj, @rg, 'ATIVO')

            -- email principal
            insert into tb_cliente_emails (id_cliente, email, principal)
            values (@idCliente, @email, 1)

            -- telefone principal
            insert into tb_cliente_telefones (id_cliente, telefone, tipo, principal)
            values (@idCliente, @telefone, UPPER(@tipoTelefone), 1)
            commit
    end try
    begin catch
        rollback
        print 'Erro ao cadastrar cliente: ' + ERROR_MESSAGE()
    end catch
end
go

-- executar - cadastrar 4 clientes
exec sp_CadCliente 'Joao Silva', '12345678901',
    'Rua A, 100 - Centro', 'joao@email.com', '17988887777', 'CELULAR', 'MG1234567'
go

exec sp_CadCliente 'Maria Santos', '98765432100',
    'Av. Brasil, 200', 'maria@email.com', '17988886666', 'CELULAR', 'SP9876543'
go

exec sp_CadCliente 'Tech Solutions LTDA', '12345678000199',
    'Av. Empresarial, 1500', 'contato@techsolutions.com', '1733334444', 'COMERCIAL', NULL
go

exec sp_CadCliente 'Carlos Oliveira', '11122233344',
    'Rua das Flores, 50', 'carlos@email.com', '17977776666', 'CELULAR', 'SP1112223'
go

---------------------------------------------------------------------------
-- 3) Adicionar email extra a um cliente
---------------------------------------------------------------------------
create procedure sp_AddEmailCliente
(
    @idCliente bigint, @email varchar(100), @principal bit = 0
)
as
begin
    begin try
        begin tran
            insert into tb_cliente_emails (id_cliente, email, principal)
            values (@idCliente, @email, @principal)
            commit
    end try
    begin catch
        rollback
    end catch
end
go

-- adicionar email de trabalho do Joao
exec sp_AddEmailCliente 3, 'joao.trabalho@empresa.com', 0
go

---------------------------------------------------------------------------
-- 4) Adicionar telefone extra a um cliente
---------------------------------------------------------------------------
create procedure sp_AddTelefoneCliente
(
    @idCliente bigint, @telefone varchar(20),
    @tipo varchar(20), @principal bit = 0
)
as
begin
    begin try
        begin tran
            insert into tb_cliente_telefones (id_cliente, telefone, tipo, principal)
            values (@idCliente, @telefone, UPPER(@tipo), @principal)
            commit
    end try
    begin catch
        rollback
    end catch
end
go

exec sp_AddTelefoneCliente 3, '1735551234', 'FIXO', 0
go

---------------------------------------------------------------------------
-- 5) Cadastrar Setup (configuracao de computador)
---------------------------------------------------------------------------
create procedure sp_CadSetup
(
    @idCliente bigint,
    @marca varchar(50), @modelo varchar(50),
    @processador varchar(100), @memoria varchar(50),
    @placaDeVideo varchar(100), @armazenamento varchar(100)
)
as
begin
    begin try
        begin tran
            insert into tb_setups
                (marca, modelo, processador, memoria,
                 placa_de_video, armazenamento, id_cliente)
            values (@marca, @modelo, @processador, @memoria,
                    @placaDeVideo, @armazenamento, @idCliente)
            commit
    end try
    begin catch
        rollback
    end catch
end
go

---------------------------------------------------------------------------
-- 6) Cadastrar Peca no estoque
---------------------------------------------------------------------------
create procedure sp_CadPeca
(
    @nome varchar(100), @descricao varchar(200),
    @marca varchar(50), @condicao varchar(20),
    @precoCusto money, @precoVenda money, @qtdEstoque int
)
as
begin
    begin try
        begin tran
            insert into tb_pecas
                (nome, descricao, marca, condicao,
                 preco_custo, preco_venda, qnt_estoque)
            values (@nome, @descricao, @marca, @condicao,
                    @precoCusto, @precoVenda, @qtdEstoque)
            commit
    end try
    begin catch
        rollback
    end catch
end
go

---------------------------------------------------------------------------
-- 7) Cadastrar Tipo de Servico (catalogo)
---------------------------------------------------------------------------
create procedure sp_CadTipoServico
(
    @nome varchar(100), @descricao varchar(255), @valorBase money
)
as
begin
    begin try
        begin tran
            insert into tb_tipos_servico (nome, descricao, valor_base)
            values (@nome, @descricao, @valorBase)
            commit
    end try
    begin catch
        rollback
    end catch
end
go

---------------------------------------------------------------------------
-- 8) Abrir Ordem de Servico (OS)
-- Valida que o setup pertence ao cliente
---------------------------------------------------------------------------
create procedure sp_AbrirOS
(
    @idCliente bigint, @idSetup bigint, @idAdmin bigint,
    @defeito varchar(MAX) = NULL
)
as
begin
    begin try
        begin tran
            -- validacao: setup tem que pertencer ao cliente
            if not exists (
                select 1 from tb_setups
                where id_setup = @idSetup and id_cliente = @idCliente
            )
            begin
                raiserror('O setup informado nao pertence ao cliente.', 16, 1)
                rollback
                return
            end

            insert into tb_ordens_servico
                (data_entrada, status, defeito, valor_total,
                 id_cliente, id_setup, id_admin)
            values (GETDATE(), 'ABERTA', @defeito, 0,
                    @idCliente, @idSetup, @idAdmin)
            commit
    end try
    begin catch
        if @@TRANCOUNT > 0 rollback
        print 'Erro ao abrir OS: ' + ERROR_MESSAGE()
    end catch
end
go

---------------------------------------------------------------------------
-- 9) Adicionar Servico a uma OS
---------------------------------------------------------------------------
create procedure sp_AddServicoOS
(
    @idOS bigint, @idTipoServico bigint,
    @descricaoServico varchar(500) = NULL, @precoMaoDeObra money
)
as
begin
    begin try
        begin tran
            -- nao deixar adicionar servico em OS finalizada
            if exists (
                select 1 from tb_ordens_servico
                where id_ordem_servico = @idOS
                  and UPPER(status) in ('ENTREGUE', 'CANCELADA')
            )
            begin
                raiserror('OS entregue ou cancelada nao aceita novos servicos.', 16, 1)
                rollback
                return
            end

            insert into tb_servicos_executados
                (id_ordem_servico, id_tipo_servico,
                 descricao_servico, preco_mao_de_obra)
            values (@idOS, @idTipoServico, @descricaoServico, @precoMaoDeObra)
            commit
    end try
    begin catch
        if @@TRANCOUNT > 0 rollback
    end catch
end
go

---------------------------------------------------------------------------
-- 10) Usar Peca em um Servico (estoque baixa pelo trigger)
-- Preco da peca e congelado no momento da venda
---------------------------------------------------------------------------
create procedure sp_UsarPecaServico
(
    @idServico bigint, @idPeca bigint, @qtdVendida int
)
as
begin
    begin try
        begin tran
            -- buscar preco atual da peca pra congelar
            declare @precoVenda money
            select @precoVenda = preco_venda
            from tb_pecas where id_peca = @idPeca

            if @precoVenda is null
            begin
                raiserror('Peca nao encontrada.', 16, 1)
                rollback
                return
            end

            -- trigger valida estoque e da baixa
            insert into tb_pecas_servicos
                (id_servico, id_peca, qnt_vendida, vlr_unitario_venda)
            values (@idServico, @idPeca, @qtdVendida, @precoVenda)
            commit
    end try
    begin catch
        if @@TRANCOUNT > 0 rollback
    end catch
end
go

---------------------------------------------------------------------------
-- 11) Alterar status de uma OS
-- Trigger preenche data_saida quando vai pra CONCLUIDA/ENTREGUE
---------------------------------------------------------------------------
create procedure sp_AlterarStatusOS
(
    @idOS bigint, @novoStatus varchar(30)
)
as
begin
    begin try
        begin tran
            if UPPER(@novoStatus) not in
               ('ABERTA', 'EM_ANDAMENTO', 'AGUARDANDO_PECA',
                'CONCLUIDA', 'ENTREGUE', 'CANCELADA')
            begin
                raiserror('Status invalido.', 16, 1)
                rollback
                return
            end

            update tb_ordens_servico
            set status = UPPER(@novoStatus)
            where id_ordem_servico = @idOS
            commit
    end try
    begin catch
        if @@TRANCOUNT > 0 rollback
    end catch
end
go

---------------------------------------------------------------------------
-- 12) Repor estoque de uma peca
---------------------------------------------------------------------------
create procedure sp_ReporEstoque
(
    @idPeca bigint, @quantidade int
)
as
begin
    begin try
        begin tran
            if @quantidade <= 0
            begin
                raiserror('Quantidade deve ser maior que zero.', 16, 1)
                rollback
                return
            end

            update tb_pecas
            set qnt_estoque = qnt_estoque + @quantidade
            where id_peca = @idPeca
            commit
    end try
    begin catch
        if @@TRANCOUNT > 0 rollback
    end catch
end
go

---------------------------------------------------------------------------
-- 13) Cancelar OS (devolve pecas pro estoque via trigger)
---------------------------------------------------------------------------
create procedure sp_CancelarOS
(
    @idOS bigint, @motivo varchar(MAX) = NULL
)
as
begin
    begin try
        begin tran
            declare @statusAtual varchar(30)
            select @statusAtual = status
            from tb_ordens_servico where id_ordem_servico = @idOS

            if @statusAtual is null
            begin
                raiserror('OS nao encontrada.', 16, 1)
                rollback
                return
            end

            if UPPER(@statusAtual) in ('ENTREGUE', 'CANCELADA')
            begin
                raiserror('OS ja esta finalizada.', 16, 1)
                rollback
                return
            end

            -- remover pecas (trigger devolve estoque)
            delete from tb_pecas_servicos
            where id_servico in (
                select id_servico from tb_servicos_executados
                where id_ordem_servico = @idOS
            )

            update tb_ordens_servico
            set status = 'CANCELADA',
                solucao = ISNULL('CANCELADA: ' + @motivo, 'CANCELADA'),
                data_saida = GETDATE()
            where id_ordem_servico = @idOS
            commit
    end try
    begin catch
        if @@TRANCOUNT > 0 rollback
    end catch
end
go

---------------------------------------------------------------------------
-- 14) Excluir Cliente em cascata
-- REGRA: bloqueia se tiver OS CONCLUIDA/ENTREGUE (preserva historico)
---------------------------------------------------------------------------
create procedure sp_ExcluirCliente
(
    @idCliente bigint
)
as
begin
    begin try
        begin tran
            if exists (
                select 1 from tb_ordens_servico
                where id_cliente = @idCliente
                  and UPPER(status) in ('CONCLUIDA', 'ENTREGUE')
            )
            begin
                raiserror('Cliente possui OS finalizada. Nao pode excluir.', 16, 1)
                rollback
                return
            end

            delete from tb_pecas_servicos
            where id_servico in (
                select s.id_servico from tb_servicos_executados s
                inner join tb_ordens_servico os
                    on os.id_ordem_servico = s.id_ordem_servico
                where os.id_cliente = @idCliente
            )

            delete from tb_servicos_executados
            where id_ordem_servico in (
                select id_ordem_servico from tb_ordens_servico
                where id_cliente = @idCliente
            )

            delete from tb_ordens_servico where id_cliente = @idCliente
            delete from tb_setups where id_cliente = @idCliente
            delete from tb_cliente_emails where id_cliente = @idCliente
            delete from tb_cliente_telefones where id_cliente = @idCliente
            delete from tb_clientes where id = @idCliente
            delete from tb_usuarios where id = @idCliente

            commit
    end try
    begin catch
        if @@TRANCOUNT > 0 rollback
    end catch
end
go
