# ⚡ QUICK START - Despliegue Rápido en Railway

## 🎯 PASOS RÁPIDOS (15 minutos)

### 1️⃣ PREPARAR GITHUB (2 minutos)

```bash
# Agregar archivos nuevos
git add Dockerfile .dockerignore railway.json railway.toml
git add src/main/resources/application-railway.properties
git add RAILWAY_SETUP_GUIDE.md ENV_VARIABLES_TEMPLATE.md
git add docker-compose.yml .gitignore

# Confirmar cambios eliminados
git add -u

# Commit
git commit -m "feat: Docker configuration for Railway deployment"

# Push
git push origin main
```

### 2️⃣ CONFIGURAR RAILWAY (5 minutos)

1. **Ir a Railway**: https://railway.app/dashboard
2. **Abrir tu proyecto** (donde está PostgreSQL)
3. **Agregar servicio**:
   - Click `+ New Service`
   - Select `GitHub Repo`
   - Elegir repositorio `pet_store`
   - Click `Deploy Now`

### 3️⃣ AGREGAR VARIABLES CRÍTICAS (3 minutos)

En **Variables** de tu servicio, agregar:

```env
# SI NO ESTÁN, AGREGAR:
JWT_SECRET=TuClaveSecretaSuperSeguraDeAlMenos32Caracteres
JWT_EXPIRATION=86400000
JAVA_OPTS=-Xmx512m -Xms256m

# VERIFICAR QUE EXISTAN:
SPRING_PROFILES_ACTIVE=railway
DATABASE_URL=postgresql://postgres:PASSWORD@postgres.railway.internal:5432/railway
```

### 4️⃣ GENERAR DOMINIO (1 minuto)

1. En tu servicio → **Settings**
2. Scroll a **Networking**
3. Click **Generate Domain**
4. Copiar la URL

### 5️⃣ VERIFICAR (2 minutos)

```bash
# Reemplazar con tu URL
curl https://tu-app.up.railway.app/actuator/health

# Abrir Swagger en navegador
https://tu-app.up.railway.app/swagger-ui.html
```

---

## ✅ CHECKLIST MÍNIMO

- [ ] Código pusheado a GitHub
- [ ] Servicio conectado en Railway
- [ ] Variable `JWT_SECRET` agregada
- [ ] Variable `SPRING_PROFILES_ACTIVE=railway`
- [ ] Dominio generado
- [ ] Health check pasa ✅

---

## 🚨 SOLUCIÓN RÁPIDA DE PROBLEMAS

### Build falla
```bash
# Verificar que Dockerfile está en GitHub
# Ir a: https://github.com/tu-usuario/pet_store/blob/main/Dockerfile
```

### No conecta a base de datos
```bash
# Verificar DATABASE_URL en Railway
# Debe usar: postgres.railway.internal (NO metro.proxy.rlwy.net)
DATABASE_URL=postgresql://postgres:PASSWORD@postgres.railway.internal:5432/railway
```

### Health check falla
```bash
# Ver logs en Railway Dashboard
# Buscar errores en los últimos logs
# Verificar que iniciò: "Started PetStoreApplication"
```

---

## 📖 DOCUMENTACIÓN COMPLETA

Para guía detallada, ver: `RAILWAY_SETUP_GUIDE.md`

---

## 🎉 ¡LISTO!

Tu aplicación debería estar corriendo en Railway.

**Próximos pasos**:
1. Probar todos los endpoints
2. Configurar GitHub Actions (opcional)
3. Configurar monitoring (opcional)
4. Setup de dominio custom (opcional)

