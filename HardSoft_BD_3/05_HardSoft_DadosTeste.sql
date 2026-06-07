---------------------------------------------------------------------------
-- HardSoft - Sistema de Ordem de Servico
-- SCRIPT 05: DADOS DE TESTE (carga inicial)
---------------------------------------------------------------------------
-- IMPORTANTE: rodar os scripts 01, 02, 03 e 04 antes deste.
-- Os admins/clientes ja foram cadastrados nos exec do script 01.
-- Este script complementa cadastrando setups, pecas, tipos de servico e OSs.
---------------------------------------------------------------------------

use HardSoftDB
go

---------------------------------------------------------------------------
-- TIPOS DE SERVICO
---------------------------------------------------------------------------
exec sp_CadTipoServico 'Formatacao',
    'Formatacao completa com instalacao de SO e drivers', 120.00
go
exec sp_CadTipoServico 'Limpeza Interna',
    'Limpeza de poeira, ventoinhas e dissipadores', 80.00
go
exec sp_CadTipoServico 'Troca de Pasta Termica',
    'Substituicao da pasta termica do processador', 50.00
go
exec sp_CadTipoServico 'Upgrade Memoria RAM',
    'Instalacao de modulos de memoria adicionais', 40.00
go
exec sp_CadTipoServico 'Upgrade SSD',
    'Instalacao de SSD e clonagem do sistema', 100.00
go
exec sp_CadTipoServico 'Diagnostico',
    'Analise completa de hardware e software', 60.00
go
exec sp_CadTipoServico 'Troca de Tela',
    'Substituicao de tela de notebook', 180.00
go
exec sp_CadTipoServico 'Remocao de Virus',
    'Limpeza completa de virus e malware', 90.00
go

---------------------------------------------------------------------------
-- PECAS
---------------------------------------------------------------------------
exec sp_CadPeca 'Memoria RAM 8GB DDR4 2666MHz', 'Modulo SODIMM',
    'Kingston', 'Novo', 90.00, 150.00, 15
go
exec sp_CadPeca 'Memoria RAM 16GB DDR4 3200MHz', 'Modulo DIMM',
    'Corsair', 'Novo', 180.00, 290.00, 8
go
exec sp_CadPeca 'SSD 240GB SATA', 'SSD 2.5"',
    'Kingston', 'Novo', 120.00, 200.00, 12
go
exec sp_CadPeca 'SSD 480GB SATA', 'SSD 2.5"',
    'WD', 'Novo', 200.00, 320.00, 10
go
exec sp_CadPeca 'Pasta Termica 4g', 'Pasta termica de alta performance',
    'Arctic MX-4', 'Novo', 15.00, 35.00, 30
go
exec sp_CadPeca 'Fonte ATX 500W', 'Fonte 80 Plus Bronze',
    'Corsair', 'Novo', 220.00, 350.00, 5
go
exec sp_CadPeca 'Tela LCD 14" Full HD', 'Tela LED 14 polegadas',
    'AUO', 'Novo', 280.00, 450.00, 4
go
exec sp_CadPeca 'Teclado Notebook ABNT2', 'Teclado de reposicao',
    'Generico', 'Novo', 80.00, 140.00, 7
go
exec sp_CadPeca 'Bateria Notebook 4 celulas', 'Bateria 2200mAh',
    'Generico', 'Novo', 120.00, 220.00, 6
go
exec sp_CadPeca 'Cooler CPU 120mm', 'Cooler com 4 heatpipes',
    'Cooler Master', 'Novo', 90.00, 160.00, 3
go

---------------------------------------------------------------------------
-- SETUPS
-- Joao = id 3, Maria = id 4, Tech = id 5, Carlos = id 6
-- (IDs dependem da ordem de criacao no script 01)
---------------------------------------------------------------------------
-- PC do Joao
exec sp_CadSetup 3, 'Dell', 'Inspiron 15', 'Intel i5-1135G7',
    '16GB DDR4', 'Intel Iris Xe', 'SSD 512GB'
go
-- Notebook do Joao
exec sp_CadSetup 3, 'Lenovo', 'IdeaPad 3', 'AMD Ryzen 5 5500U',
    '8GB DDR4', 'AMD Radeon', 'SSD 256GB'
go
-- MacBook da Maria
exec sp_CadSetup 4, 'Apple', 'MacBook Air M1', 'Apple M1',
    '8GB', 'Apple GPU 7-core', 'SSD 256GB'
go
-- PC corporativo 1 da Tech Solutions
exec sp_CadSetup 5, 'HP', 'EliteDesk 800 G6', 'Intel i7-10700',
    '32GB DDR4', 'Intel UHD 630', 'SSD 1TB'
go
-- PC corporativo 2 da Tech Solutions
exec sp_CadSetup 5, 'Dell', 'OptiPlex 7090', 'Intel i5-11500',
    '16GB DDR4', 'Intel UHD 750', 'SSD 512GB'
