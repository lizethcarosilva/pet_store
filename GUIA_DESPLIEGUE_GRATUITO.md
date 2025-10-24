# 🚀 Guía de Despliegue Gratuito - Pet Store Backend

## 📋 Opciones de Despliegue Gratuito

### 1. 🏆 Railway (RECOMENDADO)
**✅ La mejor opción - Completamente gratuita y confiable**

#### Características:
- ✅ Base de datos PostgreSQL gratuita
- ✅ No se suspende por inactividad
- ✅ HTTPS automático
- ✅ Despliegue automático desde GitHub
- ✅ 512MB RAM, 1GB storage

#### Pasos para desplegar en Railway:

1. **Crear cuenta en Railway:**
   - Ve a [railway.app](https://railway.app)
   - Regístrate con GitHub

2. **Crear nuevo proyecto:**
   - Click en "New Project"
   - Selecciona "Deploy from GitHub repo"
   - Conecta tu repositorio de GitHub

3. **Configurar la base de datos:**
   - En tu proyecto, click en "New Service"
   - Selecciona "Database" → "PostgreSQL"
   - Railway creará automáticamente las variables de entorno

4. **Configurar variables de entorno:**
   ```
   SPRING_PROFILES_ACTIVE=prod
   PORT=8090
   ```

5. **Desplegar:**
   - Railway detectará automáticamente que es un proyecto Java
   - El despliegue comenzará automáticamente
   - Tu API estará disponible en: `https://tu-proyecto.railway.app`

---

### 2. 🎯 Render (Alternativa)
**✅ Buena opción con algunas limitaciones**

#### Características:
- ✅ Plan gratuito disponible
- ✅ Base de datos PostgreSQL gratuita
- ⚠️ Se suspende después de 15 minutos de inactividad
- ✅ HTTPS automático

#### Pasos para desplegar en Render:

1. **Crear cuenta en Render:**
   - Ve a [render.com](https://render.com)
   - Regístrate con GitHub

2. **Crear Web Service:**
   - Click en "New" → "Web Service"
   - Conecta tu repositorio de GitHub
   - Configuración:
     - **Build Command:** `mvn clean package -DskipTests`
     - **Start Command:** `java -jar target/pet_store-0.0.1-SNAPSHOT.jar`

3. **Crear base de datos:**
   - Click en "New" → "PostgreSQL"
   - Configura las variables de entorno

---

### 3. 🚀 Fly.io (Alternativa avanzada)
**✅ Plan gratuito generoso**

#### Características:
- ✅ 3 apps gratuitas
- ✅ Base de datos PostgreSQL
- ⚠️ Requiere tarjeta de crédito (no cobra)
- ✅ Global deployment

---

## 🔧 Configuración del Proyecto

### Archivos creados para el despliegue:
- ✅ `railway.json` - Configuración específica para Railway
- ✅ `Dockerfile` - Configuración de contenedor
- ✅ `nixpacks.toml` - Configuración de build
- ✅ `application-prod.properties` - Configuración de producción
- ✅ `.gitignore` - Archivos a ignorar

### Cambios realizados:
- ✅ Java version cambiada de 25 a 17 (compatible con la mayoría de plataformas)
- ✅ Configuración de producción optimizada
- ✅ Health checks configurados

---

## 📝 Pasos Finales

### 1. Subir código a GitHub:
```bash
git add .
git commit -m "Configuración para despliegue en la nube"
git push origin main
```

### 2. Desplegar en Railway:
1. Ve a [railway.app](https://railway.app)
2. Crea cuenta con GitHub
3. "New Project" → "Deploy from GitHub repo"
4. Selecciona tu repositorio
5. Railway detectará automáticamente la configuración

### 3. Configurar variables de entorno:
```
SPRING_PROFILES_ACTIVE=prod
PORT=8090
```

### 4. Tu API estará disponible en:
```
https://tu-proyecto.railway.app
```

---

## 🔗 URLs importantes después del despliegue:

- **API Base:** `https://tu-proyecto.railway.app`
- **Swagger UI:** `https://tu-proyecto.railway.app/swagger-ui.html`
- **Health Check:** `https://tu-proyecto.railway.app/actuator/health`

---

## 💡 Consejos adicionales:

1. **Railway es la mejor opción** porque no se suspende por inactividad
2. **El despliegue es automático** cada vez que hagas push a GitHub
3. **La base de datos PostgreSQL** se crea automáticamente
4. **HTTPS está incluido** sin configuración adicional
5. **Los logs** están disponibles en el dashboard de Railway

---

## 🆘 Si tienes problemas:

1. **Revisa los logs** en el dashboard de Railway
2. **Verifica las variables de entorno**
3. **Asegúrate de que el puerto sea dinámico** (usar `${PORT}`)
4. **La base de datos debe estar creada** antes del despliegue

---

¡Tu backend estará disponible 24/7 de forma completamente gratuita! 🎉
