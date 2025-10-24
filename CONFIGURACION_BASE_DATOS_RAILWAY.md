# 🗄️ Configuración de Base de Datos en Railway

## ❌ **Problema identificado:**
El error `JDBC URL invalid port number: ${DB_PORT}` ocurre porque la aplicación no puede conectarse a la base de datos porque las variables de entorno de la base de datos no están configuradas en Railway.

## ✅ **Solución aplicada:**

### **Cambios realizados:**

1. **Arreglé las variables de la base de datos:**
   - Agregué valores por defecto a todas las variables de la base de datos
   - Ahora la aplicación puede arrancar sin variables de entorno

2. **Configuración optimizada:**
   - Todas las variables de la base de datos tienen valores por defecto
   - La aplicación puede arrancar sin conexión a la base de datos

---

## 🚀 **Pasos para aplicar la solución:**

### **1. Hacer commit de los cambios:**
```bash
git add .
git commit -m "Fix database connection variables with default values"
git push origin main
```

### **2. Crear base de datos PostgreSQL en Railway:**

En tu dashboard de Railway:
1. Ve a tu proyecto
2. Click en "New Service"
3. Selecciona "Database" → "PostgreSQL"
4. Railway creará automáticamente las variables de entorno

### **3. Configurar variables de entorno en Railway:**

En tu servicio principal:
1. Ve a "Variables"
2. Agrega estas variables:

```
SPRING_PROFILES_ACTIVE=railway
DEBUG=false
DDL_AUTO=update
SHOW_SQL=false
```

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
  "status": "UP"
}
```

### **2. Probar Swagger UI:**
Abre tu navegador y ve a:
```
https://petstore-production-0d9a.up.railway.app/swagger-ui.html
```

Deberías ver la interfaz de Swagger con todos tus endpoints.

---

## 📋 **Variables de entorno recomendadas para Railway:**

### **Variables de la aplicación:**
```
SPRING_PROFILES_ACTIVE=railway
DEBUG=false
DDL_AUTO=update
SHOW_SQL=false
```

### **Variables de la base de datos (automáticas):**
```
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
  ```

---

## 🚨 **Si el problema persiste:**

### **Alternativa 1: Verificar la base de datos**
1. Asegúrate de que la base de datos PostgreSQL esté creada
2. Verifica que las variables de entorno de la base de datos estén configuradas

### **Alternativa 2: Usar configuración simplificada**
Si sigues teniendo problemas, puedes usar el archivo `application-railway.properties` que no tiene variables problemáticas.

---

## 🎯 **Próximos pasos:**

1. **Hacer commit de los cambios**
2. **Crear base de datos PostgreSQL en Railway**
3. **Configurar variables de entorno**
4. **Esperar el nuevo build en Railway**
5. **Verificar que el healthcheck funcione**
6. **Probar los endpoints desde el navegador**

¡El problema debería estar solucionado ahora! 🚀
