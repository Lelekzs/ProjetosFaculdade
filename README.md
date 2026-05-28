# HardSoft — Sistema de Ordem de Serviço

Sistema de gerenciamento de ordens de serviço para assistência técnica de computadores.

## Stack

- Java 17
- Spring Boot 3.3.4
- Spring Data JPA
- Spring Validation
- H2 (desenvolvimento) / SQL Server (produção)
- Lombok
- Maven

## Como rodar

```bash
./mvnw spring-boot:run        # Linux/Mac
mvnw.cmd spring-boot:run      # Windows
```

A aplicação sobe em `http://localhost:8080`.

Console do H2 (banco em memória, dev): `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:hardsoftdb`
- User: `sa` | Password: *(vazio)*

Para usar SQL Server em vez do H2, comente as linhas do H2 e descomente as do SQL Server em `src/main/resources/application.properties`.

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
    └── PecaServicoEntity  → peça(s) usada(s) naquele serviço
```

### Decisões de modelagem

- **CPF/CNPJ e RG** ficaram em `ClienteEntity` (só faz sentido pra cliente, não pra admin).
- **`UsuarioEntity` virou abstrata**: tem só os campos comuns (nome, email, senha, telefone, endereço, dataCadastro). Admin e Cliente herdam (estratégia `JOINED` — 3 tabelas no banco).
- **`PecaOSEntity` foi removida**: era duplicada com `PecaServicoEntity`. Como peça sempre é vendida no contexto de um serviço, a ligação direta com OS era redundante.
- **`solucao`** foi adicionado em OS (estava no diagrama, faltava no código).
- **`BigDecimal`** em todos os campos monetários (`double` dá erro de arredondamento).
- **Status da OS** continua como `String` por compatibilidade, mas o ideal é converter para `enum`.

## Endpoints

### Clientes — `/api/clientes`

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/api/clientes` | Lista todos |
| GET | `/api/clientes/{id}` | Busca por ID |
| POST | `/api/clientes` | Cria cliente |
| PUT | `/api/clientes/{id}` | Atualiza |
| DELETE | `/api/clientes/{id}` | Remove |

**Exemplo POST:**
```json
{
  "nome": "João Silva",
  "email": "joao@email.com",
  "senha": "senha123",
  "cpfCnpj": "12345678901",
  "rg": "MG1234567",
  "telefone": "31999998888",
  "endereco": "Rua A, 100"
}
```

### Admins — `/api/admins`
Mesmas operações REST + campo `cargo`.

### Setups — `/api/setups`

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/api/setups` | Lista todos |
| GET | `/api/setups/{id}` | Busca por ID |
| GET | `/api/setups/cliente/{idCliente}` | Lista setups de um cliente |
| POST | `/api/setups` | Cria |
| PUT | `/api/setups/{id}` | Atualiza |
| DELETE | `/api/setups/{id}` | Remove |

### Peças — `/api/pecas`

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/api/pecas` | Lista todas |
| GET | `/api/pecas?nome=memoria` | Busca por nome parcial |
| GET | `/api/pecas/estoque-baixo?limite=5` | Peças com estoque < limite |
| GET | `/api/pecas/{id}` | Busca por ID |
| POST | `/api/pecas` | Cria |
| PUT | `/api/pecas/{id}` | Atualiza |
| DELETE | `/api/pecas/{id}` | Remove |

### Tipos de Serviço — `/api/tipos-servico`
CRUD padrão.

### Ordens de Serviço — `/api/ordens-servico`

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/api/ordens-servico` | Lista todas |
| GET | `/api/ordens-servico?status=ABERTA` | Filtra por status |
| GET | `/api/ordens-servico/{id}` | Busca por ID |
| GET | `/api/ordens-servico/cliente/{idCliente}` | Histórico do cliente |
| POST | `/api/ordens-servico` | Cria OS |
| PUT | `/api/ordens-servico/{id}` | Atualiza |
| PATCH | `/api/ordens-servico/{id}/status` | Muda só o status |
| DELETE | `/api/ordens-servico/{id}` | Remove |

**Exemplo PATCH status:**
```json
{ "status": "CONCLUIDA" }
```

Quando o status vai pra `CONCLUIDA` ou `ENTREGUE`, a `dataSaida` é preenchida automaticamente.

### Serviços (executados dentro de uma OS) — `/api/servicos`

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/api/servicos/{id}` | Busca por ID |
| GET | `/api/servicos/ordem-servico/{id}` | Serviços de uma OS |
| POST | `/api/servicos` | Adiciona serviço a OS |
| PUT | `/api/servicos/{id}` | Atualiza |
| DELETE | `/api/servicos/{id}` | Remove |

Toda criação/edição/remoção de serviço **recalcula automaticamente o valor total da OS**.

### Peças usadas em serviços — `/api/pecas-servico`

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/api/pecas-servico/{id}` | Busca por ID |
| GET | `/api/pecas-servico/servico/{idServico}` | Peças usadas em um serviço |
| POST | `/api/pecas-servico` | Adiciona peça a serviço |
| DELETE | `/api/pecas-servico/{id}` | Remove (e devolve estoque) |

Ao adicionar uma peça a um serviço:
- Valida se há estoque suficiente
- Dá baixa no estoque automaticamente
- Congela o `vlrUnitarioVenda` no preço atual da peça
- Recalcula o valor total da OS

Ao remover, devolve a quantidade ao estoque e recalcula.

## Tratamento de erros

Respostas de erro padronizadas pelo `GlobalExceptionHandler`:

```json
{
  "timestamp": "2025-11-15T10:30:00",
  "status": 404,
  "mensagem": "Cliente com id 99 nao encontrado(a)."
}
```

- `400` — validação (campos obrigatórios, formato inválido)
- `404` — recurso não encontrado (`ResourceNotFoundException`)
- `422` — regra de negócio violada (`BusinessException`) — ex: email duplicado, estoque insuficiente
- `500` — erro inesperado

## Próximos passos sugeridos

1. **Spring Security + BCrypt** — autenticação JWT, hash de senhas
2. **Status como `enum`** em vez de String livre
3. **Swagger/OpenAPI** — documentação interativa (`springdoc-openapi-starter-webmvc-ui`)
4. **Flyway ou Liquibase** — migrations de banco em produção (trocar `ddl-auto=create-drop` por `validate`)
5. **Testes** — começar pelos services (regras de negócio) com Mockito
