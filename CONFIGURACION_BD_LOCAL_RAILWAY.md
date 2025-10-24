# 🗄️ Configuración de Base de Datos Local en Railway

## ✅ **Base de datos configurada:**
- **Host:** 192.168.1.8
- **Puerto:** 5432 (por defecto)
- **Usuario:** postgres
- **Contraseña:** 12345
- **Base de datos:** petstore (o la que hayas creado)

---

## 🚀 **Pasos para configurar en Railway:**

### **1. Hacer commit de los cambios:**
```bash
git add .
git commit -m "Configure database connection for local PostgreSQL"
git push origin main
```

### **2. Configurar variables de entorno en Railway:**

En tu dashboard de Railway:
1. Ve a tu servicio principal
2. Click en "Variables"
3. Agrega estas variables:

```
SPRING_PROFILES_ACTIVE=railway
DEBUG=false
DDL_AUTO=update
SHOW_SQL=false
DB_HOST=192.168.1.8
DB_PORT=5432
DB_NAME=petstore
DB_USERNAME=postgres
DB_PASSWORD=12345
```

### **3. Railway detectará automáticamente los cambios:**
- Railway hará un nuevo build automáticamente
- La aplicación se conectará a tu base de datos local

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

### **3. Probar endpoints básicos:**
```bash
# Health check
curl https://petstore-production-0d9a.up.railway.app/actuator/health

# API Docs
curl https://petstore-production-0d9a.up.railway.app/api-docs

# Endpoints de mascotas
curl https://petstore-production-0d9a.up.railway.app/api/pets
```

---

## 📋 **Variables de entorno completas para Railway:**

```
SPRING_PROFILES_ACTIVE=railway
DEBUG=false
DDL_AUTO=update
SHOW_SQL=false
DB_HOST=192.168.1.8
DB_PORT=5432
DB_NAME=petstore
DB_USERNAME=postgres
DB_PASSWORD=12345
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

## 🚨 **Si el problema persiste:**

### **Alternativa 1: Verificar conectividad**
1. Asegúrate de que tu base de datos PostgreSQL esté accesible desde internet
2. Verifica que el puerto 5432 esté abierto en tu router/firewall

### **Alternativa 2: Verificar la base de datos**
1. Asegúrate de que la base de datos "petstore" exista
2. Verifica que el usuario "postgres" tenga permisos para conectarse

### **Alternativa 3: Usar configuración simplificada**
Si sigues teniendo problemas, puedes usar el archivo `application-railway.properties` que no tiene variables problemáticas.

---

## 🎯 **Próximos pasos:**

1. **Hacer commit de los cambios**
2. **Configurar variables de entorno en Railway**
3. **Esperar el nuevo build en Railway**
4. **Verificar que el healthcheck funcione**
5. **Probar los endpoints desde el navegador**

¡Tu backend estará disponible 24/7 de forma completamente gratuita! 🚀
