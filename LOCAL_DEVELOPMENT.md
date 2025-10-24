# 💻 GUÍA DE DESARROLLO LOCAL

## 🎯 OBJETIVO

Configurar el entorno de desarrollo local para trabajar en la aplicación mientras mantienes el despliegue automático en Railway.

---

## 📋 OPCIONES DE DESARROLLO

### Opción 1: Maven + PostgreSQL Local (Recomendado)
### Opción 2: Docker Compose (Todo en Contenedores)
### Opción 3: IDE (IntelliJ IDEA / Eclipse)

---

## ⚙️ OPCIÓN 1: Maven + PostgreSQL Local

### Prerrequisitos

- ✅ Java 17 instalado
- ✅ Maven instalado
- ✅ PostgreSQL instalado localmente

### Paso 1: Instalar PostgreSQL

#### Windows
```bash
# Descargar desde: https://www.postgresql.org/download/windows/
# Instalar con contraseña para usuario postgres
```

#### Linux (Ubuntu/Debian)
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
```

#### macOS
```bash
brew install postgresql@16
brew services start postgresql@16
```

### Paso 2: Crear Base de Datos Local

```bash
# Conectar a PostgreSQL
psql -U postgres

# Crear base de datos
CREATE DATABASE petstore;

# Crear usuario (opcional)
CREATE USER petstore_user WITH PASSWORD 'password';
GRANT ALL PRIVILEGES ON DATABASE petstore TO petstore_user;

# Salir
\q
```

### Paso 3: Configurar Variables de Entorno

Crea archivo `.env` en la raíz del proyecto:

```env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=petstore
DB_USERNAME=postgres
DB_PASSWORD=tu_password

DDL_AUTO=update
SHOW_SQL=true
DEBUG=true
PORT=8090

JWT_SECRET=development_secret_key_minimum_32_characters_long
JWT_EXPIRATION=86400000
```

### Paso 4: Ejecutar la Aplicación

```bash
# Limpiar y compilar
mvn clean install

# Ejecutar
mvn spring-boot:run

# O con perfil específico
mvn spring-boot:run -Dspring-boot.run.profiles=default
```

### Paso 5: Verificar

```bash
# Health check
curl http://localhost:8090/actuator/health

# Swagger UI
# Abrir: http://localhost:8090/swagger-ui.html
```

---

## 🐳 OPCIÓN 2: Docker Compose

### Prerrequisitos

- ✅ Docker instalado
- ✅ Docker Compose instalado

### Paso 1: Verificar docker-compose.yml

El archivo ya está creado en la raíz del proyecto.

### Paso 2: Iniciar Servicios

```bash
# Iniciar todos los servicios
docker-compose up

# O en background
docker-compose up -d

# Ver logs
docker-compose logs -f app
```

### Paso 3: Acceder a los Servicios

- **Aplicación**: http://localhost:8090
- **Swagger UI**: http://localhost:8090/swagger-ui.html
- **PgAdmin**: http://localhost:5050
  - Email: `admin@petstore.com`
  - Password: `admin`

### Paso 4: Comandos Útiles

```bash
# Detener servicios
docker-compose down

# Reconstruir después de cambios
docker-compose up --build

# Ver logs en tiempo real
docker-compose logs -f

# Acceder a la base de datos
docker-compose exec postgres psql -U postgres -d petstore

# Limpiar todo (incluyendo datos)
docker-compose down -v
```

---

## 💡 OPCIÓN 3: IDE (IntelliJ IDEA)

### Configurar en IntelliJ IDEA

1. **Abrir proyecto**: `File → Open → seleccionar pom.xml`

2. **Configurar variables de entorno**:
   - `Run → Edit Configurations`
   - Seleccionar `PetStoreApplication`
   - En `Environment Variables`, agregar:
     ```
     DB_HOST=localhost;DB_PORT=5432;DB_NAME=petstore;DB_USERNAME=postgres;DB_PASSWORD=password;DDL_AUTO=update;SHOW_SQL=true
     ```

3. **Configurar JDK**:
   - `File → Project Structure → Project`
   - SDK: Java 17

4. **Ejecutar**:
   - Click en el botón Run ▶️
   - O `Shift + F10`

### Configurar en Eclipse

1. **Importar proyecto Maven**:
   - `File → Import → Maven → Existing Maven Projects`
   - Seleccionar la carpeta del proyecto

2. **Configurar variables de entorno**:
   - `Run → Run Configurations`
   - Pestaña `Environment`
   - Agregar variables una por una

3. **Ejecutar**:
   - `Run As → Java Application`
   - Seleccionar `PetStoreApplication`

---

## 🔄 WORKFLOW DE DESARROLLO

### 1. Trabajar en una Feature Nueva

```bash
# Crear rama
git checkout -b feature/nueva-funcionalidad

# Hacer cambios
# ... editar código ...

# Probar localmente
mvn spring-boot:run

# Verificar que funciona
curl http://localhost:8090/actuator/health
```

### 2. Commit y Push

```bash
# Agregar cambios
git add .

# Commit
git commit -m "feat: agregar endpoint de consultas"

# Push a la rama
git push origin feature/nueva-funcionalidad
```

### 3. Merge a Main

```bash
# Cambiar a main
git checkout main

# Merge
git merge feature/nueva-funcionalidad

