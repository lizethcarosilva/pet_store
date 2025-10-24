# 📋 PASO A PASO DETALLADO - RAILWAY DEPLOYMENT

## 🎯 OBJETIVO
Desplegar tu aplicación Pet Store en Railway con auto-deploy desde GitHub.

---

## ⏱️ TIEMPO TOTAL: ~20 minutos

- ✅ Paso 1: Git (2 min)
- ✅ Paso 2: Railway Setup (5 min)
- ✅ Paso 3: Variables de Entorno (5 min)
- ✅ Paso 4: Deploy (6-8 min)
- ✅ Paso 5: Verificación (2 min)

---

## 📍 PASO 1: PREPARAR Y SUBIR CÓDIGO A GITHUB

### 1.1 Verificar Configuración

```powershell
# En PowerShell (Windows)
.\verify-setup.ps1

# O en Git Bash (si prefieres)
bash verify-setup.sh
```

**Resultado esperado**: ✓ CONFIGURACIÓN CORRECTA

### 1.2 Ver Estado de Git

```bash
git status
```

Deberías ver archivos nuevos y modificados.

### 1.3 Agregar Todos los Archivos

```bash
# Agregar archivos nuevos
git add Dockerfile
git add .dockerignore
git add docker-compose.yml
git add railway.json
git add railway.toml
git add .gitignore
git add src/main/resources/application-railway.properties
git add *.md
git add *.sh
git add *.ps1

# Confirmar archivos eliminados
git add -u

# Ver qué se va a commitear
git status
```

### 1.4 Hacer Commit

```bash
git commit -m "feat: Complete Railway deployment configuration

- Add optimized multi-stage Dockerfile for Railway
- Configure railway.json and railway.toml for Docker builds
- Add application-railway.properties Spring profile
- Include comprehensive deployment documentation
- Add docker-compose for local development
- Add verification scripts (PowerShell and Bash)
- Update .gitignore to exclude sensitive files
- Remove old Dockerfile variants"
```

### 1.5 Push a GitHub

```bash
git push origin main
```

### 1.6 Verificar en GitHub

1. Abre tu navegador
2. Ve a: `https://github.com/TU_USUARIO/pet_store`
3. Verifica que aparezcan los archivos:
   - ✅ Dockerfile
   - ✅ railway.toml
   - ✅ application-railway.properties
   - ✅ Documentación (*.md)

---

## 📍 PASO 2: CONFIGURAR RAILWAY

### 2.1 Abrir Railway Dashboard

1. Abre: `https://railway.app/dashboard`
2. Login con tu cuenta
3. Busca tu proyecto (donde ya tienes PostgreSQL)
4. Click en el nombre del proyecto para abrirlo

### 2.2 Agregar Servicio para la Aplicación

```
┌─────────────────────────────────┐
│  TU PROYECTO EN RAILWAY         │
│                                 │
│  ┌──────────┐                   │
│  │PostgreSQL│ ← Ya existe       │
│  └──────────┘                   │
│                                 │
│  Vamos a agregar:              │
│  ┌──────────┐                   │
│  │Pet Store │ ← Nuevo servicio  │
│  │   API    │                   │
│  └──────────┘                   │
└─────────────────────────────────┘
```

**Pasos detallados:**

1. **Click en "+ New"** (botón morado/azul en la esquina superior derecha)

2. **Seleccionar "GitHub Repo"**
   ```
   Options:
   ○ Empty Service
   ● GitHub Repo  ← SELECCIONA ESTE
   ○ Docker Image
   ○ Template
   ```

3. **Autorizar GitHub** (si es primera vez)
   - Click en "Configure GitHub App"
   - Autorizar Railway a acceder a tus repos
   - Seleccionar repositorio `pet_store`

4. **Seleccionar Repositorio**
   - Buscar: `pet_store`
   - Click en el repositorio

5. **Click "Deploy Now"**
   - NO cambies nada más
   - Railway detectará automáticamente el Dockerfile

### 2.3 Configurar Nombre del Servicio (Opcional)

1. Click en el servicio recién creado
2. Click en "Settings" (⚙️)
3. En "Service Name", cambiar a: `pet-store-api`
4. Click fuera para guardar

---

## 📍 PASO 3: CONFIGURAR VARIABLES DE ENTORNO

### 3.1 Abrir Variables del Servicio

1. Asegúrate de estar en tu servicio `pet-store-api`
2. Click en la pestaña **"Variables"**

