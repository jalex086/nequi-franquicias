#!/bin/bash

FRANCHISE_ID="f9bdb67b-12b2-48cd-91c4-4eac5171147c"
BRANCH_ID="4aa60bfc-5e1a-41c9-8d63-0d36a7d0e221"

echo "🚀 Creando 100 productos para probar estrategia SEPARATED..."

for i in {2..100}; do
    echo "Creando producto $i/100..."
    curl -s -X POST "http://localhost:8080/api/franchises/$FRANCHISE_ID/branches/$BRANCH_ID/products" \
      -H "Content-Type: application/json" \
      -d "{\"name\": \"Producto $i\", \"stock\": $((RANDOM % 100 + 1))}" > /dev/null
    
    if [ $((i % 10)) -eq 0 ]; then
        echo "✅ Creados $i productos..."
    fi
done

echo "🎯 Creando producto 101 (debería usar estrategia SEPARATED)..."
RESPONSE=$(curl -s -X POST "http://localhost:8080/api/franchises/$FRANCHISE_ID/branches/$BRANCH_ID/products" \
  -H "Content-Type: application/json" \
  -d '{"name": "Producto 101 - SEPARATED", "stock": 999}')

echo "Respuesta: $RESPONSE"
echo "✅ ¡Completado! Verifica las tablas para confirmar el cambio de estrategia."