# Push (esto dispara el deploy en Railway)
git push origin main
```

### 4. Verificar Deploy Automático

1. Ve a Railway Dashboard
2. Verás el nuevo deployment iniciando
3. Espera a que termine (~3-5 minutos)
4. Verifica en tu URL de Railway

---

## 🧪 TESTING LOCAL

### Ejecutar Tests

```bash
# Todos los tests
mvn test

# Tests específicos
mvn test -Dtest=UserControllerTest

# Con coverage
mvn test jacoco:report
```

### Probar Endpoints con cURL

```bash
# Ejemplo: Login
curl -X POST http://localhost:8090/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password"}'

# Ejemplo: Listar productos (con token)
curl -X GET http://localhost:8090/api/products \
  -H "Authorization: Bearer TU_TOKEN_AQUI"
```

### Probar con Postman/Insomnia

1. Importar desde Swagger: http://localhost:8090/api-docs
2. Configurar environment con:
   - `base_url`: http://localhost:8090
   - `token`: (obtener del login)

---

## 🔍 DEBUGGING

### En IntelliJ IDEA

1. Click en el margen izquierdo para agregar breakpoint (●)
2. Click en Debug ▶️ (icono con insecto)
3. La ejecución se detendrá en los breakpoints

### Con Maven

```bash
# Ejecutar en modo debug
mvnDebug spring-boot:run

# Conectar debugger desde IDE en puerto 8000
```

### Ver Logs Detallados

```bash
# Agregar a .env para más logs
DEBUG=true
SHOW_SQL=true
logging.level.org.springframework=DEBUG
```

---

## 🗄️ GESTIONAR BASE DE DATOS LOCAL

### Con psql (CLI)

```bash
# Conectar
psql -U postgres -d petstore

# Ver tablas
\dt

# Describir tabla
\d users

# Query
SELECT * FROM users;

# Salir
\q
```

### Con PgAdmin (GUI)

```bash
# Si usas docker-compose
# Abrir: http://localhost:5050

# Agregar servidor:
# Host: postgres (o localhost si no usas docker-compose)
# Port: 5432
# Database: petstore
# Username: postgres
# Password: password
```

### Con DBeaver (Recomendado)

1. Descargar: https://dbeaver.io/download/
2. New Connection → PostgreSQL
3. Host: localhost, Port: 5432
4. Database: petstore
5. Username: postgres, Password: password

---

## 🔄 SINCRONIZAR CON RAILWAY

### Probar con Datos de Railway Localmente

```bash
# Conectar a Railway DB desde local
psql postgresql://postgres:ykGNQOBYJxFnkLccKACNAvIBJZUuvYXO@metro.proxy.rlwy.net:19175/railway

# O configurar tunnel con Railway CLI
railway login
railway link
railway run psql $DATABASE_URL
```

### Backup de Railway

```bash
# Instalar Railway CLI
npm i -g @railway/cli

# Login
railway login

# Link al proyecto
railway link

# Backup
railway run pg_dump $DATABASE_URL > backup.sql

# Restore local
psql -U postgres -d petstore < backup.sql
```

---

## 📊 HERRAMIENTAS ÚTILES

### Hot Reload (Spring DevTools)

Ya incluido en `pom.xml`:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
</dependency>
```

Los cambios se recargan automáticamente.

### Lombok

Ya configurado. Asegúrate de instalar el plugin en tu IDE:
- IntelliJ: Settings → Plugins → Lombok
- Eclipse: Descargar lombok.jar y ejecutarlo

### Swagger UI

Accede a: http://localhost:8090/swagger-ui.html

Probar endpoints directamente desde el navegador.

---

## 🚨 TROUBLESHOOTING

### Puerto 8090 ya en uso

```bash
# Windows
netstat -ano | findstr :8090
taskkill /PID <PID> /F

# Linux/Mac
lsof -i :8090
kill -9 <PID>

# O cambiar puerto en .env
PORT=8091
```

### No puede conectar a PostgreSQL

```bash
# Verificar que está corriendo
sudo systemctl status postgresql  # Linux
brew services list                 # Mac

# Verificar puerto
sudo lsof -i :5432

# Verificar credenciales
psql -U postgres -d petstore
```

### Cambios no se reflejan

```bash
# Limpiar y recompilar
mvn clean install

# Limpiar cache de IDE
# IntelliJ: File → Invalidate Caches
# Eclipse: Project → Clean
```

---

## ✅ CHECKLIST DESARROLLO LOCAL

- [ ] Java 17 instalado y configurado
- [ ] Maven instalado
- [ ] PostgreSQL corriendo localmente
- [ ] Base de datos `petstore` creada
- [ ] Archivo `.env` configurado
- [ ] Aplicación inicia sin errores
- [ ] Health check responde OK
- [ ] Swagger UI accesible
- [ ] Puedes hacer login y obtener token
- [ ] Git configurado para push automático

---

## 🎓 MEJORES PRÁCTICAS

1. **Nunca commitear** archivos `.env`
2. **Usar ramas** para features nuevas
3. **Probar localmente** antes de push
4. **Verificar logs** después de deploy en Railway
5. **Mantener** versiones de dependencias actualizadas
6. **Escribir tests** para nuevas funcionalidades
7. **Documentar** cambios importantes en commits

---

## 📚 RECURSOS

- [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/current/reference/html/using.html#using.devtools)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Maven Lifecycle](https://maven.apache.org/guides/introduction/introduction-to-the-lifecycle.html)
- [Docker Compose](https://docs.docker.com/compose/)

---

¡Feliz coding! 🚀

