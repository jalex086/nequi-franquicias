# API de Gestión de Franquicias

## Descripción
API REST reactiva para la gestión de franquicias, sucursales y productos. Implementada con Clean Architecture, Spring WebFlux y DynamoDB.

## Arquitectura
- **Framework:** Spring Boot 3 con WebFlux (Reactivo)
- **Patrón:** Clean Architecture
- **Base de Datos:** DynamoDB con GSI optimizado
- **Programación:** Funcional con RouterFunction

## Tecnologías
- Java 25 (Virtual Threads, Pattern Matching)
- Spring Boot WebFlux (Programación Reactiva)
- DynamoDB Enhanced Client
- Lombok
- Reactor Core

## Estructura del Proyecto
```
api/
├── applications/app-service/     # Aplicación principal
├── domain/
│   ├── model/                   # Entidades de dominio
│   └── usecase/                 # Casos de uso
└── infrastructure/
    ├── driven-adapters/
    │   └── dynamodb/           # Adaptadores DynamoDB
    └── entry-points/
        └── reactive-web/       # Controladores REST
```

## API Documentation

### Base URL
```
http://localhost:8080
```

### 🏢 Franquicias

#### Crear Franquicia
```http
POST /api/franchises
Content-Type: application/json

{
  "name": "Nombre de la franquicia"
}
```

**Respuesta exitosa (200):**
```json
{
  "id": "franchise-uuid",
  "name": "Nombre de la franquicia"
}
```

**Validaciones:**
- `name`: Requerido, entre 2 y 100 caracteres

#### Obtener Todas las Franquicias
```http
GET /api/franchises
```

**Respuesta exitosa (200):**
```json
[
  {
    "id": "franchise-uuid-1",
    "name": "Franquicia 1"
  },
  {
    "id": "franchise-uuid-2", 
    "name": "Franquicia 2"
  }
]
```

#### Actualizar Nombre de Franquicia
```http
PUT /api/franchises/{id}/name
Content-Type: application/json

{
  "name": "Nuevo nombre"
}
```

**Respuesta exitosa (200):**
```json
{
  "id": "franchise-uuid",
  "name": "Nuevo nombre"
}
```

### 🏪 Sucursales

#### Crear Sucursal
```http
POST /api/franchises/{franchiseId}/branches
Content-Type: application/json

{
  "name": "Nombre de la sucursal"
}
```

**Respuesta exitosa (200):**
```json
{
  "id": "branch-uuid",
  "name": "Nombre de la sucursal",
  "franchiseId": "franchise-uuid"
}
```

**Validaciones:**
- `franchiseId`: Debe existir en la base de datos
- `name`: Requerido, entre 2 y 100 caracteres

#### Actualizar Nombre de Sucursal
```http
PUT /api/branches/{id}/name
Content-Type: application/json

{
  "name": "Nuevo nombre de sucursal"
}
```

**Respuesta exitosa (200):**
```json
{
  "id": "branch-uuid",
  "name": "Nuevo nombre de sucursal",
  "franchiseId": "franchise-uuid"
}
```

### 📦 Productos

#### Crear Producto
```http
POST /api/franchises/{franchiseId}/branches/{branchId}/products
Content-Type: application/json

{
  "name": "Nombre del producto",
  "stock": 100
}
```

**Respuesta exitosa (200):**
```json
{
  "id": "product-uuid",
  "name": "Nombre del producto",
  "stock": 100,
  "branchId": "branch-uuid"
}
```

**Validaciones:**
- `franchiseId`: Debe existir
- `branchId`: Debe existir
- `name`: Requerido, entre 2 y 100 caracteres
- `stock`: Requerido, mayor o igual a 0

#### Obtener Productos por Sucursal
```http
GET /api/branches/{branchId}/products
```

**Respuesta exitosa (200):**
```json
[
  {
    "id": "product-uuid-1",
    "name": "Producto 1",
    "stock": 50,
    "branchId": "branch-uuid"
  },
  {
    "id": "product-uuid-2",
    "name": "Producto 2", 
    "stock": 75,
    "branchId": "branch-uuid"
  }
]
```

#### Eliminar Producto
```http
DELETE /api/products/{id}
```

**Respuesta exitosa (204):** Sin contenido

#### Actualizar Nombre de Producto
```http
PUT /api/products/{id}/name
Content-Type: application/json

{
  "name": "Nuevo nombre del producto"
}
```

