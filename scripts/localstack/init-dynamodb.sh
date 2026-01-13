#!/bin/bash

echo "Inicializando tablas DynamoDB en LocalStack..."

# Eliminar tabla si existe con estructura incorrecta
echo "🗑️ Eliminando tabla existente si existe..."
awslocal dynamodb delete-table --table-name business-franquicias-local 2>/dev/null || echo "Tabla no existía"

# Esperar un momento para que se complete la eliminación
sleep 2

# Crear tabla de franquicias con estructura PK+SK
echo "📦 Creando tabla business-franquicias-local con estructura PK+SK..."
awslocal dynamodb create-table \
  --table-name business-franquicias-local \
  --attribute-definitions \
    AttributeName=PK,AttributeType=S \
    AttributeName=SK,AttributeType=S \
  --key-schema \
    AttributeName=PK,KeyType=HASH \
    AttributeName=SK,KeyType=RANGE \
  --billing-mode PAY_PER_REQUEST

echo "✅ Tabla business-franquicias-local creada exitosamente"
echo "🚀 Inicialización de DynamoDB completada"
