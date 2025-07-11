# 🎬 Script Review API

API desenvolvida como parte do teste técnico da empresa **Nava**, com foco no gerenciamento de roteiros enviados por clientes para avaliação pela equipe da Cooperfilme.

---

## ✅ Tecnologias utilizadas

- Java 21
- Spring Boot 3.3.3
- Maven
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL (via Docker)
- Swagger / OpenAPI 3.0

---

## 📌 Funcionalidades principais

- Autenticação com JWT
- Cadastro automático de usuários com diferentes cargos (analyst, reviewer, approvers)
- Submissão de roteiros por clientes
- Consulta pública de status do roteiro
- Esteira completa de avaliação de roteiro com 8 status distintos
- Validação rigorosa de transições de status por papel do usuário
- Documentação Swagger com servidor mock

---

## 🧪 Usuários cadastrados automaticamente

| Cargo     | E-mail                      | Senha  |
|-----------|-----------------------------|--------|
| Analyst   | analyst@cooperfilme.com     | 123456 |
| Reviewer  | reviewer@cooperfilme.com    | 123456 |
| Approver  | approver1@cooperfilme.com   | 123456 |
| Approver  | approver2@cooperfilme.com   | 123456 |
| Approver  | approver3@cooperfilme.com   | 123456 |

---

## 📚 Documentação

Swagger UI disponível em:
```
http://localhost:8080/swagger-ui.html
```

Arquivo OpenAPI: [`script-review-api.yaml`](./script-review-api.yaml)

---

## 📂 Principais Endpoints

### 🔐 Autenticação
- `POST /auth/login` → login via JWT

### 🎬 Roteiros
- `POST /scripts` → submeter roteiro
- `GET /scripts/{id}/status` → consultar status
- `PUT /scripts/{id}/status` → mudar status (rota protegida)
- *(extras opcionais: listagem, detalhamento, votação)*

---

## 🧠 Regras de negócio

- Cada roteiro inicia em `AWAITING_ANALYSIS`
- Regras de transição validadas conforme o `Role` do usuário
- Fluxos inválidos resultam em erro 400
- Status finais: `APPROVED` ou `REJECTED`

---

## 🚀 Como rodar localmente

```bash
# Subir banco de dados com Docker
docker-compose up -d

# Compilar o projeto
./mvnw clean install

# Rodar aplicação
./mvnw spring-boot:run
```

---

## 📁 Estrutura de pacotes

- `controller` → endpoints REST
- `entity` → entidades JPA
- `repository` → interfaces Spring Data
- `service` → regras de negócio
- `security` → configuração JWT
- `config` → beans e seeder
- `dto` → objetos de troca de dados
- `enums` → enums do domínio

---

## ✅ Status: Concluído 🎉

Todos os requisitos do desafio foram atendidos, com estrutura modular e extensível para evolução futura.