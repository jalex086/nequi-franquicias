# Makefile para comandos de deployment
# Simplifica operaciones comunes de Terraform

.PHONY: help init plan apply destroy clean

# Variables
ENV ?= dev
MODULE ?= franquicias/api

# Help
help:
	@echo "Available commands:"
	@echo "  make init ENV=dev MODULE=franquicias/api    - Initialize Terraform"
	@echo "  make plan ENV=dev MODULE=franquicias/api    - Plan Terraform changes"
	@echo "  make apply ENV=dev MODULE=franquicias/api   - Apply Terraform changes"
	@echo "  make destroy ENV=dev MODULE=franquicias/api - Destroy infrastructure"
	@echo "  make clean                                  - Clean Terraform files"
	@echo ""
	@echo "Examples:"
	@echo "  make deploy-dynamodb ENV=dev                - Deploy DynamoDB tables"
	@echo "  make deploy-api ENV=dev                     - Deploy API infrastructure"
	@echo "  make deploy-all ENV=dev                     - Deploy everything"

# Initialize Terraform
init:
	@echo "🚀 Initializing Terraform for $(MODULE) in $(ENV) environment..."
	cd $(MODULE) && terraform init -backend-config=backend-$(ENV).hcl

# Plan changes
plan:
	@echo "📋 Planning Terraform changes for $(MODULE) in $(ENV) environment..."
	cd $(MODULE) && terraform plan \
		-var-file="env/$(ENV)/terraform-$(ENV).tfvars" \
		-out=tfplan

# Apply changes
apply:
	@echo "✅ Applying Terraform changes for $(MODULE) in $(ENV) environment..."
	cd $(MODULE) && terraform apply -auto-approve tfplan

# Destroy infrastructure
destroy:
	@echo "🗑️  Destroying infrastructure for $(MODULE) in $(ENV) environment..."
	cd $(MODULE) && terraform destroy \
		-var-file="env/$(ENV)/terraform-$(ENV).tfvars" \
		-auto-approve

# Clean Terraform files
clean:
	@echo "🧹 Cleaning Terraform files..."
	find . -name ".terraform" -type d -exec rm -rf {} + 2>/dev/null || true
	find . -name "terraform.tfstate*" -type f -delete 2>/dev/null || true
	find . -name "tfplan" -type f -delete 2>/dev/null || true
	find . -name ".terraform.lock.hcl" -type f -delete 2>/dev/null || true

# Shortcuts para deployment completo
deploy-dynamodb:
	@echo "🗄️  Deploying DynamoDB tables..."
	$(MAKE) init MODULE=transversal_dynamodb/business ENV=$(ENV)
	$(MAKE) plan MODULE=transversal_dynamodb/business ENV=$(ENV)
	$(MAKE) apply MODULE=transversal_dynamodb/business ENV=$(ENV)

deploy-api:
	@echo "🚀 Deploying API infrastructure..."
	$(MAKE) init MODULE=franquicias/api ENV=$(ENV)
	$(MAKE) plan MODULE=franquicias/api ENV=$(ENV)
	$(MAKE) apply MODULE=franquicias/api ENV=$(ENV)

deploy-all:
	@echo "🌟 Deploying complete infrastructure..."
	$(MAKE) deploy-dynamodb ENV=$(ENV)
	$(MAKE) deploy-api ENV=$(ENV)
	@echo "✅ Complete infrastructure deployed successfully!"

# Validation
validate:
	@echo "🔍 Validating Terraform configuration..."
	terraform fmt -check -recursive
	cd transversal_dynamodb/business && terraform init -backend=false && terraform validate
	cd franquicias/api && terraform init -backend=false && terraform validate
	@echo "✅ Terraform validation completed!"

# Show outputs
outputs:
	@echo "📊 Terraform outputs for $(MODULE) in $(ENV):"
	cd $(MODULE) && terraform output

# Environment setup
setup-env:
	@echo "⚙️  Setting up environment variables..."
	@echo "Please set the following environment variables:"
	@echo "  export TF_STATE_BUCKET=your-terraform-state-bucket"
	@echo "  export AWS_ACCESS_KEY_ID=your-access-key"
	@echo "  export AWS_SECRET_ACCESS_KEY=your-secret-key"
	@echo "  export AWS_DEFAULT_REGION=us-east-1"
