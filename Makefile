# Makefile para desarrollo local - Franquicias API

.PHONY: help local-up local-down local-logs local-test build clean

help: ## Mostrar ayuda
	@echo "Comandos disponibles:"
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-20s\033[0m %s\n", $$1, $$2}'

local-up: ## Iniciar ambiente local con LocalStack
	@echo "🚀 Iniciando ambiente local..."
	docker-compose up -d localstack
	@echo "⏳ Esperando LocalStack..."
	sleep 10
	@echo "📋 Creando tablas DynamoDB..."
	./scripts/localstack/01-create-tables.sh
	@echo "✅ Ambiente local listo!"

local-down: ## Detener ambiente local
	@echo "🛑 Deteniendo ambiente local..."
	docker-compose down
	@echo "✅ Ambiente local detenido"

local-logs: ## Ver logs de LocalStack
	docker-compose logs -f localstack

local-test: ## Ejecutar tests locales
	@echo "🧪 Ejecutando tests..."
	cd api && ./gradlew test

build: ## Compilar la aplicación
	@echo "🔨 Compilando aplicación..."
	cd api && ./gradlew build

clean: ## Limpiar build
	@echo "🧹 Limpiando build..."
	cd api && ./gradlew clean
	docker-compose down -v

run-local: ## Ejecutar API en modo local
	@echo "🚀 Ejecutando API en modo local..."
	cd api && ./gradlew bootRun --args='--spring.profiles.active=local'

docker-build: ## Construir imagen Docker de la API
	@echo "🐳 Construyendo imagen Docker..."
	docker-compose build franquicias-api

docker-up: ## Ejecutar todo en Docker
	@echo "🐳 Iniciando todo en Docker..."
	docker-compose up -d
	@echo "✅ Aplicación disponible en http://localhost:8080"

docker-logs: ## Ver logs de la aplicación
	docker-compose logs -f franquicias-api

status: ## Ver estado de los servicios
	@echo "📊 Estado de servicios:"
	docker-compose ps
