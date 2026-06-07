---------------------------------------------------------------------------
-- HardSoft - Sistema de Ordem de Servico
-- SCRIPT 02: TRIGGERS
---------------------------------------------------------------------------

use HardSoftDB
go

---------------------------------------------------------------------------
-- 1) Tabela de Log de historico de precos das pecas
---------------------------------------------------------------------------
create table LOG_HistoricoPrecoPeca
(
    idPeca      bigint          not null    references tb_pecas(id_peca),
    data        datetime        not null,
    precoAntigo money           not null,
    precoNovo   money           not null,
    usuario     varchar(100)    not null,
    primary key(idPeca, data)
)
go

---------------------------------------------------------------------------
-- 2) Trigger: registra mudanca de preco de peca
-- Tabela: tb_pecas   Evento: update   Tipo: AFTER
---------------------------------------------------------------------------
create trigger tg_LogPrecoPeca
ON          tb_pecas
AFTER       update
as
begin
    insert into LOG_HistoricoPrecoPeca (idPeca, data, precoAntigo, precoNovo, usuario)
        select I.id_peca, GETDATE(), D.preco_venda, I.preco_venda, SYSTEM_USER
        from inserted as I, deleted as D
        where I.id_peca = D.id_peca
          and I.preco_venda != D.preco_venda  -- so se o preco mudou
end
go

---------------------------------------------------------------------------
-- 3) Trigger: garantir que so 1 email seja PRINCIPAL por cliente
-- Tabela: tb_cliente_emails   Evento: insert, update   Tipo: AFTER
---------------------------------------------------------------------------
create trigger tg_EmailPrincipalUnico
ON          tb_cliente_emails
AFTER       insert, update
as
begin
    -- desmarca os outros emails do mesmo cliente quando um novo eh marcado principal
    update tb_cliente_emails
    set principal = 0
    where id_cliente in (select id_cliente from inserted where principal = 1)
      and id_email not in (select id_email from inserted where principal = 1)
      and principal = 1
end
go

---------------------------------------------------------------------------
-- 4) Trigger: garantir que so 1 telefone seja PRINCIPAL por cliente
-- Tabela: tb_cliente_telefones   Evento: insert, update   Tipo: AFTER
---------------------------------------------------------------------------
create trigger tg_TelefonePrincipalUnico
ON          tb_cliente_telefones
AFTER       insert, update
as
begin
    update tb_cliente_telefones
    set principal = 0
    where id_cliente in (select id_cliente from inserted where principal = 1)
      and id_telefone not in (select id_telefone from inserted where principal = 1)
      and principal = 1
end
go

---------------------------------------------------------------------------
-- 5) Trigger: validar formato do CPF/CNPJ (11 ou 14 digitos numericos)
-- Tabela: tb_clientes   Evento: insert, update   Tipo: AFTER
---------------------------------------------------------------------------
create trigger tg_ValidaCpfCnpj
ON          tb_clientes
AFTER       insert, update
as
begin
    if exists (
        select 1 from inserted
        where cpf_cnpj is null
           or (LEN(cpf_cnpj) <> 11 and LEN(cpf_cnpj) <> 14)
           or cpf_cnpj like '%[^0-9]%'
    )
    begin
        raiserror('CPF/CNPJ invalido: deve ter 11 ou 14 digitos numericos.', 16, 1)
        rollback
    end
end
go

---------------------------------------------------------------------------
-- 6) Trigger: bloquear estoque/preco negativo
-- Tabela: tb_pecas   Evento: insert, update   Tipo: AFTER
---------------------------------------------------------------------------
create trigger tg_ValidaPeca
ON          tb_pecas
AFTER       insert, update
as
begin
    if exists (select 1 from inserted where qnt_estoque < 0)
    begin
        raiserror('Quantidade em estoque nao pode ser negativa.', 16, 1)
        rollback
        return
    end

    if exists (select 1 from inserted where preco_venda < 0)
    begin
        raiserror('Preco de venda nao pode ser negativo.', 16, 1)
        rollback
    end
end
go

