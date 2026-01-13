# Makefile para desarrollo local - Franquicias API

.PHONY: help local-up local-down local-logs local-test build clean deploy-dev deploy-qa deploy-pdn destroy-dev destroy-qa validate status-dev status-qa

help: ## Mostrar ayuda
	@echo "Comandos disponibles:"
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-20s\033[0m %s\n", $$1, $$2}'

# === DESARROLLO LOCAL ===
local-up: ## Iniciar ambiente local con LocalStack
	@echo "🚀 Iniciando ambiente local..."
	docker-compose up -d localstack
	@echo "⏳ Esperando LocalStack..."
	sleep 10
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

# === INFRAESTRUCTURA AWS ===
deploy-dev: ## Desplegar infraestructura en develop
	@echo "🚀 Desplegando infraestructura en DEVELOP..."
	cd infrastructure/transversal_dynamodb/business && \
		terraform init -backend-config=backend-dev.hcl && \
		terraform apply -auto-approve -var-file=env/dev/terraform-dev.tfvars
	cd infrastructure/franquicias/api && \
		terraform init -backend-config=backend-dev.hcl && \
		terraform apply -auto-approve -var-file=env/dev/terraform-dev.tfvars
	@echo "✅ Deploy de develop completado"

deploy-qa: ## Desplegar infraestructura en QA
	@echo "🚀 Desplegando infraestructura en QA..."
	cd infrastructure/transversal_dynamodb/business && \
		terraform init -backend-config=backend-qa.hcl && \
		terraform apply -auto-approve -var-file=env/qa/terraform-qa.tfvars
	cd infrastructure/franquicias/api && \
		terraform init -backend-config=backend-qa.hcl && \
		terraform apply -auto-approve -var-file=env/qa/terraform-qa.tfvars
	@echo "✅ Deploy de QA completado"

deploy-pdn: ## Desplegar infraestructura en producción
	@echo "⚠️  Desplegando infraestructura en PRODUCCIÓN..."
	@read -p "¿Continuar con PRODUCCIÓN? (y/N): " confirm && [ "$$confirm" = "y" ]
	cd infrastructure/transversal_dynamodb/business && \
		terraform init -backend-config=backend-pdn.hcl && \
		terraform plan -var-file=env/pdn/terraform-pdn.tfvars && \
		terraform apply -var-file=env/pdn/terraform-pdn.tfvars
	cd infrastructure/franquicias/api && \
		terraform init -backend-config=backend-pdn.hcl && \
		terraform plan -var-file=env/pdn/terraform-pdn.tfvars && \
		terraform apply -var-file=env/pdn/terraform-pdn.tfvars
	@echo "✅ Deploy de producción completado"

destroy-dev: ## Destruir infraestructura de develop
	@echo "💥 Destruyendo infraestructura de DEVELOP..."
	@read -p "¿Confirmar destrucción de DEVELOP? (y/N): " confirm && [ "$$confirm" = "y" ]
	cd infrastructure/franquicias/api && \
		terraform init -backend-config=backend-dev.hcl && \
		terraform destroy -auto-approve -var-file=env/dev/terraform-dev.tfvars
	cd infrastructure/transversal_dynamodb/business && \
		terraform init -backend-config=backend-dev.hcl && \
		terraform destroy -auto-approve -var-file=env/dev/terraform-dev.tfvars
	@echo "✅ Infraestructura de develop destruida"

destroy-qa: ## Destruir infraestructura de QA
	@echo "💥 Destruyendo infraestructura de QA..."
	@read -p "¿Confirmar destrucción de QA? (y/N): " confirm && [ "$$confirm" = "y" ]
	cd infrastructure/franquicias/api && \
		terraform init -backend-config=backend-qa.hcl && \
		terraform destroy -auto-approve -var-file=env/qa/terraform-qa.tfvars
	cd infrastructure/transversal_dynamodb/business && \
		terraform init -backend-config=backend-qa.hcl && \
		terraform destroy -auto-approve -var-file=env/qa/terraform-qa.tfvars
	@echo "✅ Infraestructura de QA destruida"

validate: ## Validar configuración de Terraform
	@echo "🔍 Validando configuración de Terraform..."
	cd infrastructure/franquicias/api && terraform validate
	cd infrastructure/transversal_dynamodb/business && terraform validate
	@echo "✅ Configuración válida"

status-dev: ## Ver estado de recursos en develop
	@echo "📊 Estado de recursos en DEVELOP:"
	@echo "--- DynamoDB Tables ---"
	aws dynamodb list-tables --query 'TableNames[?contains(@, `dev`)]' --output table
	@echo "--- ECS Services ---"
	aws ecs list-services --cluster business-cluster-dev --query 'serviceArns' --output table 2>/dev/null || echo "Cluster no encontrado"

status-qa: ## Ver estado de recursos en QA
	@echo "📊 Estado de recursos en QA:"
	@echo "--- DynamoDB Tables ---"
	aws dynamodb list-tables --query 'TableNames[?contains(@, `qa`)]' --output table
	@echo "--- ECS Services ---"
	aws ecs list-services --cluster business-cluster-qa --query 'serviceArns' --output table 2>/dev/null || echo "Cluster no encontrado"

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
