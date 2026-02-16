# Sistema de Gestão de Benefícios Corporativos

Plataforma para gestão de benefícios de funcionários, com foco em transferências seguras entre contas de benefício, arquitetura em camadas e alta testabilidade.

---

## 🎯 Objetivo do Sistema

- **O que o sistema resolve**
  - Centraliza o cadastro de benefícios corporativos.
  - Permite gerenciar contas de benefício de cada colaborador.
  - Oferece operações de transferência entre contas com regras de negócio consistentes.

- **Público-alvo**
  - Times de engenharia que desejam avaliar arquitetura em camadas (DB → EJB → Backend → Frontend).
  - Avaliação técnica de boas práticas em Java, Spring Boot, Jakarta EE (EJB) e Angular.

---

## 🏗️ Visão de Arquitetura

O sistema é dividido em quatro camadas principais:

- **Banco de Dados**
  - Modelo relacional para benefícios, contas de benefício e transações.
  - Utiliza H2 em desenvolvimento/teste; scripts em `db/` permitem portabilidade para outros bancos.

- **Camada EJB (Core de Negócio)**
  - Implementa as regras de negócio mais sensíveis (por exemplo, transferência entre contas, controle de concorrência e integridade transacional).
  - Utiliza Jakarta EE / EJB 4.0 com JPA e locking otimista/pessimista quando necessário.

- **Backend Spring Boot (API de Aplicação)**
  - Exposição da API REST pública.
  - Orquestração das operações de negócio através de um contrato (`TransferCorePort`) que desacopla o backend da implementação EJB.
  - Uso de DTOs, Bean Validation e `@ControllerAdvice` para tratamento consistente de erros.

- **Frontend Angular**
  - Interface web que consome a API REST.
  - Telas de visualização de benefícios, saldos e fluxo de transferência.

Fluxo resumido de uma operação de transferência:

```text
Frontend (Angular)
    → Backend (Spring Boot / API REST)
        → Core de Negócio (EJB via TransferCorePort)
            → Banco de Dados (H2 / SQL)
```

Para detalhes de decisões arquiteturais e execução passo a passo, consulte o relatório em `planejamento/relatorio-execucao.md`.

---

## 🚀 Como Executar o Projeto

### Pré-requisitos

- Java 17+
- Maven 3.9+
- Node.js 24+
- Angular CLI 21+

### Subir o Backend (Spring Boot)

```bash
cd backend-module
mvn spring-boot:run
```

Backend disponível em: `http://localhost:8080`

### Subir o Frontend (Angular)

```bash
cd frontend
npm install
ng serve
```

Frontend disponível em: `http://localhost:4200`

### Banco de Dados

Em desenvolvimento e testes automatizados é utilizado **H2 em memória**. Para cenários com banco externo, há scripts de exemplo em `db/`:

```bash
cd db
# 1. Criar schema
mysql -u root -p < schema.sql

# 2. Dados iniciais
mysql -u root -p < seed.sql
```

---

## 📦 Estrutura de Módulos

### Backend (`backend-module`)

```text
backend-module/
├── src/main/java/com/transaction/beneficio/
│   ├── api/            # Controllers REST e DTOs
│   ├── app/            # Serviços de aplicação (inclui TransferService e TransferCorePort)
│   ├── domain/         # Entidades de domínio e repositórios
│   ├── infra/          # Implementações de infraestrutura (JPA, persistência)
│   └── BackendApplication.java
├── src/main/resources/
│   └── application.properties
└── pom.xml
```

### EJB (`ejb-module`)

```text
ejb-module/
├── src/main/java/com/transaction/beneficio/ejb/
│   ├── domain/         # Entidades e regras de domínio
│   ├── app/            # Serviços EJB (ex.: BeneficioEjbService)
│   ├── infra/          # Configuração JPA / persistência
│   └── exception/      # Exceções de negócio
├── src/main/resources/
│   └── META-INF/persistence.xml
└── pom.xml
```

### Frontend (`frontend`)

```text
frontend/
├── src/app/
│   ├── core/           # Serviços base, interceptors e cross-cutting
│   ├── features/       # Funcionalidades de domínio (ex.: dashboard)
│   └── shared/         # Componentes e models reutilizáveis
├── angular.json
└── package.json
```

---

## 🔧 Tecnologias Principais

- **Java 17 / Maven** – base da aplicação backend e EJB.
- **Spring Boot 3.2.5** – API REST, integração com JPA, validação e testes.
- **Jakarta EE / EJB 4.0** – camada de negócio core com transações e concorrência.
- **Hibernate / JPA** – mapeamento ORM das entidades.
- **H2 Database** – banco em memória para desenvolvimento e testes.
- **Angular 21** – frontend SPA para consumo da API.

---

## 🧪 Qualidade, Testes e CI

### Como rodar os testes localmente

```bash
# Backend
cd backend-module
mvn test

# EJB
cd ../ejb-module
mvn test

# Frontend
cd ../frontend
npm test
```

### Estratégia de Testes

- **Unitários** – focados em serviços, regras de negócio e controllers (MockMvc).
- **Integração** – testes com banco H2 e cenários de domínio completos.
- **Fluxo completo de transferência** – testes de integração via API garantindo o comportamento end-to-end.
- **Concorrência (EJB)** – cenários que exercitam transferências simultâneas e locking.

### Integração Contínua

- Workflow GitHub Actions em `.github/workflows/ci.yml`.
- A cada push / pull request:
  - Executa testes do `backend-module`.
  - Executa testes do `ejb-module`.
  - Executa testes do `frontend`.

Objetivo: manter a **branch `main` sempre verde e estável**.

---

## 🌐 Documentação da API

- A especificação detalhada dos endpoints REST (URLs, payloads, códigos de resposta, exemplos de request/response e formato de erros) está centralizada em:

  - [`docs/api-endpoints.md`](api-endpoints.md)

Esse arquivo é a referência única para o contrato HTTP do backend.

---

## 🚀 Deploy (Visão Geral)

### Build

```bash
mvn clean package
```

### Executar backend localmente via JAR

```bash
java -jar backend-module/target/backend-module-0.0.1-SNAPSHOT.jar
```

### Build e deploy do frontend

```bash
cd frontend
npm run build
```

Para cenários de containerização, é possível criar uma imagem Docker a partir da raiz do projeto (Dockerfile opcional).

---

## � Status Atual do Projeto

- **Backend:** completo (serviços, API REST, DTOs, validação, tratamento de erros e testes).
- **EJB:** núcleo de negócio implementado com suporte a cenários de concorrência e testes dedicados.
- **Frontend:** estrutura Angular criada e pronta para evolução.

---

## � Licença

MIT License – consulte o arquivo `LICENSE` para mais detalhes.

