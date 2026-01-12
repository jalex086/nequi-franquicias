#!/bin/bash

echo "🚀 Inicializando tablas DynamoDB en LocalStack..."

# Configurar AWS CLI para LocalStack
export AWS_ACCESS_KEY_ID=test
export AWS_SECRET_ACCESS_KEY=test
export AWS_DEFAULT_REGION=us-east-1

# Endpoint de LocalStack
ENDPOINT="http://localhost:4566"

# Función para eliminar tabla si existe
delete_table_if_exists() {
    local table_name=$1
    echo "🗑️ Eliminando tabla existente: $table_name"
    aws dynamodb delete-table --endpoint-url $ENDPOINT --table-name $table_name 2>/dev/null || true
    sleep 2
}
ENDPOINT="http://localhost:4566"

# Función para crear tabla
create_table() {
    local table_name=$1
    local hash_key=$2
    local range_key=$3
    
    echo "📋 Creando tabla: $table_name"
    
    if [ -z "$range_key" ]; then
        # Tabla solo con hash key
        aws dynamodb create-table \
            --endpoint-url $ENDPOINT \
            --table-name $table_name \
            --attribute-definitions AttributeName=$hash_key,AttributeType=S \
            --key-schema AttributeName=$hash_key,KeyType=HASH \
            --billing-mode PAY_PER_REQUEST
    else
        # Tabla con hash key y range key
        aws dynamodb create-table \
            --endpoint-url $ENDPOINT \
            --table-name $table_name \
            --attribute-definitions \
                AttributeName=$hash_key,AttributeType=S \
                AttributeName=$range_key,AttributeType=S \
                AttributeName=GSI1PK,AttributeType=S \
                AttributeName=GSI2PK,AttributeType=S \
            --key-schema \
                AttributeName=$hash_key,KeyType=HASH \
                AttributeName=$range_key,KeyType=RANGE \
            --global-secondary-indexes \
                'IndexName=GSI1,KeySchema=[{AttributeName=GSI1PK,KeyType=HASH}],Projection={ProjectionType=ALL}' \
                'IndexName=GSI2,KeySchema=[{AttributeName=GSI2PK,KeyType=HASH}],Projection={ProjectionType=ALL}' \
            --billing-mode PAY_PER_REQUEST
    fi
}

# Eliminar tablas existentes
delete_table_if_exists "business-franquicias-local"
delete_table_if_exists "business-sucursales-local"
delete_table_if_exists "business-productos-local"

# Crear tablas
create_table "business-franquicias-local" "id"
create_table "business-sucursales-local" "franchiseId" "id"  
create_table "business-productos-local" "franchiseId" "id"

echo "✅ Tablas DynamoDB creadas exitosamente!"

# Listar tablas creadas
echo "📋 Tablas disponibles:"
aws dynamodb list-tables --endpoint-url $ENDPOINT --output table
