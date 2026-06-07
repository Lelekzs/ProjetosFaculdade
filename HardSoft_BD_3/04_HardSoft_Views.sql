---------------------------------------------------------------------------
-- HardSoft - Sistema de Ordem de Servico
-- SCRIPT 04: VIEWS
---------------------------------------------------------------------------

use HardSoftDB
go

---------------------------------------------------------------------------
-- 1) View para consultar Admins (todos os dados, junta tb_usuarios e tb_admins)
---------------------------------------------------------------------------
create view v_Admin
as
    select  A.id            as Cod_Admin,
            U.nome          as Admin_Nome,
            U.email         as Email,
            U.telefone      as Telefone,
            U.endereco      as Endereco,
            U.data_cadastro as Data_Cadastro,
            A.cargo         as Cargo
    from    tb_usuarios as U
    inner join tb_admins as A on U.id = A.id
go

-- consultar
select * from v_Admin
go

---------------------------------------------------------------------------
-- 2) View para consultar Clientes com email/telefone principal
-- (usa SUBSELECT pra pegar o registro marcado como principal)
---------------------------------------------------------------------------
create view v_Cliente
as
    select  C.id            as Cod_Cliente,
            U.nome          as Cliente_Nome,
            C.cpf_cnpj      as CPF_CNPJ,
            C.rg            as RG,
            U.endereco      as Endereco,
            U.data_cadastro as Data_Cadastro,
            case
                when LEN(C.cpf_cnpj) = 11 then 'Pessoa Fisica'
                else 'Pessoa Juridica'
            end as Tipo_Pessoa,
            (select top 1 email from tb_cliente_emails
             where id_cliente = C.id and principal = 1) as Email_Principal,
            (select top 1 telefone from tb_cliente_telefones
             where id_cliente = C.id and principal = 1) as Telefone_Principal,
            (select top 1 tipo from tb_cliente_telefones
             where id_cliente = C.id and principal = 1) as Tipo_Telefone
    from    tb_usuarios as U
    inner join tb_clientes as C on U.id = C.id
go

-- consultar
select * from v_Cliente
go

---------------------------------------------------------------------------
-- 3) View para consultar TODOS os contatos do cliente (1 linha por contato)
-- Util para relatorio de exportacao
---------------------------------------------------------------------------
create view v_ClienteContatos
as
    select  C.id        as Cod_Cliente,
            U.nome      as Cliente_Nome,
            C.cpf_cnpj  as CPF_CNPJ,
            'EMAIL'     as Tipo_Contato,
            E.email     as Valor,
            NULL        as Subtipo,
            case E.principal when 1 then 'Principal' else 'Secundario' end as Situacao
    from    tb_usuarios as U
    inner join tb_clientes as C on U.id = C.id
    inner join tb_cliente_emails as E on E.id_cliente = C.id
    union all
    select  C.id, U.nome, C.cpf_cnpj,
            'TELEFONE', T.telefone, T.tipo,
            case T.principal when 1 then 'Principal' else 'Secundario' end
    from    tb_usuarios as U
    inner join tb_clientes as C on U.id = C.id
    inner join tb_cliente_telefones as T on T.id_cliente = C.id
go

-- consultar
select * from v_ClienteContatos order by Cod_Cliente, Tipo_Contato
go

---------------------------------------------------------------------------
-- 4) View para consultar Setups com nome do cliente
---------------------------------------------------------------------------
create view v_Setup
as
    select  S.id_setup      as Cod_Setup,
            S.marca + ' ' + S.modelo as Equipamento,
            S.processador   as Processador,
            S.memoria       as Memoria,
            S.armazenamento as Armazenamento,
            C.id            as Cod_Cliente,
            U.nome          as Cliente
    from    tb_setups as S
    inner join tb_clientes as C on C.id = S.id_cliente
    inner join tb_usuarios as U on U.id = C.id
go

-- consultar
select * from v_Setup
go

---------------------------------------------------------------------------
-- 5) View para consultar OSs com TODOS os dados (cliente, setup, admin)
---------------------------------------------------------------------------
create view v_OS
as
    select  OS.id_ordem_servico as Cod_OS,
            OS.data_entrada     as Data_Entrada,
            OS.data_saida       as Data_Saida,
            case UPPER(OS.status)
                when 'ABERTA'           then 'Aberta'
                when 'EM_ANDAMENTO'     then 'Em Andamento'
                when 'AGUARDANDO_PECA'  then 'Aguardando Peca'
                when 'CONCLUIDA'        then 'Concluida'
                when 'ENTREGUE'         then 'Entregue'
                when 'CANCELADA'        then 'Cancelada'
                else OS.status
            end                 as Status,
            OS.defeito          as Defeito,
            OS.solucao          as Solucao,
            OS.valor_total      as Valor_Total,
            C.id                as Cod_Cliente,
            UC.nome             as Cliente,
            C.cpf_cnpj          as CPF_CNPJ,
            S.id_setup          as Cod_Setup,
            S.marca + ' ' + S.modelo as Equipamento,
            A.id                as Cod_Admin,
            UA.nome             as Tecnico_Responsavel,
            A.cargo             as Cargo_Tecnico
    from    tb_ordens_servico as OS
    inner join tb_clientes as C  on C.id  = OS.id_cliente
    inner join tb_usuarios as UC on UC.id = C.id
    inner join tb_setups   as S  on S.id_setup = OS.id_setup
    inner join tb_admins   as A  on A.id  = OS.id_admin
    inner join tb_usuarios as UA on UA.id = A.id
