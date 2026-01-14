# Infrastructure Documentation

## Descripción
Infraestructura como código para el despliegue del Sistema de Gestión de Franquicias en AWS, utilizando Terraform con **backends separados por ambiente** y GitHub Actions para CI/CD automatizado.

## 🏗️ Arquitectura AWS

![Infraestructura AWS](../docs/aws_infrastructure.png)

### Componentes Principales
- **ECS Fargate**: Contenedores serverless para la API
- **DynamoDB**: Base de datos NoSQL con esquema híbrido
- **Application Load Balancer**: Balanceador de carga
- **VPC**: Red privada virtual con subnets públicas
- **CloudWatch**: Monitoreo y logs centralizados

## 📁 Estructura de Terraform

```
infrastructure/
├── franquicias/
│   └── api/                          # Infraestructura de la API
│       ├── main.tf                   # Recursos principales (ECS, ALB, SG)
│       ├── locals.tf                 # Variables locales uniformes
│       ├── inputs.tf                 # Variables de entrada
│       ├── providers.tf              # Configuración de providers
│       ├── backend-dev.hcl          # Backend S3 para develop
│       ├── backend-qa.hcl           # Backend S3 para QA
│       ├── backend-pdn.hcl          # Backend S3 para producción
│       └── env/
│           ├── dev/terraform-dev.tfvars
│           ├── qa/terraform-qa.tfvars
│           └── pdn/terraform-pdn.tfvars
├── transversal_dynamodb/
│   └── business/                     # Tablas DynamoDB transversales
│       ├── main.tf                   # Definición de tablas
│       ├── providers.tf              # Configuración de providers
│       ├── backend-dev.hcl          # Backend S3 para develop
│       ├── backend-qa.hcl           # Backend S3 para QA
│       ├── backend-pdn.hcl          # Backend S3 para producción
│       └── env/
│           ├── dev/terraform-dev.tfvars
│           ├── qa/terraform-qa.tfvars
│           └── pdn/terraform-pdn.tfvars
├── modules/
│   └── networking/                   # Módulo de red reutilizable
│       ├── main.tf
│       ├── variables.tf
│       └── outputs.tf
└── Makefile                         # Comandos automatizados
```

## 🚀 Despliegue

### Prerrequisitos
- **AWS CLI** configurado con credenciales
- **Terraform >= 1.13.3**
- **Permisos IAM** para ECS, DynamoDB, S3, ALB
- **Bucket S3** para backend: `nequi-franquicias-terraform-state`

### Backends Separados por Ambiente
Cada ambiente tiene su propio estado de Terraform para evitar conflictos:

```bash
# Backend files
backend-dev.hcl  # Estado de develop
backend-qa.hcl   # Estado de QA  
backend-pdn.hcl  # Estado de producción
```

### Comandos de Despliegue

#### Desarrollo (develop)
```bash
# 1. Desplegar DynamoDB
cd infrastructure/transversal_dynamodb/business
terraform init -backend-config=backend-dev.hcl
terraform plan -var-file=env/dev/terraform-dev.tfvars
terraform apply -auto-approve -var-file=env/dev/terraform-dev.tfvars

# 2. Desplegar API
cd ../../franquicias/api
terraform init -backend-config=backend-dev.hcl
terraform plan -var-file=env/dev/terraform-dev.tfvars
terraform apply -auto-approve -var-file=env/dev/terraform-dev.tfvars
```

#### QA
```bash
# 1. Desplegar DynamoDB
cd infrastructure/transversal_dynamodb/business
terraform init -backend-config=backend-qa.hcl
terraform plan -var-file=env/qa/terraform-qa.tfvars
terraform apply -auto-approve -var-file=env/qa/terraform-qa.tfvars

# 2. Desplegar API
cd ../../franquicias/api
terraform init -backend-config=backend-qa.hcl
terraform plan -var-file=env/qa/terraform-qa.tfvars
terraform apply -auto-approve -var-file=env/qa/terraform-qa.tfvars
```

#### Producción (pdn)
```bash
# 1. Desplegar DynamoDB
cd infrastructure/transversal_dynamodb/business
terraform init -backend-config=backend-pdn.hcl
terraform plan -var-file=env/pdn/terraform-pdn.tfvars
terraform apply -var-file=env/pdn/terraform-pdn.tfvars  # Sin auto-approve en producción

# 2. Desplegar API
cd ../../franquicias/api
terraform init -backend-config=backend-pdn.hcl
terraform plan -var-file=env/pdn/terraform-pdn.tfvars
terraform apply -var-file=env/pdn/terraform-pdn.tfvars  # Sin auto-approve en producción
```

### Comandos con Makefile (Simplificado)
```bash
# Desarrollo
make deploy-dev

# QA
make deploy-qa

# Producción
make deploy-pdn

# Destruir ambiente
make destroy-dev
make destroy-qa
```

## 🔧 Recursos AWS

### ECS Fargate
- **Cluster:** `business-cluster-{env}`
- **Service:** `business-franquicias-{env}`
- **Task Definition:** Configuración uniforme para todos los ambientes
- **Scaling:** 1 instancia (dev/qa), 2 instancias (pdn)

### DynamoDB
- **Tablas:**
  - `business-franquicias-{env}` - Datos de franquicias
  - `business-sucursales-{env}` - Datos de sucursales  
  - `business-productos-{env}` - Datos de productos
- **Billing Mode:** Pay per request
- **GSI:** Índices secundarios para optimización
- **Esquema híbrido:** Single-table design con múltiples entidades

### Networking
- **VPC:** Default VPC de AWS
- **Subnets:** Subnets públicas automáticas
- **Security Groups:** 
  - ALB: Puerto 80 desde Internet
  - ECS: Puerto 8080 desde ALB únicamente
