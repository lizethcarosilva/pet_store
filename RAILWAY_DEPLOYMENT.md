# 🚀 Despliegue en Railway - Guía Completa

## ✅ Problema Solucionado
- **Error de permisos en mvnw** → Usar Maven directamente
- **Dockerfile optimizado** → Multi-stage build
- **Configuración de Railway** → Detección automática de Java

## 🎯 Pasos para Desplegar en Railway

### 1. Preparar el Repositorio
```bash
# Asegúrate de que estos archivos estén en tu repositorio:
- pom.xml
- src/ (código fuente)
- application-prod.properties
- railway.json
- nixpacks.toml
```

### 2. Ir a Railway
1. Visita [railway.app](https://railway.app)
2. Haz clic en "Login" y conecta con GitHub
3. Haz clic en "New Project"
4. Selecciona "Deploy from GitHub repo"
5. Elige tu repositorio `pet_store`

### 3. Configuración Automática
Railway detectará automáticamente:
- ✅ Proyecto Java (por `pom.xml`)
- ✅ Maven como build tool
- ✅ Puerto 8090
- ✅ Variables de entorno

### 4. Agregar Base de Datos PostgreSQL
1. En tu proyecto de Railway, haz clic en "+ New"
2. Selecciona "Database" → "PostgreSQL"
3. Railway creará automáticamente las variables:
   - `DATABASE_URL`
   - `PGHOST`
   - `PGPORT`
   - `PGUSER`
   - `PGPASSWORD`
   - `PGDATABASE`

### 5. Variables de Entorno
Railway configurará automáticamente:
```bash
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=postgresql://...
```

## 🔧 Archivos de Configuración

### railway.json
```json
{
  "$schema": "https://railway.app/railway.schema.json",
  "build": {
    "builder": "NIXPACKS"
  },
  "deploy": {
    "startCommand": "java -jar target/pet_store-0.0.1-SNAPSHOT.jar",
    "healthcheckPath": "/actuator/health"
  }
}
```

### nixpacks.toml
```toml
[phases.setup]
nixPkgs = ["maven"]

[phases.install]
cmds = ["mvn clean package -DskipTests"]

[start]
cmd = "java -jar target/pet_store-0.0.1-SNAPSHOT.jar"
```

## 🌐 URLs Después del Despliegue

Una vez desplegado, tendrás:
- **API Base**: `https://tu-proyecto.railway.app`
- **Crear Usuario**: `POST https://tu-proyecto.railway.app/api/users/create`
- **Login**: `POST https://tu-proyecto.railway.app/api/users/login`
- **Swagger**: `https://tu-proyecto.railway.app/swagger-ui.html`
- **Health Check**: `https://tu-proyecto.railway.app/actuator/health`

## 🚨 Solución de Problemas

### Error de Build
- Railway usa Nixpacks que detecta automáticamente Java
- No necesitas Dockerfile si usas la detección automática
- Si usas Dockerfile, asegúrate de que use `mvn` en lugar de `./mvnw`

### Error de Base de Datos
- Verifica que PostgreSQL esté agregado como servicio
- Las variables de entorno se configuran automáticamente

### Error de Puerto
- Railway asigna automáticamente el puerto
- Tu aplicación debe usar `PORT` del entorno, no 8090 fijo

## ✅ Ventajas de Railway

- **500 horas/mes gratis**
- **PostgreSQL incluido**
- **No se duerme**
- **Deploy automático desde GitHub**
- **Detección automática de Java/Maven**
- **Variables de entorno automáticas**

## 🎉 ¡Listo!

Una vez desplegado, tu backend estará disponible 24/7 y cualquier PC podrá consumir los endpoints desde la URL pública de Railway.
