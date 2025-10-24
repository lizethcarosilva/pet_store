# 🚂 GUÍA COMPLETA DE CONFIGURACIÓN RAILWAY

Esta guía te llevará paso a paso para configurar correctamente tu aplicación en Railway con despliegue automático desde GitHub.

## 📋 ÍNDICE

1. [Configuración de GitHub](#1-configuración-de-github)
2. [Configuración de Railway](#2-configuración-de-railway)
3. [Variables de Entorno](#3-variables-de-entorno)
4. [Despliegue Inicial](#4-despliegue-inicial)
5. [Desarrollo Local](#5-desarrollo-local)
6. [Despliegue Automático](#6-despliegue-automático)
7. [Verificación y Monitoreo](#7-verificación-y-monitoreo)
8. [Solución de Problemas](#8-solución-de-problemas)

---

## 1. CONFIGURACIÓN DE GITHUB

### 1.1 Preparar el Repositorio

```bash
# Agregar los archivos nuevos al staging
git add Dockerfile
git add .dockerignore
git add railway.json
git add railway.toml
git add src/main/resources/application-railway.properties
git add RAILWAY_SETUP_GUIDE.md
git add .env.example

# Confirmar los cambios de los archivos eliminados
git add -u

# Hacer commit de todos los cambios
git commit -m "feat: Configuración completa de Docker para Railway"

# Subir los cambios a GitHub
git push origin main
```

### 1.2 Verificar en GitHub

1. Ve a tu repositorio en GitHub
2. Verifica que los siguientes archivos estén presentes:
   - ✅ `Dockerfile`
   - ✅ `.dockerignore`
   - ✅ `railway.json`
   - ✅ `railway.toml`
   - ✅ `src/main/resources/application-railway.properties`

---

## 2. CONFIGURACIÓN DE RAILWAY

### 2.1 Conectar GitHub con Railway

1. **Accede a Railway**: https://railway.app/
2. **Ve a tu proyecto** (donde ya tienes la base de datos PostgreSQL)
3. **Agregar nuevo servicio**:
   - Click en `+ New Service`
   - Selecciona `GitHub Repo`
   - Autoriza Railway a acceder a tu cuenta de GitHub (si no lo has hecho)
   - Selecciona el repositorio `pet_store`
   - Click en `Deploy Now`

### 2.2 Configurar el Servicio

1. **Nombre del Servicio**: Cambia el nombre a `pet-store-api` (opcional)
2. **Branch**: Asegúrate que esté configurado en `main`
3. **Build Settings**:
   - Builder: `Dockerfile` (se detecta automáticamente)
   - Root Directory: `/` (raíz del proyecto)

---

## 3. VARIABLES DE ENTORNO

### 3.1 Variables Requeridas en Railway

En tu proyecto de Railway, ve a **Settings → Variables** y configura las siguientes:

#### 🔴 **VARIABLES CRÍTICAS** (Ya las tienes configuradas)

```bash
# Base de Datos
DATABASE_URL=postgresql://postgres:ykGNQOBYJxFnkLccKACNAvIBJZUuvYXO@postgres.railway.internal:5432/railway
POSTGRES_USER=postgres
POSTGRES_PASSWORD=ykGNQOBYJxFnkLccKACNAvIBJZUuvYXO
POSTGRES_DB=railway

# Configuración de Base de Datos (para compatibilidad)
DB_HOST=postgres.railway.internal
DB_PORT=5432
DB_NAME=railway
DB_USERNAME=postgres
DB_PASSWORD=ykGNQOBYJxFnkLccKACNAvIBJZUuvYXO

# Configuración JPA
DDL_AUTO=update
SHOW_SQL=false

# Puerto (Railway lo asigna automáticamente)
PORT=8090

# Perfil de Spring
SPRING_PROFILES_ACTIVE=railway

# Debug
DEBUG=false
```

#### 🟡 **VARIABLES ADICIONALES RECOMENDADAS**

Agrega estas variables para seguridad y funcionalidad:

```bash
# JWT Secret (genera una clave segura)
JWT_SECRET=tu_clave_super_secreta_de_al_menos_32_caracteres_aqui_cambiar_esto

# JWT Expiration (24 horas en milisegundos)
JWT_EXPIRATION=86400000

# Timezone
TZ=America/Bogota

# Java Options (optimización de memoria)
JAVA_OPTS=-Xmx512m -Xms256m
```

### 3.2 Cómo Agregar Variables en Railway

1. En tu proyecto de Railway, click en tu servicio `pet-store-api`
2. Ve a la pestaña **Variables**
3. Click en **+ New Variable**
4. Agrega cada variable con su valor
5. Click en **Deploy** después de agregar todas las variables

### 3.3 Variables que Railway Proporciona Automáticamente

Railway proporciona estas variables automáticamente:
- `PORT` - Puerto asignado dinámicamente
- `RAILWAY_ENVIRONMENT` - Entorno actual
- `RAILWAY_PROJECT_ID` - ID del proyecto
- `RAILWAY_DEPLOYMENT_ID` - ID del despliegue

---

## 4. DESPLIEGUE INICIAL

### 4.1 Trigger del Primer Despliegue

Después de conectar GitHub y configurar las variables:

1. Railway detectará automáticamente el `Dockerfile`
2. Iniciará el proceso de build
3. Puedes ver los logs en tiempo real

### 4.2 Monitorear el Build

1. Ve a la pestaña **Deployments**
2. Click en el despliegue activo
3. Observa los logs:
   ```
   ✓ Building Dockerfile
   ✓ Building dependencies
   ✓ Compiling Java application
   ✓ Creating Docker image
   ✓ Pushing image
   ✓ Deploying
   ✓ Health check passed
   ```

### 4.3 Tiempo de Despliegue Esperado

- **Build**: 5-8 minutos (primera vez, luego más rápido por caché)
- **Deploy**: 30-60 segundos
- **Total**: ~6-9 minutos primera vez

---

## 5. DESARROLLO LOCAL

### 5.1 Configurar Entorno Local

1. **Crea archivo `.env`** en la raíz del proyecto:

```bash
# Copiar el archivo de ejemplo
cp .env.example .env
```

2. **Edita `.env`** con tus valores locales:

```env
# Base de Datos Local
DB_HOST=localhost
DB_PORT=5432
DB_NAME=petstore
DB_USERNAME=postgres
DB_PASSWORD=tu_password_local

# Configuración JPA
DDL_AUTO=update
SHOW_SQL=true
DEBUG=true

# Puerto Local
PORT=8090

# JWT (usar la misma que en producción o una diferente)
JWT_SECRET=development_secret_key_change_in_production_minimum_32_chars
JWT_EXPIRATION=86400000
```

### 5.2 Ejecutar Localmente

#### Opción A: Con Maven

```bash
# Compilar el proyecto
mvn clean install

# Ejecutar la aplicación
mvn spring-boot:run
```

#### Opción B: Con Docker (igual que Railway)

```bash
# Build de la imagen
docker build -t pet-store-local .

# Ejecutar el contenedor
docker run -p 8090:8090 \
  -e DB_HOST=host.docker.internal \
  -e DB_PORT=5432 \
  -e DB_NAME=petstore \
  -e DB_USERNAME=postgres \
  -e DB_PASSWORD=tu_password \
  -e SPRING_PROFILES_ACTIVE=railway \
  pet-store-local
```

### 5.3 Verificar que Funciona Local

```bash
# Health check
curl http://localhost:8090/actuator/health

# Swagger UI
# Abre en navegador: http://localhost:8090/swagger-ui.html
```

---

## 6. DESPLIEGUE AUTOMÁTICO

### 6.1 Workflow Automático

Una vez configurado, cada vez que hagas push a GitHub:

```bash
# Hacer cambios en tu código
# ... editar archivos ...

# Commit de cambios
git add .
git commit -m "feat: nuevo endpoint de veterinaria"

# Push a GitHub
git push origin main
```

### 6.2 Proceso Automático en Railway

Railway automáticamente:
1. ✅ Detecta el push en GitHub
2. ✅ Inicia el build del Dockerfile
3. ✅ Ejecuta las pruebas (si están configuradas)
4. ✅ Crea la imagen Docker
5. ✅ Despliega la nueva versión
6. ✅ Verifica el health check
7. ✅ Cambia el tráfico a la nueva versión
8. ✅ Mantiene la versión anterior por si hay rollback

### 6.3 Notificaciones

Configura notificaciones en Railway:
1. Ve a **Settings → Notifications**
2. Agrega tu email o webhook de Discord/Slack
3. Recibirás notificaciones de:
   - ✅ Despliegue exitoso
   - ❌ Despliegue fallido
   - ⚠️ Errores de build

---

## 7. VERIFICACIÓN Y MONITOREO

### 7.1 Verificar Despliegue Exitoso

1. **Obtener la URL de tu aplicación**:
   - En Railway, ve a tu servicio
   - Click en **Settings → Generate Domain**
   - Copia la URL (ejemplo: `pet-store-production.up.railway.app`)

2. **Probar endpoints**:

```bash
# Reemplaza con tu URL de Railway
RAILWAY_URL="https://pet-store-production.up.railway.app"

# Health check
curl $RAILWAY_URL/actuator/health

# Swagger UI
# Abre en navegador: $RAILWAY_URL/swagger-ui.html

# API Docs
curl $RAILWAY_URL/api-docs
```

### 7.2 Ver Logs en Tiempo Real

```bash
# En Railway Dashboard
# Click en tu servicio → View Logs
```

O instala Railway CLI:

```bash
# Instalar Railway CLI
npm i -g @railway/cli

# Login
railway login

# Ver logs en terminal
railway logs
```

### 7.3 Métricas y Monitoreo

En Railway Dashboard:
- **CPU Usage**: Uso de CPU
- **Memory Usage**: Uso de memoria
- **Network**: Tráfico de red
- **Response Times**: Tiempos de respuesta

---

## 8. SOLUCIÓN DE PROBLEMAS

### 8.1 Build Falla

**Problema**: El build de Docker falla

**Soluciones**:
```bash
# 1. Verificar que el Dockerfile está en la raíz
ls -la Dockerfile

# 2. Probar build localmente
docker build -t test-build .

# 3. Ver logs detallados en Railway
# Dashboard → Deployments → Click en el deployment fallido
```

### 8.2 La Aplicación No Inicia

**Problema**: El contenedor se construye pero no inicia

**Verifica**:
1. Variables de entorno en Railway
2. DATABASE_URL esté correcta
3. Puerto correcto (Railway asigna PORT automáticamente)

```bash
# En los logs debe aparecer:
# "Tomcat started on port(s): 8090"
# "Started PetStoreApplication"
```

### 8.3 Error de Conexión a Base de Datos

**Problema**: No puede conectar a PostgreSQL

**Verifica**:
```bash
# 1. DATABASE_URL debe usar postgres.railway.internal
DATABASE_URL=postgresql://postgres:PASSWORD@postgres.railway.internal:5432/railway

# 2. No debe usar el URL público dentro de Railway
# ❌ MAL: metro.proxy.rlwy.net
# ✅ BIEN: postgres.railway.internal

# 3. Verificar que la base de datos esté en el mismo proyecto
```

### 8.4 Health Check Falla

**Problema**: Deployment falla en health check

**Soluciones**:
```bash
# 1. Verificar que actuator esté habilitado
curl $RAILWAY_URL/actuator/health

# 2. Aumentar timeout en railway.toml
healthcheckTimeout = 200  # aumentar a 200 segundos

# 3. Verificar logs para ver por qué no responde
```

### 8.5 Despliegue Lento

**Problema**: El build tarda mucho

**Optimizaciones**:
```bash
# 1. El Dockerfile ya está optimizado con multi-stage build
# 2. Railway cachea capas de Docker
# 3. Después del primer build, los siguientes son más rápidos

# Primera vez: ~6-8 minutos
# Siguientes: ~2-4 minutos (con caché)
```

### 8.6 Memoria Insuficiente

**Problema**: Out of Memory error

**Soluciones**:
```bash
# 1. Ajustar JAVA_OPTS en railway.toml
JAVA_OPTS=-Xmx768m -Xms384m  # Aumentar límites

# 2. Upgrade de plan en Railway si es necesario
# Free tier: 512MB RAM
# Hobby: 8GB RAM
```

---

## 📊 CHECKLIST FINAL

Antes de considerar la configuración completa, verifica:

### GitHub
- [ ] Dockerfile en la raíz del repositorio
- [ ] .dockerignore configurado
- [ ] railway.json y railway.toml actualizados
- [ ] application-railway.properties creado
- [ ] Cambios pusheados a GitHub

### Railway - Proyecto
- [ ] Servicio conectado a GitHub
- [ ] PostgreSQL database creada
- [ ] Domain generado

### Railway - Variables de Entorno
- [ ] DATABASE_URL configurada
- [ ] POSTGRES_USER, POSTGRES_PASSWORD, POSTGRES_DB
- [ ] DB_HOST, DB_PORT, DB_NAME, DB_USERNAME, DB_PASSWORD
- [ ] SPRING_PROFILES_ACTIVE=railway
- [ ] DDL_AUTO, SHOW_SQL, DEBUG
- [ ] JWT_SECRET configurado
- [ ] PORT configurado

### Despliegue
- [ ] Build completo sin errores
- [ ] Health check pasa
- [ ] Aplicación accesible en URL de Railway
- [ ] Endpoints funcionan correctamente
- [ ] Swagger UI accesible

### Local
- [ ] .env configurado para desarrollo local
- [ ] Aplicación corre localmente
- [ ] Conecta a base de datos local

---

## 🎯 PRÓXIMOS PASOS

1. **Seguir esta guía paso a paso**
2. **Hacer commit y push de los archivos**
3. **Configurar Railway según las instrucciones**
4. **Verificar el primer despliegue**
5. **Probar el workflow de desarrollo local**
6. **Hacer un cambio pequeño y verificar auto-deploy**

---

## 📚 RECURSOS ADICIONALES

- [Railway Documentation](https://docs.railway.app/)
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)
- [Spring Boot Docker](https://spring.io/guides/topicals/spring-boot-docker/)
- [Railway CLI](https://docs.railway.app/develop/cli)

---

## 💬 SOPORTE

Si tienes problemas:
1. Revisa los logs en Railway Dashboard
2. Consulta la sección de Solución de Problemas
3. Verifica que todas las variables de entorno estén configuradas
4. Prueba el build de Docker localmente

¡Buena suerte con tu despliegue! 🚀

