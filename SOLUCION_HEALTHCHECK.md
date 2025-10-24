# 🔧 Solución al Error de Healthcheck

## ❌ **Problema identificado:**
El error `service unavailable` ocurre porque:
1. Tu aplicación no está escuchando en el puerto correcto que Railway inyecta
2. El healthcheck no puede acceder al endpoint `/actuator/health`

## ✅ **Solución aplicada:**

### **Cambios realizados:**

1. **Configuración de puerto dinámico:**
   - Cambié `server.port=8090` por `server.port=${PORT:8090}`
   - Railway inyecta la variable `PORT` automáticamente

2. **Configuración del actuator:**
   - Agregué `management.server.port=${PORT:8090}`
   - Esto asegura que el healthcheck funcione correctamente

3. **Dockerfile actualizado:**
   - Cambié `EXPOSE 8090` por `EXPOSE $PORT`
   - Ahora usa el puerto dinámico de Railway

---

## 🚀 **Pasos para aplicar la solución:**

### **1. Hacer commit de los cambios:**
```bash
git add .
git commit -m "Fix healthcheck - use dynamic PORT from Railway"
git push origin main
```

### **2. Railway detectará automáticamente los cambios:**
- Railway hará un nuevo build automáticamente
- El healthcheck debería funcionar correctamente ahora

### **3. Verificar el despliegue:**
- Ve a tu dashboard de Railway
- Espera a que el status cambie a "Deployed" (verde)
- El healthcheck debería pasar correctamente

---

## 🔍 **Cómo verificar que funciona:**

### **1. En Railway Dashboard:**
- Ve a tu proyecto en Railway
- Busca la sección "Deployments"
- Verifica que el status sea "Deployed" (verde)

### **2. Obtener la URL de tu proyecto:**
- En Railway, ve a tu servicio
- Busca la sección "Settings" o "Domains"
- Ahí encontrarás la URL pública de tu proyecto

### **3. Probar el healthcheck manualmente:**
Una vez que tengas la URL, prueba:
```
https://tu-proyecto.railway.app/actuator/health
```

Debería devolver:
```json
{
  "status": "UP"
}
```

---

## 🌐 **Cómo encontrar la URL de tu proyecto:**

### **Método 1: Desde Railway Dashboard**
1. Ve a [railway.app](https://railway.app)
2. Selecciona tu proyecto
3. Ve a la sección "Settings" o "Domains"
4. Ahí verás la URL pública

### **Método 2: Desde el servicio**
1. En tu proyecto, click en el servicio
2. Ve a la pestaña "Settings"
3. Busca "Domains" o "Public URL"
4. Copia la URL que te dé

### **Método 3: Desde los logs**
1. Ve a la pestaña "Logs" en tu servicio
2. Busca líneas que digan algo como:
   ```
   Server started on port 8090
   Application is running on https://tu-proyecto.railway.app
   ```

---

## 📋 **URLs importantes después del despliegue exitoso:**

```
Base URL: https://tu-proyecto.railway.app
Swagger UI: https://tu-proyecto.railway.app/swagger-ui.html
API Docs: https://tu-proyecto.railway.app/api-docs
Health Check: https://tu-proyecto.railway.app/actuator/health
```

---

## 🎯 **Verificación completa:**

### **Checklist de éxito:**
1. ✅ **Status "Deployed" (verde)** en Railway
2. ✅ **Health check pasa** (no más errores de "service unavailable")
3. ✅ **URL pública generada** y accesible
4. ✅ **Swagger UI carga** correctamente
5. ✅ **Endpoints responden** (aunque sea con error 401 sin autenticación)

---

## 🚨 **Si el problema persiste:**

### **Alternativa 1: Configurar variables de entorno manualmente**
En Railway, ve a tu servicio → Variables y agrega:
```
SPRING_PROFILES_ACTIVE=prod
PORT=8090
```

### **Alternativa 2: Verificar logs**
1. Ve a la pestaña "Logs" en Railway
2. Busca errores relacionados con:
   - Puerto no disponible
   - Base de datos no conectada
   - Configuración incorrecta

### **Alternativa 3: Usar Nixpacks (sin Dockerfile)**
Si el Dockerfile sigue fallando:
1. Elimina el `Dockerfile`
2. Railway usará Nixpacks automáticamente
3. Debería detectar que es un proyecto Java

---

¡El healthcheck debería funcionar correctamente ahora! 🚀
