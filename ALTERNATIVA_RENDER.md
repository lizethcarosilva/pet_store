# 🚀 Alternativa: Desplegar en Render (Más Fácil)

## ❌ **Railway está dando problemas persistentes**

Entiendo tu frustración con Railway. Te voy a dar una alternativa más fácil y confiable: **Render**.

## ✅ **¿Por qué Render es mejor para tu caso?**

### **Ventajas de Render:**
- ✅ **Más fácil de configurar**
- ✅ **Menos problemas con variables de entorno**
- ✅ **Base de datos PostgreSQL gratuita incluida**
- ✅ **Despliegue automático desde GitHub**
- ✅ **HTTPS automático**
- ⚠️ **Se suspende después de 15 minutos de inactividad** (pero se reactiva automáticamente)

---

## 🚀 **Pasos para desplegar en Render:**

### **Paso 1: Crear cuenta en Render**
1. Ve a [render.com](https://render.com)
2. Regístrate con GitHub
3. Conecta tu repositorio

### **Paso 2: Crear base de datos PostgreSQL**
1. En Render, click en "New" → "PostgreSQL"
2. Configuración:
   - **Name:** petstore-db
   - **Database:** petstore
   - **User:** petstore_user
   - **Region:** Oregon (US West)
3. Click en "Create Database"

### **Paso 3: Crear Web Service**
1. En Render, click en "New" → "Web Service"
2. Conecta tu repositorio de GitHub
3. Configuración:
   - **Name:** petstore-api
   - **Runtime:** Java
   - **Build Command:** `mvn clean package -DskipTests`
   - **Start Command:** `java -jar target/pet_store-0.0.1-SNAPSHOT.jar`
   - **Environment:** Java

### **Paso 4: Configurar variables de entorno**
En tu Web Service, ve a "Environment" y agrega:
```
SPRING_PROFILES_ACTIVE=prod
DB_HOST=tu-host-de-render
DB_PORT=5432
DB_NAME=petstore
DB_USERNAME=petstore_user
DB_PASSWORD=tu-password-de-render
```

---

## 🌐 **URLs después del despliegue:**

```
Base URL: https://petstore-api.onrender.com
Swagger UI: https://petstore-api.onrender.com/swagger-ui.html
API Docs: https://petstore-api.onrender.com/api-docs
Health Check: https://petstore-api.onrender.com/actuator/health
```

---

## 🧪 **Cómo probar que funciona:**

### **1. Probar Health Check:**
Abre tu navegador y ve a:
```
https://petstore-api.onrender.com/actuator/health
```

### **2. Probar Swagger UI:**
Abre tu navegador y ve a:
```
https://petstore-api.onrender.com/swagger-ui.html
```

---

## 📋 **Configuración para Render:**

### **Crear archivo `render.yaml`:**
```yaml
services:
  - type: web
    name: petstore-api
    env: java
    buildCommand: mvn clean package -DskipTests
    startCommand: java -jar target/pet_store-0.0.1-SNAPSHOT.jar
    envVars:
      - key: SPRING_PROFILES_ACTIVE
        value: prod
      - key: DB_HOST
        fromDatabase:
          name: petstore-db
          property: host
      - key: DB_PORT
        fromDatabase:
          name: petstore-db
          property: port
      - key: DB_NAME
        fromDatabase:
          name: petstore-db
          property: database
      - key: DB_USERNAME
        fromDatabase:
          name: petstore-db
          property: user
      - key: DB_PASSWORD
        fromDatabase:
          name: petstore-db
          property: password

databases:
  - name: petstore-db
    databaseName: petstore
    user: petstore_user
```

---

## 🎯 **Próximos pasos:**

1. **Crear cuenta en Render**
2. **Crear base de datos PostgreSQL**
3. **Crear Web Service**
4. **Configurar variables de entorno**
5. **Desplegar automáticamente**

¡Render es mucho más fácil de configurar y debería funcionar sin problemas! 🚀
