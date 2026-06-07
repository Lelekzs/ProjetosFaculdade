---------------------------------------------------------------------------
-- HardSoft - Sistema de Ordem de Servico
-- SCRIPT 03: FUNCTIONS
---------------------------------------------------------------------------

use HardSoftDB
go

---------------------------------------------------------------------------
-- SCALAR-VALUE FUNCTION: retorna apenas um valor
---------------------------------------------------------------------------

---------------------------------------------------------------------------
-- 1) Calcular reajuste de valor com taxa
---------------------------------------------------------------------------
create function fc_CalcReajuste
(
    @valor decimal(10,2), @taxa decimal(10,2)
)
RETURNS decimal(10,2)
as
begin
    return (@valor * (1 + (@taxa/100.0)))
end
go

-- testar: aumento de 10% em 1000.00
select valorReajustado = dbo.fc_CalcReajuste(1000.00, 10)
go

-- desconto de 15% em 500.00
select valorReajustado = dbo.fc_CalcReajuste(500.00, -15)
go

---------------------------------------------------------------------------
-- 2) Calcular total de uma OS (mao de obra + pecas)
-- Replica a logica do trigger - util para consultas pontuais
---------------------------------------------------------------------------
create function fc_CalcTotalOS
(
    @idOS bigint
)
RETURNS decimal(10,2)
as
begin
    declare @total decimal(10,2)

    select @total =
        ISNULL(SUM(S.preco_mao_de_obra), 0) +
        ISNULL((
            select SUM(PS.qnt_vendida * PS.vlr_unitario_venda)
            from tb_pecas_servicos as PS
            where PS.id_servico in (
                select id_servico from tb_servicos_executados
                where id_ordem_servico = @idOS
            )
        ), 0)
    from tb_servicos_executados as S
    where S.id_ordem_servico = @idOS

    return ISNULL(@total, 0)
end
go

-- testar: calcular total da OS 1
select total_OS = dbo.fc_CalcTotalOS(1)
go

---------------------------------------------------------------------------
-- 3) Calcular quantidade total vendida de uma peca
---------------------------------------------------------------------------
create function fc_QtdVendidaPeca
(
    @idPeca bigint
)
RETURNS int
as
begin
    return (
        select ISNULL(SUM(qnt_vendida), 0)
        from tb_pecas_servicos
        where id_peca = @idPeca
    )
end
go

-- testar: qtd vendida da peca 1
select qtd_Vendida = dbo.fc_QtdVendidaPeca(1)
go

---------------------------------------------------------------------------
-- 4) Calcular lucro de uma peca (preco_venda - preco_custo)
---------------------------------------------------------------------------
create function fc_LucroPeca
(
    @idPeca bigint
)
RETURNS money
as
begin
    declare @lucro money

    select @lucro = ISNULL(preco_venda - preco_custo, 0)
    from tb_pecas where id_peca = @idPeca

    return @lucro
end
go

---------------------------------------------------------------------------
-- 5) Calcular idade em dias de uma OS (data atual - data_entrada)
-- Util pra ver quanto tempo a OS esta aberta
---------------------------------------------------------------------------
create function fc_DiasAbertaOS
(
    @idOS bigint
)
RETURNS int
as
begin
    declare @dias int
    declare @dataEntrada datetime, @dataSaida datetime

    select @dataEntrada = data_entrada, @dataSaida = data_saida
    from tb_ordens_servico where id_ordem_servico = @idOS

    -- se OS ja foi entregue, calcula ate a data_saida; senao ate hoje
    set @dias = DATEDIFF(day, @dataEntrada, ISNULL(@dataSaida, GETDATE()))

    return @dias
end
go

-- testar: ver quantos dias a OS 1 esta/ficou aberta
select dias_aberta = dbo.fc_DiasAbertaOS(1)
go

---------------------------------------------------------------------------
-- TABLE-VALUE FUNCTION: retorna uma tabela
---------------------------------------------------------------------------

---------------------------------------------------------------------------
-- 6) Listar pecas com estoque abaixo de um valor minimo
---------------------------------------------------------------------------
create function fc_PecasEstoqueMinimo
(
    @minimo int
)
RETURNS table
as
    return (
        select id_peca, nome, marca, qnt_estoque, preco_venda
        from tb_pecas
        where qnt_estoque < @minimo
    )
