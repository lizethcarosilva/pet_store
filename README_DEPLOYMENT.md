# 🚀 PET STORE - RAILWAY DEPLOYMENT

## 📖 INICIO RÁPIDO

Este proyecto está configurado para desplegarse automáticamente en Railway usando Docker.

### ⚡ Para Deploy (15 minutos)
Ver: [`QUICK_START.md`](QUICK_START.md)

### 📚 Para Configuración Completa
Ver: [`RAILWAY_SETUP_GUIDE.md`](RAILWAY_SETUP_GUIDE.md)

### 💻 Para Desarrollo Local
Ver: [`LOCAL_DEVELOPMENT.md`](LOCAL_DEVELOPMENT.md)

### 🔐 Variables de Entorno
Ver: [`ENV_VARIABLES_TEMPLATE.md`](ENV_VARIABLES_TEMPLATE.md)

---

## 📁 ARCHIVOS CLAVE

```
pet_store/
├── Dockerfile                          # Imagen Docker optimizada
├── .dockerignore                       # Excluir archivos del build
├── docker-compose.yml                  # Desarrollo local
├── railway.json                        # Config Railway (JSON)
├── railway.toml                        # Config Railway (TOML) ⭐
├── .gitignore                          # Excluir archivos sensibles
│
├── src/main/resources/
│   ├── application.properties          # Config local
│   ├── application-prod.properties     # Config producción
│   └── application-railway.properties  # Config Railway ⭐
│
└── docs/
    ├── QUICK_START.md                  # Inicio rápido
    ├── RAILWAY_SETUP_GUIDE.md          # Guía completa
    ├── LOCAL_DEVELOPMENT.md            # Dev local
    ├── ENV_VARIABLES_TEMPLATE.md       # Variables de entorno
    └── RAILWAY_DEPLOYMENT_SUMMARY.md   # Resumen ejecutivo
```

---

## 🎯 RESUMEN DEL PROCESO

### 1. Deploy a Railway

```bash
# Push a GitHub
git push origin main

# Railway detecta cambios y deploys automáticamente
# Tiempo: ~6-8 minutos primera vez
```

### 2. Desarrollo Local

```bash
# Opción A: Maven
mvn spring-boot:run

# Opción B: Docker
docker-compose up
```

### 3. Workflow Completo

```
LOCAL → COMMIT → PUSH → RAILWAY → PRODUCCIÓN
  ↓       ↓        ↓       ↓          ↓
Develop  Git    GitHub  Auto-Deploy  Live
```

---

## ✅ CHECKLIST PRE-DEPLOY

Antes de hacer tu primer deploy, verifica:

- [ ] Código en GitHub (branch `main`)
- [ ] Variables de entorno configuradas en Railway
- [ ] `JWT_SECRET` configurado
- [ ] `DATABASE_URL` apunta a `postgres.railway.internal`
- [ ] Dominio generado en Railway

---

## 🔧 TECNOLOGÍAS

- **Backend**: Spring Boot 3.5.6
- **Java**: 17
- **Database**: PostgreSQL 16
- **Build**: Maven
- **Container**: Docker (multi-stage)
- **Platform**: Railway
- **CI/CD**: GitHub → Railway (auto-deploy)

---

## 📊 ENDPOINTS PRINCIPALES

```
# Health Check
GET /actuator/health

# API Documentation
GET /swagger-ui.html
GET /api-docs

# Authentication
POST /api/auth/login
POST /api/auth/register

# Resources
GET /api/pets
GET /api/products
GET /api/appointments
GET /api/users
```

---

## 🌐 URLs

### Producción (Railway)
- App: `https://tu-app.up.railway.app`
- Swagger: `https://tu-app.up.railway.app/swagger-ui.html`
- Health: `https://tu-app.up.railway.app/actuator/health`

### Desarrollo Local
- App: `http://localhost:8090`
- Swagger: `http://localhost:8090/swagger-ui.html`
- Health: `http://localhost:8090/actuator/health`

---

## 🆘 TROUBLESHOOTING

### Build falla en Railway
```bash
# Ver logs en: Railway Dashboard → Deployments → Click deployment
# Buscar línea con "ERROR" o "FAILED"
```

### No conecta a base de datos
```bash
# Verificar DATABASE_URL en Railway Variables
# Debe ser: postgres.railway.internal (NO metro.proxy.rlwy.net)
```