---------------------------------------------------------------------------
-- 7) Trigger: BAIXA NO ESTOQUE quando peca for usada num servico
-- (e DEVOLVE quando removida)
-- Tabela: tb_pecas_servicos   Evento: insert, update, delete   Tipo: AFTER
---------------------------------------------------------------------------
create trigger tg_BaixaEstoque
ON          tb_pecas_servicos
AFTER       insert, update, delete
as
begin
    -- devolve o estoque do que foi removido (delete ou linha antiga do update)
    update P
    set P.qnt_estoque = P.qnt_estoque + D.qnt_vendida
    from tb_pecas as P
    inner join deleted as D on D.id_peca = P.id_peca

    -- valida estoque suficiente do que vai entrar
    if exists (
        select 1 from inserted as I
        inner join tb_pecas as P on P.id_peca = I.id_peca
        where P.qnt_estoque < I.qnt_vendida
    )
    begin
        raiserror('Estoque insuficiente para a peca solicitada.', 16, 1)
        rollback
        return
    end

    -- da baixa
    update P
    set P.qnt_estoque = P.qnt_estoque - I.qnt_vendida
    from tb_pecas as P
    inner join inserted as I on I.id_peca = P.id_peca
end
go

---------------------------------------------------------------------------
-- 8) Trigger: RECALCULAR valor_total da OS quando servicos mudam
-- valor_total = SUM(mao_de_obra) + SUM(qtd_peca * preco)
-- Tabela: tb_servicos_executados   Evento: insert, update, delete   Tipo: AFTER
---------------------------------------------------------------------------
create trigger tg_AtualizaValorOS_Servico
ON          tb_servicos_executados
AFTER       insert, update, delete
as
begin
    declare @OSsAfetadas table (id_os bigint primary key)

    insert into @OSsAfetadas (id_os)
    select distinct id_ordem_servico from inserted
    union
    select distinct id_ordem_servico from deleted

    update OS
    set OS.valor_total = ISNULL(T.total, 0)
    from tb_ordens_servico as OS
    inner join @OSsAfetadas as A on A.id_os = OS.id_ordem_servico
    left join (
        select S.id_ordem_servico,
               SUM(S.preco_mao_de_obra) +
               ISNULL((
                   select SUM(PS.qnt_vendida * PS.vlr_unitario_venda)
                   from tb_pecas_servicos as PS
                   where PS.id_servico in (
                       select S2.id_servico
                       from tb_servicos_executados as S2
                       where S2.id_ordem_servico = S.id_ordem_servico
                   )
               ), 0) as total
        from tb_servicos_executados as S
        group by S.id_ordem_servico
    ) as T on T.id_ordem_servico = OS.id_ordem_servico
end
go

---------------------------------------------------------------------------
-- 9) Trigger: recalcular valor_total quando pecas mudam
-- Tabela: tb_pecas_servicos   Evento: insert, update, delete   Tipo: AFTER
---------------------------------------------------------------------------
create trigger tg_AtualizaValorOS_Peca
ON          tb_pecas_servicos
AFTER       insert, update, delete
as
begin
    declare @OSsAfetadas table (id_os bigint primary key)

    insert into @OSsAfetadas (id_os)
    select distinct S.id_ordem_servico
    from tb_servicos_executados as S
    where S.id_servico in (
        select id_servico from inserted
        union
        select id_servico from deleted
    )

    update OS
    set OS.valor_total = ISNULL(T.total, 0)
    from tb_ordens_servico as OS
    inner join @OSsAfetadas as A on A.id_os = OS.id_ordem_servico
    left join (
        select S.id_ordem_servico,
               SUM(S.preco_mao_de_obra) +
               ISNULL((
                   select SUM(PS.qnt_vendida * PS.vlr_unitario_venda)
                   from tb_pecas_servicos as PS
                   where PS.id_servico in (
                       select S2.id_servico
                       from tb_servicos_executados as S2
                       where S2.id_ordem_servico = S.id_ordem_servico
                   )
               ), 0) as total
        from tb_servicos_executados as S
        group by S.id_ordem_servico
    ) as T on T.id_ordem_servico = OS.id_ordem_servico
end
go

