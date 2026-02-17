.PHONY: help build up up-dev down clean logs logs-backend logs-frontend test ps

# Cores para output
CYAN := \033[36m
RESET := \033[0m

help: ## Mostra esta mensagem de ajuda
	@echo "Comandos disponíveis:"
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "  $(CYAN)%-15s$(RESET) %s\n", $$1, $$2}'

build: ## Constrói todas as imagens Docker
	@echo "========================================="
	@echo " Building Docker Images"
	@echo "========================================="
	docker-compose build
	@echo ""
	@echo "Build complete!"

up: ## Inicia ambiente de produção (PostgreSQL)
	@echo "Starting production environment..."
	docker-compose up -d
	@echo ""
	@echo "========================================="
	@echo " System Started!"
	@echo "========================================="
	@echo "Frontend: http://localhost"
	@echo "Backend:  http://localhost:8080"
	@echo "Swagger:  http://localhost:8080/swagger-ui.html"
	@echo ""

up-dev: ## Inicia ambiente de desenvolvimento (H2)
	@echo "Starting development environment..."
	docker-compose -f docker-compose.dev.yml up -d
	@echo ""
	@echo "========================================="
	@echo " Development System Started!"
	@echo "========================================="
	@echo "Frontend: http://localhost:4200"
	@echo "Backend:  http://localhost:8080"
	@echo ""

down: ## Para todos os containers
	docker-compose down
	docker-compose -f docker-compose.dev.yml down 2>/dev/null || true

clean: ## Remove containers, imagens e volumes
	@echo "Stopping containers..."
	docker-compose down -v 2>/dev/null || true
	docker-compose -f docker-compose.dev.yml down -v 2>/dev/null || true
	@echo "Removing images..."
	docker rmi tms-backend:latest tms-frontend:latest 2>/dev/null || true
	docker rmi transaction-management-system-backend:latest 2>/dev/null || true
	docker rmi transaction-management-system-frontend:latest 2>/dev/null || true
	@echo "Cleanup complete!"

logs: ## Mostra logs de todos os containers
	docker-compose logs -f

logs-backend: ## Mostra logs apenas do backend
	docker-compose logs -f backend

logs-frontend: ## Mostra logs apenas do frontend
	docker-compose logs -f frontend

ps: ## Lista containers em execução
	docker-compose ps

test: ## Executa testes (backend + frontend)
	@echo "Running backend tests..."
	cd backend-module && mvn test -q
	@echo ""
	@echo "Running EJB tests..."
	cd ejb-module && mvn test -q
	@echo ""
	@echo "Running frontend tests..."
	cd frontend && npm test -- --watch=false
	@echo ""
	@echo "All tests passed!"
