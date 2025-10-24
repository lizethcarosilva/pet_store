# 🔐 Sistema de Seguridad y Permisos por Rol

## 📋 Roles del Sistema

### 1. SUPERADMIN
**Descripción**: Administrador del sistema con acceso total  
**Permisos**:
- ✅ Acceso a **TODOS** los endpoints
- ✅ Gestión de tenants (crear, editar, eliminar)
- ✅ Ver todos los datos de todos los tenants
- ✅ Gestión de roles
- ✅ Acceso total sin restricciones

**Endpoints Exclusivos**:
- `/api/tenants/**` - Gestión completa de tenants

---

### 2. ADMIN
**Descripción**: Administrador de un tenant específico  
**Permisos**:
- ✅ Usuarios (CRUD completo de su tenant)
- ✅ Mascotas (CRUD completo de su tenant)
- ✅ Servicios (CRUD completo de su tenant)
- ✅ Productos (CRUD completo de su tenant)
- ✅ Citas (CRUD completo de su tenant)
- ✅ Facturas (CRUD completo de su tenant)
- ✅ Dashboard (ver estadísticas de su tenant)
- ✅ Roles (solo lectura, sin SUPERADMIN)

**Restricciones**:
- ❌ NO puede gestionar tenants
- ❌ NO puede ver el rol SUPERADMIN
- ❌ NO puede modificar datos de otros tenants
- ❌ Solo accede a datos de su propio tenant_id

---

### 3. GERENTE
**Descripción**: Gerente con permisos similares a ADMIN  
**Permisos**:
- ✅ Usuarios (CRUD completo de su tenant)
- ✅ Mascotas (CRUD completo de su tenant)
- ✅ Servicios (CRUD completo de su tenant)
- ✅ Productos (CRUD completo de su tenant)
- ✅ Citas (CRUD completo de su tenant)
- ✅ Facturas (CRUD completo de su tenant)
- ✅ Dashboard (ver estadísticas de su tenant)
- ✅ Roles (solo lectura)

**Restricciones**:
- ❌ NO puede gestionar tenants
- ❌ NO puede ver el rol SUPERADMIN
- ❌ NO puede modificar datos de otros tenants
- ❌ Solo accede a datos de su propio tenant_id

---

### 4. VENDEDOR (EMPLOYEE)
**Descripción**: Empleado con permisos limitados  
**Permisos**:
- ✅ Facturas (CRUD completo)
- ✅ Productos (ver y actualizar stock)
- ✅ Servicios (solo lectura)
- ✅ Citas (ver y crear)
- ✅ Mascotas (solo lectura)
- ✅ Dashboard (ver estadísticas básicas)

**Restricciones**:
- ❌ NO puede gestionar usuarios
- ❌ NO puede eliminar productos o servicios
- ❌ NO puede gestionar tenants
- ❌ NO puede ver roles
- ❌ Solo accede a datos de su propio tenant_id

---

### 5. CLIENTE
**Descripción**: Cliente con acceso muy limitado  
**Permisos**:
- ✅ Dashboard (ver sus propias estadísticas)
- ✅ Mascotas (solo sus propias mascotas)
- ✅ Citas (solo sus propias citas)
- ✅ Facturas (solo sus propias facturas)
- ✅ Servicios (solo lectura)
- ✅ Productos (solo lectura)

**Restricciones**:
- ❌ NO puede ver datos de otros usuarios
- ❌ NO puede crear/editar usuarios
- ❌ NO puede gestionar productos/servicios
- ❌ NO puede ver el dashboard completo del negocio
- ❌ Solo ve sus propios datos dentro de su tenant

---

## 🔒 Implementación de Seguridad

### Validación Multi-Capa

```
┌─────────────────────────────────────┐
│  1. Bearer Token Validation         │
│     - Verifica que el token existe  │
│     - Valida firma JWT              │
└──────────────┬──────────────────────┘
               ↓
┌─────────────────────────────────────┐
│  2. Tenant Validation               │
│     - Extrae tenant_id del token    │
│     - Valida pertenencia al tenant  │
│     - Bloquea acceso cruzado        │
└──────────────┬──────────────────────┘
               ↓
┌─────────────────────────────────────┐
│  3. Role Permission Check           │
│     - Verifica rol del usuario      │
│     - Valida permisos del endpoint  │
│     - Aplica reglas de negocio      │
└──────────────┬──────────────────────┘
               ↓
┌─────────────────────────────────────┐
│  4. Data Access Control             │
│     - Filtra datos por tenant_id    │
│     - Aplica restricciones de rol   │
│     - Retorna solo datos permitidos │
└─────────────────────────────────────┘
```

---

## 📡 Formato de Requests