### 3.2 Verificar Variables Existentes

Verifica que existan estas variables (ya las configuraste):

```env
DATABASE_URL
POSTGRES_USER
POSTGRES_PASSWORD
POSTGRES_DB
DB_HOST
DB_PORT
DB_NAME
DB_USERNAME
DB_PASSWORD
DDL_AUTO
SHOW_SQL
PORT
DEBUG
SPRING_PROFILES_ACTIVE
```

### 3.3 IMPORTANTE: Verificar DATABASE_URL

**MUY IMPORTANTE** ⚠️

La variable `DATABASE_URL` debe usar la **red interna** de Railway:

```
✅ CORRECTO:
DATABASE_URL=postgresql://postgres:PASSWORD@postgres.railway.internal:5432/railway

❌ INCORRECTO:
DATABASE_URL=postgresql://postgres:PASSWORD@metro.proxy.rlwy.net:19175/railway
```

**Si está incorrecta, EDÍTALA:**

1. Click en `DATABASE_URL`
2. Cambiar a: `postgresql://postgres:ykGNQOBYJxFnkLccKACNAvIBJZUuvYXO@postgres.railway.internal:5432/railway`
3. Click "Update"

### 3.4 Agregar Variables Faltantes

Agrega estas variables **NUEVAS** (importantes para seguridad):

#### Variable 1: JWT_SECRET

```
Click "+ New Variable"

Variable Name: JWT_SECRET
Value: [Genera una clave segura - ver abajo]

Click "Add"
```

**Generar JWT_SECRET seguro:**

Opción A - Online:
1. Ve a: https://generate-secret.vercel.app/32
2. Copia la clave generada
3. Úsala como valor de JWT_SECRET

Opción B - Terminal (Git Bash):
```bash
openssl rand -base64 32
```

Opción C - Manual:
```
Usa una cadena aleatoria de al menos 32 caracteres
Ejemplo: TuClaveSecretaSuperSegura123456789AbCdEfGhIjKlMnOpQrStUvWxYz
```

#### Variable 2: JWT_EXPIRATION

```
Click "+ New Variable"

Variable Name: JWT_EXPIRATION
Value: 86400000

Click "Add"
```

#### Variable 3: JAVA_OPTS

```
Click "+ New Variable"

Variable Name: JAVA_OPTS
Value: -Xmx512m -Xms256m

Click "Add"
```

#### Variable 4: TZ

```
Click "+ New Variable"

Variable Name: TZ
Value: America/Bogota

Click "Add"
```

### 3.5 Verificar SPRING_PROFILES_ACTIVE

**MUY IMPORTANTE** ⚠️

Asegúrate que esta variable exista y tenga el valor correcto:

```
Variable: SPRING_PROFILES_ACTIVE
Value: railway
```

Si no existe, agrégala. Si existe con otro valor (como "prod"), edítala a "railway".

### 3.6 Resumen de Variables Requeridas

Lista completa de variables que DEBEN estar configuradas:

```env
# Base de Datos
DATABASE_URL=postgresql://postgres:ykGNQOBYJxFnkLccKACNAvIBJZUuvYXO@postgres.railway.internal:5432/railway
POSTGRES_USER=postgres
POSTGRES_PASSWORD=ykGNQOBYJxFnkLccKACNAvIBJZUuvYXO
POSTGRES_DB=railway
DB_HOST=postgres.railway.internal
DB_PORT=5432
DB_NAME=railway
DB_USERNAME=postgres
DB_PASSWORD=ykGNQOBYJxFnkLccKACNAvIBJZUuvYXO

# JPA
DDL_AUTO=update
SHOW_SQL=false

# Aplicación
PORT=8090
DEBUG=false
SPRING_PROFILES_ACTIVE=railway

# JWT (NUEVAS)
JWT_SECRET=TuClaveSecretaGeneradaAqui
JWT_EXPIRATION=86400000

# Java (NUEVAS)
JAVA_OPTS=-Xmx512m -Xms256m

# Timezone (NUEVA)
TZ=America/Bogota
```

---

## 📍 PASO 4: GENERAR DOMINIO Y DESPLEGAR

### 4.1 Generar Dominio Público

1. En tu servicio `pet-store-api`
2. Click en **"Settings"**
3. Scroll hasta **"Networking"**
4. Click en **"Generate Domain"**
5. Esperar unos segundos
6. Copiar la URL generada (ejemplo: `pet-store-production.up.railway.app`)
7. Guardar esta URL - la necesitarás