go

-- testar: pecas com menos de 10 unidades
select * from dbo.fc_PecasEstoqueMinimo(10)
order by qnt_estoque
go

---------------------------------------------------------------------------
-- 7) Consultar OSs criadas em um determinado mes/ano
---------------------------------------------------------------------------
create function fc_OSsPorMes
(
    @mes int, @ano int
)
RETURNS table
as
    return (
        select id_ordem_servico, data_entrada, status, valor_total
        from tb_ordens_servico
        where MONTH(data_entrada) = @mes
          and YEAR(data_entrada) = @ano
    )
go

-- testar: OSs de janeiro de 2026
select * from dbo.fc_OSsPorMes(1, 2026)
go

---------------------------------------------------------------------------
-- MULTI-STATEMENT TABLE-VALUE FUNCTION: retorna variavel do tipo table
-- Com colunas e calculos definidos pelo DBA
---------------------------------------------------------------------------

---------------------------------------------------------------------------
-- 8) Consultar pecas com valor acima de um limite, mostrando o total
-- em estoque calculado (qtd * preco)
---------------------------------------------------------------------------
create function fc_PecasMaiorValor
(
    @valorAcima money
)
RETURNS @TB_PecasAcima table
        (
            idPeca          bigint,
            nome            varchar(100),
            qtdEstoque      int,
            precoVenda      money,
            valorTotalEst   money    -- coluna calculada
        )
as
begin
    insert into @TB_PecasAcima (idPeca, nome, qtdEstoque, precoVenda, valorTotalEst)
        select id_peca, nome, qnt_estoque, preco_venda,
               (qnt_estoque * preco_venda)
        from tb_pecas
        where preco_venda > @valorAcima

    return
end
go

-- testar: pecas com valor acima de 200.00
select * from dbo.fc_PecasMaiorValor(200.00)
go

---------------------------------------------------------------------------
-- 9) Consultar OSs de um cliente, com dados completos para relatorio
---------------------------------------------------------------------------
create function fc_OSsPorCliente
(
    @idCliente bigint
)
RETURNS @TB_OSs table
        (
            idOS            bigint,
            dataEntrada     datetime,
            dataSaida       datetime,
            status          varchar(30),
            valorTotal      money,
            diasAberta      int,
            nomeAdmin       varchar(100)
        )
as
begin
    insert into @TB_OSs (idOS, dataEntrada, dataSaida, status, valorTotal, diasAberta, nomeAdmin)
        select OS.id_ordem_servico,
               OS.data_entrada, OS.data_saida, OS.status, OS.valor_total,
               dbo.fc_DiasAbertaOS(OS.id_ordem_servico),    -- usa outra function
               UA.nome
        from tb_ordens_servico as OS
        inner join tb_usuarios as UA on UA.id = OS.id_admin
        where OS.id_cliente = @idCliente

    return
end
go

-- testar: OSs do cliente 3
select * from dbo.fc_OSsPorCliente(3)
go

---------------------------------------------------------------------------
-- 10) Receita gerada por uma peca (qtd vendida * preco congelado)
---------------------------------------------------------------------------
create function fc_ReceitaPorPeca
(
    @idPeca bigint
)
RETURNS @TB_Receita table
        (
            idPeca          bigint,
            nome            varchar(100),
            totalVendido    int,
            receitaGerada   money,
            estoqueAtual    int
        )
as
begin
    insert into @TB_Receita (idPeca, nome, totalVendido, receitaGerada, estoqueAtual)
        select P.id_peca, P.nome,
               ISNULL(SUM(PS.qnt_vendida), 0),
               ISNULL(SUM(PS.qnt_vendida * PS.vlr_unitario_venda), 0),
               P.qnt_estoque
        from tb_pecas as P
        left join tb_pecas_servicos as PS on PS.id_peca = P.id_peca
        where P.id_peca = @idPeca
        group by P.id_peca, P.nome, P.qnt_estoque

    return
end
go

-- testar: receita da peca 1
select * from dbo.fc_ReceitaPorPeca(1)
go
