# Sistema de Gestión de Franquicias - Nequi

## Descripción
Microservicio completo para la gestión de franquicias, sucursales y productos, desarrollado como prueba técnica para Nequi. Implementa Clean Architecture con Spring WebFlux y despliegue en AWS.

## 🚀 Inicio Rápido

### Prerrequisitos
- Java 25
- Docker y Docker Compose
- Make

### Configuración Local

1. **Clonar el repositorio**
```bash
git clone <repository-url>
cd prueba-tecnica
```

2. **Iniciar servicios locales (DynamoDB)**
```bash
make local-up
```

3. **Ejecutar la aplicación**
```bash
make run-local
```

4. **Verificar que funciona**
```bash
curl http://localhost:8080/api/franchises
```

### Comandos Disponibles

| Comando | Descripción |
|---------|-------------|
| `make local-up` | Inicia LocalStack (DynamoDB) |
| `make local-down` | Detiene servicios locales |
| `make run-local` | Ejecuta la API en modo desarrollo |
| `make build` | Compila el proyecto |
| `make test` | Ejecuta las pruebas |

## 📚 Documentación

### [📖 API Documentation](./api/README.md)
Documentación completa de la API REST:
- Endpoints disponibles
- Ejemplos de request/response
- Códigos de estado
- Arquitectura Clean
- Detalles técnicos

### [🏗️ Infrastructure Documentation](./infrastructure/README.md)
Documentación de infraestructura y despliegue:
- Terraform configurations
- AWS deployment
- CI/CD pipelines
- Arquitectura cloud

## 🏛️ Arquitectura General

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Frontend/     │    │   API Gateway   │    │   Microservice  │
│   Mobile App    │───▶│   (AWS ALB)     │───▶│   (ECS Fargate) │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                                                       │
                                              ┌─────────────────┐
                                              │   DynamoDB      │
                                              │   (NoSQL)       │
                                              └─────────────────┘
```

## 🛠️ Tecnologías

### Backend
- **Java 25** - Virtual Threads, Pattern Matching
- **Spring Boot 3** - WebFlux (Reactivo)
- **Clean Architecture** - Separación de responsabilidades
- **DynamoDB** - Base de datos NoSQL escalable

### Infraestructura
- **AWS ECS Fargate** - Contenedores serverless
- **AWS DynamoDB** - Base de datos managed
- **Terraform** - Infrastructure as Code
- **GitHub Actions** - CI/CD

### Desarrollo
- **LocalStack** - AWS local para desarrollo
- **Docker** - Containerización
- **Gradle** - Build tool

## 🎯 Funcionalidades

✅ **Gestión de Franquicias**
- Crear franquicia
- Listar franquicias
- Actualizar nombre de franquicia

✅ **Gestión de Sucursales**
- Crear sucursal en franquicia
- Actualizar nombre de sucursal
- Validación de existencia de franquicia

✅ **Gestión de Productos**
- Crear producto en sucursal
- Listar productos por sucursal
- Eliminar producto
- Actualizar nombre de producto
- Actualizar stock de producto
- Consultar productos con mayor stock por franquicia
- Consultar producto con mayor stock por sucursal

✅ **Características Técnicas**
- API REST reactiva
- Validaciones completas
- Manejo de errores profesional
- Optimización con GSI en DynamoDB
- Documentación completa

## 🔧 Desarrollo

### Estructura del Proyecto
```
├── api/                    # Microservicio (Clean Architecture)
├── infrastructure/         # Infraestructura como código (Terraform)
├── .github/workflows/      # Pipelines de CI/CD
├── scripts/               # Scripts de utilidad
└── docs/                  # Documentación adicional
```

### Variables de Entorno
```bash
# Desarrollo local
AWS_ACCESS_KEY_ID=test
AWS_SECRET_ACCESS_KEY=test
AWS_DEFAULT_REGION=us-east-1
```

## 🚀 Despliegue

### Local
```bash
make local-up && make run-local
```

### AWS (Producción)
Ver [Infrastructure Documentation](./infrastructure/README.md) para detalles de despliegue en AWS.

## 👨‍💻 Desarrollador
**Jonathan Alexander Mosquera Ramirez**

---

**🔗 Enlaces Rápidos:**
- [📖 Documentación API](./api/README.md)
- [🏗️ Documentación Infraestructura](./infrastructure/README.md)
- [🐛 Reportar Issues](https://github.com/jalex086/nequi-franquicias/issues)
