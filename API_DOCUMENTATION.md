# Documentación API - Pet Store Backend

## Base URL
```
http://localhost:8090/api
```

## Swagger UI
```
http://localhost:8090/swagger-ui.html
```

---

## 📋 MÓDULOS IMPLEMENTADOS

### 1. **Usuarios (Users)** - `/api/users`
### 2. **Mascotas (Pets)** - `/api/pets`
### 3. **Servicios (Services)** - `/api/services`
### 4. **Productos (Products)** - `/api/products`
### 5. **Citas (Appointments)** - `/api/appointments`
### 6. **Facturas (Invoices)** - `/api/invoices`
### 7. **Dashboard** - `/api/dashboard`

---

## 🔐 MÓDULO: USUARIOS

### Autenticación
- **POST** `/api/users/login` - Iniciar sesión
  - Body: `{ "correo": "string", "password": "string" }`
  - Retorna: Token JWT y datos del usuario

### CRUD Usuarios
- **POST** `/api/users/create` - Crear usuario
- **GET** `/api/users` - Obtener todos los usuarios
- **GET** `/api/users/active` - Obtener usuarios activos
- **GET** `/api/users/getId` - Obtener usuario por ID (Body: `Integer`)
- **GET** `/api/users/search?name={name}` - Buscar usuarios por nombre
- **GET** `/api/users/rolId` - Obtener usuarios por rol (Body: `String`)
- **PUT** `/api/users/update` - Actualizar usuario
- **DELETE** `/api/users/deleteUser` - Eliminar usuario (soft delete)
- **DELETE** `/api/users/userPermanent` - Eliminar usuario permanentemente
- **PUT** `/api/users/userActivate` - Activar usuario
- **PUT** `/api/users/userDeactivate` - Desactivar usuario
- **GET** `/api/users/count` - Contar total de usuarios
- **GET** `/api/users/count/active` - Contar usuarios activos

---

## 🐾 MÓDULO: MASCOTAS

### CRUD Mascotas
- **POST** `/api/pets/create` - Crear mascota
  - Body: `PetCreateDto` con lista de `ownerIds`
- **GET** `/api/pets` - Obtener todas las mascotas
- **GET** `/api/pets/active` - Obtener mascotas activas
- **GET** `/api/pets/getId` - Obtener mascota por ID (Body: `Integer`)
- **GET** `/api/pets/search?name={name}` - Buscar mascotas por nombre
- **GET** `/api/pets/type?tipo={tipo}` - Obtener mascotas por tipo
- **GET** `/api/pets/owner?ownerId={id}` - Obtener mascotas por dueño
- **PUT** `/api/pets/update` - Actualizar mascota
- **POST** `/api/pets/addOwner?petId={id}&ownerId={id}` - Agregar dueño a mascota
- **DELETE** `/api/pets/removeOwner?petId={id}&ownerId={id}` - Remover dueño de mascota
- **DELETE** `/api/pets/deletePet` - Eliminar mascota (soft delete)
- **DELETE** `/api/pets/petPermanent` - Eliminar mascota permanentemente
- **GET** `/api/pets/count` - Contar mascotas activas

### Características Especiales
- Relación **muchos a muchos** con usuarios (dueños)
- Una mascota puede tener múltiples dueños
- Un dueño puede tener múltiples mascotas

---

## 🏥 MÓDULO: SERVICIOS VETERINARIOS

### CRUD Servicios
- **POST** `/api/services/create` - Crear servicio
  - Body: `ServiceCreateDto`
- **GET** `/api/services` - Obtener todos los servicios
- **GET** `/api/services/active` - Obtener servicios activos
- **GET** `/api/services/getId` - Obtener servicio por ID (Body: `Integer`)
- **GET** `/api/services/codigo?codigo={codigo}` - Obtener servicio por código
- **GET** `/api/services/search?name={name}` - Buscar servicios por nombre
- **PUT** `/api/services/update` - Actualizar servicio
- **DELETE** `/api/services/deleteService` - Eliminar servicio (soft delete)
- **DELETE** `/api/services/servicePermanent` - Eliminar servicio permanentemente
- **GET** `/api/services/count` - Contar servicios activos

### Ejemplos de Servicios
- Consulta médica
- Baño y peluquería
- Desparasitación
- Vacunación
- Cirugía
- Hospitalización

---

## 📦 MÓDULO: PRODUCTOS E INVENTARIO