go
-- Notebook do Carlos
exec sp_CadSetup 6, 'Acer', 'Aspire 5', 'Intel i3-1115G4',
    '4GB DDR4', 'Intel UHD', 'HD 1TB'
go

---------------------------------------------------------------------------
-- ORDENS DE SERVICO
-- Admin Matheus = id 1, Ana = id 2
---------------------------------------------------------------------------

-- OS#1: Joao (cliente 3) - PC dele (setup 1) - Matheus (admin 1)
declare @os1 int
exec sp_AbrirOS 3, 1, 1,
    'Computador muito lento, suspeita de virus'

select @os1 = MAX(id_ordem_servico) from tb_ordens_servico where id_cliente = 3

exec sp_AddServicoOS @os1, 1,
    'Formatacao com Windows 11 e Office', 120.00

update tb_ordens_servico
set solucao = 'Sistema formatado e otimizado. Instalado antivirus.'
where id_ordem_servico = @os1

exec sp_AlterarStatusOS @os1, 'CONCLUIDA'
go

-- OS#2: Maria (cliente 4) - MacBook (setup 3) - Matheus (admin 1)
declare @os2 int
exec sp_AbrirOS 4, 3, 1,
    'Tela trincada apos queda'

select @os2 = MAX(id_ordem_servico) from tb_ordens_servico where id_cliente = 4

exec sp_AddServicoOS @os2, 7, 'Substituicao da tela LCD', 180.00

-- pegar id do servico recem criado e usar a peca tela14 (id 7)
declare @servico2 int
select @servico2 = MAX(id_servico) from tb_servicos_executados
where id_ordem_servico = @os2

exec sp_UsarPecaServico @servico2, 7, 1

update tb_ordens_servico
set solucao = 'Tela substituida com sucesso.'
where id_ordem_servico = @os2

exec sp_AlterarStatusOS @os2, 'ENTREGUE'
go

-- OS#3: Tech Solutions (cliente 5) - HP (setup 4) - Ana (admin 2)
declare @os3 int, @s3 int
exec sp_AbrirOS 5, 4, 2,
    'Upgrade: aumento de RAM e troca de HD por SSD'

select @os3 = MAX(id_ordem_servico) from tb_ordens_servico where id_cliente = 5

-- Servico 1: Upgrade RAM
exec sp_AddServicoOS @os3, 4, 'Instalacao de 2 modulos 8GB DDR4', 40.00
select @s3 = MAX(id_servico) from tb_servicos_executados where id_ordem_servico = @os3
exec sp_UsarPecaServico @s3, 1, 2

-- Servico 2: Upgrade SSD
exec sp_AddServicoOS @os3, 5, 'Instalacao SSD 480GB e clonagem', 100.00
select @s3 = MAX(id_servico) from tb_servicos_executados where id_ordem_servico = @os3
exec sp_UsarPecaServico @s3, 4, 1

-- Servico 3: Limpeza preventiva
exec sp_AddServicoOS @os3, 2, 'Limpeza interna e troca de pasta termica', 80.00
select @s3 = MAX(id_servico) from tb_servicos_executados where id_ordem_servico = @os3
exec sp_UsarPecaServico @s3, 5, 1

exec sp_AlterarStatusOS @os3, 'EM_ANDAMENTO'
go

-- OS#4: Carlos (cliente 6) - Acer (setup 6) - Matheus (admin 1) - Aberta
declare @os4 int
exec sp_AbrirOS 6, 6, 1, 'Notebook nao liga - necessita diagnostico'

select @os4 = MAX(id_ordem_servico) from tb_ordens_servico where id_cliente = 6

exec sp_AddServicoOS @os4, 6, 'Diagnostico inicial', 60.00
-- fica em ABERTA (default)
go

---------------------------------------------------------------------------
-- VERIFICAR carga
---------------------------------------------------------------------------
print '=== RESUMO ==='
select 'Admins',           COUNT(*) as total from tb_admins
union all select 'Clientes',          COUNT(*) from tb_clientes
union all select 'Emails clientes',   COUNT(*) from tb_cliente_emails
union all select 'Telefones clientes',COUNT(*) from tb_cliente_telefones
union all select 'Setups',            COUNT(*) from tb_setups
union all select 'Tipos servico',     COUNT(*) from tb_tipos_servico
union all select 'Pecas',             COUNT(*) from tb_pecas
union all select 'OSs',               COUNT(*) from tb_ordens_servico
union all select 'Servicos exec',     COUNT(*) from tb_servicos_executados
union all select 'Pecas em servicos', COUNT(*) from tb_pecas_servicos
go

print '=== OSs com valor calculado pelo trigger ==='
select * from v_OS order by Cod_OS
go

print '=== Estoque apos uso ==='
select * from v_PecaEstoque order by Cod_Peca
go