### 4.2 Iniciar Despliegue

Si agregaste variables nuevas, Railway iniciará el deploy automáticamente.

Si no, puedes forzarlo:
1. Ve a la pestaña **"Deployments"**
2. Click en **"Deploy"** (o espera el trigger automático)

### 4.3 Monitorear el Build

```
┌────────────────────────────────────┐
│ PROGRESO DEL DEPLOY                │
├────────────────────────────────────┤
│ ⏳ Building...                     │
│   ├─ Downloading code from GitHub │
│   ├─ Detecting Dockerfile         │
│   ├─ Building Docker image         │
│   │  ├─ Stage 1: Maven build      │
│   │  └─ Stage 2: Runtime image    │
│   └─ Pushing image to registry    │
│                                    │
│ ⏳ Deploying...                    │
│   ├─ Starting container            │
│   ├─ Connecting to database       │
│   ├─ Running health check         │
│   └─ Routing traffic              │
│                                    │
│ ✅ Deployment successful!          │
└────────────────────────────────────┘
```

**Tiempo estimado:**
- Primera vez: 6-8 minutos
- Siguientes: 3-5 minutos (con caché)

### 4.4 Ver Logs en Tiempo Real

1. Mientras se despliega, ve a la pestaña **"Logs"**
2. Verás logs en tiempo real
3. Busca estas líneas importantes:

```
[BUILD] Successfully built image
[DEPLOY] Starting container...
[APP] Started PetStoreApplication in XX.XXX seconds
[HEALTH] Health check passed
[DEPLOY] Deployment successful
```

### 4.5 Señales de Éxito

✅ **Build exitoso:**
```
Successfully built docker image
```

✅ **Aplicación iniciada:**
```
Started PetStoreApplication in 45.231 seconds
```

✅ **Base de datos conectada:**
```
HikariPool-1 - Start completed.
```

✅ **Health check OK:**
```
Health check passed on /actuator/health
```

---

## 📍 PASO 5: VERIFICAR Y PROBAR

### 5.1 Verificar Health Check

```bash
# Reemplaza con tu URL de Railway
curl https://pet-store-production.up.railway.app/actuator/health
```

**Respuesta esperada:**
```json
{
  "status": "UP"
}
```

### 5.2 Abrir Swagger UI

1. Abre tu navegador
2. Ve a: `https://TU-URL-RAILWAY.up.railway.app/swagger-ui.html`
3. Deberías ver la interfaz de Swagger

### 5.3 Probar un Endpoint

En Swagger UI:

1. Busca el endpoint **POST /api/auth/login**
2. Click en **"Try it out"**
3. En el body, pon (si tienes un usuario creado):
```json
{
  "username": "admin",
  "password": "password"
}
```
4. Click en **"Execute"**
5. Deberías recibir un token JWT

### 5.4 Verificar en Railway Dashboard

En Railway:
1. Ve a tu servicio
2. Deberías ver:
   - 🟢 Status: Running
   - ✅ Health: Healthy
   - 📊 CPU: < 10%
   - 💾 Memory: ~300-400MB

---

## 📍 PASO 6: CONFIGURAR DESARROLLO LOCAL (OPCIONAL)

### 6.1 Crear Base de Datos Local

```bash
# Conectar a PostgreSQL local
psql -U postgres

# Crear base de datos
CREATE DATABASE petstore;

# Salir
\q
```

### 6.2 Crear Archivo .env

```bash
# En la raíz del proyecto, crear .env
# (usa un editor de texto o IDE)
```

Contenido del archivo `.env`:

```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=petstore
DB_USERNAME=postgres
DB_PASSWORD=tu_password_local

DDL_AUTO=update
SHOW_SQL=true
DEBUG=true
PORT=8090

JWT_SECRET=development_secret_key_minimum_32_characters_long
JWT_EXPIRATION=86400000
```

### 6.3 Ejecutar Localmente

```bash
# Compilar
mvn clean install

# Ejecutar
mvn spring-boot:run
```

### 6.4 Verificar Local

```bash
curl http://localhost:8090/actuator/health
```

Abrir: http://localhost:8090/swagger-ui.html

---

## 🔄 WORKFLOW COMPLETO

### Desarrollo → Producción

