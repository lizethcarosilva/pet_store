# 🔧 Configuración de Variables de Entorno para Railway PostgreSQL

## ✅ **Base de datos PostgreSQL creada exitosamente**

Veo que ya tienes la base de datos PostgreSQL funcionando en Railway. Ahora necesitamos configurar las variables de entorno para que tu aplicación se conecte a esta base de datos.

---

## 🚀 **Pasos para configurar las variables de entorno:**

### **Paso 1: Ir a tu servicio principal**
1. En tu dashboard de Railway, ve a tu servicio principal (el que tiene tu aplicación Java)
2. Click en la pestaña **"Variables"**

### **Paso 2: Agregar las variables de entorno**
Agrega estas variables de entorno:

```
SPRING_PROFILES_ACTIVE=railway
DEBUG=false
DDL_AUTO=update
SHOW_SQL=false
```

### **Paso 3: Railway configurará automáticamente las variables de la base de datos**
Railway ya debería haber configurado automáticamente estas variables:
```
DATABASE_URL=postgresql://user:password@host:port/database
DB_HOST=host
DB_PORT=5432
DB_NAME=database
DB_USERNAME=user
DB_PASSWORD=password
```

### **Paso 4: Verificar que las variables estén configuradas**
1. En la pestaña "Variables" de tu servicio principal
2. Deberías ver las variables de la base de datos listadas
3. Si no las ves, Railway las configurará automáticamente

---

## 🌐 **URLs de tu proyecto:**

```
Base URL: https://petstore-production-0d9a.up.railway.app
Swagger UI: https://petstore-production-0d9a.up.railway.app/swagger-ui.html
API Docs: https://petstore-production-0d9a.up.railway.app/api-docs
Health Check: https://petstore-production-0d9a.up.railway.app/actuator/health
```

---

## 🧪 **Cómo probar que funciona:**

### **1. Probar Health Check:**
Abre tu navegador y ve a:
```
https://petstore-production-0d9a.up.railway.app/actuator/health
```

Debería devolver:
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP"
    }
  }
}
```

### **2. Probar Swagger UI:**
Abre tu navegador y ve a:
```
https://petstore-production-0d9a.up.railway.app/swagger-ui.html
```

Deberías ver la interfaz de Swagger con todos tus endpoints.

---

## 📋 **Variables de entorno completas para Railway:**

```
SPRING_PROFILES_ACTIVE=railway
DEBUG=false
DDL_AUTO=update
SHOW_SQL=false
DATABASE_URL=postgresql://user:password@host:port/database
DB_HOST=host
DB_PORT=5432
DB_NAME=database
DB_USERNAME=user
DB_PASSWORD=password
```

---

## 🔍 **Verificación del despliegue:**

### **1. En Railway Dashboard:**
- Ve a tu proyecto en Railway
- Busca la sección "Deployments"
- Verifica que el status sea "Deployed" (verde)

### **2. Verificar logs:**
- Ve a la pestaña "Logs" en Railway
- Busca mensajes como:
  ```
  Started PetStoreApplication in X.XXX seconds
  Tomcat started on port(s): 8090 (http)
  HikariPool-1 - Started
  ```

---

## 🎯 **Próximos pasos:**

1. **Configurar variables de entorno en tu servicio principal**
2. **Railway detectará automáticamente los cambios**
3. **Esperar el nuevo build en Railway**
4. **Verificar que el healthcheck funcione**
5. **Probar los endpoints desde el navegador**

¡Tu backend estará disponible 24/7 de forma completamente gratuita! 🚀