**Respuesta exitosa (200):**
```json
{
  "id": "product-uuid",
  "name": "Nuevo nombre del producto",
  "stock": 100,
  "branchId": "branch-uuid"
}
```

#### Actualizar Stock de Producto
```http
PUT /api/products/{id}/stock
Content-Type: application/json

{
  "stock": 250
}
```

**Respuesta exitosa (200):**
```json
{
  "id": "product-uuid",
  "name": "Nombre del producto",
  "stock": 250,
  "branchId": "branch-uuid"
}
```

**Validaciones:**
- `stock`: Requerido, mayor o igual a 0

#### Obtener Productos con Mayor Stock por Franquicia
```http
GET /api/franchises/{franchiseId}/products/top-stock
```

**Respuesta exitosa (200):**
```json
[
  {
    "id": "product-uuid-1",
    "name": "Producto con más stock",
    "stock": 500,
    "branchId": "branch-uuid-1"
  },
  {
    "id": "product-uuid-2",
    "name": "Segundo producto",
    "stock": 300,
    "branchId": "branch-uuid-2"
  }
]
```

#### Obtener Producto con Mayor Stock por Sucursal
```http
GET /api/franchises/{franchiseId}/branches/top-stock-product
```

**Respuesta exitosa (200):**
```json
[
  {
    "id": "product-uuid-1",
    "name": "Producto A",
    "stock": 500,
    "branchId": "branch-uuid-1",
    "branchName": "Sucursal Centro"
  },
  {
    "id": "product-uuid-2",
    "name": "Producto B",
    "stock": 300,
    "branchId": "branch-uuid-2",
    "branchName": "Sucursal Norte"
  }
]
```

**Descripción:** Retorna el producto con mayor stock de cada sucursal de la franquicia, ordenados por stock descendente.

## Códigos de Estado HTTP

| Código | Descripción |
|--------|-------------|
| 200    | Operación exitosa |
| 204    | Sin contenido (eliminación exitosa) |
| 400    | Solicitud incorrecta (validaciones) |
| 404    | Recurso no encontrado |
| 500    | Error interno del servidor |

## Ejemplos de Uso

### Flujo Completo
```bash
# 1. Crear franquicia
curl -X POST http://localhost:8080/api/franchises \
  -H "Content-Type: application/json" \
  -d '{"name": "Mi Franquicia"}'

# 2. Crear sucursal
curl -X POST http://localhost:8080/api/franchises/{franchiseId}/branches \
  -H "Content-Type: application/json" \
  -d '{"name": "Sucursal Centro"}'

# 3. Crear producto
curl -X POST http://localhost:8080/api/franchises/{franchiseId}/branches/{branchId}/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Producto A", "stock": 100}'

# 4. Consultar productos de la sucursal
curl http://localhost:8080/api/branches/{branchId}/products

# 5. Ver productos con mayor stock
curl http://localhost:8080/api/franchises/{franchiseId}/products/top-stock

# 6. Actualizar nombre de franquicia
curl -X PUT http://localhost:8080/api/franchises/{franchiseId}/name \
  -H "Content-Type: application/json" \
  -d '{"name": "Nuevo Nombre Franquicia"}'

# 7. Actualizar nombre de sucursal
curl -X PUT http://localhost:8080/api/branches/{branchId}/name \
  -H "Content-Type: application/json" \
  -d '{"name": "Nuevo Nombre Sucursal"}'

# 8. Actualizar nombre de producto
curl -X PUT http://localhost:8080/api/products/{productId}/name \
  -H "Content-Type: application/json" \
  -d '{"name": "Nuevo Nombre Producto"}'

# 9. Actualizar stock de producto
curl -X PUT http://localhost:8080/api/products/{productId}/stock \
  -H "Content-Type: application/json" \
  -d '{"stock": 500}'

# 10. Ver producto con mayor stock por sucursal
curl http://localhost:8080/api/franchises/{franchiseId}/branches/top-stock-product
```

## Desarrollo

### Compilar
```bash
./gradlew build
```

### Ejecutar Tests
```bash
./gradlew test
```

### Ejecutar Localmente
```bash
# Desde el directorio raíz del proyecto
make run-local
```
**🔗 Enlaces:**
- [🏠 Documentación Principal](../README.md)
