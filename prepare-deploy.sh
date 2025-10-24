#!/bin/bash

echo "🚀 Preparando proyecto para despliegue..."

# Limpiar y compilar
echo "📦 Compilando proyecto..."
./mvnw clean package -DskipTests

# Verificar que el JAR se creó
if [ -f "target/pet_store-0.0.1-SNAPSHOT.jar" ]; then
    echo "✅ JAR creado exitosamente"
    ls -la target/pet_store-0.0.1-SNAPSHOT.jar
else
    echo "❌ Error: JAR no encontrado"
    exit 1
fi

# Crear directorio para Docker si no existe
mkdir -p docker-build

# Copiar archivos necesarios para Docker
cp target/pet_store-0.0.1-SNAPSHOT.jar docker-build/
cp Dockerfile docker-build/

echo "🎉 Proyecto listo para despliegue!"
echo "📁 Archivos en docker-build/:"
ls -la docker-build/

echo ""
echo "🔧 Para probar localmente:"
echo "cd docker-build && docker build -t pet-store ."
echo "docker run -p 8090:8090 pet-store"