---------------------------------------------------------------------------
-- 10) Trigger: preencher data_saida automaticamente quando OS for finalizada
-- (e bloquear mudancas em OS CANCELADA)
-- Tabela: tb_ordens_servico   Evento: update   Tipo: AFTER
---------------------------------------------------------------------------
create trigger tg_DataSaidaAutomatica
ON          tb_ordens_servico
AFTER       update
as
begin
    if not UPDATE(status)
        return

    -- preencher data_saida ao concluir/entregar
    update OS
    set OS.data_saida = GETDATE()
    from tb_ordens_servico as OS
    inner join inserted as I on I.id_ordem_servico = OS.id_ordem_servico
    inner join deleted as D on D.id_ordem_servico = OS.id_ordem_servico
    where OS.data_saida is null
      and UPPER(I.status) in ('CONCLUIDA', 'ENTREGUE')
      and UPPER(D.status) not in ('CONCLUIDA', 'ENTREGUE')

    -- bloquear mudanca em OS CANCELADA
    if exists (
        select 1 from inserted as I
        inner join deleted as D on D.id_ordem_servico = I.id_ordem_servico
        where UPPER(D.status) = 'CANCELADA'
          and UPPER(I.status) <> 'CANCELADA'
    )
    begin
        raiserror('OS cancelada nao pode ter o status alterado.', 16, 1)
        rollback
    end
end
go

---------------------------------------------------------------------------
-- 11) Tabela de Log de OSs criadas
---------------------------------------------------------------------------
create table LOG_HistoricoOS
(
    idOS        bigint          not null,
    dataCad     datetime        not null,
    idCliente   bigint          not null,
    nomeCliente varchar(100)    not null,
    nomeAdmin   varchar(100)    not null,
    usuario     varchar(100)    not null,
    primary key(idOS, dataCad)
)
go

---------------------------------------------------------------------------
-- 12) Trigger: registra cada OS criada (com nome do cliente e admin via JOIN)
-- Tabela: tb_ordens_servico   Evento: insert   Tipo: AFTER
---------------------------------------------------------------------------
create trigger tg_LogOSNova
ON          tb_ordens_servico
AFTER       insert
as
begin
    insert into LOG_HistoricoOS (idOS, dataCad, idCliente, nomeCliente, nomeAdmin, usuario)
        select I.id_ordem_servico, GETDATE(), I.id_cliente, UC.nome, UA.nome, SYSTEM_USER
        from inserted as I
        inner join tb_usuarios as UC on UC.id = I.id_cliente
        inner join tb_usuarios as UA on UA.id = I.id_admin
end
go

---------------------------------------------------------------------------
-- 13) Adicionar coluna de status no cliente (se ainda nao existir)
-- ATIVO = cliente em uso | INATIVO = "excluido" logicamente (soft delete)
---------------------------------------------------------------------------
if not exists (
    select 1 from sys.columns
    where Name = N'status_cliente'
      and Object_ID = Object_ID(N'tb_clientes')
)
begin
    alter table tb_clientes add status_cliente varchar(10) not null default 'ATIVO'
end
go

---------------------------------------------------------------------------
-- 14) Trigger: NAO permitir exclusao fisica do Cliente (soft delete)
-- Em vez de apagar, marca status_cliente = 'INATIVO'.
-- REGRA: bloqueia se o cliente tiver OS CONCLUIDA/ENTREGUE (historico).
-- Tabela: tb_clientes   Evento: delete   Tipo: INSTEAD OF
---------------------------------------------------------------------------
create trigger tg_SoftDeleteCliente
ON          tb_clientes
INSTEAD OF  delete
as
begin
    -- bloquear se algum cliente que se tentou excluir tiver OS finalizada
    if exists (
        select 1
        from deleted as D
        inner join tb_ordens_servico as OS on OS.id_cliente = D.id
        where UPPER(OS.status) in ('CONCLUIDA', 'ENTREGUE')
    )
    begin
        raiserror('Cliente possui OS finalizada (historico). Nao pode ser inativado.', 16, 1)
        return
    end

    -- em vez de deletar, marca como INATIVO
    update tb_clientes
    set status_cliente = 'INATIVO'
    where id in (select id from deleted)
end
go

-- testar (descomente para testar):
-- delete from tb_clientes where id = 6
-- select id, cpf_cnpj, status_cliente from tb_clientes
