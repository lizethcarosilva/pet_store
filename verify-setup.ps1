# ================================================
# SCRIPT DE VERIFICACIÓN DE CONFIGURACIÓN (PowerShell)
# ================================================
# Este script verifica que todos los archivos necesarios
# estén presentes antes del deploy a Railway

Write-Host "🔍 VERIFICANDO CONFIGURACIÓN DE RAILWAY..." -ForegroundColor Cyan
Write-Host ""

# Contadores
$script:ERRORS = 0
$script:WARNINGS = 0

# Función para verificar archivo
function Check-File {
    param([string]$FilePath)
    
    if (Test-Path $FilePath) {
        Write-Host "✓ $FilePath" -ForegroundColor Green
    } else {
        Write-Host "✗ $FilePath - FALTA" -ForegroundColor Red
        $script:ERRORS++
    }
}

# Función para verificar directorio
function Check-Dir {
    param([string]$DirPath)
    
    if (Test-Path $DirPath -PathType Container) {
        Write-Host "✓ $DirPath/" -ForegroundColor Green
    } else {
        Write-Host "✗ $DirPath/ - FALTA" -ForegroundColor Red
        $script:ERRORS++
    }
}

# Función para advertencia
function Warn {
    param([string]$Message)
    
    Write-Host "⚠ $Message" -ForegroundColor Yellow
    $script:WARNINGS++
}

Write-Host "=== ARCHIVOS DOCKER ===" -ForegroundColor Cyan
Check-File "Dockerfile"
Check-File ".dockerignore"
Check-File "docker-compose.yml"
Write-Host ""

Write-Host "=== CONFIGURACIÓN RAILWAY ===" -ForegroundColor Cyan
Check-File "railway.json"
Check-File "railway.toml"
Write-Host ""

Write-Host "=== CONFIGURACIÓN SPRING ===" -ForegroundColor Cyan
Check-File "pom.xml"
Check-File "src/main/resources/application.properties"
Check-File "src/main/resources/application-prod.properties"
Check-File "src/main/resources/application-railway.properties"
Write-Host ""

Write-Host "=== DOCUMENTACIÓN ===" -ForegroundColor Cyan
Check-File "RAILWAY_SETUP_GUIDE.md"
Check-File "QUICK_START.md"
Check-File "LOCAL_DEVELOPMENT.md"
Check-File "ENV_VARIABLES_TEMPLATE.md"
Check-File "RAILWAY_DEPLOYMENT_SUMMARY.md"
Check-File "README_DEPLOYMENT.md"
Write-Host ""

Write-Host "=== GIT ===" -ForegroundColor Cyan
Check-File ".gitignore"

# Verificar si .env está en gitignore
if (Test-Path ".gitignore") {
    $gitignoreContent = Get-Content ".gitignore" -Raw
    if ($gitignoreContent -match "^\.env$") {
        Write-Host "✓ .env está en .gitignore" -ForegroundColor Green
    } else {
        Warn ".env debería estar en .gitignore"
    }
}
Write-Host ""

Write-Host "=== DIRECTORIOS IMPORTANTES ===" -ForegroundColor Cyan
Check-Dir "src/main/java/com/cipasuno/petstore/pet_store"
Check-Dir "src/main/resources"
Write-Host ""

Write-Host "=== VERIFICACIÓN DE GIT ===" -ForegroundColor Cyan
try {
    $gitDir = git rev-parse --git-dir 2>$null
    if ($gitDir) {
        Write-Host "✓ Repositorio Git inicializado" -ForegroundColor Green
        
        # Verificar remote
        try {
            $remote = git remote get-url origin 2>$null
            if ($remote) {
                Write-Host "✓ Remote configurado: $remote" -ForegroundColor Green
            } else {
                Warn "No hay remote configurado (git remote add origin URL)"
            }
        } catch {
            Warn "No hay remote configurado (git remote add origin URL)"
        }
        
        # Verificar branch
        $branch = git branch --show-current
        if ($branch -eq "main" -or $branch -eq "master") {
            Write-Host "✓ Branch actual: $branch" -ForegroundColor Green
        } else {
            Warn "Branch actual: $branch (Railway usará 'main' por defecto)"
        }
        
        # Verificar archivos sin commit
        $status = git status --porcelain
        if ($status) {
            Warn "Hay archivos sin commit"
            Write-Host "  Ejecuta: git add . ; git commit -m 'feat: Railway configuration'" -ForegroundColor Yellow
        } else {
            Write-Host "✓ No hay cambios sin commit" -ForegroundColor Green
        }
    } else {
        Write-Host "✗ No es un repositorio Git" -ForegroundColor Red
        $script:ERRORS++
    }
} catch {
    Write-Host "✗ No es un repositorio Git" -ForegroundColor Red
    $script:ERRORS++
}
Write-Host ""

