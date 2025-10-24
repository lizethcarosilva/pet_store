# 🧪 Comandos para Testing - Pet Store

## 🎯 TESTING COMPLETO DEL SISTEMA

---

## 1️⃣ LOGIN Y AUTENTICACIÓN

### Login VET001 (Admin)
```bash
curl -X POST http://localhost:8090/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "correo": "admin@vetsanfrancisco.com",
    "password": "admin123"
  }'
```

**Guardar el token de la respuesta como `TOKEN_VET001`**

---

### Login VET002 (Admin)
```bash
curl -X POST http://localhost:8090/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "correo": "admin@mascotasfelices.com",
    "password": "admin123"
  }'
```

**Guardar el token como `TOKEN_VET002`**

---

## 2️⃣ GESTIÓN DE USUARIOS

### Listar Usuarios de VET001
```bash
curl -X GET http://localhost:8090/api/users \
  -H "Authorization: Bearer <TOKEN_VET001>"
```

### Crear Usuario en VET001
```bash
curl -X POST http://localhost:8090/api/users/create \
  -H "Authorization: Bearer <TOKEN_VET001>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Dr. Carlos Pérez",
    "tipoId": "CC",
    "ident": "11111111",
    "correo": "carlos@vetsanfrancisco.com",
    "telefono": "3001234567",
    "direccion": "Calle 50 #30-20",
    "password": "carlos123",
    "rolId": "VET"
  }'
```

### Obtener Usuario por ID
```bash
curl -X POST http://localhost:8090/api/users/getId \
  -H "Authorization: Bearer <TOKEN_VET001>" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1
  }'
```

### Actualizar Usuario
```bash
curl -X PUT http://localhost:8090/api/users/update \
  -H "Authorization: Bearer <TOKEN_VET001>" \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "name": "Nuevo Nombre",
    "telefono": "3009876543"
  }'
```

---

## 3️⃣ GESTIÓN DE MASCOTAS

### Crear Mascota
```bash
curl -X POST http://localhost:8090/api/pets/create \
  -H "Authorization: Bearer <TOKEN_VET001>" \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Firulais",
    "tipo": "Perro",
    "raza": "Labrador",
    "edad": 3,
    "sexo": "Macho",
    "color": "Dorado",
    "cuidadosEspeciales": "Ninguno",
    "ownerIds": [1]
  }'
```

### Listar Mascotas
```bash
curl -X GET http://localhost:8090/api/pets \
  -H "Authorization: Bearer <TOKEN_VET001>"
```

---

## 4️⃣ GESTIÓN DE PRODUCTOS

### Crear Producto
```bash
curl -X POST http://localhost:8090/api/products/create \
  -H "Authorization: Bearer <TOKEN_VET001>" \
  -H "Content-Type: application/json" \
  -d '{
    "codigo": "PRD010",
    "nombre": "Juguete para Perros",
    "descripcion": "Juguete resistente",
    "presentacion": "Unitario",
    "precio": 25000.00,
    "stock": 50,
    "stockMinimo": 10
  }'
```

### Listar Productos
```bash
curl -X GET http://localhost:8090/api/products \
  -H "Authorization: Bearer <TOKEN_VET001>"
```

### Productos con Stock Bajo
```bash
curl -X GET http://localhost:8090/api/products/lowStock \
  -H "Authorization: Bearer <TOKEN_VET001>"
```

---

## 5️⃣ GESTIÓN DE SERVICIOS

### Crear Servicio
```bash
curl -X POST http://localhost:8090/api/services/create \
  -H "Authorization: Bearer <TOKEN_VET001>" \
  -H "Content-Type: application/json" \
  -d '{
    "codigo": "SRV010",
    "nombre": "Radiografía",
    "descripcion": "Servicio de radiografía digital",
    "precio": 80000.00,
    "duracionMinutos": 30
  }'
```

### Listar Servicios
```bash
curl -X GET http://localhost:8090/api/services \
  -H "Authorization: Bearer <TOKEN_VET001>"
```

---

## 6️⃣ GESTIÓN DE CITAS

### Crear Cita
```bash
curl -X POST http://localhost:8090/api/appointments/create \
  -H "Authorization: Bearer <TOKEN_VET001>" \
  -H "Content-Type: application/json" \
  -d '{
    "petId": 1,
    "serviceId": 1,
    "userId": 1,
    "veterinarianId": 1,
    "fechaHora": "2025-10-25T10:00:00",
    "observaciones": "Control de rutina"
  }'
```

### Citas de Hoy
```bash
curl -X GET http://localhost:8090/api/appointments/today \
  -H "Authorization: Bearer <TOKEN_VET001>"
```

---

## 7️⃣ FACTURACIÓN

### Crear Factura
```bash
curl -X POST http://localhost:8090/api/invoices/create \
  -H "Authorization: Bearer <TOKEN_VET001>" \
  -H "Content-Type: application/json" \
  -d '{
    "clientId": 1,
    "employeeId": 1,
    "descuento": 0.00,
    "impuesto": 0.00,
    "observaciones": "Venta regular",
    "details": [
      {
        "tipo": "PRODUCTO",
        "productId": 1,
        "cantidad": 2,
        "descuento": 0.00
      },
      {
        "tipo": "SERVICIO",
        "serviceId": 1,
        "cantidad": 1,
        "descuento": 0.00
      }
    ]
  }'
```