go

-- consultar
select * from v_OS order by Cod_OS
go

---------------------------------------------------------------------------
-- 6) View de pecas com nivel de alerta de estoque (usa CASE WHEN)
---------------------------------------------------------------------------
create view v_PecaEstoque
as
    select  P.id_peca       as Cod_Peca,
            P.nome          as Peca,
            P.marca         as Marca,
            P.condicao      as Condicao,
            P.preco_custo   as Preco_Custo,
            P.preco_venda   as Preco_Venda,
            (P.preco_venda - P.preco_custo) as Lucro_Unitario,
            P.qnt_estoque   as Estoque,
            case
                when P.qnt_estoque = 0 then 'Sem Estoque'
                when P.qnt_estoque <= 2 then 'Critico'
                when P.qnt_estoque <= 5 then 'Baixo'
                when P.qnt_estoque <= 15 then 'Normal'
                else 'Alto'
            end as Nivel_Estoque
    from tb_pecas as P
go

-- consultar pecas com alerta
select * from v_PecaEstoque
where Nivel_Estoque in ('Sem Estoque', 'Critico', 'Baixo')
go

---------------------------------------------------------------------------
-- 7) View para consultar Servicos executados em OSs
-- (usa a view v_OS para enriquecer os dados)
---------------------------------------------------------------------------
create view v_ServicoExecutado
as
    select  SE.id_servico       as Cod_Servico,
            vOS.Cod_OS          as Cod_OS,
            vOS.Cliente         as Cliente,
            vOS.Equipamento     as Equipamento,
            TS.nome             as Tipo_Servico,
            SE.descricao_servico as Descricao,
            SE.preco_mao_de_obra as Mao_De_Obra,
            vOS.Status          as Status_OS
    from    tb_servicos_executados as SE
    inner join tb_tipos_servico as TS on TS.id_tipo_servico = SE.id_tipo_servico
    inner join v_OS as vOS on vOS.Cod_OS = SE.id_ordem_servico
go

-- consultar
select * from v_ServicoExecutado
go

---------------------------------------------------------------------------
-- 8) View de pecas usadas em servicos (com calculo de subtotal)
---------------------------------------------------------------------------
create view v_PecaServico
as
    select  PS.id_peca_servico  as Cod_Registro,
            SE.id_ordem_servico as Cod_OS,
            SE.id_servico       as Cod_Servico,
            P.nome              as Peca,
            PS.qnt_vendida      as Qtd_Vendida,
            PS.vlr_unitario_venda as Preco_Unit,
            (PS.qnt_vendida * PS.vlr_unitario_venda) as Subtotal
    from    tb_pecas_servicos as PS
    inner join tb_pecas as P on P.id_peca = PS.id_peca
    inner join tb_servicos_executados as SE on SE.id_servico = PS.id_servico
go

-- consultar
select * from v_PecaServico
go

---------------------------------------------------------------------------
-- 9) View de faturamento por Admin (so OSs finalizadas)
---------------------------------------------------------------------------
create view v_FaturamentoAdmin
as
    select  A.id        as Cod_Admin,
            U.nome      as Admin_Nome,
            A.cargo     as Cargo,
            COUNT(OS.id_ordem_servico)  as Qtd_OS_Finalizadas,
            ISNULL(SUM(OS.valor_total), 0) as Faturamento_Total
    from    tb_admins as A
    inner join tb_usuarios as U on U.id = A.id
    left join tb_ordens_servico as OS
        on OS.id_admin = A.id
       and UPPER(OS.status) in ('CONCLUIDA', 'ENTREGUE')
    group by A.id, U.nome, A.cargo
go

-- consultar
select * from v_FaturamentoAdmin order by Faturamento_Total desc
go

---------------------------------------------------------------------------
-- 10) View de ranking de pecas mais vendidas
---------------------------------------------------------------------------
create view v_RankingPecas
as
    select  P.id_peca       as Cod_Peca,
            P.nome          as Peca,
            P.marca         as Marca,
            ISNULL(SUM(PS.qnt_vendida), 0)  as Total_Vendido,
            ISNULL(SUM(PS.qnt_vendida * PS.vlr_unitario_venda), 0) as Receita,
            P.qnt_estoque   as Estoque_Atual
    from    tb_pecas as P
    left join tb_pecas_servicos as PS on PS.id_peca = P.id_peca
    group by P.id_peca, P.nome, P.marca, P.qnt_estoque
go

-- consultar top 5 mais vendidas
select top 5 * from v_RankingPecas order by Total_Vendido desc
go
