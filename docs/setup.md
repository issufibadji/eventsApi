# Setup e Ambiente de Desenvolvimento

## Pré-requisitos

- Java 21
- Maven 3.9+ (ou use o wrapper `./mvnw`)

---

## Etapa 1 — Local (sem Docker, sem AWS S3)

Não precisa de nenhum serviço externo. O banco é H2 in-memory e o upload de imagens é ignorado.

```bash
# Build
./mvnw clean install

# Rodar com o profile local
./mvnw spring-boot:run "-Dspring-boot.run.profiles=local"
```

A API fica em `http://localhost:8090`.
Console web do H2 disponível em `http://localhost:8090/h2-console`:

- JDBC URL: `jdbc:h2:mem:eventostec`
- Usuário: `sa` | Senha: *(vazia)*

> O schema é criado automaticamente pelo Hibernate (`ddl-auto=create-drop`).
> O Flyway fica desativado nesse profile porque as migrations usam a extensão
> `pgcrypto` do PostgreSQL, que não existe no H2.

---

## Etapa 2 — Com Docker e AWS S3

### Pré-requisitos adicionais

- Docker + Docker Compose
- Conta AWS com bucket S3

### Subir PostgreSQL local

```bash
docker-compose up -d        # inicia PostgreSQL na porta 5432
docker-compose down         # para e remove containers
docker-compose down -v      # remove também o volume (apaga dados)
```

### Rodar a aplicação

```bash
./mvnw spring-boot:run
```

### Ativar upload real para S3

Defina a propriedade `storage.type=s3` e as credenciais AWS:

```bash
# Via variáveis de ambiente
export STORAGE_TYPE=s3
export AWS_REGION=us-east-1
export AWS_BUCKET_NAME=eventostec-imagens
# Opcional: credenciais estáticas (se não usar IAM role / ~/.aws/credentials)
# export AWS_ACCESS_KEY_ID=...
# export AWS_SECRET_ACCESS_KEY=...

./mvnw spring-boot:run
```

Ou adicione `storage.type=s3` diretamente em `application.properties`.

---

## Variáveis de ambiente (Etapa 2)

| Variável | Padrão | Propósito |
| --- | --- | --- |
| `DB_URL` | `jdbc:postgresql://localhost/eventostec` | URL do PostgreSQL |
| `DB_USER` | `admin` | Usuário do banco |
| `DB_PASSWORD` | `admin` | Senha do banco |
| `AWS_REGION` | `us-east-1` | Região S3 |
| `AWS_BUCKET_NAME` | `eventostec-imagens` | Bucket S3 |
| `ADMIN_KEY` | `admin` | Chave para endpoints de admin |

---

## Build e deploy com Docker

```bash
# Build da imagem
docker build --platform linux/amd64 -t backend-eventostec:3.0 .

# Publicar no Docker Hub
docker tag backend-eventostec:3.0 kipperdev/backend-eventostec:3.0
docker push kipperdev/backend-eventostec:3.0

# Rodar em produção (EC2)
docker run -d -p 80:80 \
  -e DB_URL=... \
  -e DB_USER=... \
  -e DB_PASSWORD=... \
  -e AWS_REGION=... \
  -e AWS_BUCKET_NAME=... \
  -e ADMIN_KEY=... \
  kipperdev/backend-eventostec:3.0
```

---

## Testes

```bash
# Todos os testes
./mvnw test

# Classe específica
./mvnw test -Dtest=EventServiceTest

# Método específico
./mvnw test -Dtest=EventServiceTest#test_shouldSaveEvent

# Relatório de cobertura JaCoCo
./mvnw verify
# Relatório em: target/site/jacoco/index.html
```

Os testes usam H2 in-memory com Flyway desativado (`application-test.properties`).
Controllers são testados com `@WebMvcTest` + mocks — sem banco.
