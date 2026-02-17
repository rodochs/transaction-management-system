# Docker - Guia Completo

Este documento detalha a configuração Docker do projeto Transaction Management System.

## Arquitetura de Containers

```
┌─────────────────────────────────────────────────────────────┐
│                      tms-network                             │
│                                                              │
│  ┌──────────────┐    ┌──────────────┐    ┌──────────────┐  │
│  │   frontend   │    │   backend    │    │   postgres   │  │
│  │   (Nginx)    │───▶│ (Spring Boot)│───▶│ (PostgreSQL) │  │
│  │   :80        │    │   :8080      │    │   :5432      │  │
│  └──────────────┘    └──────────────┘    └──────────────┘  │
│         │                                                    │
└─────────┼────────────────────────────────────────────────────┘
          │
          ▼
    Browser (usuário)
```

### Serviços

| Serviço | Imagem Base | Porta | Descrição |
|---------|-------------|-------|-----------|
| **frontend** | nginx:1.25-alpine | 80 | Aplicação Angular + Proxy reverso |
| **backend** | eclipse-temurin:17-jre-alpine | 8080 | API Spring Boot |
| **postgres** | postgres:15-alpine | 5432 | Banco de dados PostgreSQL |

## Pré-requisitos

### Linux
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install docker.io
sudo usermod -aG docker $USER
# Fazer logout e login novamente
```

### macOS
- Instalar [Docker Desktop for Mac](https://www.docker.com/products/docker-desktop)

### Windows
- Instalar [Docker Desktop for Windows](https://www.docker.com/products/docker-desktop)
- Habilitar WSL 2

### Recursos Mínimos
- 4GB RAM disponível
- 10GB espaço em disco
- Portas livres: 80, 8080, 5432

## Início Rápido

### Ambiente de Produção (PostgreSQL)

```bash
# Clonar repositório
git clone https://github.com/seu-usuario/transaction-management-system.git
cd transaction-management-system

# Iniciar todos os serviços
docker compose up -d

# Verificar status
docker compose ps

# Ver logs
docker compose logs -f
```

**Acessar:**
- Frontend: http://localhost
- Backend API: http://localhost:8080/api/v1
- Swagger: http://localhost:8080/swagger-ui.html

### Ambiente de Desenvolvimento (H2)

```bash
docker compose -f docker-compose.dev.yml up -d
```

**Acessar:**
- Frontend: http://localhost:4200
- Backend: http://localhost:8080

## Comandos Docker Compose

### Produção (PostgreSQL)

```bash
# Construir imagens
docker compose build

# Iniciar serviços
docker compose up -d

# Parar serviços
docker compose down

# Ver logs
docker compose logs -f

# Logs de um serviço específico
docker compose logs -f backend
docker compose logs -f frontend
docker compose logs -f postgres

# Listar containers
docker compose ps

# Remover tudo (containers, volumes, imagens)
docker compose down -v
docker rmi transaction-management-system-backend transaction-management-system-frontend
```

### Desenvolvimento (H2)

```bash
# Iniciar
docker compose -f docker-compose.dev.yml up -d

# Parar
docker compose -f docker-compose.dev.yml down

# Ver logs
docker compose -f docker-compose.dev.yml logs -f
```

## Estrutura de Arquivos

```
docker/
├── backend/
│   ├── Dockerfile           # Build de produção (multi-stage)
│   ├── Dockerfile.dev       # Build de desenvolvimento
│   └── application-docker.properties
├── frontend/
│   ├── Dockerfile           # Build de produção (multi-stage)
│   ├── Dockerfile.dev       # Build de desenvolvimento
│   └── nginx.conf           # Configuração do Nginx
└── postgres/
    └── init/
        ├── 01-schema.sql    # Estrutura do banco
        └── 02-seed.sql      # Dados iniciais

docker-compose.yml           # Orquestração de produção
docker-compose.dev.yml       # Orquestração de desenvolvimento
.dockerignore                # Arquivos ignorados no build
.env.example                 # Exemplo de variáveis de ambiente
```

## Detalhamento dos Dockerfiles

### Backend (Multi-stage Build)

```dockerfile
# Stage 1: Build com Maven
FROM maven:3.9-eclipse-temurin-17 AS build
# Compila o código e gera o JAR

# Stage 2: Runtime com JRE
FROM eclipse-temurin:17-jre-alpine
# Apenas o JAR e JRE necessários
```

**Benefícios:**
- Imagem final ~200MB (vs ~800MB com JDK completo)
- Sem ferramentas de build no runtime
- Menor superfície de ataque

### Frontend (Multi-stage Build)

```dockerfile
# Stage 1: Build com Node
FROM node:20-alpine AS build
# Compila o Angular

