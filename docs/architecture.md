# Arquitetura

## Estrutura de Pacotes

```
com.eventostec.api
├── ApiApplication                  # Ponto de entrada Spring Boot
├── config/
│   ├── AWSConfig                   # Bean S3Client — só ativo quando storage.type=s3
│   └── CorsConfig                  # Libera todas origens para GET, POST, PUT
├── controller/
│   ├── EventController             # /api/event
│   └── CouponController            # /api/coupon
├── service/
│   ├── EventService                # Orquestra Address, Coupon e StorageService
│   ├── StorageService              # Interface de upload (local ou S3)
│   ├── LocalStorageService         # Implementação no-op (storage.type=local, padrão)
│   ├── S3StorageService            # Implementação real AWS S3 (storage.type=s3)
│   ├── CouponService               # Persiste e consulta cupons válidos
│   └── AddressService              # Persiste e busca endereço por evento
├── repositories/
│   ├── EventRepository             # JPQL customizado: paginação, filtro, busca
│   ├── CouponRepository            # Filtro por validade (> now)
│   └── AddressRepository           # findByEventId
├── domain/
│   ├── event/                      # Event, EventRequestDTO, EventResponseDTO, EventDetailsDTO, EventAddressProjection
│   ├── coupon/                     # Coupon, CouponRequestDTO, CouponResponseDTO
│   └── address/                    # Address, AddressRequestDTO
├── mappers/
│   └── EventMapper                 # MapStruct: epoch ms ↔ Date; DTO ↔ Entity
├── exceptions/config/
│   ├── ApiExceptionHandler         # @ControllerAdvice global
│   └── ProblemDetails              # Record do payload RFC 7807
└── utils/
    └── ExceptionUtil               # Formata mensagens de erro do Bean Validation
```

## Decisões de Design

### Por que `EventAddressProjection`?
Os endpoints de listagem (`GET /api/event`, filtro e busca) precisam de campos de `Address` junto com `Event`. Usar uma projeção evita carregar a coleção de `Coupon` (OneToMany) desnecessariamente, reduzindo o volume de dados trazidos do banco.

### Por que serviços separados (EventService + AddressService + CouponService)?
`EventService` delega criação de endereço e consulta de cupons para serviços próprios. Isso mantém cada serviço coeso com uma única entidade e permite testar em isolamento com mocks.

### Autenticação do admin
Não há Spring Security. A validação de `adminKey` é feita manualmente em `EventService.deleteEvent()` comparando o valor recebido no body com a propriedade `admin.key` injetada via `@Value`.

### Abstração de storage (`StorageService`)

`EventService` depende da interface `StorageService`, não de `S3Client` diretamente. A implementação ativa é selecionada pela propriedade `storage.type`:

- `local` (padrão / `matchIfMissing=true`): `LocalStorageService` — ignora o arquivo e retorna string vazia; não precisa de credenciais AWS.
- `s3`: `S3StorageService` — faz upload real; `AWSConfig` só é carregado nesse caso. O fluxo é síncrono: upload antes do `repository.save()`; se falhar, o evento não é persistido.

## Diagrama de Entidades

```
Event (1) ──────── (0..1) Address
  │
  └── (1) ──────── (0..N) Coupon
```

- `Address.event_id` e `Coupon.event_id` têm FK com CASCADE DELETE
- Chaves primárias são UUID gerados pela extensão `pgcrypto` do PostgreSQL

## Migrations Flyway

| Versão | Conteúdo |
|---|---|
| V1 | Cria tabela `event` (com `pgcrypto`) |
| V2 | Cria tabela `coupon` com FK para `event` |
| V3 | Cria tabela `address` com FK para `event` |
| V4 | Altera `img_url` e `event_url` de VARCHAR(100) para VARCHAR(250) |
| V5 | Limpa dados de produção |
| V6 | Altera `description` de VARCHAR(250) para VARCHAR(400) |

## Testes

| Tipo | Ferramenta | Banco |
|---|---|---|
| Controller | `@WebMvcTest` + `@MockBean` | Nenhum (mocks) |
| Service | JUnit 5 + Mockito | Nenhum (mocks) |
| Integração (contexto) | `@SpringBootTest` | H2 in-memory |

O profile de teste é ativado automaticamente por `src/test/resources/application-test.properties`, que desabilita o Flyway e usa H2.
