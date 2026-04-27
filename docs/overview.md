# EventosTec Backend — Visão Geral

API REST em Spring Boot para gerenciamento de eventos de tecnologia, com suporte a cupons de desconto, endereços e upload de imagens na AWS S3.

## Stack

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 21 |
| Framework | Spring Boot 3.3.4 |
| Persistência | Spring Data JPA + PostgreSQL 15 |
| Migrations | Flyway 9.8.1 |
| Mapeamento | MapStruct 1.4.2 |
| Storage | AWS S3 SDK v2 |
| Build | Maven (mvnw wrapper) |
| Testes | JUnit 5 + Mockito + H2 |
| Cobertura | JaCoCo |

## Módulos Principais

- **Event** — entidade central; contém título, descrição, URL, imagem, data e flag `remote`
- **Address** — endereço físico do evento (cidade e UF); somente para eventos presenciais
- **Coupon** — cupons de desconto vinculados a um evento com código, percentual e validade

## Portas e Acessos

| Ambiente | Porta |
|---|---|
| Local (dev) | 8090 |
| Docker / produção | 80 |

## Fluxo Resumido de Criação de Evento

```
Cliente → POST /api/event (multipart/form-data)
              ↓
        EventController
              ↓
        EventService
         ├── Upload de imagem → AWS S3 (se houver arquivo)
         ├── Salva Event no PostgreSQL
         └── Se não remoto → AddressService.createAddress()
```
