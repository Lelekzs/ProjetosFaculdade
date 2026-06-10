# HardSoft — Sistema de Ordem de Serviço

Sistema de gerenciamento de ordens de serviço para a assistência técnica **Leandro Técnico de Informática** (manutenção de computadores e notebooks).
**Backend + Frontend integrados num único projeto Spring Boot.**

Projeto acadêmico — FATEC São José do Rio Preto.
Grupo **HardSoft**: Leandro Belati, Matheus Vilela e Davi Vieira.

## Stack

**Backend**
- Java 17
- Spring Boot 3.5.11
- Spring Data JPA + Validation
- SQL Server (banco principal) / H2 (alternativa em memória para testes)
- Lombok
- Maven

**Frontend** (servido pelo próprio Spring Boot)
- HTML5 + CSS3 + JavaScript puro
- Tema escuro
- Autenticação por sessão (login de administrador)

## Funcionalidades

- **Login de administrador** — só administradores cadastrados acessam o sistema.
- **CRUD completo** de administradores, clientes, equipamentos (setups), peças e tipos de serviço.
- **Cadastro de cliente** com vários e-mails e telefones (um de cada marcado como principal).
- **Ordens de Serviço** vinculando cliente, equipamento e administrador, com:
  - serviços executados e peças utilizadas (baixa automática de estoque),
  - cálculo automático do valor total,
  - controle de status (ABERTA, EM_ANDAMENTO, AGUARDANDO_PECA, CONCLUIDA, ENTREGUE, CANCELADA).
- **Soft delete de clientes** — ao "excluir", o cliente é marcado como INATIVO (preserva o histórico), inclusive por um trigger no banco.
- **Recursos de banco** (SQL Server): procedures, triggers, views e functions chamados pela aplicação.

## Como rodar

### 1. Pré-requisitos
- Java 17+
- SQL Server (ex.: SQL Server Express) com um banco chamado `HardSoftDB` criado:
  ```sql
  CREATE DATABASE HardSoftDB;
  ```

### 2. Configurar a conexão
Edite `src/main/resources/application.properties` com os dados do seu SQL Server (servidor, usuário e senha). A aplicação usa `ddl-auto=update`, então ela cria as tabelas automaticamente na primeira execução.

### 3. Subir a aplicação
```bash
mvnw.cmd spring-boot:run      # Windows
./mvnw spring-boot:run        # Linux/Mac
```

### 4. Acessar
```
http://localhost:8080
```
A aplicação abre na **tela de login**. Use um administrador cadastrado (veja abaixo).

### 5. (Opcional) Rodar os scripts de banco
Depois que a aplicação subir uma vez (criando as tabelas), rode no SQL Server Management Studio, nesta ordem:
```
01_HardSoft_Procedures.sql
02_HardSoft_Triggers.sql
03_HardSoft_Functions.sql
04_HardSoft_Views.sql
05_HardSoft_DadosTeste.sql
```
O script `01` já cadastra 2 administradores para teste:
- `matheus@hardsoft.com` / senha `senha123`
- `ana@hardsoft.com` / senha `senha456`

> Se não usar os scripts, crie o primeiro admin pela API:
> ```bash
> curl -X POST http://localhost:8080/api/admins -H "Content-Type: application/json" -d "{\"nome\":\"Admin\",\"email\":\"admin@hardsoft.com\",\"senha\":\"123456\",\"cargo\":\"Gerente\"}"
> ```

## Estrutura do projeto

```
sistema/
├── pom.xml
├── mvnw, mvnw.cmd
└── src/main/
    ├── java/com/hardsoft/sistema/
    │   ├── SistemaApplication.java
    │   ├── controllers/      ← controllers REST (inclui AuthController)
    │   ├── services/         ← regras de negócio (inclui AuthService)
    │   ├── repositories/     ← repositories JPA
    │   ├── entities/         ← entities (herança JOINED em UsuarioEntity)
    │   ├── dtos/             ← DTOs (inclui Login request/response)
    │   └── exceptions/       ← GlobalExceptionHandler + exceptions
    └── resources/
        ├── application.properties
        └── static/                       ← FRONTEND
            ├── login.html                 ← Tela de login
            ├── index.html                 ← Dashboard
            ├── css/style.css
            ├── js/
            │   ├── api.js                 ← Cliente da API + sessão
            │   └── ui.js                  ← Helpers + proteção de páginas
            └── pages/
                ├── ordens-servico.html
                ├── os-detalhe.html        ← Tela principal de uso
                ├── clientes.html
                ├── admins.html
                ├── setups.html
                ├── pecas.html
                └── tipos-servico.html
```

## Modelo de domínio

```
UsuarioEntity (abstrata, raiz)
├── AdminEntity      → operador do sistema; loga (cargo)
└── ClienteEntity    → quem traz o PC (cpfCnpj, rg, statusCliente, emails, telefones, setups)

SetupEntity          → equipamento de um cliente (1 cliente → N setups)
PecaEntity           → peça em estoque
TipoServicoEntity    → catálogo de serviços (Formatação, Limpeza, etc.)

OrdemServicoEntity   → entidade central; liga Cliente + Setup + Admin
└── ServicoEntity    → execução de um TipoServico dentro da OS
    └── PecaServicoEntity → peça(s) usada(s) naquele serviço
```

## Endpoints da API

- `/api/auth/login` — autenticação do administrador
- `/api/admins` — CRUD
- `/api/clientes` — CRUD + inativos (`/inativos`) + reativar (`PATCH /{id}/reativar`)
- `/api/setups` — CRUD + filtro por cliente
- `/api/pecas` — CRUD + busca por nome + alerta de estoque
- `/api/tipos-servico` — CRUD
- `/api/ordens-servico` — CRUD + filtro por status + histórico por cliente + alterar status
- `/api/servicos` — serviços de uma OS
- `/api/pecas-servico` — peças usadas em serviços

## Autenticação

Versão simples (projeto acadêmico): o login valida e-mail e senha contra a tabela de administradores e guarda a sessão no navegador (`sessionStorage`). Não usa Spring Security nem hash de senha. Para produção, o recomendado seria Spring Security + BCrypt + JWT.

## Build

```bash
./mvnw package
java -jar target/hardsoft-0.0.1-SNAPSHOT.jar
```
