# 🚀 Guía de Despliegue - Pet Store Backend

## Opciones Gratuitas para Desplegar

### 1. Railway (Recomendado) ⭐
**Ventajas:**
- ✅ 500 horas/mes gratis
- ✅ PostgreSQL incluido
- ✅ Deploy automático desde GitHub
- ✅ URL pública automática
- ✅ No se duerme

**Pasos:**
1. Ir a [railway.app](https://railway.app)
2. Conectar con GitHub
3. Seleccionar este repositorio
4. Railway detectará automáticamente que es un proyecto Java
5. Agregar base de datos PostgreSQL
6. Configurar variables de entorno

### 2. Render
**Ventajas:**
- ✅ 750 horas/mes gratis
- ✅ PostgreSQL gratuito
- ✅ Deploy automático

**Pasos:**
1. Ir a [render.com](https://render.com)
2. Conectar con GitHub
3. Crear nuevo "Web Service"
4. Seleccionar este repositorio
5. Configurar:
   - Build Command: `./mvnw clean package -DskipTests`
   - Start Command: `java -jar target/pet_store-0.0.1-SNAPSHOT.jar`

### 3. Heroku
**Limitaciones:**
- ⚠️ Solo 550-1000 horas/mes
- ⚠️ Se duerme después de 30 min de inactividad

## Variables de Entorno Necesarias

```bash
# Base de datos (se configuran automáticamente en Railway/Render)
DATABASE_URL=postgresql://...
DB_USERNAME=...
DB_PASSWORD=...
DB_HOST=...
DB_PORT=5432
DB_NAME=...

# Configuración de la aplicación
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8090
```

## URLs de Acceso

Una vez desplegado, tu API estará disponible en:
- **Railway**: `https://tu-proyecto.railway.app`
- **Render**: `https://tu-proyecto.onrender.com`
- **Heroku**: `https://tu-proyecto.herokuapp.com`

## Endpoints Principales

- `POST /api/users/login` - Login
- `POST /api/users/create` - Crear usuario
- `GET /api/users` - Listar usuarios
- `GET /swagger-ui.html` - Documentación API

## Solución de Problemas

### Error 403 Forbidden
✅ **Solucionado** - El endpoint `/api/users/create` ya no requiere autenticación

### Error de Base de Datos
- Verificar que las variables de entorno estén configuradas
- La base de datos PostgreSQL debe estar creada

### Error de Puerto
- Railway/Render asignan automáticamente el puerto
- La aplicación usa la variable `PORT` del entorno
