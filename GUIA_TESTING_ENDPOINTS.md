# 🧪 Guía para Probar tu Backend desde Otro PC

## 📋 Cómo verificar que tu backend funciona

### 1. 🔍 **Verificar que el despliegue fue exitoso**

#### En Railway:
1. Ve a tu dashboard de Railway
2. Busca tu proyecto
3. Verifica que el status sea "Deployed" (verde)
4. Anota la URL que te dio Railway: `https://tu-proyecto.railway.app`

### 2. 🌐 **URLs importantes después del despliegue:**

```
Base URL: https://tu-proyecto.railway.app
Swagger UI: https://tu-proyecto.railway.app/swagger-ui.html
API Docs: https://tu-proyecto.railway.app/api-docs
Health Check: https://tu-proyecto.railway.app/actuator/health
```

---

## 🧪 **Cómo probar desde cualquier PC:**

### **Opción 1: Usar el navegador web**
Simplemente abre tu navegador y ve a:
```
https://tu-proyecto.railway.app/swagger-ui.html
```

### **Opción 2: Usar Postman**
1. Descarga Postman
2. Crea una nueva request
3. Usa la URL base: `https://tu-proyecto.railway.app`
4. Prueba los endpoints

### **Opción 3: Usar curl desde terminal**
```bash
# Health check
curl https://tu-proyecto.railway.app/actuator/health

# Obtener documentación de la API
curl https://tu-proyecto.railway.app/api-docs
```

---

## 🔐 **Endpoints principales de tu Pet Store:**

### **Autenticación:**
```
POST https://tu-proyecto.railway.app/api/auth/login
POST https://tu-proyecto.railway.app/api/auth/register
```

### **Mascotas:**
```
GET https://tu-proyecto.railway.app/api/pets
POST https://tu-proyecto.railway.app/api/pets
GET https://tu-proyecto.railway.app/api/pets/{id}
PUT https://tu-proyecto.railway.app/api/pets/{id}
DELETE https://tu-proyecto.railway.app/api/pets/{id}
```

### **Productos:**
```
GET https://tu-proyecto.railway.app/api/products
POST https://tu-proyecto.railway.app/api/products
```

### **Servicios:**
```
GET https://tu-proyecto.railway.app/api/services
POST https://tu-proyecto.railway.app/api/services
```

### **Dashboard:**
```
GET https://tu-proyecto.railway.app/api/dashboard/stats
GET https://tu-proyecto.railway.app/api/dashboard/recent-activities
```

---

## 📱 **Ejemplos de uso desde otro PC:**

### **1. Probar Health Check:**
```bash
curl https://tu-proyecto.railway.app/actuator/health
```
**Respuesta esperada:**
```json
{
  "status": "UP"
}
```

### **2. Ver Swagger UI:**
Abre en el navegador:
```
https://tu-proyecto.railway.app/swagger-ui.html
```
Deberías ver la interfaz de Swagger con todos tus endpoints.

### **3. Probar endpoint de mascotas (sin autenticación):**
```bash
curl https://tu-proyecto.railway.app/api/pets
```

### **4. Probar con autenticación:**
```bash
# Primero hacer login
curl -X POST https://tu-proyecto.railway.app/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"tu_usuario","password":"tu_password"}'

# Luego usar el token en otros endpoints
curl -H "Authorization: Bearer TU_TOKEN_AQUI" \
  https://tu-proyecto.railway.app/api/pets
```

---

## 🔍 **Cómo verificar que funciona correctamente:**

### **Checklist de verificación:**

1. ✅ **Health Check funciona:**
   - Ve a: `https://tu-proyecto.railway.app/actuator/health`
   - Debe devolver: `{"status":"UP"}`

2. ✅ **Swagger UI carga:**
   - Ve a: `https://tu-proyecto.railway.app/swagger-ui.html`
   - Debe mostrar la interfaz de Swagger

3. ✅ **API Docs están disponibles:**
   - Ve a: `https://tu-proyecto.railway.app/api-docs`
   - Debe devolver JSON con la documentación

4. ✅ **Endpoints responden:**
   - Prueba al menos un endpoint GET
   - Debe devolver datos o un error controlado

---

## 🌍 **Acceso desde cualquier PC del mundo:**

Una vez desplegado, tu backend estará disponible globalmente. Cualquier persona puede acceder usando:

```
https://tu-proyecto.railway.app
```

### **Para compartir con otros:**
1. **Comparte la URL base:** `https://tu-proyecto.railway.app`
2. **Comparte Swagger UI:** `https://tu-proyecto.railway.app/swagger-ui.html`
3. **Explica que es una API REST** que pueden consumir

---

## 🚨 **Si algo no funciona:**

### **Problemas comunes:**

1. **Error 404:** La URL no existe o el despliegue falló
2. **Error 500:** Error interno del servidor
3. **Error de CORS:** Problema de configuración (ya está solucionado en tu proyecto)

### **Cómo debuggear:**

1. **Revisa los logs en Railway:**
   - Ve a tu dashboard de Railway
   - Click en "Logs"
   - Busca errores

2. **Prueba el health check primero:**
   ```
   https://tu-proyecto.railway.app/actuator/health
   ```

3. **Verifica que la base de datos esté conectada**

---

## 📊 **Ejemplo de respuesta exitosa:**

### **Health Check:**
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

### **Lista de mascotas:**
```json
[
  {
    "id": 1,
    "name": "Max",
    "species": "Dog",
    "breed": "Golden Retriever",
    "age": 3
  }
]
```

---

¡Tu backend estará disponible 24/7 y accesible desde cualquier PC del mundo! 🌍🚀