# Stage 2: Servir com Nginx
FROM nginx:1.25-alpine
# Apenas arquivos estáticos + Nginx
```

**Benefícios:**
- Imagem final ~50MB
- Nginx otimizado para servir SPAs
- Proxy reverso para API

## Configuração de Rede

### Bridge Network (tms-network)

Todos os containers compartilham a mesma rede Docker:

```yaml
networks:
  tms-network:
    driver: bridge
```

**DNS Interno:**
- `postgres` → Container do PostgreSQL
- `backend` → Container do Spring Boot
- `frontend` → Container do Nginx

### Proxy Reverso (Nginx)

O Nginx roteia requisições `/api/*` para o backend:

```nginx
location /api {
    proxy_pass http://backend:8080;
}
```

Isso permite que o frontend acesse a API sem problemas de CORS.

## Volumes e Persistência

### Volume do PostgreSQL

```yaml
volumes:
  postgres_data:
```

Dados persistem entre reinicializações do container.

**Backup:**
```bash
docker exec tms-postgres pg_dump -U postgres beneficio_db > backup.sql
```

**Restore:**
```bash
cat backup.sql | docker exec -i tms-postgres psql -U postgres beneficio_db
```

**Limpar dados:**
```bash
docker-compose down -v  # Remove volumes
```

## Variáveis de Ambiente

### Backend

| Variável | Descrição | Padrão |
|----------|-----------|--------|
| `SPRING_PROFILES_ACTIVE` | Profile do Spring | `docker` |
| `SPRING_DATASOURCE_URL` | URL do banco | `jdbc:postgresql://postgres:5432/beneficio_db` |
| `SPRING_DATASOURCE_USERNAME` | Usuário do banco | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Senha do banco | `postgres` |
| `JAVA_OPTS` | Opções da JVM | `-Xmx512m -Xms256m` |

### PostgreSQL

| Variável | Descrição | Padrão |
|----------|-----------|--------|
| `POSTGRES_DB` | Nome do banco | `beneficio_db` |
| `POSTGRES_USER` | Usuário | `postgres` |
| `POSTGRES_PASSWORD` | Senha | `postgres` |

## Health Checks

### Backend
```yaml
healthcheck:
  test: ["CMD", "wget", "--spider", "http://localhost:8080/actuator/health"]
  interval: 30s
  timeout: 3s
  retries: 3
```

### PostgreSQL
```yaml
healthcheck:
  test: ["CMD-SHELL", "pg_isready -U postgres"]
  interval: 10s
  timeout: 5s
  retries: 5
```

## Troubleshooting

### Container não inicia

```bash
# Ver logs detalhados
docker-compose logs backend

# Verificar se portas estão em uso
netstat -an | grep 8080
```

### Erro de conexão com banco

```bash
# Verificar se PostgreSQL está healthy
docker-compose ps

# Conectar manualmente
docker exec -it tms-postgres psql -U postgres -d beneficio_db
```

### Rebuild após alterações

```bash
# Rebuild completo
docker-compose up --build

# Rebuild de um serviço específico
docker-compose build backend
docker-compose up -d backend
```

### Limpar cache do Docker

```bash
# Remover imagens não utilizadas
docker image prune

# Remover tudo (CUIDADO)
docker system prune -a
```

## Acesso em Rede Local

Para acessar de outros dispositivos:

1. Descubra o IP da máquina:
   ```bash
   # Linux/macOS
   ip addr show | grep inet
   
   # Windows
   ipconfig
   ```

2. Acesse via IP:
   - Frontend: `http://192.168.x.x`
   - Backend: `http://192.168.x.x:8080`

## Deploy em Cloud

### AWS ECS/Fargate

1. Criar repositório ECR
2. Push das imagens
3. Criar Task Definition
4. Criar Service no ECS

### Docker Hub

```bash
# Login
docker login

# Tag e push
docker tag tms-backend:latest usuario/tms-backend:latest
docker push usuario/tms-backend:latest
```

### Kubernetes (básico)

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: tms-backend
spec:
  replicas: 2
  selector:
    matchLabels:
      app: tms-backend
  template:
    spec:
      containers:
      - name: backend
        image: tms-backend:latest
        ports:
        - containerPort: 8080
```

## Segurança

### Práticas Implementadas

- ✅ Containers rodam com usuário não-root
- ✅ Imagens Alpine (menor superfície de ataque)
- ✅ Multi-stage builds (sem ferramentas de build no runtime)
- ✅ Health checks para disponibilidade
- ✅ Rede isolada entre containers

### Recomendações para Produção

- Usar secrets manager para senhas
- Habilitar TLS/HTTPS
- Configurar rate limiting no Nginx
- Usar imagens com tags específicas (não `latest`)
- Escanear imagens com ferramentas como Trivy
