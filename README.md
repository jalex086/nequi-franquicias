# Sistema de Gestión de Franquicias - Nequi

## Descripción
Microservicio completo para la gestión de franquicias, sucursales y productos, desarrollado como prueba técnica para Nequi. Implementa Clean Architecture con Spring WebFlux, **esquema híbrido DynamoDB** y despliegue en AWS.

## 🚀 Despliegue Local - Guía Completa

### Prerrequisitos
- **Java 25** (obligatorio para Virtual Threads)
- **Docker y Docker Compose** (para LocalStack)
- **Git** (para clonar el repositorio)

### Pasos para Despliegue Local

#### 1. Clonar y Configurar
```bash
# Clonar el repositorio
git clone <repository-url>
cd prueba-tecnica

# Verificar que Docker esté corriendo
docker --version
docker-compose --version
```

#### 2. Iniciar Infraestructura Local
```bash
# Opción A: Con Make (recomendado)
make local-up

# Opción B: Con Docker Compose directamente
docker-compose up -d localstack

# Verificar que LocalStack esté corriendo
docker logs franquicias-localstack

# Verificar que las tablas se crearon correctamente
aws dynamodb list-tables --endpoint-url http://localhost:4566 --region us-east-1
```

#### 3. Ejecutar la Aplicación
```bash
# Opción A: Con Make
make run-local

# Opción B: Con Gradle directamente
cd api
./gradlew bootRun --args='--spring.profiles.active=local'

# Opción C: Con Docker
make docker-up
```

#### 4. Verificar Funcionamiento
```bash
# Health check
curl http://localhost:8080/actuator/health

# Crear una franquicia de prueba
curl -X POST http://localhost:8080/api/franchises \
  -H "Content-Type: application/json" \
  -d '{"name": "Franquicia Test"}'

# Listar franquicias
curl http://localhost:8080/api/franchises
```

### Comandos de Desarrollo

```bash
# Ver todos los comandos disponibles
make help

# Iniciar solo LocalStack
make local-up

# Ejecutar la aplicación
make run-local

# Ver logs de LocalStack
make local-logs

# Ejecutar pruebas
make local-test

# Compilar proyecto
make build

# Iniciar todo en Docker
make docker-up

# Ver estado de servicios
make status

# Limpiar todo
make clean
```

### Configuración Automática

El proyecto incluye **inicialización automática** de DynamoDB:
- ✅ **Script automático**: `scripts/localstack/init-dynamodb.sh`
- ✅ **Tablas pre-configuradas**: Estructura PK+SK compatible con AWS
- ✅ **Sin pasos manuales**: Solo ejecutar `docker-compose up`

### Estructura de Datos Local

La tabla DynamoDB local (`business-franquicias-local`) usa la misma estructura que AWS:
```json
{
  "PK": "FRANCHISE#123e4567-e89b-12d3-a456-426614174000",
  "SK": "METADATA",
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "name": "Franquicia McDonald's",
  "createdAt": "2026-01-12T20:00:00",
  "updatedAt": "2026-01-12T20:00:00"
}
```

### Solución de Problemas

#### LocalStack no inicia
```bash
# Verificar puertos disponibles
lsof -i :4566

# Reiniciar Docker
docker-compose down && docker system prune -f
docker-compose up -d localstack
```

#### Aplicación no conecta a DynamoDB
```bash
# Verificar variables de entorno
echo $AWS_ACCESS_KEY_ID  # debe ser 'test'
echo $AWS_SECRET_ACCESS_KEY  # debe ser 'test'

# Verificar endpoint en application-local.yml
cat api/applications/app-service/src/main/resources/application-local.yml
```

#### Tabla no existe
```bash
# Verificar tablas en LocalStack
aws dynamodb list-tables --endpoint-url http://localhost:4566 --region us-east-1

# Recrear tabla manualmente si es necesario
aws dynamodb describe-table --table-name business-franquicias-local --endpoint-url http://localhost:4566 --region us-east-1
```

## 📚 Documentación Técnica

### [📖 API Documentation](./api/README.md)
- Endpoints completos con ejemplos
- Arquitectura Clean detallada
- Patrones de diseño implementados

## 🏗️ Infrastructure & Deployment

### [🚀 Infrastructure Documentation](./infrastructure/README.md)
- Arquitectura AWS completa
- Terraform modules y configuración
- Pipelines CI/CD con GitHub Actions
- Monitoreo y observabilidad

### Entornos Disponibles
- **Development**: `business-franquicias-alb-dev-1817262481.us-east-1.elb.amazonaws.com`
- **QA**: Configurado para testing automatizado
- **Production**: Listo para despliegue con alta disponibilidad

## 🏛️ Arquitectura

### Vista General del Sistema
![Contexto del Sistema](docs/c1_contexto_franquicias.png)

### Comparación de Entornos
![Deployment Comparison](docs/deployment_comparison.png)

## 🛠️ Stack Tecnológico

### Backend
- **Java 25** - Virtual Threads, Pattern Matching, Records
- **Spring Boot 3.4** - WebFlux (Programación Reactiva)
- **Clean Architecture** - Hexagonal Architecture
- **DynamoDB Enhanced Client** - AWS SDK v2

