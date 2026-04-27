# Integração com AWS S3

## Configuração

O bean `S3Client` é criado em `AWSConfig` com base nas variáveis:

```
AWS_REGION        → região do bucket (ex: us-east-1)
AWS_BUCKET_NAME   → nome do bucket (ex: eventostec-imagens)
```

Se `AWS_ACCESS_KEY_ID` e `AWS_SECRET_ACCESS_KEY` estiverem vazias, o SDK usa o `DefaultCredentialsProvider` (IAM role, ~/.aws/credentials, etc.). Para credenciais estáticas, basta definir essas variáveis de ambiente.

## Fluxo de Upload

1. `EventController` recebe o `MultipartFile` via `multipart/form-data`
2. `EventService.uploadImage()` é chamado se o arquivo não for nulo
3. Um nome de arquivo UUID é gerado: `<uuid>-<nomeOriginal>`
4. `S3Client.putObject()` envia o arquivo com `ContentType` do arquivo original
5. A URL pública é construída como `https://<bucket>.s3.<region>.amazonaws.com/<filename>`
6. A URL é salva em `event.imgUrl`

## Permissões IAM necessárias (mínimo)

```json
{
  "Effect": "Allow",
  "Action": ["s3:PutObject", "s3:GetObject"],
  "Resource": "arn:aws:s3:::eventostec-imagens/*"
}
```

## Desenvolvimento sem AWS

Para rodar localmente sem configurar S3, basta não enviar o campo `image` na requisição de criação de evento. O `imgUrl` ficará `null` e nenhuma chamada ao S3 será feita.