- **ALB:** `business-franquicias-alb-{env}`

### Backend State Management
- **S3 Bucket:** `nequi-franquicias-terraform-state`
- **DynamoDB Lock Table:** `nequi-franquicias-terraform-locks`
- **Estados separados** por ambiente y módulo

## 🔄 CI/CD Pipeline

### GitHub Actions Workflows

#### Deploy Develop
```yaml
# .github/workflows/deploy-dev.yml
name: Deploy to Develop
on:
  push:
    branches: [develop]

jobs:
  deploy:
    runs-on: ubuntu-latest
    steps:
      - name: Configure AWS
        uses: aws-actions/configure-aws-credentials@v2
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: us-east-1

      - name: Deploy DynamoDB
        working-directory: infrastructure/transversal_dynamodb/business
        run: |
          terraform init -backend-config=backend-dev.hcl
          terraform apply -auto-approve -var-file=env/dev/terraform-dev.tfvars

      - name: Deploy API Infrastructure
        working-directory: infrastructure/franquicias/api
        run: |
          terraform init -backend-config=backend-dev.hcl
          terraform apply -auto-approve -var-file=env/dev/terraform-dev.tfvars
```

#### Deploy QA
```yaml
# .github/workflows/deploy-qa.yml
name: Deploy to QA
on:
  push:
    branches: [qa]
# Similar estructura con backend-qa.hcl
```

#### Deploy Producción
```yaml
# .github/workflows/deploy-pdn.yml
name: Deploy to Production
on:
  push:
    branches: [main]
# Similar estructura con backend-pdn.hcl (sin auto-approve)
```

### Ambientes y Ramas

| Ambiente | Rama | Backend | Auto-Deploy |
|----------|------|---------|-------------|
| Develop | `develop` | `backend-dev.hcl` | ✅ Automático |
| QA | `qa` | `backend-qa.hcl` | ✅ Automático |
| Producción | `main` | `backend-pdn.hcl` | ⚠️ Manual approval |

### Estado de Terraform
- **Bucket S3**: `nequi-franquicias-terraform-state`
- **DynamoDB Lock**: `nequi-franquicias-terraform-locks`
- **Estados separados** por ambiente para evitar conflictos

## 📊 Monitoreo

### CloudWatch
- **Logs:** Agregación de logs de aplicación
- **Metrics:** CPU, memoria, requests
- **Alarms:** Alertas automáticas

### Métricas Clave
- Response time
- Error rate
- Throughput
- DynamoDB consumed capacity

## 🔒 Seguridad

### IAM Roles
- **ECS Task Role:** Permisos mínimos para DynamoDB
- **ECS Execution Role:** Permisos para ECR y CloudWatch

### Security Groups
- **ALB:** Puerto 80/443 desde Internet
- **ECS:** Puerto 8080 desde ALB únicamente
- **DynamoDB:** Acceso desde ECS únicamente

### Secrets Management
- **AWS Secrets Manager:** Credenciales sensibles
- **Parameter Store:** Configuración de aplicación

## 💰 Costos Estimados

### Desarrollo
- **ECS Fargate:** ~$20/mes
- **DynamoDB:** ~$5/mes
- **ALB:** ~$20/mes
- **Total:** ~$45/mes

### Producción
- **ECS Fargate:** ~$100/mes
- **DynamoDB:** ~$25/mes
- **ALB:** ~$20/mes
- **Total:** ~$145/mes

## 🛠️ Comandos Útiles

### Terraform
```bash
# Validar configuración
terraform validate

# Ver plan de cambios (con backend específico)
terraform plan -var-file=env/dev/terraform-dev.tfvars

# Aplicar cambios
terraform apply -var-file=env/dev/terraform-dev.tfvars

# Destruir infraestructura
terraform destroy -var-file=env/dev/terraform-dev.tfvars

# Ver estado actual
terraform state list

# Importar recurso existente
terraform import aws_dynamodb_table.franquicias business-franquicias-dev
```

### AWS CLI
```bash
# Ver servicios ECS
aws ecs list-services --cluster business-cluster-dev

# Ver logs de aplicación
aws logs filter-log-events --log-group-name /ecs/business-franquicias-dev

# Verificar tablas DynamoDB
aws dynamodb list-tables

# Ver estado del ALB
aws elbv2 describe-load-balancers --names business-franquicias-alb-dev

# Escalar servicio ECS
aws ecs update-service --cluster business-cluster-dev --service business-franquicias-dev --desired-count 2
```

### Makefile
```bash
# Ver todos los comandos disponibles
make help

# Desplegar ambiente completo
make deploy-dev
make deploy-qa
make deploy-pdn

# Destruir ambiente
make destroy-dev

# Validar configuración
make validate

# Ver estado de recursos
make status-dev
```

## 🔧 Troubleshooting

### Problemas Comunes

#### ECS Task no inicia
```bash
# Verificar logs
aws logs describe-log-groups --log-group-name-prefix /ecs/franquicias

# Verificar task definition
aws ecs describe-task-definition --task-definition franquicias-api
```

#### DynamoDB Access Denied
```bash
# Verificar IAM role
aws iam get-role --role-name ecs-task-role

# Verificar políticas
aws iam list-attached-role-policies --role-name ecs-task-role
```

## 📚 Referencias

- [Terraform AWS Provider](https://registry.terraform.io/providers/hashicorp/aws/latest/docs)
- [ECS Fargate Documentation](https://docs.aws.amazon.com/AmazonECS/latest/developerguide/AWS_Fargate.html)
- [DynamoDB Best Practices](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/best-practices.html)

---

**🔗 Enlaces:**
- [🏠 Documentación Principal](../README.md)
- [📖 Documentación API](../api/README.md)
