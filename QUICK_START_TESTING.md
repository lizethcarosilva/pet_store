# 🚀 Guía Rápida de Testing - Pet Store

## ⚡ Inicio Rápido (5 minutos)

### Paso 1: Ejecutar Script SQL
```bash
psql -U postgres -d petstore -f MULTI_TENANT_SCHEMA.sql
```

### Paso 2: Iniciar Aplicación
```bash
./mvnw.cmd spring-boot:run
```

Espera a ver:
```
Started PetStoreApplication in X seconds
```

### Paso 3: Verificar Swagger
Abre en navegador:
```
http://localhost:8090/swagger-ui.html
```

---

## 🧪 Tests Básicos

### Test 1: Login ✅
```bash
POST http://localhost:8090/api/users/login
Content-Type: application/json

{
  "correo": "admin@vetsanfrancisco.com",
  "password": "admin123"
}
```

**Respuesta Esperada**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 1,
  "name": "Admin San Francisco",
  "correo": "admin@vetsanfrancisco.com",
  "rolId": "ADMIN",
  "tenantId": "VET001"
}
```

**✅ Guardar el token para los siguientes tests**

---

### Test 2: Obtener Usuarios (Con Token) ✅
```bash
GET http://localhost:8090/api/users
Authorization: Bearer <TU_TOKEN_DEL_LOGIN>
```

**Resultado Esperado**:
- Solo usuarios del tenant VET001
- Sin contraseñas
- Status: 200 OK

---

### Test 3: Crear Usuario ✅
```bash
POST http://localhost:8090/api/users/create
Authorization: Bearer <TU_TOKEN>
Content-Type: application/json

{
  "name": "Dr. Carlos Veterinario",
  "tipoId": "CC",
  "ident": "99999999",
  "correo": "carlos@vetsanfrancisco.com",
  "telefono": "3009876543",
  "direccion": "Calle 123",
  "password": "carlos123",
  "rolId": "VET"
}
```

**Resultado Esperado**:
- Usuario creado con tenant_id = VET001 (automático)
- Password encriptado
- Status: 201 CREATED

---

### Test 4: Verificar Aislamiento de Tenants 🔒

#### 4a. Login como VET001
```bash
POST http://localhost:8090/api/users/login
{
  "correo": "admin@vetsanfrancisco.com",
  "password": "admin123"
}
```
Guardar token como `TOKEN_VET001`

#### 4b. Login como VET002
```bash
POST http://localhost:8090/api/users/login
{
  "correo": "admin@mascotasfelices.com",
  "password": "admin123"
}
```
Guardar token como `TOKEN_VET002`

#### 4c. Obtener usuarios de VET001
```bash
GET http://localhost:8090/api/users
Authorization: Bearer <TOKEN_VET001>
```
**Debe mostrar**: Solo usuarios de VET001

#### 4d. Obtener usuarios de VET002
```bash
GET http://localhost:8090/api/users
Authorization: Bearer <TOKEN_VET002>
```
**Debe mostrar**: Solo usuarios de VET002 (diferentes)

**✅ Esto confirma que los datos están AISLADOS**

---

### Test 5: Validar Permisos por Rol 🔐

#### 5a. Login como CLIENTE
Primero crear un cliente:
```bash
POST http://localhost:8090/api/users/create
Authorization: Bearer <TOKEN_ADMIN_VET001>
{
  "name": "Juan Cliente",
  "correo": "juan@gmail.com",
  "password": "cliente123",
  "ident": "88888888",
  "rolId": "CLIENTE"
}
```

#### 5b. Login como Cliente
```bash
POST http://localhost:8090/api/users/login
{
  "correo": "juan@gmail.com",
  "password": "cliente123"
}
```
Guardar token como `TOKEN_CLIENTE`

#### 5c. Intentar crear usuario (debe fallar)
```bash
POST http://localhost:8090/api/users/create
Authorization: Bearer <TOKEN_CLIENTE>
{
  "name": "Test",
  "correo": "test@test.com",
  "password": "test",
  "ident": "111111"
}
```

**Resultado Esperado**:
- Status: 403 Forbidden
- Error: "No tiene permisos para realizar esta acción"

**✅ Esto confirma que los PERMISOS funcionan**

---

## 🎯 Tests Adicionales

### Test 6: Obtener Roles
```bash
GET http://localhost:8090/api/roles
Authorization: Bearer <TOKEN_ADMIN>
```

**Resultado**: Lista de roles (SIN SUPERADMIN si eres ADMIN)

---

### Test 7: Crear Tenant (Solo SUPERADMIN)
```bash
POST http://localhost:8090/api/tenants/create
{
  "tenantId": "VET004",
  "nombre": "Nueva Veterinaria",
  "nit": "900111222-3",
  "plan": "BASIC"
}
```

**Nota**: Este endpoint solo funciona sin token o con SUPERADMIN

---

### Test 8: Intentar Acceso Sin Token
```bash
GET http://localhost:8090/api/users
# SIN HEADER Authorization
```

**Resultado Esperado**:
- Status: 401 Unauthorized
- No retorna datos

**✅ Esto confirma que la AUTENTICACIÓN es obligatoria**

---

## 🔍 Troubleshooting

### Error: "Token inválido"
- Verificar que el token sea reciente
- Los tokens expiran en 24 horas
- Hacer login nuevamente

### Error: "No tiene permisos"
- Verificar el rol del usuario
- Ver matriz de permisos en SECURITY_ROLES_PERMISSIONS.md

### Error: "Usuario no encontrado"
- Verificar que ejecutaste el script SQL
- Verificar la conexión a PostgreSQL

### Error al iniciar aplicación
- Verificar que PostgreSQL esté corriendo
- Verificar las credenciales en application.properties
- Verificar que la BD "petstore" exista

---

## 📊 Herramientas Recomendadas

### Para Testing:
- **Postman** - Colecciones de requests
- **Insomnia** - Cliente REST
- **cURL** - Línea de comandos
- **Thunder Client** (VS Code) - Extensión

### Configuración en Postman:
1. Crear colección "Pet Store"
2. Agregar variable `{{token}}`
3. Agregar variable `{{baseUrl}}` = `http://localhost:8090`
4. En Authorization: Type = "Bearer Token", Token = `{{token}}`

