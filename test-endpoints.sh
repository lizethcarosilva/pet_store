#!/bin/bash

# Script para probar endpoints del Pet Store Backend
# Reemplaza 'tu-proyecto' con tu URL real de Railway

BASE_URL="https://tu-proyecto.railway.app"

echo "🧪 Probando endpoints del Pet Store Backend..."
echo "Base URL: $BASE_URL"
echo ""

# Test 1: Health Check
echo "1. 🔍 Health Check:"
curl -s "$BASE_URL/actuator/health" | jq '.' 2>/dev/null || curl -s "$BASE_URL/actuator/health"
echo ""
echo ""

# Test 2: API Docs
echo "2. 📚 API Documentation:"
curl -s "$BASE_URL/api-docs" | jq '.' 2>/dev/null || curl -s "$BASE_URL/api-docs"
echo ""
echo ""

# Test 3: Swagger UI (solo verificar que responde)
echo "3. 🌐 Swagger UI:"
curl -s -o /dev/null -w "%{http_code}" "$BASE_URL/swagger-ui.html"
echo " (200 = OK)"
echo ""

# Test 4: Endpoints principales
echo "4. 🐕 Endpoints de Mascotas:"
curl -s "$BASE_URL/api/pets" | jq '.' 2>/dev/null || curl -s "$BASE_URL/api/pets"
echo ""
echo ""

echo "5. 🛍️ Endpoints de Productos:"
curl -s "$BASE_URL/api/products" | jq '.' 2>/dev/null || curl -s "$BASE_URL/api/products"
echo ""
echo ""

echo "6. 🏥 Endpoints de Servicios:"
curl -s "$BASE_URL/api/services" | jq '.' 2>/dev/null || curl -s "$BASE_URL/api/services"
echo ""
echo ""

echo "✅ Pruebas completadas!"
echo ""
echo "🌐 Para ver la interfaz completa, ve a:"
echo "   $BASE_URL/swagger-ui.html"