```
┌──────────────────┐
│ 1. DESARROLLO    │
│    LOCAL         │
│                  │
│ - Editar código  │
│ - Probar local   │
│ - mvn run        │
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│ 2. GIT           │
│                  │
│ git add .        │
│ git commit -m    │
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│ 3. GITHUB        │
│                  │
│ git push         │
└────────┬─────────┘
         │ webhook
         ▼
┌──────────────────┐
│ 4. RAILWAY       │
│                  │
│ - Auto build     │
│ - Auto deploy    │
│ - Health check   │
└────────┬─────────┘
         │
         ▼
┌──────────────────┐
│ 5. PRODUCCIÓN    │
│                  │
│ ✅ Live!         │
│ tu-app.up...     │
└──────────────────┘
```

### Ejemplo Práctico

```bash
# DÍA A DÍA

# 1. Hacer cambios
code src/main/java/.../UserController.java

# 2. Probar localmente
mvn spring-boot:run
curl http://localhost:8090/api/users

# 3. Commit
git add .
git commit -m "feat: agregar endpoint de búsqueda de usuarios"

# 4. Push (Railway deploys automáticamente)
git push origin main

# 5. Esperar 3-5 minutos y verificar
curl https://tu-app.up.railway.app/api/users
```

---

## ✅ CHECKLIST FINAL

### GitHub
- [ ] Código pusheado a `main`
- [ ] Dockerfile visible en GitHub
- [ ] railway.toml visible en GitHub
- [ ] application-railway.properties visible en GitHub

### Railway - Servicio
- [ ] Servicio creado y conectado a GitHub
- [ ] Nombre del servicio configurado
- [ ] Branch configurado en `main`

### Railway - Variables (20+ variables)
- [ ] `DATABASE_URL` configurada (con postgres.railway.internal)
- [ ] `SPRING_PROFILES_ACTIVE=railway`
- [ ] `JWT_SECRET` configurado (clave segura)
- [ ] `JWT_EXPIRATION=86400000`
- [ ] `JAVA_OPTS=-Xmx512m -Xms256m`
- [ ] Todas las variables de DB configuradas
- [ ] `DDL_AUTO=update`
- [ ] `SHOW_SQL=false`
- [ ] `DEBUG=false`

### Railway - Networking
- [ ] Dominio público generado
- [ ] URL copiada y guardada

### Despliegue
- [ ] Build completado sin errores
- [ ] Logs muestran "Started PetStoreApplication"
- [ ] Health check pasa (🟢)
- [ ] Status: Running

### Verificación
- [ ] `/actuator/health` responde OK
- [ ] Swagger UI accesible
- [ ] Endpoint de login funciona
- [ ] Token JWT se genera correctamente

### Desarrollo Local (Opcional)
- [ ] `.env` creado
- [ ] Base de datos local creada
- [ ] Aplicación corre localmente
- [ ] Puede conectar a DB local

### Workflow
- [ ] Push a GitHub dispara deploy
- [ ] Deploy completa automáticamente
- [ ] Cambios reflejados en producción

---

## 🎉 ¡FELICIDADES!

Si completaste todos los pasos, tu aplicación está:

✅ Desplegada en Railway
✅ Conectada a PostgreSQL
✅ Con auto-deploy desde GitHub
✅ Accesible públicamente
✅ Lista para desarrollo

---

## 📞 SIGUIENTE PASO

Prueba el workflow completo:

```bash
# Hacer un cambio pequeño
echo "// Test auto-deploy" >> README.md

# Commit y push
git add README.md
git commit -m "test: verificar auto-deploy"
git push origin main

# Esperar 3-5 minutos
# Ver en Railway Dashboard el nuevo deployment
# Verificar que se desplegó correctamente
```

---

## 📚 DOCUMENTACIÓN

- **Guía Completa**: `RAILWAY_SETUP_GUIDE.md`
- **Inicio Rápido**: `QUICK_START.md`
- **Desarrollo Local**: `LOCAL_DEVELOPMENT.md`
- **Variables**: `ENV_VARIABLES_TEMPLATE.md`
- **Resumen**: `RAILWAY_DEPLOYMENT_SUMMARY.md`

---

## 🆘 AYUDA

Si algo falla:
1. Ver logs en Railway Dashboard
2. Consultar `RAILWAY_SETUP_GUIDE.md` sección 8 (Troubleshooting)
3. Verificar variables de entorno
4. Verificar que DATABASE_URL use postgres.railway.internal

---

**Tiempo invertido**: ~20 minutos
**Resultado**: Aplicación en producción con CI/CD completo

¡Éxito! 🚀

