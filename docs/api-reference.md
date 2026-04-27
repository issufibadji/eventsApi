# API Reference

Base URL: `http://localhost:8090`

---

## Eventos — `/api/event`

### Criar evento
`POST /api/event`

Content-Type: `multipart/form-data`

| Campo | Tipo | Obrigatório | Descrição |
|---|---|---|---|
| title | String (max 100) | Sim | Título do evento |
| description | String (max 400) | Não | Descrição |
| date | Long (epoch ms) | Sim | Data do evento |
| remote | Boolean | Sim | Se é remoto |
| eventUrl | String (max 250) | Não | Link externo |
| image | MultipartFile | Não | Imagem (enviada para S3) |
| city | String | Se presencial | Cidade |
| state | String | Se presencial | UF (2 chars) |

**Resposta:** `200 OK` com `EventResponseDTO`

---

### Listar eventos futuros
`GET /api/event?page=0&size=10`

Retorna eventos com data futura, paginados, ordenados por data.

**Resposta:** `200 OK` com `List<EventResponseDTO>`

---

### Detalhe do evento
`GET /api/event/{eventId}`

Retorna dados completos do evento com lista de cupons válidos.

**Resposta:** `200 OK` com `EventDetailsDTO`

---

### Buscar por título
`GET /api/event/search?title=<texto>`

Busca case-insensitive pelo título.

**Resposta:** `200 OK` com `List<EventResponseDTO>`

---

### Filtrar eventos
`GET /api/event/filter`

| Query param | Tipo | Descrição |
|---|---|---|
| city | String | Cidade (opcional) |
| uf | String | Estado (opcional) |
| startDate | Long (epoch ms) | Data inicial (opcional) |
| endDate | Long (epoch ms) | Data final (opcional) |
| page | int | Página (default 0) |
| size | int | Tamanho (default 10) |

**Resposta:** `200 OK` com `List<EventResponseDTO>`

---

### Deletar evento
`DELETE /api/event/{eventId}`

Body JSON:
```json
{ "adminKey": "admin" }
```

A chave é validada contra a propriedade `admin.key`. Sem autorização via token/header — apenas chave no body.

**Resposta:** `200 OK`

---

## Cupons — `/api/coupon`

### Adicionar cupom a evento
`POST /api/coupon/event/{eventId}`

Body JSON:
```json
{
  "code": "DESCONTO10",
  "discount": 10,
  "valid": 1800000000000
}
```

| Campo | Tipo | Descrição |
|---|---|---|
| code | String | Código do cupom |
| discount | Integer | Percentual de desconto |
| valid | Long (epoch ms) | Data de validade |

**Resposta:** `200 OK` com `CouponResponseDTO`

---

## Erros

Todos os erros seguem RFC 7807:

```json
{
  "title": "Título do erro",
  "code": 400,
  "status": "Bad Request",
  "detail": "Mensagem detalhada em português",
  "instance": "/api/event"
}
```

| Código | Situação |
|---|---|
| 400 | Parâmetro inválido, ausente ou falha de validação de Bean |
| 409 | Violação de constraint no banco |
| 500 | Erro interno |
