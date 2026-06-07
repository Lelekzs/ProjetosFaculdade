# HardSoft — Sistema Completo

Sistema de gerenciamento de ordens de serviço para assistência técnica de computadores.
**Backend + Frontend integrados num único projeto.**

## Stack

**Backend**
- Java 17
- Spring Boot 3.5.11
- Spring Data JPA + Validation + Actuator
- H2 (desenvolvimento) / SQL Server (produção)
- Lombok
- Maven

**Frontend** (servido pelo próprio Spring Boot)
- HTML5 + CSS3 + JavaScript puro
- Tema escuro moderno
- Responsivo

## Como rodar

```bash
./mvnw spring-boot:run        # Linux/Mac
mvnw.cmd spring-boot:run      # Windows
```

Depois abra no navegador:
```
http://localhost:8080
```

Pronto. Uma URL só pra tudo: frontend + API.

- Frontend: `http://localhost:8080/` (dashboard)
- API REST: `http://localhost:8080/api/*`
- Console H2: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:hardsoftdb`, user `sa`, sem senha)

## Estrutura do projeto

```
sistema/
├── pom.xml
├── mvnw, mvnw.cmd
└── src/main/
    ├── java/com/hardsoft/sistema/
    │   ├── SistemaApplication.java
    │   ├── controllers/      ← 8 controllers REST
    │   ├── services/         ← 8 services com regras de negócio
    │   ├── repositories/     ← 9 repositories JPA
    │   ├── entities/         ← 9 entities (com herança JOINED)
    │   ├── dtos/             ← 14 DTOs (request/response)
    │   └── exceptions/       ← GlobalExceptionHandler + 2 exceptions
    └── resources/
        ├── application.properties
        └── static/                       ← FRONTEND
            ├── index.html                  ← Dashboard (página raiz)
            ├── css/style.css
            ├── js/
            │   ├── api.js                  ← Cliente da API
            │   └── ui.js                   ← Helpers (toast, modal, formatação)
            └── pages/
                ├── ordens-servico.html
                ├── os-detalhe.html         ← Tela principal de uso
                ├── clientes.html
                ├── admins.html
                ├── setups.html
                ├── pecas.html
                └── tipos-servico.html
```

## Fluxo de uso

Recomendo testar nessa ordem:

1. **Admin** → cadastre um técnico (sem admin, não dá pra abrir OS)
2. **Cliente** → cadastre um cliente
3. **Setup** → cadastre o equipamento do cliente
4. **Tipo de Serviço** → cadastre tipos (Formatação, Limpeza, etc.)
5. **Peça** → cadastre o estoque inicial
6. **Nova OS** → abra uma OS vinculando cliente + setup + admin
7. Na tela de detalhe da OS:
   - Adicione serviços
   - Adicione peças aos serviços (estoque baixa automaticamente)
   - Veja o valor total recalculado
   - Mude o status (CONCLUÍDA/ENTREGUE preenche data de saída sozinho)

## Modelo de domínio

```
UsuarioEntity (abstrata, raiz)
├── AdminEntity      → operador do sistema (cargo, OSs sob responsabilidade)
└── ClienteEntity    → quem traz o PC (cpfCnpj, rg, setups, OSs)

SetupEntity          → configuração de um PC do cliente (1 cliente → N setups)
PecaEntity           → peça em estoque
TipoServicoEntity    → catálogo (Formatação, Limpeza, etc)

OrdemServicoEntity   → entidade central; liga Cliente + Setup + Admin
└── ServicoEntity    → execução de um TipoServico dentro da OS
    └── PecaServicoEntity → peça(s) usada(s) naquele serviço
```

### Decisões de modelagem

- **CPF/CNPJ e RG** ficaram em `ClienteEntity` (só faz sentido pra cliente, não pra admin).
- **`UsuarioEntity` é abstrata**: tem só os campos comuns (nome, email, senha, telefone, endereço, dataCadastro). Admin e Cliente herdam (estratégia `JOINED` — 3 tabelas no banco).
- **`PecaOSEntity` foi removida**: era duplicada com `PecaServicoEntity`.
- **`solucao`** foi adicionado em OS.
- **`BigDecimal`** em todos os campos monetários.

## Endpoints da API

Mantidos em `/api/*`. Resumo:
- `/api/admins` — CRUD
- `/api/clientes` — CRUD
- `/api/setups` — CRUD + filtro por cliente (`/api/setups/cliente/{id}`)
- `/api/pecas` — CRUD + busca por nome (`?nome=`) + alerta estoque (`/estoque-baixo?limite=`)
- `/api/tipos-servico` — CRUD
- `/api/ordens-servico` — CRUD + filtro (`?status=`) + histórico (`/cliente/{id}`) + status (`PATCH /{id}/status`)
- `/api/servicos` — gerenciar serviços de uma OS
- `/api/pecas-servico` — gerenciar peças usadas em serviços

## Configuração do banco

O `application.properties` está configurado pra H2 (banco em memória) por padrão.

Pra usar **SQL Server**:
1. Abra `src/main/resources/application.properties`
2. Comente as linhas do H2
3. Descomente as linhas do SQL Server
4. Ajuste usuário/senha/banco
5. Reinicie a aplicação

## Build para distribuição

Pra gerar um JAR único com o sistema inteiro embutido:

```bash
./mvnw package
```

Resultado: `target/hardsoft-0.0.1-SNAPSHOT.jar`

Pra executar em qualquer máquina (só precisa do Java 17):
```bash
java -jar hardsoft-0.0.1-SNAPSHOT.jar
```

Frontend + backend rodando com um comando.

## Próximos passos sugeridos

1. **Spring Security + BCrypt** — autenticação JWT, hash de senhas
2. **Tela de login** no frontend
3. **Status como `enum`** em vez de String livre
4. **Swagger/OpenAPI** — documentação interativa
5. **Flyway** — migrations de banco em produção
6. **Testes** — services com Mockito
