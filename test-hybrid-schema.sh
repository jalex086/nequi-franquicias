#!/bin/bash

echo "🧪 PRUEBA COMPLETA DEL ESQUEMA HÍBRIDO DYNAMODB"
echo "=============================================="
echo "Este script demuestra el funcionamiento del esquema híbrido:"
echo "- EMBEDDED: Sucursales con <100 productos"
echo "- SEPARATED: Sucursales con ≥100 productos"
echo "- Cambio automático sin intervención manual"
echo ""

# 1. Crear Franquicia McDonald's
echo "1️⃣ Creando Franquicia McDonald's..."
MCDONALDS_RESPONSE=$(curl -s -X POST http://localhost:8080/api/franchises \
  -H "Content-Type: application/json" \
  -d '{"name": "McDonalds Colombia"}')
MCDONALDS_ID=$(echo $MCDONALDS_RESPONSE | jq -r '.id')
echo "✅ McDonald's ID: $MCDONALDS_ID"

# 2. Crear Franquicia Subway
echo "2️⃣ Creando Franquicia Subway..."
SUBWAY_RESPONSE=$(curl -s -X POST http://localhost:8080/api/franchises \
  -H "Content-Type: application/json" \
  -d '{"name": "Subway Colombia"}')
SUBWAY_ID=$(echo $SUBWAY_RESPONSE | jq -r '.id')
echo "✅ Subway ID: $SUBWAY_ID"

# 3. Crear sucursales McDonald's
echo "3️⃣ Creando sucursales McDonald's..."

