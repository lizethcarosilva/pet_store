# 🚀 GUÍA MAESTRA COMPLETA - RAILWAY DEPLOYMENT

## ⚠️ PROBLEMA ACTUAL Y SOLUCIÓN

**TU PROBLEMA**: El build de Docker se completa pero la aplicación no arranca.

**CAUSA**: El `Dockerfile` tiene un error en la secuencia de COPY que impide que el JAR se compile correctamente.

**SOLUCIÓN**: Vamos a arreglar el Dockerfile Y configurar todo correctamente.

---

# 📋 ÍNDICE

1. [ARREGLAR DOCKERFILE AHORA](#paso-1-arreglar-dockerfile-ahora)
2. [PREPARAR GITHUB](#paso-2-preparar-github)
3. [CONFIGURAR RAILWAY DESDE CERO](#paso-3-configurar-railway-desde-cero)
4. [CONFIGURAR VARIABLES DE ENTORNO](#paso-4-configurar-variables-de-entorno)
5. [FORZAR NUEVO DEPLOY](#paso-5-forzar-nuevo-deploy)
6. [VERIFICAR Y MONITOREAR](#paso-6-verificar-y-monitorear)
7. [TROUBLESHOOTING ESPECÍFICO](#paso-7-troubleshooting-específico)
8. [DESARROLLO LOCAL](#paso-8-desarrollo-local-opcional)

---

# PASO 1: ARREGLAR DOCKERFILE AHORA

## 🔴 PROBLEMA EN TU DOCKERFILE

El orden de los COPY está mal. Debe copiar primero `pom.xml` y `.mvn`, LUEGO descargar dependencias, y FINALMENTE copiar `src`.

## ✅ DOCKERFILE CORRECTO

Vamos a reemplazar completamente tu Dockerfile con esta versión corregida:

```dockerfile
# ==================================
# DOCKERFILE CORREGIDO PARA RAILWAY
# ==================================

# ============================================
# ETAPA 1: BUILD (Compilación con Maven)
# ============================================
FROM maven:3.9.9-eclipse-temurin-17-alpine AS build

# Establecer directorio de trabajo
WORKDIR /app

# PASO 1: Copiar archivos de Maven primero (para caché de dependencias)
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# PASO 2: Descargar dependencias (se cachea si pom.xml no cambia)
RUN mvn dependency:go-offline -B || true

# PASO 3: Ahora copiar el código fuente
COPY src ./src

# PASO 4: Compilar la aplicación (skip tests para build más rápido)
RUN mvn clean package -DskipTests -B

# Verificar que el JAR se creó
RUN ls -la /app/target/

# ============================================
# ETAPA 2: RUNTIME (Ejecución ligera)
# ============================================
FROM eclipse-temurin:17-jre-alpine

# Metadata
LABEL maintainer="cipasuno"
LABEL description="Pet Store Application for Railway"

# Instalar curl para health checks
RUN apk add --no-cache curl

# Crear usuario no-root para seguridad
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Establecer directorio de trabajo
WORKDIR /app

# Copiar el JAR compilado desde la etapa de build
COPY --from=build /app/target/pet_store-0.0.1-SNAPSHOT.jar app.jar

# Verificar que el JAR existe
RUN ls -la /app/ && test -f /app/app.jar

# Cambiar permisos
RUN chown -R appuser:appgroup /app

# Cambiar al usuario no-root
USER appuser

# Exponer el puerto (Railway lo asignará dinámicamente)
EXPOSE ${PORT:-8090}

# Healthcheck para Railway
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:${PORT:-8090}/actuator/health || exit 1

# Variables de entorno por defecto
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0" \
    SPRING_PROFILES_ACTIVE=railway

# Comando de inicio con logging para debug
ENTRYPOINT ["sh", "-c", "echo 'Starting Pet Store Application...' && echo 'Java opts: $JAVA_OPTS' && java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]
```

### 🔧 APLICAR EL CAMBIO

**ABRE** el archivo `Dockerfile` y **REEMPLAZA TODO** su contenido con el código de arriba.

---

# PASO 2: PREPARAR GITHUB

## 2.1 Verificar Archivos Necesarios

Ejecuta este comando para ver qué archivos tienes:

```powershell
dir Dockerfile, railway.toml, railway.json, .gitignore
```

Todos deben existir.

## 2.2 Ver Estado de Git

```bash
git status
```

Deberías ver archivos modificados y nuevos.

## 2.3 Agregar TODOS los Archivos

```bash
# Agregar archivos específicos
git add Dockerfile
git add railway.toml
git add railway.json
git add .gitignore
git add .dockerignore
git add src/main/resources/application-railway.properties
git add docker-compose.yml

# Agregar documentación
git add *.md

# Agregar scripts
git add *.ps1
git add *.sh

# Confirmar archivos eliminados
git add -u
```

## 2.4 Verificar Qué Se Va a Commitear

```bash
git status
```

Deberías ver en verde los archivos que se van a commitear.

## 2.5 Hacer Commit

```bash
git commit -m "fix: Correct Dockerfile build sequence and Railway configuration

- Fix Dockerfile COPY order (pom.xml before src)
- Add verification steps in Dockerfile
- Configure railway.toml for Docker builds
- Add application-railway.properties profile
- Include comprehensive documentation
- Add verification scripts
- Update .gitignore to exclude .env files

This commit fixes the Docker build issue where the application
was not starting correctly due to incorrect file copy sequence."
```

## 2.6 Push a GitHub

```bash
git push origin main
```

## 2.7 Verificar en GitHub

1. Abre tu navegador
2. Ve a: `https://github.com/TU_USUARIO/pet_store`
3. Verifica que veas:
   - ✅ Dockerfile actualizado
   - ✅ railway.toml
   - ✅ railway.json
   - ✅ Documentación (*.md)

---

# PASO 3: CONFIGURAR RAILWAY DESDE CERO

## 3.1 Acceder a Railway

1. Abre: `https://railway.app/dashboard`
2. Login con tu cuenta
3. Busca tu proyecto (donde tienes PostgreSQL)

## 3.2 Verificar PostgreSQL

Antes de continuar, asegúrate que tienes PostgreSQL:

1. En tu proyecto, deberías ver un servicio de **PostgreSQL**
2. Click en PostgreSQL
3. Ve a la pestaña **Variables**
4. Copia estos valores (los necesitarás):
   - `POSTGRES_USER` (debería ser: `postgres`)
   - `POSTGRES_PASSWORD` (debería ser: `ykGNQOBYJxFnkLccKACNAvIBJZUuvYXO`)
   - `POSTGRES_DB` (debería ser: `railway`)

## 3.3 ¿Ya Tienes un Servicio para la App?

### SI YA EXISTE UN SERVICIO:

1. Click en el servicio de tu aplicación
2. Ve a **Settings** (⚙️)
3. Scroll hasta abajo
4. Click en **Remove Service**
5. Confirma la eliminación
6. Continúa con "Crear Nuevo Servicio" abajo

### SI NO EXISTE:

Continúa con "Crear Nuevo Servicio" abajo

## 3.4 Crear Nuevo Servicio

1. En tu proyecto, click en **"+ New"** (botón en la esquina superior derecha)

2. Selecciona **"GitHub Repo"**

3. **Si es la primera vez conectando GitHub:**
   - Click en "Configure GitHub App"
   - Autoriza Railway
   - Selecciona tu repositorio `pet_store`
   - Click "Install & Authorize"

4. **Seleccionar repositorio:**
   - Busca: `pet_store`
   - Click en el repositorio

5. **Railway detectará el Dockerfile automáticamente**
   - NO cambies nada
   - Click en **"Deploy Now"**

## 3.5 Configurar el Servicio

1. El servicio se creará y empezará a hacer deploy
2. **Cancela este primer deploy** (porque falta configurar variables)
   - Click en el deployment activo
   - Click en los 3 puntos (⋮)
   - Click en "Cancel Deployment"

3. Click en el nombre del servicio (arriba)
4. Cambiar nombre a: `pet-store-api` (opcional pero recomendado)

## 3.6 Configurar Build Settings

1. En tu servicio, ve a **Settings**
2. Scroll a **Build**
3. Verifica:
   - ✅ Builder: `Dockerfile`
   - ✅ Dockerfile Path: `Dockerfile`
   - ✅ Build Command: (dejar vacío)
4. Scroll a **Deploy**
5. Verifica:
   - ✅ Start Command: (dejar vacío - usa el del Dockerfile)

---

# PASO 4: CONFIGURAR VARIABLES DE ENTORNO

## 4.1 Ir a Variables

1. En tu servicio `pet-store-api`
2. Click en la pestaña **"Variables"**

## 4.2 Agregar Variables UNA POR UNA

### ⚠️ IMPORTANTE: LEER ANTES DE EMPEZAR

- Agrega cada variable con el botón **"+ New Variable"**
- Copia y pega EXACTAMENTE (sin espacios extra)
- Después de agregar TODAS, NO hagas deploy todavía

### 🔴 VARIABLES CRÍTICAS DE BASE DE DATOS

#### Variable 1: DATABASE_URL
```
Click: + New Variable

Variable name: DATABASE_URL
Value: postgresql://postgres:ykGNQOBYJxFnkLccKACNAvIBJZUuvYXO@postgres.railway.internal:5432/railway

Click: Add
```

**⚠️ CRÍTICO**: Debe usar `postgres.railway.internal` NO `metro.proxy.rlwy.net`

#### Variable 2: POSTGRES_USER
```
Variable name: POSTGRES_USER
Value: postgres
```

#### Variable 3: POSTGRES_PASSWORD
```
Variable name: POSTGRES_PASSWORD
Value: ykGNQOBYJxFnkLccKACNAvIBJZUuvYXO
```

#### Variable 4: POSTGRES_DB
```
Variable name: POSTGRES_DB
Value: railway
```

#### Variable 5: DB_HOST
```
Variable name: DB_HOST
Value: postgres.railway.internal
```

#### Variable 6: DB_PORT
```
Variable name: DB_PORT
Value: 5432
```

#### Variable 7: DB_NAME
```
Variable name: DB_NAME
Value: railway
```

#### Variable 8: DB_USERNAME
```
Variable name: DB_USERNAME
Value: postgres
```

#### Variable 9: DB_PASSWORD
```
Variable name: DB_PASSWORD
Value: ykGNQOBYJxFnkLccKACNAvIBJZUuvYXO
```

### 🔵 VARIABLES DE CONFIGURACIÓN JPA

#### Variable 10: DDL_AUTO
```
Variable name: DDL_AUTO
Value: update
```

#### Variable 11: SHOW_SQL
```
Variable name: SHOW_SQL
Value: false
```

### 🟢 VARIABLES DE APLICACIÓN

#### Variable 12: PORT
```
Variable name: PORT
Value: 8090
```

#### Variable 13: DEBUG
```
Variable name: DEBUG
Value: false
```

#### Variable 14: SPRING_PROFILES_ACTIVE
```
Variable name: SPRING_PROFILES_ACTIVE
Value: railway
```

### 🟡 VARIABLES DE SEGURIDAD JWT

#### Variable 15: JWT_SECRET

**⚠️ GENERA UNA CLAVE SEGURA**

Opción A - Generador Online:
1. Ve a: https://generate-secret.vercel.app/32
2. Copia la clave generada

Opción B - Terminal (Git Bash):
```bash
openssl rand -base64 32
```

Opción C - Manual:
Usa una cadena de al menos 32 caracteres aleatorios.

```
Variable name: JWT_SECRET
Value: [TU_CLAVE_GENERADA_AQUI]

Ejemplo: kJ8vN2pQ5wR9xE4mT7yU0iO3aS6dF1gH9jK2lZ5cV8bN4mX7wQ0rT3yE6uI9oP
```

#### Variable 16: JWT_EXPIRATION
```
Variable name: JWT_EXPIRATION
Value: 86400000
```

### 🟣 VARIABLES DE JAVA Y SISTEMA

#### Variable 17: JAVA_OPTS
```
Variable name: JAVA_OPTS
Value: -Xmx512m -Xms256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0
```

#### Variable 18: TZ
```
Variable name: TZ
Value: America/Bogota
```

## 4.3 Verificar TODAS las Variables

Después de agregar todas, verifica que tienes estas 18 variables:

```
✅ DATABASE_URL
✅ POSTGRES_USER
✅ POSTGRES_PASSWORD
✅ POSTGRES_DB
✅ DB_HOST
✅ DB_PORT
✅ DB_NAME
✅ DB_USERNAME
✅ DB_PASSWORD
✅ DDL_AUTO
✅ SHOW_SQL
✅ PORT
✅ DEBUG
✅ SPRING_PROFILES_ACTIVE
✅ JWT_SECRET
✅ JWT_EXPIRATION
✅ JAVA_OPTS
✅ TZ
```

---

# PASO 5: FORZAR NUEVO DEPLOY

## 5.1 Generar Dominio Público

1. En tu servicio, ve a **Settings**
2. Scroll a **Networking**
3. Click en **"Generate Domain"**
4. Espera unos segundos
5. Copia la URL generada (ejemplo: `pet-store-production.up.railway.app`)
6. **GUARDA ESTA URL** - la necesitarás para probar

## 5.2 Trigger Manual del Deploy

1. Ve a la pestaña **"Deployments"**
2. Click en **"Deploy"** (botón en la esquina superior derecha)
3. Se iniciará un nuevo deployment

## 5.3 Ver Logs en Tiempo Real

1. En la pestaña **Deployments**, click en el deployment activo
2. Verás los logs scrolleando
3. **NO CIERRES ESTA VENTANA** - necesitamos monitorear

---

# PASO 6: VERIFICAR Y MONITOREAR

## 6.1 Logs que Debes Ver (EN ORDEN)

### 📦 FASE 1: BUILDING (2-3 minutos)

Busca estas líneas:

```
✓ Using Detected Dockerfile
✓ load build definition from Dockerfile
✓ load .dockerignore
✓ FROM docker.io/library/maven:...
✓ COPY pom.xml .
✓ RUN mvn dependency:go-offline -B
✓ COPY src ./src
✓ RUN mvn clean package -DskipTests -B
```

**Debe aparecer:**
```
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

**Y luego:**
```
✓ RUN ls -la /app/target/
✓ pet_store-0.0.1-SNAPSHOT.jar  [SIZE]
```

### 🚀 FASE 2: DEPLOYING (1-2 minutos)

```
✓ FROM eclipse-temurin:17-jre-alpine
✓ COPY --from=build /app/target/pet_store-0.0.1-SNAPSHOT.jar app.jar
✓ RUN ls -la /app/
✓ app.jar exists
✓ importing to docker
```

### ✅ FASE 3: STARTING (30-60 segundos)

Busca estas líneas CLAVE:

```
Starting Pet Store Application...
Java opts: -Xmx512m...

  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/

:: Spring Boot ::                (v3.5.6)

[INFO] Starting PetStoreApplication...
[INFO] The following 1 profile is active: "railway"
[INFO] Tomcat initialized with port(s): 8090 (http)
[INFO] Tomcat started on port(s): 8090 (http)
[INFO] Started PetStoreApplication in XX.XXX seconds
```

### 🏥 FASE 4: HEALTH CHECK (10-20 segundos)

```
✓ Health check passed on /actuator/health
✓ Deployment successful
```

## 6.2 ¿Qué Hacer Si Se Queda Atascado?

### SI SE QUEDA EN "Building":

**Espera 5 minutos completos**. Maven descarga dependencias la primera vez.

Si después de 5 minutos sigue igual:
- Busca en los logs: `ERROR` o `FAILED`
- Ve a [PASO 7: TROUBLESHOOTING](#paso-7-troubleshooting-específico)

### SI SE QUEDA EN "Starting" (TU PROBLEMA ACTUAL):

Esto significa que el contenedor arranca pero la aplicación no inicia.

**Busca en los logs:**
```
Error starting ApplicationContext
Unable to connect to database
Connection refused
```

Si ves alguno de estos:
- Ve a [Sección 7.2: Problemas de Base de Datos](#72-problema-no-conecta-a-base-de-datos)

### SI SE QUEDA EN "Health Check":

```
Health check failed
Connection refused on :8090
```

Esto puede significar:
1. La app no está escuchando en el puerto correcto
2. El health check tarda más de lo esperado

**Solución**:
1. Ve a **Settings** → **Deploy**
2. Cambia **Health Check Timeout** a: `200` segundos
3. Ve a **Deployments**
4. Click **Redeploy**

---

# PASO 7: TROUBLESHOOTING ESPECÍFICO

## 7.1 Problema: Build Falla

### Síntoma:
```
ERROR: failed to solve: process "/bin/sh -c mvn clean package..." did not complete successfully
```

### Solución:

1. **Verifica el Dockerfile**:
```bash
# En tu terminal local
cat Dockerfile | findstr "COPY"
```

Debe mostrar en este orden:
```
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY src ./src
```

2. **Si el orden está mal**, edita el Dockerfile con el correcto del [PASO 1](#paso-1-arreglar-dockerfile-ahora)

3. **Push los cambios**:
```bash
git add Dockerfile
git commit -m "fix: correct Dockerfile COPY order"
git push origin main
```

4. Railway detectará el cambio y hará redeploy automáticamente

## 7.2 Problema: No Conecta a Base de Datos

### Síntoma en logs:
```
Unable to open JDBC Connection
Connection refused
org.postgresql.util.PSQLException
```

### Solución:

1. **Verifica DATABASE_URL en Railway**:
   - Ve a **Variables**
   - Busca `DATABASE_URL`
   - **DEBE** contener: `postgres.railway.internal`
   - **NO** debe contener: `metro.proxy.rlwy.net`

2. **Si está incorrecta, corrígela**:
   - Click en `DATABASE_URL`
   - Cambia a:
   ```
   postgresql://postgres:ykGNQOBYJxFnkLccKACNAvIBJZUuvYXO@postgres.railway.internal:5432/railway
   ```
   - Click **Update**

3. **Verifica que PostgreSQL esté en el mismo proyecto**:
   - En el dashboard del proyecto
   - Debes ver DOS servicios:
     1. PostgreSQL (database)
     2. pet-store-api (tu app)

4. **Redeploy**:
   - Ve a **Deployments**
   - Click **Deploy**

## 7.3 Problema: Puerto Incorrecto

### Síntoma:
```
Health check failed on :8090
```

### Solución:

1. **Verifica variable PORT**:
   - Variables → `PORT` debe ser `8090`

2. **Verifica en Dockerfile**:
   - Debe tener: `EXPOSE ${PORT:-8090}`

3. **Verifica application-railway.properties**:
```bash
# En local
cat src/main/resources/application-railway.properties | findstr "port"
```

Debe mostrar:
```
server.port=${PORT:8090}
```

## 7.4 Problema: JAR No Se Encuentra

### Síntoma:
```
Error: Unable to access jarfile app.jar
```

### Solución:

Este es el problema que tenías. Se soluciona con el Dockerfile corregido del [PASO 1](#paso-1-arreglar-dockerfile-ahora).

Verifica que el Dockerfile tenga estas líneas de verificación:

```dockerfile
# En la etapa de build
RUN ls -la /app/target/

# En la etapa runtime
RUN ls -la /app/ && test -f /app/app.jar
```

Si los logs muestran que el JAR existe pero aún falla:

1. **Verifica permisos**:
   - El Dockerfile debe tener:
   ```dockerfile
   RUN chown -R appuser:appgroup /app
   USER appuser
   ```

2. **Verifica el nombre del JAR**:
   ```bash
   # En local
   cat pom.xml | findstr "artifactId"
   ```
   Debe ser: `<artifactId>pet_store</artifactId>`

## 7.5 Problema: OutOfMemoryError

### Síntoma:
```
java.lang.OutOfMemoryError: Java heap space
```

### Solución:

1. **Aumentar memoria en JAVA_OPTS**:
   - Ve a Variables
   - Edita `JAVA_OPTS`
   - Cambia a:
   ```
   -Xmx768m -Xms384m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0
   ```

2. **Si sigue fallando, upgrade el plan de Railway**:
   - Free tier: 512MB RAM
   - Hobby tier: 8GB RAM

## 7.6 Problema: Profile No Se Activa

### Síntoma:
```
The following profiles are active: "default"
```

Debería decir `"railway"`.

### Solución:

1. **Verifica SPRING_PROFILES_ACTIVE**:
   - Variables → `SPRING_PROFILES_ACTIVE` = `railway`

2. **Verifica que existe application-railway.properties**:
```bash
# En local
dir src\main\resources\application-railway.properties
```

3. **Si no existe, créalo** con el contenido de la sección [8.2](#82-crear-archivo-env-local)

4. **Push el cambio**:
```bash
git add src/main/resources/application-railway.properties
git commit -m "add: application-railway.properties profile"
git push origin main
```

---

# PASO 8: DESARROLLO LOCAL (OPCIONAL)

## 8.1 Requisitos

- ✅ Java 17
- ✅ Maven
- ✅ PostgreSQL instalado localmente

## 8.2 Crear Archivo .env Local

En la raíz del proyecto, crea un archivo llamado `.env`:

```env
# Base de Datos Local
DB_HOST=localhost
DB_PORT=5432
DB_NAME=petstore
DB_USERNAME=postgres
DB_PASSWORD=tu_password_local

# JPA
DDL_AUTO=update
SHOW_SQL=true
DEBUG=true

# Server
PORT=8090

# JWT (desarrollo)
JWT_SECRET=development_secret_key_for_local_testing_minimum_32_characters
JWT_EXPIRATION=86400000
```

**⚠️ NO subas este archivo a Git** (ya está en .gitignore)

## 8.3 Crear Base de Datos Local

```bash
# Conectar a PostgreSQL
psql -U postgres

# Crear base de datos
CREATE DATABASE petstore;

# Salir
\q
```

## 8.4 Ejecutar Localmente

```bash
# Compilar
mvn clean install

# Ejecutar
mvn spring-boot:run
```

## 8.5 Verificar Local

```bash
# Health check
curl http://localhost:8090/actuator/health

# Swagger UI en navegador
# http://localhost:8090/swagger-ui.html
```

---

# VERIFICACIÓN FINAL

## ✅ Checklist Completo

### GitHub
- [ ] Dockerfile corregido y pusheado
- [ ] railway.toml en GitHub
- [ ] application-railway.properties en GitHub
- [ ] Todos los archivos de documentación pusheados

### Railway - Servicio
- [ ] Servicio conectado a GitHub
- [ ] Builder: Dockerfile
- [ ] Branch: main

### Railway - Variables (18 variables)
- [ ] DATABASE_URL (con postgres.railway.internal)
- [ ] POSTGRES_USER, POSTGRES_PASSWORD, POSTGRES_DB
- [ ] DB_HOST, DB_PORT, DB_NAME, DB_USERNAME, DB_PASSWORD
- [ ] DDL_AUTO, SHOW_SQL
- [ ] PORT, DEBUG
- [ ] SPRING_PROFILES_ACTIVE=railway
- [ ] JWT_SECRET (clave segura)
- [ ] JWT_EXPIRATION
- [ ] JAVA_OPTS
- [ ] TZ

### Railway - Networking
- [ ] Dominio público generado
- [ ] URL guardada

### Despliegue
- [ ] Build completa sin errores
- [ ] Maven muestra "BUILD SUCCESS"
- [ ] JAR se crea correctamente
- [ ] Contenedor inicia
- [ ] Logs muestran "Started PetStoreApplication"
- [ ] Health check pasa
- [ ] Status: Running (🟢)

### Pruebas
- [ ] `/actuator/health` responde UP
- [ ] Swagger UI accesible
- [ ] Puede hacer login
- [ ] Token JWT se genera

---

# COMANDOS RÁPIDOS DE REFERENCIA

## Git
```bash
# Ver estado
git status

# Agregar todo
git add .

# Commit
git commit -m "mensaje"

# Push
git push origin main

# Ver último commit
git log -1
```

## Verificar Localmente
```bash
# Ver Dockerfile
cat Dockerfile

# Ver railway.toml
cat railway.toml

# Ver variables
cat src/main/resources/application-railway.properties

# Compilar y probar
mvn clean install
mvn spring-boot:run
```

## Probar API
```bash
# Health check (local)
curl http://localhost:8090/actuator/health

# Health check (Railway)
curl https://tu-app.up.railway.app/actuator/health

# Ver info
curl https://tu-app.up.railway.app/actuator/info
```

---

# RESUMEN DEL PROBLEMA Y SOLUCIÓN

## 🔴 Tu Problema Original

El Docker build se completaba pero la aplicación no arrancaba porque:

1. **Orden incorrecto en Dockerfile**: El `COPY src` estaba ANTES de descargar dependencias
2. **Falta de verificación**: No había checks para confirmar que el JAR se creó
3. **Variables de entorno incompletas**: Faltaban JWT_SECRET y otras

## ✅ Solución Aplicada

1. **Dockerfile corregido** con orden correcto:
   - Primero: pom.xml y .mvn
   - Segundo: descargar dependencias
   - Tercero: copiar src
   - Cuarto: compilar

2. **Verificaciones agregadas**:
   - `RUN ls -la /app/target/` después de compilar
   - `RUN test -f /app/app.jar` antes de iniciar

3. **Variables completas**: 18 variables configuradas

4. **Logging mejorado**: Echo statements para debug

## 📊 Tiempo Esperado

- Build: 3-5 minutos (primera vez)
- Deploy: 1-2 minutos
- Start: 30-60 segundos
- Health check: 10-20 segundos
- **Total: 5-8 minutos**

---

# ¿SIGUE SIN FUNCIONAR?

Si después de seguir TODOS los pasos sigue fallando:

## 1. Captura los Logs Completos

En Railway:
1. Deployments → Click en el deployment
2. Scroll hasta el final de los logs
3. Copia TODO el log
4. Busca la palabra "ERROR" o "Exception"

## 2. Verifica Estas Líneas Específicas

En los logs, busca:

```
✓ BUILD SUCCESS          ← Maven compiló bien
✓ pet_store-0.0.1...jar  ← JAR se creó
Starting Pet Store...    ← App está iniciando
active: "railway"        ← Profile correcto
Tomcat started           ← Servidor web OK
Started PetStoreApp...   ← App arrancó completo
Health check passed      ← Health check OK
```

Si falta alguna de estas líneas, ese es el punto de falla.

## 3. Problemas Comunes Adicionales

### Si ves: `PKIX path building failed`
**Causa**: Problema de certificados SSL
**Solución**: Agregar a JAVA_OPTS:
```
-Djavax.net.ssl.trustStore=/etc/ssl/certs/java/cacerts
```

### Si ves: `Address already in use`
**Causa**: Puerto 8090 ocupado
**Solución**: Railway asigna el puerto automáticamente, no debería pasar

### Si ves: `Table 'xxx' doesn't exist`
**Causa**: DDL_AUTO no está creando tablas
**Solución**: Cambiar `DDL_AUTO=create` (solo la primera vez)

---

# CONTACTO Y SOPORTE

Si necesitas ayuda adicional, proporciona:

1. **Logs completos** del último deployment
2. **Screenshot** de tus variables en Railway
3. **Output** de `git log -1` en tu terminal
4. **El error específico** que aparece en los logs

---

**🎉 ¡ÉXITO!**

Si llegaste hasta aquí y todo funciona:
- ✅ Tu app está en Railway
- ✅ Auto-deploy configurado
- ✅ Base de datos conectada
- ✅ URL pública funcionando

**Próximo push a GitHub = Deploy automático en 3-5 minutos**

---

**Versión**: 2.0  
**Última actualización**: 2025-10-24  
**Status**: ✅ Production Ready con Dockerfile Corregido