### CRUD Productos
- **POST** `/api/products/create` - Crear producto
  - Body: `ProductCreateDto`
- **GET** `/api/products` - Obtener todos los productos
- **GET** `/api/products/active` - Obtener productos activos
- **GET** `/api/products/getId` - Obtener producto por ID (Body: `Integer`)
- **GET** `/api/products/codigo?codigo={codigo}` - Obtener producto por código
- **GET** `/api/products/search?name={name}` - Buscar productos por nombre
- **GET** `/api/products/lowStock` - Obtener productos con stock bajo
- **GET** `/api/products/expiringSoon` - Obtener productos próximos a vencer (30 días)
- **GET** `/api/products/expiringBefore?fecha={yyyy-MM-dd}` - Productos que vencen antes de fecha
- **PUT** `/api/products/update` - Actualizar producto
- **PUT** `/api/products/updateStock?productId={id}&cantidad={cantidad}` - Actualizar stock
- **DELETE** `/api/products/deleteProduct` - Eliminar producto (soft delete)
- **DELETE** `/api/products/productPermanent` - Eliminar producto permanentemente
- **GET** `/api/products/count` - Contar productos activos
- **GET** `/api/products/count/lowStock` - Contar productos con stock bajo

### Alertas de Inventario
- **Stock bajo**: Cuando stock < stockMinimo
- **Próximo a vencer**: Productos que vencen en menos de 30 días

---

## 📅 MÓDULO: CITAS

### CRUD Citas
- **POST** `/api/appointments/create` - Crear cita
  - Body: `AppointmentCreateDto`
- **GET** `/api/appointments` - Obtener todas las citas
- **GET** `/api/appointments/active` - Obtener citas activas
- **GET** `/api/appointments/getId` - Obtener cita por ID (Body: `Integer`)
- **GET** `/api/appointments/pet?petId={id}` - Obtener citas por mascota
- **GET** `/api/appointments/user?userId={id}` - Obtener citas por usuario
- **GET** `/api/appointments/estado?estado={estado}` - Obtener citas por estado
- **GET** `/api/appointments/today` - Obtener citas del día
- **GET** `/api/appointments/dateRange?inicio={fecha}&fin={fecha}` - Citas en rango de fechas
- **PUT** `/api/appointments/update` - Actualizar cita
- **PUT** `/api/appointments/complete?appointmentId={id}&diagnostico={texto}` - Completar cita
- **PUT** `/api/appointments/cancel?appointmentId={id}` - Cancelar cita
- **DELETE** `/api/appointments/deleteAppointment` - Eliminar cita (soft delete)
- **DELETE** `/api/appointments/appointmentPermanent` - Eliminar cita permanentemente
- **GET** `/api/appointments/count/today` - Contar citas del día

### Estados de Citas
- `PROGRAMADA` - Cita agendada
- `EN_PROCESO` - Cita en curso
- `COMPLETADA` - Cita finalizada con diagnóstico
- `CANCELADA` - Cita cancelada

---

## 💰 MÓDULO: FACTURAS Y VENTAS

### CRUD Facturas
- **POST** `/api/invoices/create` - Crear factura
  - Body: `InvoiceCreateDto` con lista de detalles
  - **Importante**: Reduce automáticamente el stock de productos
- **GET** `/api/invoices` - Obtener todas las facturas
- **GET** `/api/invoices/active` - Obtener facturas activas
- **GET** `/api/invoices/getId` - Obtener factura por ID (Body: `Integer`)
- **GET** `/api/invoices/numero?numero={numero}` - Obtener factura por número
- **GET** `/api/invoices/client?clientId={id}` - Obtener facturas por cliente
- **GET** `/api/invoices/estado?estado={estado}` - Obtener facturas por estado
- **GET** `/api/invoices/dateRange?inicio={fecha}&fin={fecha}` - Facturas en rango
- **PUT** `/api/invoices/updateStatus?invoiceId={id}&estado={estado}` - Actualizar estado
- **PUT** `/api/invoices/cancel?invoiceId={id}` - Anular factura (devuelve stock)
- **DELETE** `/api/invoices/deleteInvoice` - Eliminar factura (soft delete)

### Estadísticas de Ventas
- **GET** `/api/invoices/sales/today` - Total ventas del día
- **GET** `/api/invoices/sales/month` - Total ventas del mes
- **GET** `/api/invoices/topProducts` - Productos más vendidos
- **GET** `/api/invoices/topServices` - Servicios más solicitados
- **GET** `/api/invoices/count/today` - Contar facturas del día

