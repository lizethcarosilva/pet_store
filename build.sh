#!/bin/bash

# Script de build para Railway
echo "🚀 Iniciando build para Railway..."

# Limpiar y compilar
echo "📦 Compilando proyecto..."
./mvnw clean package -DskipTests

# Verificar que el JAR se creó
if [ -f "target/pet_store-0.0.1-SNAPSHOT.jar" ]; then
    echo "✅ Build exitoso - JAR creado"
    ls -la target/pet_store-0.0.1-SNAPSHOT.jar
else
    echo "❌ Error: JAR no encontrado"
    exit 1
fi

echo "🎉 Build completado exitosamente"
