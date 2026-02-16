# Documentação da API – Backend

Este documento descreve o contrato HTTP exposto pelo **backend Spring Boot** do Sistema de Gestão de Benefícios Corporativos.

---

## 1. Convenções Gerais

- **Base URL (desenvolvimento):** `http://localhost:8080`
- **Prefixo padrão:** todas as rotas consideram o prefixo `/api` já configurado.
- **Formato de dados:** JSON em requisições e respostas.
- **Autenticação:** não há autenticação neste desafio (foco em arquitetura e domínio).
- **Versionamento:** recursos de negócio expostos em `/api/v1/...`.

### 1.1 Validação e Tratamento de Erros

- DTOs de entrada utilizam **Bean Validation** (`jakarta.validation`).
- Erros de validação geram **HTTP 400** com corpo padronizado.
- Erros de negócio mapeados como `IllegalArgumentException` também resultam em **HTTP 400**.
- Recursos não encontrados retornam **HTTP 404** (quando aplicável).
- Erros inesperados retornam **HTTP 500**.

Formato padrão para erros de validação:

```json
{
  "error": "VALIDATION_ERROR",
  "details": {
    "campo": "mensagem de erro",
    "amount": "must be greater than 0"
  }
}
```

Esse payload é gerado pelo `GlobalExceptionHandler` a partir de `MethodArgumentNotValidException`.

---

## 2. Health Check

### `GET /api/health`

Retorna o status da aplicação e algumas informações básicas.

**Resposta 200 – Exemplo**

```json
{
  "status": "UP",
  "application": "transaction-beneficio-backend",
  "version": "0.0.1-SNAPSHOT",
  "timestamp": "2024-01-01T12:00:00Z"
}
```

---

## 3. Benefícios

Recurso responsável pelo cadastro e manutenção de tipos de benefício.

### 3.1 Listar benefícios

- **Endpoint:** `GET /api/v1/beneficios`
- **Descrição:** retorna a lista de benefícios cadastrados.

**Resposta 200 – Exemplo**

```json
[
  {
    "id": 1,
    "nome": "Vale Alimentação",
    "descricao": "Cartão alimentação",
    "valor": 500.00,
    "ativo": true
  }
]
```

### 3.2 Obter benefício por ID

- **Endpoint:** `GET /api/v1/beneficios/{id}`

**Códigos de resposta**

- `200` – benefício encontrado.
- `404` – benefício não encontrado.

### 3.3 Criar benefício

- **Endpoint:** `POST /api/v1/beneficios`

**Body (`BeneficioRequest`)**

```json
{
  "nome": "Vale Refeição",
  "descricao": "Cartão refeição",
  "valor": 600.00,
  "ativo": true
}
```

**Regras de validação**

- `nome`: obrigatório, não vazio.
- `valor`: obrigatório, `>= 0`.
- `ativo`: obrigatório.

**Códigos de resposta**

- `201` – benefício criado, com header `Location` apontando para o recurso.
- `400` – erro de validação.

### 3.4 Atualizar benefício

- **Endpoint:** `PUT /api/v1/beneficios/{id}`

**Body:** mesmo formato do `BeneficioRequest` do `POST`.

**Códigos de resposta**

- `200` – benefício atualizado com sucesso.
- `400` – erro de validação.
- `404` – benefício não encontrado.

### 3.5 Remover benefício

- **Endpoint:** `DELETE /api/v1/beneficios/{id}`

**Códigos de resposta**

- `204` – removido com sucesso.
- `404` – benefício não encontrado.

---

## 4. Contas de Benefício

Recurso que representa a associação entre um cliente, um benefício e o saldo disponível.

### 4.1 Criar conta de benefício

- **Endpoint:** `POST /api/v1/contas-beneficio`

**Body (`ContaBeneficioRequest`)**

```json
{
  "clienteId": 10,
  "beneficioId": 1,
  "saldoInicial": 1000.00
}
```

**Regras de validação**

- `clienteId`: obrigatório.
- `beneficioId`: obrigatório.
- `saldoInicial`: obrigatório, `>= 0`.

**Códigos de resposta**

- `201` – conta criada.
- `400` – erro de validação ou referência a cliente/benefício inexistente.

**Resposta 201 – Exemplo (`ContaBeneficioResponse`)**

```json
{
  "id": 5,
  "clienteId": 10,
  "beneficioId": 1,
  "saldo": 1000.00
}
```

### 4.2 Consultar conta de benefício por ID

- **Endpoint:** `GET /api/v1/contas-beneficio/{id}`

**Códigos de resposta**

- `200` – conta encontrada.
- `404` – conta não encontrada.

---

## 5. Histórico de Transações de Benefício

Representa o histórico de operações envolvendo contas de benefício.

### 5.1 Listar transações

- **Endpoint:** `GET /api/v1/transacoes`

**Resposta 200 – Exemplo (lista de `TransacaoBeneficioResponse`)**

```json
[
  {
    "id": 123,
    "contaOrigemId": 5,
    "contaDestinoId": 6,
    "valor": 150.00,
    "tipo": "TRANSFERENCIA",
    "dataHora": "2024-01-01T10:15:30"
  }
]
```

---

## 6. Operação de Transferência de Benefícios

Operação responsável por movimentar saldo entre duas contas de benefício.

### 6.1 Executar transferência

- **Endpoint:** `POST /api/v1/transfers`

**Body (`TransferRequest`)**

```json
{
  "fromAccountId": 5,
  "toAccountId": 6,
  "amount": 150.00
}
```

**Regras de validação**

- `fromAccountId`: obrigatório.
- `toAccountId`: obrigatório.
- `amount`: obrigatório, `> 0`.

**Fluxo interno (resumo)**

- O controller delega para `TransferService`.
- `TransferService` orquestra a operação chamando o contrato `TransferCorePort`.
- O backend permanece desacoplado da implementação EJB, permitindo evolução independente da camada core.

**Resposta 200 – Exemplo (`TransferResult`)**

```json
{
  "fromAccountId": 5,
  "toAccountId": 6,
  "amount": 150.00
}
```

**Códigos de resposta / erros possíveis**

- `200` – transferência executada com sucesso.
- `400` – erro de validação ou parâmetros inconsistentes.
- `500` – falha inesperada na orquestração ou integração com o core.

---

## 7. Resumo de Códigos de Status HTTP

| Código | Significado geral                | Exemplos de uso                              |
|--------|----------------------------------|----------------------------------------------|
| 200    | Sucesso                          | GET de recursos, execução de transferência   |
| 201    | Criado                           | Criação de benefício, criação de conta       |
| 204    | Sem conteúdo                     | Remoção de benefício                         |
| 400    | Requisição inválida              | Erros de validação, parâmetros inconsistentes|
| 404    | Não encontrado                   | Benefício ou conta inexistente               |
| 500    | Erro interno inesperado          | Exceções não mapeadas explicitamente         |