---

## ✅ Checklist de Verificación

Antes de considerar el backend completo, verificar:

- [ ] La aplicación inicia sin errores
- [ ] Login funciona y retorna token con tenant_id
- [ ] Token se valida en requests protegidos
- [ ] Usuarios solo ven datos de su tenant
- [ ] Permisos por rol funcionan correctamente
- [ ] No se puede acceder a datos de otros tenants
- [ ] Swagger muestra todos los endpoints
- [ ] Base de datos tiene los constraints correctos

---

## 🎓 Datos de Prueba Incluidos

### Tenants Disponibles:
- **VET001**: Veterinaria San Francisco (PREMIUM)
- **VET002**: Mascotas Felices (BASIC)
- **VET003**: Hospital Premium (ENTERPRISE)

### Usuarios de Prueba:
Todos con password: `admin123`
- admin@vetsanfrancisco.com (VET001 - ADMIN)
- admin@mascotasfelices.com (VET002 - ADMIN)
- admin@vetpremium.com (VET003 - ADMIN)

### Servicios (en VET001):
- SRV001 - Consulta General
- SRV002 - Vacunación
- SRV003 - Desparasitación
- SRV004 - Baño y Peluquería
- SRV005 - Cirugía Menor

### Productos (en VET001):
- PRD001 - Croquetas Premium Perro
- PRD002 - Croquetas Premium Gato
- PRD003 - Shampoo Antipulgas
- PRD004 - Collar Antipulgas
- PRD005 - Vitaminas

---

## 🚀 Siguiente Paso

**Ejecuta la aplicación y prueba el login:**
```bash
./mvnw.cmd spring-boot:run
```

Luego abre Swagger:
```
http://localhost:8090/swagger-ui.html
```

Y prueba el endpoint de login con los datos de arriba.

---

**¡Buena suerte con el proyecto!** 🎉

**Versión**: 2.1.0  
**Fecha**: Octubre 2025

