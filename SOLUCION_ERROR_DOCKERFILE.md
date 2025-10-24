# 🔧 Solución al Error del Dockerfile

## ❌ **Problema identificado:**
El error `./mvnw: Permission denied` ocurre porque el archivo `mvnw` no tiene permisos de ejecución en el contenedor Docker.

## ✅ **Solución aplicada:**

### **Cambios realizados:**

1. **Dockerfile simplificado:**
   - Eliminé el uso del wrapper de Maven (`mvnw`)
   - Ahora usa Maven directamente (`mvn`)
   - Más confiable y menos propenso a errores

2. **Configuración optimizada:**
   - Agregué `.dockerignore` para optimizar el build
   - Actualicé `railway.json` para usar Dockerfile builder

### **Archivos modificados:**

#### **Dockerfile (nuevo):**
```dockerfile
FROM eclipse-temurin:17-jdk-alpine
RUN apk add --no-cache maven
WORKDIR /app
COPY pom.xml .
COPY src src
RUN mvn clean package -DskipTests
EXPOSE 8090
CMD ["java", "-jar", "target/pet_store-0.0.1-SNAPSHOT.jar"]
```

#### **railway.json (actualizado):**
```json
{
  "build": {
    "builder": "DOCKERFILE"
  },
  "deploy": {
    "healthcheckPath": "/actuator/health"
  }
}
```

---

## 🚀 **Pasos para aplicar la solución:**

### **1. Hacer commit de los cambios:**
```bash
git add .
git commit -m "Fix Dockerfile - use Maven directly instead of wrapper"
git push origin main
```

### **2. Railway detectará automáticamente los cambios:**
- Railway hará un nuevo build automáticamente
- El build debería funcionar correctamente ahora

### **3. Verificar el despliegue:**
- Ve a tu dashboard de Railway
- Espera a que el status cambie a "Deployed" (verde)
- Anota la URL que te dé Railway

---

## 🔍 **Si el problema persiste:**

### **Alternativa 1: Usar Nixpacks (sin Dockerfile)**
Si el Dockerfile sigue fallando, puedes:

1. **Eliminar el Dockerfile:**
   ```bash
   rm Dockerfile
   ```

2. **Railway usará Nixpacks automáticamente** y detectará que es un proyecto Java

### **Alternativa 2: Usar el Dockerfile alternativo**
Si necesitas usar el wrapper de Maven:

1. **Renombrar el archivo:**
   ```bash
   mv Dockerfile.alternative Dockerfile
   ```

2. **Hacer commit:**
   ```bash
   git add .
   git commit -m "Use alternative Dockerfile"
   git push origin main
   ```

---

## 📋 **Verificación del despliegue exitoso:**

### **Signos de éxito:**
1. ✅ **Status "Deployed" (verde)** en Railway
2. ✅ **URL pública generada** (ej: `https://tu-proyecto.railway.app`)
3. ✅ **Health check funciona:** `https://tu-proyecto.railway.app/actuator/health`
4. ✅ **Swagger UI accesible:** `https://tu-proyecto.railway.app/swagger-ui.html`

### **URLs importantes:**
```
Base URL: https://tu-proyecto.railway.app
Swagger UI: https://tu-proyecto.railway.app/swagger-ui.html
API Docs: https://tu-proyecto.railway.app/api-docs
Health Check: https://tu-proyecto.railway.app/actuator/health
```

---

## 🎯 **Próximos pasos:**

1. **Hacer commit de los cambios**
2. **Esperar el nuevo build en Railway**
3. **Verificar que el despliegue sea exitoso**
4. **Probar los endpoints desde el navegador**

¡El problema debería estar solucionado ahora! 🚀
