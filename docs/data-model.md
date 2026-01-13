# MODELO DE DATOS - SINGLE TABLE DESIGN
## API Franquicias Nequi - DynamoDB Optimizado

### Estrategia Implementada: Single Table Design

**Principio**: Una sola tabla DynamoDB con patrón PK+SK para máxima eficiencia y costo optimizado.

**Tabla**: `business-franquicias-{env}`
- **Partition Key (PK)**: Identifica el tipo y contexto del registro
- **Sort Key (SK)**: Identifica el registro específico dentro del contexto
- **GSI1**: Índice secundario para consultas alternativas
- **GSI2**: Índice secundario adicional para búsquedas específicas

### Estructura de Datos Implementada

#### 1. Franquicias
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

#### 2. Sucursales
```json
{
  "PK": "123e4567-e89b-12d3-a456-426614174000", // franchiseId
  "SK": "branch-001", // branchId
  "id": "branch-001",
  "franchiseId": "123e4567-e89b-12d3-a456-426614174000",
  "name": "Sucursal Centro",
  "createdAt": "2026-01-12T20:00:00",
  "updatedAt": "2026-01-12T20:00:00",
  "GSI1PK": "BRANCH#branch-001",
  "GSI2PK": "FRANCHISE_BRANCHES#123e4567-e89b-12d3-a456-426614174000"
}
```

#### 3. Productos
```json
{
  "PK": "123e4567-e89b-12d3-a456-426614174000", // franchiseId
  "SK": "product-001", // productId
  "id": "product-001",
  "franchiseId": "123e4567-e89b-12d3-a456-426614174000",
  "branchId": "branch-001",
  "name": "Big Mac",
  "stock": 150,
  "createdAt": "2026-01-12T20:00:00",
  "updatedAt": "2026-01-12T20:00:00",
  "GSI1PK": "BRANCH#branch-001",
  "GSI2PK": "PRODUCT#product-001"
}
```

### Patrones de Acceso Optimizados

#### 1. Obtener Franquicia por ID
```
Table: Main
PK = "FRANCHISE#123e4567-e89b-12d3-a456-426614174000"
SK = "METADATA"
```

#### 2. Listar Todas las Franquicias
```
Table: Main
PK begins_with "FRANCHISE#"
SK = "METADATA"
```

#### 3. Obtener Sucursales de una Franquicia
```
Table: Main
PK = "123e4567-e89b-12d3-a456-426614174000"
SK begins_with "branch-"
```

#### 4. Obtener Productos de una Franquicia
```
Table: Main
PK = "123e4567-e89b-12d3-a456-426614174000"
SK begins_with "product-"
```

#### 5. Obtener Productos de una Sucursal Específica
```
Table: GSI1
GSI1PK = "BRANCH#branch-001"
```

#### 6. Buscar Producto por ID Global
```
Table: GSI2
GSI2PK = "PRODUCT#product-001"
```

### Ventajas del Single Table Design

#### **Performance**
- **Una sola consulta** para obtener franquicia completa
- **Latencia consistente** sub-10ms
- **Hot partitions evitadas** con distribución uniforme de PK

#### **Costo**
- **Una sola tabla** = menor costo base
- **Menos RCU/WCU** por consultas optimizadas
- **Storage optimizado** sin duplicación entre tablas

#### **Escalabilidad**
- **Auto-scaling** automático de DynamoDB
- **Distribución uniforme** de carga
- **GSI optimizados** para consultas específicas

### Implementación en Código

#### Entidades DynamoDB
```java
// FranchiseEntity
@DynamoDbPartitionKey: PK = "FRANCHISE#{id}"
@DynamoDbSortKey: SK = "METADATA"

// BranchEntity  
@DynamoDbPartitionKey: franchiseId
@DynamoDbSortKey: id (branchId)

// ProductEntity
@DynamoDbPartitionKey: franchiseId  
@DynamoDbSortKey: id (productId)
```

#### Repositorios Optimizados
```java
// Obtener franquicia completa
public Mono<Franchise> findById(String id) {
    return enhancedClient.table("business-franquicias", FranchiseEntity.class)
        .getItem(Key.builder()
            .partitionValue("FRANCHISE#" + id)
            .sortValue("METADATA")
            .build());
}

// Obtener sucursales de franquicia
public Flux<Branch> findBranchesByFranchiseId(String franchiseId) {
    return enhancedClient.table("business-franquicias", BranchEntity.class)
        .query(QueryConditional.keyEqualTo(Key.builder()
            .partitionValue(franchiseId)
            .build()))
        .items()
        .filter(item -> item.getId().startsWith("branch-"));
}
```

### Estimación de Costos Real

#### Volumen Estimado (Escala Colombia)
- **Franquicias**: ~600 registros
- **Sucursales**: ~15,000 registros (25 promedio por franquicia)
- **Productos**: ~150,000 registros (10 promedio por sucursal)
- **Total items**: ~165,600 registros

#### Costo Mensual DynamoDB (On-Demand)
- **Storage**: 165K items × 1KB promedio = ~165MB = **$0.04/mes**
- **Read**: 1M requests/mes = **$0.25/mes**
- **Write**: 100K requests/mes = **$0.125/mes**
- **Total estimado**: **~$0.42/mes** (desarrollo) | **~$4.20/mes** (producción 10x tráfico)

### Consultas de Negocio Implementadas

#### 1. Productos con Mayor Stock por Franquicia
```java
// Query: PK = franchiseId, SK begins_with "product-"
// Sort: Por stock descendente en aplicación
// Limit: Top 10
```

#### 2. Producto con Mayor Stock por Sucursal
```java  
// Query GSI1: GSI1PK = "BRANCH#{branchId}"
// Sort: Por stock descendente
// Limit: 1 por sucursal
```

#### 3. Actualización de Stock
```java
// Update: PK = franchiseId, SK = productId
// Atomic: UpdateExpression para stock
```

### Conclusión

La implementación con **Single Table Design** ofrece:

✅ **Simplicidad** - Una tabla optimizada para todos los casos de uso
✅ **Economía** - Costo mínimo con máximo rendimiento  
✅ **Performance** - Consultas optimizadas con latencia consistente
✅ **Escalabilidad** - Patrón probado para aplicaciones de gran escala
✅ **Mantenibilidad** - Menor complejidad operacional

