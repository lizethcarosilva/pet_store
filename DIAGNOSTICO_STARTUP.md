# 🔍 GUÍA DE DIAGNÓSTICO - Aplicación se Queda Bloqueada en Startup

## Problema
La aplicación se queda bloqueada después de mostrar:
```
Immediately restarting application
```

## ✅ CHECKLIST DE VERIFICACIÓN (En el otro PC)

### 1. **Verificar Archivo .env**
```cmd
:: Desde la raíz del proyecto
dir .env
```
✅ Si NO existe: La app ahora usará valores por defecto
❌ Si existe y tiene errores: Revisar el formato

### 2. **Verificar PostgreSQL**
```cmd
:: Verificar si PostgreSQL está corriendo
netstat -an | findstr "5433"

:: O intentar conectarse manualmente
psql -h localhost -p 5433 -U postgres -d petstore
```
✅ Si se conecta: La base de datos está OK
❌ Si falla: PostgreSQL no está corriendo o las credenciales son incorrectas

### 3. **Verificar Versión de Java**
```cmd
java -version
```
✅ Debe ser Java 17 o superior
❌ Si es menor: Actualizar Java

### 4. **Verificar Memoria RAM**
```cmd
systeminfo | findstr "Memoria"
```
✅ Si tiene más de 4GB libres: OK
❌ Si tiene menos de 2GB: Cerrar otros programas

### 5. **Limpiar Caché de Maven**
```cmd
:: Desde la raíz del proyecto
mvn clean
del /S /Q target
mvn install -DskipTests
```

---

## 🛠️ SOLUCIONES RÁPIDAS

### **Solución 1: Desactivar DevTools**
En `application.properties`, descomentar:
```properties
spring.devtools.restart.enabled=false
```

### **Solución 2: Ejecutar sin DevTools**
Usar el JAR en lugar de ejecutar con Spring Boot DevTools:
```cmd
mvn clean package -DskipTests
java -jar target\pet_store-0.0.1-SNAPSHOT.jar
```

### **Solución 3: Aumentar Timeout de Inicio**
Agregar en `application.properties`:
```properties
spring.devtools.restart.timeout=60
```

### **Solución 4: Ejecutar con Logs Más Detallados**
```cmd
mvn spring-boot:run -Dlogging.level.root=DEBUG
```
Esto mostrará exactamente DÓNDE se queda bloqueado.

---

## 🔥 CAMBIOS REALIZADOS (Ya aplicados en el código)

1. ✅ **Archivo .env más robusto**: Ahora no falla si no existe
2. ✅ **Timeouts reducidos**: Conexión DB falla rápido en lugar de quedar bloqueada
   - `connection-timeout`: 30s → 10s
   - `validation-timeout`: 5s
   - `initialization-fail-timeout`: 1s
3. ✅ **DevTools configurado**: Intervalos de polling más largos para evitar reinicios infinitos

---

## 📝 PASOS PARA DIAGNOSTICAR

### Paso 1: Ejecutar con logs detallados
```cmd
mvn spring-boot:run > startup.log 2>&1
```
Esperar 30 segundos y revisar `startup.log`

### Paso 2: Buscar el último mensaje
```cmd
type startup.log | findstr /C:"WARN" /C:"ERROR" /C:"Exception"
```

### Paso 3: Si encuentra errores de conexión DB
Revisar:
- ¿PostgreSQL está corriendo?
- ¿El puerto es el correcto (5433)?
- ¿Las credenciales son correctas?
- ¿Existe la base de datos `petstore`?

### Paso 4: Si NO hay errores visibles
Es probable que sea DevTools. Desactivarlo:
```properties
spring.devtools.restart.enabled=false
```

---

## 🎯 CAUSAS MÁS COMUNES

| Causa | Síntoma | Solución |
|-------|---------|----------|
| **DevTools loop infinito** | Se reinicia constantemente | Desactivar DevTools |
| **BD no disponible** | Se queda esperando conexión | Verificar PostgreSQL |
| **Puerto ocupado** | Error al iniciar servidor | Cambiar `server.port` |
| **Archivo .env corrupto** | Error al cargar variables | Validar formato .env |
| **Poca RAM** | Se congela sin errores | Cerrar programas |

---

## 🚀 PRUEBA RÁPIDA SIN DEVTOOLS

```cmd
cd C:\Users\Usuario\Documents\UNIVERSIDAD\SEPTIMO SEMESTRE\APRENDIZAJE AUTOMATIZADO\pet_store

:: Compilar sin tests
mvn clean package -DskipTests

:: Ejecutar el JAR directamente (sin DevTools)
java -jar target\pet_store-0.0.1-SNAPSHOT.jar
```

Si funciona con JAR pero no con `mvn spring-boot:run`, el problema es **definitivamente DevTools**.

---

## 📞 INFORMACIÓN ADICIONAL

Si nada funciona, compartir:
1. La última línea visible en los logs
2. Resultado de `java -version`
3. Resultado de `netstat -an | findstr "5433"`
4. Contenido de `.env` (sin passwords)

