# Script de PowerShell para preparar el despliegue
Write-Host "🚀 Preparando proyecto para despliegue..." -ForegroundColor Green

# Limpiar y compilar
Write-Host "📦 Compilando proyecto..." -ForegroundColor Yellow
./mvnw clean package -DskipTests

# Verificar que el JAR se creó
if (Test-Path "target/pet_store-0.0.1-SNAPSHOT.jar") {
    Write-Host "✅ JAR creado exitosamente" -ForegroundColor Green
    Get-ChildItem target/pet_store-0.0.1-SNAPSHOT.jar | Format-Table Name, Length, LastWriteTime
} else {
    Write-Host "❌ Error: JAR no encontrado" -ForegroundColor Red
    exit 1
}

# Crear directorio para Docker si no existe
if (!(Test-Path "docker-build")) {
    New-Item -ItemType Directory -Path "docker-build"
}

# Copiar archivos necesarios para Docker
Copy-Item "target/pet_store-0.0.1-SNAPSHOT.jar" "docker-build/"
Copy-Item "Dockerfile" "docker-build/"

Write-Host "🎉 Proyecto listo para despliegue!" -ForegroundColor Green
Write-Host "📁 Archivos en docker-build/:" -ForegroundColor Cyan
Get-ChildItem docker-build/ | Format-Table Name, Length, LastWriteTime

Write-Host ""
Write-Host "🔧 Para probar localmente:" -ForegroundColor Yellow
Write-Host "cd docker-build" -ForegroundColor White
Write-Host "docker build -t pet-store ." -ForegroundColor White
Write-Host "docker run -p 8090:8090 pet-store" -ForegroundColor White
