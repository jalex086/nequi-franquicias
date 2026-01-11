# Franquicias API Infrastructure
## Nequi

### Descripción
Infraestructura como código para el microservicio de gestión de franquicias, sucursales y productos.

### Arquitectura
- **ECS Fargate**: Contenedores serverless para la API
- **DynamoDB**: Base de datos NoSQL con modelo híbrido
- **API Gateway**: Punto de entrada con VPC Link
- **CloudWatch**: Observabilidad y métricas

### Estructura del Proyecto

```
franquicias/
├── api/                    # Infraestructura principal del microservicio
│   ├── main.tf            # Módulos principales (ECS, ECR, API Gateway)
│   ├── locals.tf          # Variables locales y lógica de naming
│   ├── inputs.tf          # Variables de entrada
│   ├── data.tf            # Data sources (VPC, subnets, etc.)
│   ├── custom_inputs.tf   # Variables específicas del microservicio
│   ├── custom_data.tf     # Data sources específicos
│   ├── env/               # Configuración por ambiente
│   │   ├── dev/
│   │   ├── qa/
│   │   └── pdn/
│   └── .azure-pipelines/  # Variables de Azure DevOps
└── observability/         # Métricas, dashboards y alertas
    ├── main.tf
    ├── inputs.tf
    ├── outputs.tf
    └── env/
```

### Ambientes

| Ambiente | Descripción | Branch | Approval |
|----------|-------------|--------|----------|
| **dev** | Desarrollo | `develop` | Automático |
| **qa** | Quality Assurance | `main` | Automático |
| **pdn** | Producción | `tag v*` | Manual |

### Variables Corporativas

```hcl
country         = "co"                    # País
capacity        = "business"              # Capacidad de negocio
functionality   = "franquicias"           # Funcionalidad específica
owner           = "platform-team"         # Equipo propietario
serviceid       = "NEQ0001"               # ID de servicio asignado
confidentiality = "internal"             # Clasificación de datos
integrity       = "moderate"             # Nivel de integridad
availability    = "critical"             # Nivel de disponibilidad
```

### Módulos Utilizados

- `terraform_api_resources_Mod`: API Gateway resources con VPC Link
- `terraform_ecr_mod`: Elastic Container Registry
- `terraform_ecs_mod`: ECS Fargate service
- `terraform_dynamodb_mod`: DynamoDB tables con auto-scaling
- `terraform_observability_mod`: CloudWatch dashboards y alertas

### Deployment

#### Desarrollo Local
```bash
# Planificar cambios
make plan ENV=dev

# Aplicar cambios
make apply ENV=dev
```

### CI/CD Pipeline

El deployment se realiza automáticamente mediante GitHub Actions:

- **develop** → ambiente dev
- **main** → ambiente qa  
- **tags v*** → ambiente prod

#### Comandos Útiles

```bash
# Deployment completo
make deploy-all

# Por módulos
make deploy-dynamodb
make deploy-franquicias

# Validación
make validate
make plan

# Limpieza
make destroy
```

### Naming Convention

```hcl
# Recursos principales
resource_name = "${var.capacity}-${var.functionality}-${var.env}"
# Ejemplo: "business-franquicias-dev"

# Account name para ECS
account_name = "albecs${var.capacity}${var.country}${var.env}"
# Ejemplo: "albecsbusinesscodev"
```

### Seguridad

- **VPC**: Red privada con subnets públicas y privadas
- **Security Groups**: Acceso restringido por puerto y protocolo
- **IAM Roles**: Principio de menor privilegio para acceso a DynamoDB
- **Container Security**: Imágenes base actualizadas y escaneadas

### Monitoreo

- **CloudWatch Metrics**: Métricas de aplicación y infraestructura
- **CloudWatch Logs**: Logs centralizados con retention
- **CloudWatch Dashboards**: Visualización en tiempo real
- **CloudWatch Alarms**: Alertas automáticas

### Contacto

- **Owner**: Jonathan Alexander Mosquera Ramirez
- **Proyecto**: Sistema de Gestión de Franquicias
