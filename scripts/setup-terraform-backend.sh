#!/bin/bash

# Script para crear bucket S3 para Terraform state
# Ejecutar una sola vez antes del primer despliegue

set -e

AWS_REGION="us-east-1"
BUCKET_NAME="nequi-franquicias-terraform-state"
DYNAMODB_TABLE="nequi-franquicias-terraform-locks"

echo "🚀 Configurando backend de Terraform..."

# Crear bucket S3 para Terraform state
echo "📦 Creando bucket S3: $BUCKET_NAME"
aws s3api create-bucket \
    --bucket $BUCKET_NAME \
    --region $AWS_REGION \
    --create-bucket-configuration LocationConstraint=$AWS_REGION 2>/dev/null || echo "Bucket ya existe"

# Habilitar versionado
echo "🔄 Habilitando versionado en bucket"
aws s3api put-bucket-versioning \
    --bucket $BUCKET_NAME \
    --versioning-configuration Status=Enabled

# Habilitar encriptación
echo "🔒 Habilitando encriptación en bucket"
aws s3api put-bucket-encryption \
    --bucket $BUCKET_NAME \
    --server-side-encryption-configuration '{
        "Rules": [
            {
                "ApplyServerSideEncryptionByDefault": {
                    "SSEAlgorithm": "AES256"
                }
            }
        ]
    }'

# Bloquear acceso público
echo "🛡️ Bloqueando acceso público"
aws s3api put-public-access-block \
    --bucket $BUCKET_NAME \
    --public-access-block-configuration \
    BlockPublicAcls=true,IgnorePublicAcls=true,BlockPublicPolicy=true,RestrictPublicBuckets=true

# Crear tabla DynamoDB para locks
echo "🔐 Creando tabla DynamoDB para locks: $DYNAMODB_TABLE"
aws dynamodb create-table \
    --table-name $DYNAMODB_TABLE \
    --attribute-definitions AttributeName=LockID,AttributeType=S \
    --key-schema AttributeName=LockID,KeyType=HASH \
    --billing-mode PAY_PER_REQUEST \
    --region $AWS_REGION 2>/dev/null || echo "Tabla ya existe"

echo "✅ Backend de Terraform configurado exitosamente!"
echo "📋 Información:"
echo "   Bucket S3: $BUCKET_NAME"
echo "   Tabla DynamoDB: $DYNAMODB_TABLE"
echo "   Región: $AWS_REGION"
echo ""
echo "🚀 Ahora puedes ejecutar terraform init en cualquier módulo"