Write-Host "=== VERIFICACIÓN DE RAILWAY.TOML ===" -ForegroundColor Cyan
if (Test-Path "railway.toml") {
    $tomlContent = Get-Content "railway.toml" -Raw
    
    if ($tomlContent -match 'builder = "DOCKERFILE"') {
        Write-Host "✓ Builder configurado como DOCKERFILE" -ForegroundColor Green
    } else {
        Write-Host "✗ Builder no configurado correctamente" -ForegroundColor Red
        $script:ERRORS++
    }
    
    if ($tomlContent -match 'SPRING_PROFILES_ACTIVE = "railway"') {
        Write-Host "✓ Spring profile configurado para Railway" -ForegroundColor Green
    } else {
        Warn "Spring profile no configurado en railway.toml"
    }
}
Write-Host ""

Write-Host "=== VERIFICACIÓN DE DOCKERFILE ===" -ForegroundColor Cyan
if (Test-Path "Dockerfile") {
    $dockerContent = Get-Content "Dockerfile" -Raw
    
    if ($dockerContent -match "FROM maven") {
        Write-Host "✓ Dockerfile usa Maven para build" -ForegroundColor Green
    }
    
    if ($dockerContent -match "FROM eclipse-temurin:17") {
        Write-Host "✓ Dockerfile usa Java 17" -ForegroundColor Green
    }
    
    if ($dockerContent -match "HEALTHCHECK") {
        Write-Host "✓ Healthcheck configurado" -ForegroundColor Green
    }
}
Write-Host ""

Write-Host "=== RECOMENDACIONES ===" -ForegroundColor Cyan
Write-Host ""
Write-Host "📋 Archivos locales opcionales:"
if (-not (Test-Path ".env")) {
    Write-Host "  • Crear .env para desarrollo local"
    Write-Host "    Ver plantilla en ENV_VARIABLES_TEMPLATE.md"
}
Write-Host ""

Write-Host "🔐 Seguridad:"
Write-Host "  • Asegúrate de cambiar JWT_SECRET en Railway"
Write-Host "  • No commitear archivos .env"
Write-Host "  • Usar contraseñas seguras en producción"
Write-Host ""

Write-Host "================================================" -ForegroundColor Cyan
Write-Host "RESUMEN" -ForegroundColor Cyan
Write-Host "================================================" -ForegroundColor Cyan

if ($script:ERRORS -eq 0) {
    Write-Host "✓ CONFIGURACIÓN CORRECTA" -ForegroundColor Green
    Write-Host ""
    Write-Host "Próximos pasos:"
    Write-Host "1. git add ."
    Write-Host "2. git commit -m 'feat: Railway configuration'"
    Write-Host "3. git push origin main"
    Write-Host "4. Configurar Railway (ver QUICK_START.md)"
} else {
    Write-Host "✗ FALTAN $($script:ERRORS) ARCHIVO(S) REQUERIDO(S)" -ForegroundColor Red
    Write-Host ""
    Write-Host "Por favor, crea los archivos faltantes antes de continuar."
}

if ($script:WARNINGS -gt 0) {
    Write-Host "⚠ HAY $($script:WARNINGS) ADVERTENCIA(S)" -ForegroundColor Yellow
    Write-Host "Revisa las advertencias arriba."
}

Write-Host ""
Write-Host "📚 Documentación: RAILWAY_SETUP_GUIDE.md"
Write-Host "⚡ Inicio rápido: QUICK_START.md"
Write-Host ""

exit $script:ERRORS

