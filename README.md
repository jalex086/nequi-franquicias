# Sistema de Gestión de Franquicias - Nequi

## Descripción
Microservicio para la gestión completa de franquicias, sucursales y productos, desarrollado como prueba técnica para Nequi.

## Arquitectura
- **API**: Clean Architecture con Java 25 + Spring WebFlux
- **Infraestructura**: Terraform + AWS (ECS Fargate + DynamoDB)
- **CI/CD**: GitHub Actions con deployment automático

## Estructura del Proyecto
```
├── api/                    # Microservicio Clean Architecture
├── infrastructure/         # Infraestructura como código (Terraform)
└── .github/workflows/      # Pipelines de CI/CD
```

## Tecnologías
- Java 25 (Virtual Threads, Pattern Matching)
- Spring Boot WebFlux (Programación Reactiva)
- DynamoDB (Modelo híbrido escalable)
- Terraform (Infrastructure as Code)
- GitHub Actions (CI/CD)

## Desarrollador
**Jonathan Alexander Mosquera Ramirez**
