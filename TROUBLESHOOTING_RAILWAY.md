# 🔧 Solución de Problemas - Railway Deployment

## ❌ Error: "Deployment failed during the build process"

### 🔍 Causas Comunes y Soluciones

#### 1. **Problema de Versión de Java**
**Causa**: Java 25 es muy reciente y puede no estar soportado
**Solución**: ✅ **Ya corregido** - Cambiado a Java 17

#### 2. **Problema de Configuración de Nixpacks**
**Causa**: Configuración incorrecta de Nixpacks
**Solución**: ✅ **Ya corregido** - Eliminado `nixpacks.toml` para usar detección automática

#### 3. **Problema de Dependencias**
**Causa**: Dependencias no resueltas durante el build
**Solución**: Verificar que todas las dependencias estén en `pom.xml`

## 🚀 Pasos para Solucionar

### 1. **Verificar Archivos de Configuración**
Asegúrate de que estos archivos estén en tu repositorio:
```
✅ pom.xml (con Java 17)
✅ railway.json
✅ system.properties
✅ Procfile
✅ application-prod.properties
```

### 2. **Configuración de Railway**
1. Ve a tu proyecto en Railway
2. Ve a "Settings" → "Environment"
3. Agrega estas variables:
   ```
   SPRING_PROFILES_ACTIVE=prod
   JAVA_VERSION=17
   ```

### 3. **Re-deploy**
1. En Railway, ve a "Deployments"
2. Haz clic en "Redeploy"
3. O haz un nuevo commit a GitHub

## 🔧 Configuración Alternativa

### Si Railway sigue fallando, usa Render:

#### **Render.com** (Alternativa)
1. Ve a [render.com](https://render.com)
2. Conecta con GitHub
3. Crea "New Web Service"
4. Configuración:
   - **Build Command**: `mvn clean package -DskipTests`
   - **Start Command**: `java -jar target/pet_store-0.0.1-SNAPSHOT.jar`
   - **Environment**: Java 17

### Variables de Entorno para Render:
```
SPRING_PROFILES_ACTIVE=prod
DATABASE_URL=postgresql://...
```

## 🐳 Alternativa con Docker

Si Railway sigue fallando, usa el Dockerfile:

### 1. **Configurar Railway para Docker**
1. En Railway, ve a "Settings"
2. Cambia "Build Command" a: `docker build -t pet-store .`
3. Cambia "Start Command" a: `docker run -p $PORT:8090 pet-store`

### 2. **Verificar Dockerfile**
El `Dockerfile` actual usa Java 17 y Maven directamente:
```dockerfile
FROM openjdk:17-jdk-slim as builder
# ... resto del Dockerfile
```

## 📋 Checklist de Verificación

- [ ] Java 17 en `pom.xml` ✅
- [ ] `railway.json` configurado ✅
- [ ] `system.properties` con Java 17 ✅
- [ ] `Procfile` creado ✅
- [ ] Variables de entorno en Railway
- [ ] PostgreSQL agregado como servicio

## 🆘 Si Nada Funciona

### **Opción 1: Heroku**
1. Ve a [heroku.com](https://heroku.com)
2. Crea nueva app
3. Conecta con GitHub
4. Agrega PostgreSQL addon
5. Deploy automático

### **Opción 2: Render**
1. Ve a [render.com](https://render.com)
2. Más estable que Railway
3. 750 horas/mes gratis

## 🎯 Próximo Paso

**Recomendación**: Intenta con **Render** si Railway sigue fallando. Es más estable y tiene mejor soporte para Java/Spring Boot.

¿Quieres que te ayude a configurar Render como alternativa?