### Headers Requeridos
```http
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

### Body (JSON)
Todos los endpoints reciben datos en formato JSON:

```json
{
  "campo1": "valor1",
  "campo2": "valor2"
}
```

**NO se usan más**:
- ❌ `@RequestBody Integer id`
- ❌ `@RequestBody String campo`

**SE USA**:
- ✅ `@RequestBody RequestDto dto`
- ✅ `@RequestParam` para query params

---

## 🎯 Matriz de Permisos por Endpoint

| Endpoint | SUPERADMIN | ADMIN | GERENTE | VENDEDOR | CLIENTE |
|----------|------------|-------|---------|----------|---------|
| **Tenants** |
| `/api/tenants/**` | ✅ | ❌ | ❌ | ❌ | ❌ |
| **Usuarios** |
| `/api/users` (GET) | ✅ | ✅ | ✅ | ❌ | ❌ |
| `/api/users/create` | ✅ | ✅ | ✅ | ❌ | ❌ |
| `/api/users/update` | ✅ | ✅ | ✅ | ❌ | ❌ |
| `/api/users/delete` | ✅ | ✅ | ✅ | ❌ | ❌ |
| **Mascotas** |
| `/api/pets` (GET) | ✅ | ✅ | ✅ | ✅ | ✅* |
| `/api/pets/create` | ✅ | ✅ | ✅ | ❌ | ❌ |
| `/api/pets/update` | ✅ | ✅ | ✅ | ❌ | ❌ |
| `/api/pets/delete` | ✅ | ✅ | ✅ | ❌ | ❌ |
| **Servicios** |
| `/api/services` (GET) | ✅ | ✅ | ✅ | ✅ | ✅ |
| `/api/services/create` | ✅ | ✅ | ✅ | ❌ | ❌ |
| `/api/services/update` | ✅ | ✅ | ✅ | ❌ | ❌ |
| `/api/services/delete` | ✅ | ✅ | ✅ | ❌ | ❌ |
| **Productos** |
| `/api/products` (GET) | ✅ | ✅ | ✅ | ✅ | ✅ |
| `/api/products/create` | ✅ | ✅ | ✅ | ❌ | ❌ |
| `/api/products/update` | ✅ | ✅ | ✅ | ✅ | ❌ |
| `/api/products/delete` | ✅ | ✅ | ✅ | ❌ | ❌ |
| **Citas** |
| `/api/appointments` (GET) | ✅ | ✅ | ✅ | ✅ | ✅* |
| `/api/appointments/create` | ✅ | ✅ | ✅ | ✅ | ✅ |
| `/api/appointments/update` | ✅ | ✅ | ✅ | ✅ | ❌ |
| `/api/appointments/delete` | ✅ | ✅ | ✅ | ❌ | ❌ |
| **Facturas** |
| `/api/invoices` (GET) | ✅ | ✅ | ✅ | ✅ | ✅* |
| `/api/invoices/create` | ✅ | ✅ | ✅ | ✅ | ❌ |
| `/api/invoices/update` | ✅ | ✅ | ✅ | ✅ | ❌ |
| `/api/invoices/cancel` | ✅ | ✅ | ✅ | ❌ | ❌ |
| **Dashboard** |
| `/api/dashboard/summary` | ✅ | ✅ | ✅ | ✅ | ✅* |
| `/api/dashboard/**` | ✅ | ✅ | ✅ | ✅ | ✅* |
| **Roles** |
| `/api/roles` (GET) | ✅ | ✅ | ✅ | ❌ | ❌ |

**Leyenda**:
- ✅ = Acceso completo
- ✅* = Acceso limitado (solo sus propios datos)
- ❌ = Sin acceso

---

## 🔐 Validación de Tenant

### Regla de Oro
> **NADIE puede ver o modificar datos de un tenant diferente al suyo**

### Implementación
```java
// 1. El token JWT contiene el tenant_id
// 2. El JwtAuthenticationFilter extrae tenant_id
// 3. TenantContext.setTenantId(tenantId) lo almacena
// 4. Todos los queries filtran por tenant_id automáticamente
```

### Ejemplo de Query Seguro
```java
@Query("SELECT p FROM Product p WHERE p.tenantId = :tenantId AND p.activo = true")
List<Product> findByTenantId(@Param("tenantId") String tenantId);
```

---

## 🚀 Uso del Sistema

### 1. Login
```bash
POST /api/users/login
{
  "correo": "admin@tenant1.com",
  "password": "password123"
}
```

**Response**:
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "userId": 1,
  "name": "Admin User",
  "correo": "admin@tenant1.com",
  "rolId": "ADMIN",
  "tenantId": "TENANT001"
}
```

### 2. Request Autenticado
```bash
GET /api/products
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**El sistema automáticamente**:
- ✅ Valida el token
- ✅ Extrae tenant_id del token
- ✅ Filtra productos solo del tenant del usuario
- ✅ Valida permisos del rol

---

## ⚠️ Seguridad Crítica

### Protecciones Implementadas

1. **Bearer Token Obligatorio**
   - Todos los endpoints (excepto login/registro) requieren token

2. **Validación de Tenant**
   - Imposible acceder a datos de otros tenants
   - El tenant_id viene del token, no del request

3. **Permisos por Rol**
   - Validación automática con `@RequiresRole`
   - Respuesta 403 Forbidden si no tiene permisos

4. **Datos Filtrados**
   - Todos los queries incluyen filtro por tenant_id
   - Los clientes solo ven sus propios datos

---

## 📝 Ejemplo de Implementación

### Controller con Seguridad
```java
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @GetMapping
    @RequiresRole({"SUPERADMIN", "ADMIN", "GERENTE", "VENDEDOR", "CLIENTE"})
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        // El tenantId se obtiene automáticamente del contexto
        String tenantId = TenantContext.getTenantId();
        List<ProductResponseDto> products = productService.getProductsByTenant(tenantId);
        return ResponseEntity.ok(products);
    }

    @PostMapping("/create")
    @RequiresRole({"SUPERADMIN", "ADMIN", "GERENTE"})
    public ResponseEntity<?> createProduct(@RequestBody ProductCreateDto dto) {
        // Solo ADMIN y GERENTE pueden crear
        ProductResponseDto product = productService.createProduct(dto);
        return ResponseEntity.ok(product);
    }
}
```

---

**Sistema de Seguridad Implementado y Documentado** 🎉  
**Versión**: 2.1.0 (Security + Multi-Tenant)