### Health check falla
```bash
# Aumentar timeout en railway.toml:
healthcheckTimeout = 200
```

Ver más soluciones en: [`RAILWAY_SETUP_GUIDE.md`](RAILWAY_SETUP_GUIDE.md#8-solución-de-problemas)

---

## 📚 DOCUMENTACIÓN COMPLETA

| Documento | Descripción | Tiempo |
|-----------|-------------|--------|
| [QUICK_START.md](QUICK_START.md) | Inicio rápido para deploy | 15 min |
| [RAILWAY_SETUP_GUIDE.md](RAILWAY_SETUP_GUIDE.md) | Guía completa paso a paso | 30 min |
| [LOCAL_DEVELOPMENT.md](LOCAL_DEVELOPMENT.md) | Configurar entorno local | 20 min |
| [ENV_VARIABLES_TEMPLATE.md](ENV_VARIABLES_TEMPLATE.md) | Plantilla de variables | 5 min |
| [RAILWAY_DEPLOYMENT_SUMMARY.md](RAILWAY_DEPLOYMENT_SUMMARY.md) | Resumen ejecutivo | 10 min |

---

## 🎓 ARQUITECTURA

```
┌──────────┐      ┌──────────┐      ┌──────────┐
│  GitHub  │─────▶│ Railway  │─────▶│   Live   │
│   Push   │      │  Deploy  │      │   App    │
└──────────┘      └──────────┘      └──────────┘
                         │
                         ▼
                  ┌──────────────┐
                  │  PostgreSQL  │
                  │   Railway    │
                  └──────────────┘
```

---

## 🏃 COMANDOS RÁPIDOS

```bash
# === GIT ===
git add .
git commit -m "feat: cambios"
git push origin main

# === MAVEN ===
mvn clean install
mvn spring-boot:run
mvn test

# === DOCKER ===
docker build -t pet-store .
docker-compose up
docker-compose down

# === RAILWAY CLI (opcional) ===
railway login
railway link
railway logs
railway status

# === VERIFICACIÓN ===
curl http://localhost:8090/actuator/health
curl https://tu-app.up.railway.app/actuator/health
```

---

## 🔐 SEGURIDAD

- ✅ Contraseñas encriptadas (BCrypt)
- ✅ JWT Authentication
- ✅ CORS configurado
- ✅ Variables de entorno para secretos
- ✅ Usuario no-root en Docker
- ✅ .env excluido de Git

---

## 📈 MÉTRICAS

Railway proporciona:
- CPU usage
- Memory usage
- Network I/O
- Response times
- Request counts
- Error rates

Acceder en: Railway Dashboard → Tu Servicio → Metrics

---

## 🤝 CONTRIBUIR

1. Fork el proyecto
2. Crear feature branch (`git checkout -b feature/nueva-funcionalidad`)
3. Commit cambios (`git commit -m 'feat: agregar funcionalidad'`)
4. Push a branch (`git push origin feature/nueva-funcionalidad`)
5. Abrir Pull Request

---

## 📝 LICENCIA

Este proyecto es parte de un curso universitario.

---

## 👥 EQUIPO

- **Universidad**: CIPASUNO
- **Semestre**: Séptimo
- **Materia**: Aprendizaje Automatizado

---

## 📞 SOPORTE

Para problemas técnicos:
1. Revisar documentación en `/docs`
2. Ver logs en Railway Dashboard
3. Consultar troubleshooting guides

---

## 🎉 ESTADO DEL PROYECTO

- [x] Aplicación Spring Boot funcional
- [x] Docker configurado
- [x] Railway configurado
- [x] Auto-deploy desde GitHub
- [x] Documentación completa
- [x] Desarrollo local setup
- [ ] Tests automatizados
- [ ] Monitoring avanzado
- [ ] Dominio custom

---

**Version**: 1.0.0  
**Last Updated**: 2025-10-24  
**Platform**: Railway  
**Status**: ✅ Production Ready

---

## 🚀 ¡EMPEZAR AHORA!

```bash
# 1. Lee el Quick Start
cat QUICK_START.md

# 2. Haz push a GitHub
git push origin main

# 3. Configura Railway
# Ver: QUICK_START.md

# 4. ¡Deploy automático!
```

**Tiempo total de configuración**: ~20 minutos

¡Buena suerte! 🎯