### Estados de Facturas
- `PAGADA` - Factura pagada
- `PENDIENTE` - Factura pendiente de pago
- `ANULADA` - Factura anulada

### Estructura de Factura
```json
{
  "clientId": 1,
  "employeeId": 2,
  "descuento": 0.00,
  "impuesto": 0.00,
  "observaciones": "string",
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
}
```

---

## 📊 MÓDULO: DASHBOARD

### Resumen General
- **GET** `/api/dashboard/summary` - Resumen completo del dashboard
  - Retorna: citas hoy, total productos, ventas día/mes, usuarios, mascotas, servicios, alertas

### Estadísticas por Módulo
- **GET** `/api/dashboard/appointments/stats` - Estadísticas de citas
- **GET** `/api/dashboard/products/stats` - Estadísticas de productos
- **GET** `/api/dashboard/sales/stats` - Estadísticas de ventas
- **GET** `/api/dashboard/users/stats` - Estadísticas de usuarios

### Alertas y Notificaciones
- **GET** `/api/dashboard/alerts` - Todas las alertas (stock bajo y vencimientos)
- **GET** `/api/dashboard/products/lowStock` - Productos con stock bajo
- **GET** `/api/dashboard/products/expiringSoon` - Productos próximos a vencer

### Citas del Día
- **GET** `/api/dashboard/appointments/today` - Listado de citas programadas para hoy

### Top Ventas
- **GET** `/api/dashboard/topProducts` - Top 10 productos más vendidos
- **GET** `/api/dashboard/topServices` - Top 10 servicios más solicitados
- **GET** `/api/dashboard/performance` - Mejor y peor producto en rendimiento

---

## 🗂️ ESTRUCTURA DE DATOS

### Entidades Principales

#### User
```java
{
  "userId": Integer,
  "name": String,
  "tipoId": String,
  "ident": String (único),
  "correo": String (único),
  "telefono": String,
  "direccion": String,
  "rolId": String,
  "activo": Boolean,
  "createdOn": LocalDateTime
}
```

#### Pet
```java
{
  "petId": Integer,
  "nombre": String,
  "tipo": String,
  "raza": String,
  "cuidadosEspeciales": String,
  "edad": Integer,
  "sexo": String,
  "color": String,
  "activo": Boolean,
  "createdOn": LocalDateTime,
  "owners": List<OwnerInfoDto>
}
```

#### Service
```java
{
  "serviceId": Integer,
  "codigo": String (único),
  "nombre": String,
  "descripcion": String,
  "precio": BigDecimal,
  "duracionMinutos": Integer,
  "activo": Boolean,
  "createdOn": LocalDateTime
}
```

#### Product
```java
{
  "productId": Integer,
  "codigo": String (único),
  "nombre": String,
  "descripcion": String,
  "presentacion": String,
  "precio": BigDecimal,
  "stock": Integer,
  "stockMinimo": Integer,
  "fechaVencimiento": LocalDate,
  "activo": Boolean,
  "createdOn": LocalDateTime,
  "lowStock": Boolean (calculado),
  "nearExpiration": Boolean (calculado)
}
```

#### Appointment
```java
{
  "appointmentId": Integer,
  "petId": Integer,
  "petNombre": String,
  "serviceId": Integer,
  "serviceName": String,
  "userId": Integer,
  "userName": String,
  "veterinarianId": Integer (opcional),
  "veterinarianName": String (opcional),
  "fechaHora": LocalDateTime,
  "estado": String,
  "observaciones": String,
  "diagnostico": String,
  "activo": Boolean,
  "createdOn": LocalDateTime
}
```

#### Invoice
```java
{
  "invoiceId": Integer,
  "numero": String (auto-generado: FAC-YYYYMM-000001),
  "clientId": Integer,
  "clientName": String,
  "employeeId": Integer,
  "employeeName": String,
  "fechaEmision": LocalDateTime,
  "subtotal": BigDecimal,
  "descuento": BigDecimal,
  "impuesto": BigDecimal,
  "total": BigDecimal,
  "estado": String,
  "observaciones": String,
  "activo": Boolean,
  "createdOn": LocalDateTime,
  "details": List<InvoiceDetailDto>
}
```

---

## 🔄 RELACIONES ENTRE ENTIDADES

### Relación User ↔ Pet (Muchos a Muchos)
- Un usuario puede ser dueño de varias mascotas
- Una mascota puede tener varios dueños
- Tabla intermedia: `pet_owner`