### Infraestructura
- **AWS ECS Fargate** - Contenedores serverless
- **AWS Application Load Balancer** - Balanceador de carga
- **AWS DynamoDB** - Base de datos NoSQL
- **Terraform** - Infrastructure as Code
- **LocalStack** - AWS local para desarrollo

### DevOps
- **Docker** - Containerización
- **GitHub Actions** - CI/CD
- **Gradle** - Build automation
- **AWS CLI** - Gestión de recursos

## 🎯 Funcionalidades Implementadas

✅ **Gestión de Franquicias**
- Crear franquicia con validaciones
- Listar todas las franquicias
- Actualizar nombre de franquicia

✅ **Gestión de Sucursales**  
- Crear sucursal en franquicia existente
- Actualizar nombre de sucursal
- Validación de franquicia padre

✅ **Gestión de Productos**
- Crear producto en sucursal
- Eliminar producto de sucursal
- Actualizar nombre y stock de producto
- Consultar productos con mayor stock por franquicia
- Consultar producto con mayor stock por sucursal

✅ **Esquema Híbrido DynamoDB**
- **Estrategia EMBEDDED**: Productos <100 embebidos en sucursal
- **Estrategia SEPARATED**: Productos ≥100 en tabla separada
- **Transición automática**: Cambio transparente al alcanzar límite
- **Concurrencia robusta**: UpdateExpression atómica para productos embebidos
- **Monitoreo**: Campo `storageStrategy` indica estrategia actual

## 🧪 Pruebas del Esquema Híbrido

### Probar Estrategia EMBEDDED
```bash
# 1. Crear franquicia
curl -X POST http://localhost:8080/api/franchises \
  -H "Content-Type: application/json" \
  -d '{"name": "Test Híbrido"}'

# 2. Crear sucursal
curl -X POST http://localhost:8080/api/franchises/{franchise_id}/branches \
  -H "Content-Type: application/json" \
  -d '{"name": "Sucursal Test"}'

# 3. Crear producto (se embebe en sucursal)
curl -X POST http://localhost:8080/api/franchises/{franchise_id}/branches/{branch_id}/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Producto Embebido", "stock": 50}'

# 4. Verificar estrategia EMBEDDED
aws dynamodb get-item --table-name business-sucursales-local \
  --endpoint-url http://localhost:4566 --region us-east-1 \
  --key '{"PK":{"S":"BRANCH#{branch_id}"},"SK":{"S":"METADATA"}}'
```

### Probar Transición a SEPARATED
```bash
# Script automatizado para crear 100+ productos
./test-hybrid-strategy.sh

# Verificar cambio automático a estrategia SEPARATED
# - Productos 1-100: Embebidos en sucursal
# - Producto 101+: En tabla business-productos-local
```

### Verificar Concurrencia
```bash
# Crear múltiples productos simultáneamente
for i in {1..10}; do
  curl -X POST http://localhost:8080/api/franchises/{franchise_id}/branches/{branch_id}/products \
    -H "Content-Type: application/json" \
    -d "{\"name\": \"Producto Concurrente $i\", \"stock\": $i}" &
done
wait

# Verificar que todos los productos se guardaron correctamente
aws dynamodb get-item --table-name business-sucursales-local \
  --endpoint-url http://localhost:4566 --region us-east-1 \
  --key '{"PK":{"S":"BRANCH#{branch_id}"},"SK":{"S":"METADATA"}}' \
  --projection-expression "products" | jq '.Item.products.L | length'
```

## 🔧 Configuración de Desarrollo

### Variables de Entorno Locales
```bash
# Configuración automática en docker-compose.yml
AWS_ACCESS_KEY_ID=test
AWS_SECRET_ACCESS_KEY=test
AWS_REGION=us-east-1
AWS_ENDPOINT=http://localstack:4566
SPRING_PROFILES_ACTIVE=local
```

### Estructura del Proyecto
```
prueba-tecnica/
├── api/                           # Microservicio (Clean Architecture)
│   ├── domain/                    # Lógica de negocio
│   ├── infrastructure/            # Adaptadores (DynamoDB, Web)
│   └── applications/              # Configuración y main
├── infrastructure/                # Terraform (IaC)
│   ├── franquicias/              # Recursos específicos
│   └── transversal_dynamodb/     # Recursos compartidos
├── scripts/                       # Scripts de automatización
│   └── localstack/               # Inicialización automática
├── docs/                         # Documentación arquitectónica
└── docker-compose.yml            # Orquestación local
```

## 🚀 Despliegue en Diferentes Entornos

### Desarrollo Local
```bash
# Inicio rápido (un comando)
make local-up && make run-local

# Verificación
curl http://localhost:8080/actuator/health
```

### AWS (Staging/Production)
```bash
# Configurar backend de Terraform (una sola vez)
./scripts/setup-terraform-backend.sh

# Desplegar infraestructura
cd infrastructure/franquicias/api
terraform init
terraform plan -var-file="env/dev/terraform-dev.tfvars"
terraform apply

# Desplegar aplicación (GitHub Actions automático)
git tag v1.0.0
git push origin v1.0.0
```

## 👨💻 Información del Desarrollador

**Jonathan Alexander Mosquera Ramirez**

---

**🔗 Enlaces de Documentación:**
- [📖 API REST Documentation](./api/README.md)
- [🏗️ Infrastructure & Deployment](./infrastructure/README.md)
- [🎯 Data Model Design](./docs/data-model.md)