### Ventas del Día
```bash
curl -X GET http://localhost:8090/api/invoices/sales/today \
  -H "Authorization: Bearer <TOKEN_VET001>"
```

### Top Productos Vendidos
```bash
curl -X GET http://localhost:8090/api/invoices/topProducts \
  -H "Authorization: Bearer <TOKEN_VET001>"
```

---

## 8️⃣ DASHBOARD

### Resumen General
```bash
curl -X GET http://localhost:8090/api/dashboard/summary \
  -H "Authorization: Bearer <TOKEN_VET001>"
```

### Alertas
```bash
curl -X GET http://localhost:8090/api/dashboard/alerts \
  -H "Authorization: Bearer <TOKEN_VET001>"
```

### Estadísticas de Ventas
```bash
curl -X GET http://localhost:8090/api/dashboard/sales/stats \
  -H "Authorization: Bearer <TOKEN_VET001>"
```

---

## 9️⃣ ROLES

### Listar Roles
```bash
curl -X GET http://localhost:8090/api/roles \
  -H "Authorization: Bearer <TOKEN_VET001>"
```

---

## 🔟 TENANTS (Solo SUPERADMIN)

### Listar Todos los Tenants
```bash
curl -X GET http://localhost:8090/api/tenants
```

### Crear Nuevo Tenant
```bash
curl -X POST http://localhost:8090/api/tenants/create \
  -H "Content-Type: application/json" \
  -d '{
    "tenantId": "VET004",
    "nombre": "Veterinaria Nueva",
    "nit": "900444555-6",
    "razonSocial": "Veterinaria Nueva LTDA",
    "direccion": "Calle 80 #50-30",
    "telefono": "3104445555",
    "email": "info@vetnueva.com",
    "plan": "BASIC"
  }'
```

---

## 🔒 TESTS DE SEGURIDAD

### Test 1: Sin Token (Debe Fallar)
```bash
curl -X GET http://localhost:8090/api/users
```
**Esperado**: 401 Unauthorized

---

### Test 2: Token Inválido (Debe Fallar)
```bash
curl -X GET http://localhost:8090/api/users \
  -H "Authorization: Bearer token_inventado_12345"
```
**Esperado**: 401 Unauthorized

---

### Test 3: Rol Sin Permisos (Debe Fallar)
```bash
# Primero crear un CLIENTE
curl -X POST http://localhost:8090/api/users/create \
  -H "Authorization: Bearer <TOKEN_ADMIN>" \
  -d '{
    "name": "Juan Cliente",
    "correo": "cliente@test.com",
    "password": "cliente123",
    "ident": "77777777",
    "rolId": "CLIENTE"
  }'

# Login como cliente
curl -X POST http://localhost:8090/api/users/login \
  -d '{
    "correo": "cliente@test.com",
    "password": "cliente123"
  }'

# Intentar crear usuario (DEBE FALLAR)
curl -X POST http://localhost:8090/api/users/create \
  -H "Authorization: Bearer <TOKEN_CLIENTE>" \
  -d '{...}'
```
**Esperado**: 403 Forbidden

---

### Test 4: Aislamiento de Tenants

#### Paso 1: Crear mascota en VET001
```bash
curl -X POST http://localhost:8090/api/pets/create \
  -H "Authorization: Bearer <TOKEN_VET001>" \
  -d '{
    "nombre": "Max VET001",
    "tipo": "Perro",
    "ownerIds": [1]
  }'
```

#### Paso 2: Listar mascotas con VET001
```bash
curl -X GET http://localhost:8090/api/pets \
  -H "Authorization: Bearer <TOKEN_VET001>"
```
**Debe mostrar**: Max VET001

#### Paso 3: Listar mascotas con VET002
```bash
curl -X GET http://localhost:8090/api/pets \
  -H "Authorization: Bearer <TOKEN_VET002>"
```
**NO debe mostrar**: Max VET001 (es de otro tenant)

**✅ Esto confirma AISLAMIENTO PERFECTO**

---

## 📊 VERIFICACIÓN DE DASHBOARD

### Dashboard Completo
```bash
curl -X GET http://localhost:8090/api/dashboard/summary \
  -H "Authorization: Bearer <TOKEN_VET001>"
```

**Debe retornar**:
```json
{
  "citasHoy": 0,
  "totalProductos": 5,
  "ventasHoy": 0,
  "ventasMes": 0,
  "totalUsuarios": 1,
  "totalMascotas": 0,
  "totalServicios": 5,
  "productosStockBajo": 0,
  "facturasHoy": 0
}
```

---

## ✅ TODOS LOS TESTS PASARON

Si todos estos comandos funcionan correctamente:
- ✅ El sistema está completamente operativo
- ✅ La seguridad funciona correctamente
- ✅ El aislamiento de tenants es perfecto
- ✅ Los permisos por rol funcionan
- ✅ Listo para producción

---

**Happy Testing!** 🧪🎉

