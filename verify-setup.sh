#!/bin/bash

# ================================================
# SCRIPT DE VERIFICACIÓN DE CONFIGURACIÓN
# ================================================
# Este script verifica que todos los archivos necesarios
# estén presentes antes del deploy a Railway

echo "🔍 VERIFICANDO CONFIGURACIÓN DE RAILWAY..."
echo ""

# Colores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Contador de errores
ERRORS=0
WARNINGS=0

# Función para verificar archivo
check_file() {
    if [ -f "$1" ]; then
        echo -e "${GREEN}✓${NC} $1"
    else
        echo -e "${RED}✗${NC} $1 - FALTA"
        ((ERRORS++))
    fi
}

# Función para verificar directorio
check_dir() {
    if [ -d "$1" ]; then
        echo -e "${GREEN}✓${NC} $1/"
    else
        echo -e "${RED}✗${NC} $1/ - FALTA"
        ((ERRORS++))
    fi
}

# Función para advertencia
warn() {
    echo -e "${YELLOW}⚠${NC} $1"
    ((WARNINGS++))
}

echo "=== ARCHIVOS DOCKER ==="
check_file "Dockerfile"
check_file ".dockerignore"
check_file "docker-compose.yml"
echo ""

echo "=== CONFIGURACIÓN RAILWAY ==="
check_file "railway.json"
check_file "railway.toml"
echo ""

echo "=== CONFIGURACIÓN SPRING ==="
check_file "pom.xml"
check_file "src/main/resources/application.properties"
check_file "src/main/resources/application-prod.properties"
check_file "src/main/resources/application-railway.properties"
echo ""

echo "=== DOCUMENTACIÓN ==="
check_file "RAILWAY_SETUP_GUIDE.md"
check_file "QUICK_START.md"
check_file "LOCAL_DEVELOPMENT.md"
check_file "ENV_VARIABLES_TEMPLATE.md"
check_file "RAILWAY_DEPLOYMENT_SUMMARY.md"
check_file "README_DEPLOYMENT.md"
echo ""

echo "=== GIT ==="
check_file ".gitignore"

# Verificar si .env está en gitignore
if grep -q "^\.env$" .gitignore 2>/dev/null; then
    echo -e "${GREEN}✓${NC} .env está en .gitignore"
else
    warn ".env debería estar en .gitignore"
fi
echo ""

echo "=== DIRECTORIOS IMPORTANTES ==="
check_dir "src/main/java/com/cipasuno/petstore/pet_store"
check_dir "src/main/resources"
echo ""

echo "=== VERIFICACIÓN DE GIT ==="
if git rev-parse --git-dir > /dev/null 2>&1; then
    echo -e "${GREEN}✓${NC} Repositorio Git inicializado"
    
    # Verificar remote
    if git remote get-url origin > /dev/null 2>&1; then
        REMOTE=$(git remote get-url origin)
        echo -e "${GREEN}✓${NC} Remote configurado: $REMOTE"
    else
        warn "No hay remote configurado (git remote add origin URL)"
    fi
    
    # Verificar branch
    BRANCH=$(git branch --show-current)
    if [ "$BRANCH" = "main" ] || [ "$BRANCH" = "master" ]; then
        echo -e "${GREEN}✓${NC} Branch actual: $BRANCH"
    else
        warn "Branch actual: $BRANCH (Railway usará 'main' por defecto)"
    fi
    
    # Verificar archivos sin commit
    if [ -n "$(git status --porcelain)" ]; then
        warn "Hay archivos sin commit"
        echo "  Ejecuta: git add . && git commit -m 'feat: Railway configuration'"
    else
        echo -e "${GREEN}✓${NC} No hay cambios sin commit"
    fi
else
    echo -e "${RED}✗${NC} No es un repositorio Git"
    ((ERRORS++))
fi
echo ""

echo "=== VERIFICACIÓN DE RAILWAY.TOML ==="
if [ -f "railway.toml" ]; then
    if grep -q "builder = \"DOCKERFILE\"" railway.toml; then
        echo -e "${GREEN}✓${NC} Builder configurado como DOCKERFILE"
    else
        echo -e "${RED}✗${NC} Builder no configurado correctamente"
        ((ERRORS++))
    fi
    
    if grep -q "SPRING_PROFILES_ACTIVE = \"railway\"" railway.toml; then
        echo -e "${GREEN}✓${NC} Spring profile configurado para Railway"
    else
        warn "Spring profile no configurado en railway.toml"
    fi
fi
echo ""

echo "=== VERIFICACIÓN DE DOCKERFILE ==="
if [ -f "Dockerfile" ]; then
    if grep -q "FROM maven" Dockerfile; then
        echo -e "${GREEN}✓${NC} Dockerfile usa Maven para build"
    fi
    
    if grep -q "FROM eclipse-temurin:17" Dockerfile; then
        echo -e "${GREEN}✓${NC} Dockerfile usa Java 17"
    fi
    
    if grep -q "HEALTHCHECK" Dockerfile; then
        echo -e "${GREEN}✓${NC} Healthcheck configurado"
    fi
fi
echo ""

echo "=== RECOMENDACIONES ==="
echo ""
echo "📋 Archivos locales opcionales:"
if [ ! -f ".env" ]; then
    echo "  • Crear .env para desarrollo local"
    echo "    cp ENV_VARIABLES_TEMPLATE.md .env (y editar)"
fi
echo ""

echo "🔐 Seguridad:"
echo "  • Asegúrate de cambiar JWT_SECRET en Railway"
echo "  • No commitear archivos .env"
echo "  • Usar contraseñas seguras en producción"
echo ""

echo "================================================"
echo "RESUMEN"
echo "================================================"
if [ $ERRORS -eq 0 ]; then
    echo -e "${GREEN}✓ CONFIGURACIÓN CORRECTA${NC}"
    echo ""
    echo "Próximos pasos:"
    echo "1. git add ."
    echo "2. git commit -m 'feat: Railway configuration'"
    echo "3. git push origin main"
    echo "4. Configurar Railway (ver QUICK_START.md)"
else
    echo -e "${RED}✗ FALTAN $ERRORS ARCHIVO(S) REQUERIDO(S)${NC}"
    echo ""
    echo "Por favor, crea los archivos faltantes antes de continuar."
fi

if [ $WARNINGS -gt 0 ]; then
    echo -e "${YELLOW}⚠ HAY $WARNINGS ADVERTENCIA(S)${NC}"
    echo "Revisa las advertencias arriba."
fi

echo ""
echo "📚 Documentación: RAILWAY_SETUP_GUIDE.md"
echo "⚡ Inicio rápido: QUICK_START.md"
echo ""

exit $ERRORS

