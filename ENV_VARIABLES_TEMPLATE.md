# 🔐 PLANTILLA DE VARIABLES DE ENTORNO

## Para Desarrollo Local (.env)

Crea un archivo `.env` en la raíz del proyecto con estas variables:

```env
# BASE DE DATOS LOCAL
DB_HOST=localhost
DB_PORT=5432
DB_NAME=petstore
DB_USERNAME=postgres
DB_PASSWORD=password

# CONFIGURACIÓN JPA
DDL_AUTO=update
SHOW_SQL=true
DEBUG=true

# SERVIDOR
PORT=8090

# JWT CONFIGURATION
JWT_SECRET=development_secret_key_change_in_production_minimum_32_characters_required
JWT_EXPIRATION=86400000

# SPRING PROFILES
# SPRING_PROFILES_ACTIVE=default
```

## Para Railway (Configurar en Dashboard)

Configura estas variables en **Railway Dashboard → Tu Servicio → Variables**:

### ✅ Variables ya configuradas (verificar)

```env
# Base de Datos PostgreSQL Railway
DATABASE_URL=postgresql://postgres:ykGNQOBYJxFnkLccKACNAvIBJZUuvYXO@postgres.railway.internal:5432/railway
POSTGRES_USER=postgres
POSTGRES_PASSWORD=ykGNQOBYJxFnkLccKACNAvIBJZUuvYXO
POSTGRES_DB=railway

# Compatibilidad
DB_HOST=postgres.railway.internal
DB_PORT=5432
DB_NAME=railway
DB_USERNAME=postgres
DB_PASSWORD=ykGNQOBYJxFnkLccKACNAvIBJZUuvYXO

# Configuración JPA
DDL_AUTO=update
SHOW_SQL=false

# Puerto
PORT=8090

# Perfil de Spring
SPRING_PROFILES_ACTIVE=railway

# Debug
DEBUG=false
```

### ⚠️ Variables adicionales REQUERIDAS

Agrega estas variables en Railway:

```env
# JWT Secret - CAMBIAR ESTE VALOR
JWT_SECRET=TuClaveSecretaSuperSeguraDeAlMenos32CaracteresParaProduccion123456789

# JWT Expiration (24 horas)
JWT_EXPIRATION=86400000

# Timezone
TZ=America/Bogota

# Java Options
JAVA_OPTS=-Xmx512m -Xms256m
```

---

## 🔒 NOTAS DE SEGURIDAD

1. **NUNCA** subas el archivo `.env` a GitHub
2. El `.env` está en `.gitignore` por seguridad
3. Usa valores diferentes entre desarrollo y producción
4. Genera un JWT_SECRET seguro para producción:
   ```bash
   # Generar clave aleatoria en Linux/Mac
   openssl rand -base64 32
   
   # O usar un generador online
   # https://generate-secret.vercel.app/32
   ```

---

## 📝 CÓMO USAR

### Desarrollo Local

1. Crea el archivo `.env` en la raíz del proyecto
2. Copia las variables de "Desarrollo Local"
3. Ajusta los valores según tu configuración
4. Ejecuta la aplicación con `mvn spring-boot:run`

### Railway

1. Ve a tu proyecto en Railway
2. Click en tu servicio (pet-store-api)
3. Click en la pestaña **Variables**
4. Click en **+ New Variable**
5. Agrega cada variable manualmente
6. Click en **Deploy** después de agregar todas

---

## ✅ VERIFICACIÓN

### Local
```bash
# Verificar que las variables se cargaron
mvn spring-boot:run

# Debería ver en los logs:
# "The following 1 profile is active: "default""
```

### Railway
```bash
# Verificar en los logs de Railway
# "The following 1 profile is active: "railway""
```

