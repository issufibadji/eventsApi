# eventostec-backend

API REST em Spring Boot para gerenciamento de eventos de tecnologia.

## Tecnologias

- Java 21 + Spring Boot 3.4.3
- PostgreSQL (produção) / H2 (local e testes)
- Flyway 10 para migrações de banco
- MapStruct + Lombok
- JUnit 5 + Mockito + JaCoCo

## Pré-requisitos (Etapa 1 — local, sem Docker)

- Java 21+
- PostgreSQL instalado localmente com um banco `eventostec` criado
- Maven Wrapper incluído (`./mvnw`) — não é necessário instalar o Maven

## Configuração

Defina as variáveis de ambiente antes de iniciar:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/eventostec
export DB_USER=postgres
export DB_PASSWORD=sua_senha
```

No Windows (PowerShell):

```powershell
$env:DB_URL = "jdbc:postgresql://localhost:5432/eventostec"
$env:DB_USER = "postgres"
$env:DB_PASSWORD = "sua_senha"
```

## Como executar

```bash
# Compilar
./mvnw clean install

# Rodar com PostgreSQL local (Etapa 1)
./mvnw spring-boot:run

# Rodar com H2 em memória (sem precisar de PostgreSQL)
./mvnw spring-boot:run "-Dspring-boot.run.profiles=local"
```

> **Windows:** as aspas duplas são obrigatórias ao passar flags `-D` no PowerShell.

A API estará disponível em `http://localhost:8090`.

Com o perfil `local`, o console H2 fica em `http://localhost:8090/h2-console`
(JDBC URL: `jdbc:h2:mem:eventostec`).

## Testes

```bash
./mvnw test
./mvnw verify   # testes + relatório de cobertura em target/site/jacoco/index.html
```

## Endpoints principais

| Método | Caminho | Descrição |
| --- | --- | --- |
| `POST` | `/api/event` | Criar evento (`multipart/form-data`) |
| `GET` | `/api/event` | Listar próximos eventos (paginado) |
| `GET` | `/api/event/{id}` | Detalhes do evento com cupons |
| `GET` | `/api/event/filter` | Filtrar por cidade, UF e período |
| `GET` | `/api/event/search` | Buscar por título |
| `PUT` | `/api/event/{id}` | Atualizar evento (campos opcionais) |
| `DELETE` | `/api/event/{id}` | Remover evento (requer `adminKey`) |
| `POST` | `/api/coupon/event/{id}` | Adicionar cupom ao evento |

Documentação detalhada dos endpoints em [docs/api-reference.md](docs/api-reference.md).

## Estrutura do projeto

```text
src/main/java/com/eventostec/api/
├── controller/       # Endpoints REST
├── service/          # Lógica de negócio
├── repositories/     # Spring Data JPA
├── domain/           # Entidades e DTOs
├── mappers/          # MapStruct (DTO ↔ entidade)
├── config/           # Configurações (AWS condicional)
└── exceptions/       # Tratamento global de erros
```

## Documentação

Veja a pasta [docs/](docs/) para mais detalhes sobre arquitetura, API e integrações.