### Relación Pet → Appointment (Uno a Muchos)
- Una mascota puede tener múltiples citas

### Relación Service → Appointment (Uno a Muchos)
- Un servicio puede estar en múltiples citas

### Relación User → Appointment (Uno a Muchos)
- Un usuario (cliente) puede tener múltiples citas

### Relación Invoice → InvoiceDetail (Uno a Muchos)
- Una factura contiene múltiples detalles

### Relación Product → InvoiceDetail (Uno a Muchos)
- Un producto puede estar en múltiples detalles de factura

### Relación Service → InvoiceDetail (Uno a Muchos)
- Un servicio puede estar en múltiples detalles de factura

---

## ⚙️ FUNCIONALIDADES ESPECIALES

### 1. Gestión de Stock Automática
- Al crear una factura con productos, el stock se reduce automáticamente
- Al anular una factura, el stock se restaura
- Alertas automáticas cuando stock < stockMinimo

### 2. Alertas de Vencimiento
- Sistema de alertas para productos que vencen en 30 días
- Endpoint específico para consultar productos próximos a vencer

### 3. Generación Automática de Números de Factura
- Formato: `FAC-YYYYMM-000001`
- Incremento automático secuencial

### 4. Soft Delete
- Todos los módulos implementan soft delete (campo `activo`)
- Opción de eliminación permanente disponible

### 5. Cálculos Automáticos en Facturas
- Subtotales de detalles
- Descuentos por ítem y generales
- Impuestos
- Total final

### 6. Historial de Citas por Mascota
- Permite consultar todas las citas de una mascota
- Incluye diagnósticos y observaciones

---

## 🚀 INICIO RÁPIDO

### 1. Configurar Base de Datos
Crear base de datos PostgreSQL:
```sql
CREATE DATABASE petstore;
```

### 2. Configurar Variables de Entorno
Crear archivo `.env` en la raíz del proyecto:
```properties
DB_HOST=localhost
DB_PORT=5432
DB_NAME=petstore
DB_USERNAME=postgres
DB_PASSWORD=tu_password
DDL_AUTO=update
SHOW_SQL=true
DEBUG=false
```

### 3. Ejecutar Aplicación
```bash
./mvnw spring-boot:run
```

### 4. Acceder a Swagger
```
http://localhost:8090/swagger-ui.html
```

---

## 📝 NOTAS IMPORTANTES

1. **Autenticación**: El módulo de login está implementado pero sin JWT interceptor. Se recomienda agregar seguridad Spring Security para producción.

2. **Validaciones**: Se implementan validaciones básicas. Se recomienda agregar validaciones más robustas con Bean Validation.

3. **Transacciones**: Todas las operaciones de creación, actualización y eliminación están anotadas con `@Transactional`.

4. **CORS**: Actualmente configurado con `origins = "*"`. Ajustar para producción.

5. **Paginación**: No implementada. Se recomienda agregar para listas grandes.

6. **Filtros Avanzados**: Los filtros actuales son básicos. Se pueden extender según necesidades.

---

## 📧 ENDPOINTS ÚTILES PARA TESTING

### Crear Usuario Admin
```bash
POST http://localhost:8090/api/users/create
{
  "name": "Administrador",
  "tipoId": "CC",
  "ident": "12345678",
  "correo": "admin@petstore.com",
  "telefono": "3001234567",
  "direccion": "Calle 123",
  "password": "admin123",
  "rolId": "ADMIN"
}
```

### Login
```bash
POST http://localhost:8090/api/users/login
{
  "correo": "admin@petstore.com",
  "password": "admin123"
}
```

### Obtener Resumen Dashboard
```bash
GET http://localhost:8090/api/dashboard/summary
```

---

## 🎯 PRÓXIMAS MEJORAS SUGERIDAS

1. Implementar Spring Security con JWT
2. Agregar paginación y ordenamiento
3. Implementar filtros avanzados
4. Agregar búsqueda full-text
5. Implementar caché con Redis
6. Agregar auditoría completa
7. Implementar notificaciones por email
8. Agregar reportes en PDF
9. Implementar backup automático
10. Agregar documentación OpenAPI completa

---

## 📄 LICENCIA

Este proyecto es parte del sistema de gestión veterinaria Pet Store.

---

**Versión**: 1.0.0  
**Fecha**: Octubre 2025  
**Puerto**: 8090  
**Framework**: Spring Boot 3.5.6