# Sucursal pequeña (< 100 productos)
MC_SMALL_RESPONSE=$(curl -s -X POST http://localhost:8080/api/franchises/$MCDONALDS_ID/branches \
  -H "Content-Type: application/json" \
  -d '{"name": "McDonald'\''s Centro Comercial"}')
MC_SMALL_ID=$(echo $MC_SMALL_RESPONSE | jq -r '.id')
echo "✅ McDonald's Centro Comercial ID: $MC_SMALL_ID"

# Sucursal grande (>= 100 productos)
MC_LARGE_RESPONSE=$(curl -s -X POST http://localhost:8080/api/franchises/$MCDONALDS_ID/branches \
  -H "Content-Type: application/json" \
  -d '{"name": "McDonald'\''s Aeropuerto"}')
MC_LARGE_ID=$(echo $MC_LARGE_RESPONSE | jq -r '.id')
echo "✅ McDonald's Aeropuerto ID: $MC_LARGE_ID"

# 4. Crear sucursales Subway
echo "4️⃣ Creando sucursales Subway..."

# Sucursal mediana (< 100 productos)
SUBWAY_MED_RESPONSE=$(curl -s -X POST http://localhost:8080/api/franchises/$SUBWAY_ID/branches \
  -H "Content-Type: application/json" \
  -d '{"name": "Subway Universidad"}')
SUBWAY_MED_ID=$(echo $SUBWAY_MED_RESPONSE | jq -r '.id')
echo "✅ Subway Universidad ID: $SUBWAY_MED_ID"

# Sucursal grande (>= 100 productos)
SUBWAY_LARGE_RESPONSE=$(curl -s -X POST http://localhost:8080/api/franchises/$SUBWAY_ID/branches \
  -H "Content-Type: application/json" \
  -d '{"name": "Subway Plaza Mayor"}')
SUBWAY_LARGE_ID=$(echo $SUBWAY_LARGE_RESPONSE | jq -r '.id')
echo "✅ Subway Plaza Mayor ID: $SUBWAY_LARGE_ID"

# 5. Crear productos McDonald's Centro Comercial (50 productos - EMBEDDED)
echo "5️⃣ Creando 50 productos en McDonald's Centro Comercial (EMBEDDED)..."
for i in {1..50}; do
  curl -s -X POST http://localhost:8080/api/franchises/$MCDONALDS_ID/branches/$MC_SMALL_ID/products \
    -H "Content-Type: application/json" \
    -d "{\"name\": \"Big Mac $i\", \"stock\": $((i * 10))}" > /dev/null
done
echo "✅ 50 productos creados"

# 6. Crear productos McDonald's Aeropuerto (120 productos - SEPARATED)
echo "6️⃣ Creando 120 productos en McDonald's Aeropuerto (SEPARATED)..."
for i in {1..120}; do
  curl -s -X POST http://localhost:8080/api/franchises/$MCDONALDS_ID/branches/$MC_LARGE_ID/products \
    -H "Content-Type: application/json" \
    -d "{\"name\": \"McFlurry $i\", \"stock\": $((i * 5))}" > /dev/null
done
echo "✅ 120 productos creados"

# 7. Crear productos Subway Universidad (80 productos - EMBEDDED)
echo "7️⃣ Creando 80 productos en Subway Universidad (EMBEDDED)..."
for i in {1..80}; do
  curl -s -X POST http://localhost:8080/api/franchises/$SUBWAY_ID/branches/$SUBWAY_MED_ID/products \
    -H "Content-Type: application/json" \
    -d "{\"name\": \"Sub $i\", \"stock\": $((i * 8))}" > /dev/null
done
echo "✅ 80 productos creados"

# 8. Crear productos Subway Plaza Mayor (150 productos - SEPARATED)
echo "8️⃣ Creando 150 productos en Subway Plaza Mayor (SEPARATED)..."
for i in {1..150}; do
  curl -s -X POST http://localhost:8080/api/franchises/$SUBWAY_ID/branches/$SUBWAY_LARGE_ID/products \
    -H "Content-Type: application/json" \
    -d "{\"name\": \"Cookie $i\", \"stock\": $((i * 3))}" > /dev/null
done
echo "✅ 150 productos creados"

# 9. Verificar estrategias de almacenamiento
echo "9️⃣ Verificando estrategias de almacenamiento..."
echo "McDonald's Centro Comercial (50 productos):"
aws dynamodb get-item --table-name business-sucursales-local --endpoint-url http://localhost:4566 --region us-east-1 --key "{\"PK\":{\"S\":\"BRANCH#$MC_SMALL_ID\"},\"SK\":{\"S\":\"METADATA\"}}" --query 'Item.storageStrategy.S' --output text

echo "McDonald's Aeropuerto (120 productos):"
aws dynamodb get-item --table-name business-sucursales-local --endpoint-url http://localhost:4566 --region us-east-1 --key "{\"PK\":{\"S\":\"BRANCH#$MC_LARGE_ID\"},\"SK\":{\"S\":\"METADATA\"}}" --query 'Item.storageStrategy.S' --output text

echo "Subway Universidad (80 productos):"
aws dynamodb get-item --table-name business-sucursales-local --endpoint-url http://localhost:4566 --region us-east-1 --key "{\"PK\":{\"S\":\"BRANCH#$SUBWAY_MED_ID\"},\"SK\":{\"S\":\"METADATA\"}}" --query 'Item.storageStrategy.S' --output text

echo "Subway Plaza Mayor (150 productos):"
aws dynamodb get-item --table-name business-sucursales-local --endpoint-url http://localhost:4566 --region us-east-1 --key "{\"PK\":{\"S\":\"BRANCH#$SUBWAY_LARGE_ID\"},\"SK\":{\"S\":\"METADATA\"}}" --query 'Item.storageStrategy.S' --output text

# 10. Mostrar IDs para consultas
echo ""
echo "🔍 IDs PARA CONSULTAS:"
echo "McDonald's ID: $MCDONALDS_ID"
echo "Subway ID: $SUBWAY_ID"
echo ""
echo "📊 CONSULTAS DE PRUEBA:"
echo "Top stock McDonald's: curl http://localhost:8080/api/franchises/$MCDONALDS_ID/products/top-stock"
echo "Top stock Subway: curl http://localhost:8080/api/franchises/$SUBWAY_ID/products/top-stock"
echo "Top por sucursal McDonald's: curl http://localhost:8080/api/franchises/$MCDONALDS_ID/branches/top-stock-products"
echo "Top por sucursal Subway: curl http://localhost:8080/api/franchises/$SUBWAY_ID/branches/top-stock-products"

echo ""
echo "🎯 PRUEBA COMPLETA FINALIZADA"
echo "=============================================="
echo "✅ Esquema híbrido funcionando correctamente:"
echo "   - McDonald's Centro Comercial: EMBEDDED (50 productos)"
echo "   - McDonald's Aeropuerto: SEPARATED (120 productos)"
echo "   - Subway Universidad: EMBEDDED (80 productos)"
echo "   - Subway Plaza Mayor: SEPARATED (150 productos)"
echo ""
echo "🔍 Usa los comandos de consulta mostrados arriba para verificar los resultados"
