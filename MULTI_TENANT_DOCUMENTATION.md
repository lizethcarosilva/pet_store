
# 🏢 Documentación Multi-Tenancy - Pet Store

## 📋 Índice
1. [Introducción](#introducción)
2. [Arquitectura Multi-Tenant](#arquitectura-multi-tenant)
3. [Estructura de Base de Datos](#estructura-de-base-de-datos)
4. [Configuración](#configuración)
5. [Uso del Sistema](#uso-del-sistema)
6. [Ejemplos de Peticiones](#ejemplos-de-peticiones)
7. [Seguridad](#seguridad)
8. [Mejores Prácticas](#mejores-prácticas)

---

## 🎯 Introducción

El sistema Pet Store ahora soporta **Multi-Tenancy**, permitiendo que múltiples empresas veterinarias utilicen la misma instancia de la aplicación con **aislamiento completo de datos**.

### ¿Qué es Multi-Tenancy?

Multi-tenancy es un patrón arquitectónico donde una única instancia de software sirve a múltiples clientes (tenants/inquilinos). Cada tenant tiene:

- ✅ **Datos completamente aislados**
- ✅ **Usuarios independientes**
- ✅ **Configuraciones propias**
- ✅ **Sin acceso a datos de otros tenants**

### Beneficios

1. **Reducción de costos**: Una sola infraestructura para múltiples clientes
2. **Mantenimiento simplificado**: Actualizaciones centralizadas
3. **Escalabilidad**: Agregar nuevos tenants es sencillo
4. **Seguridad**: Aislamiento de datos por tenant

---

## 🏗️ Arquitectura Multi-Tenant

### Componentes Principales

```
┌─────────────────────────────────────────┐
│         Cliente (Frontend)              │
└─────────────────┬───────────────────────┘
                  │ HTTP Request
                  │ Header: X-Tenant-ID: VET001
                  ▼
┌─────────────────────────────────────────┐
│      TenantInterceptor                  │
│  - Captura tenant_id del request        │
│  - Almacena en TenantContext            │
└─────────────────┬───────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────┐
│         Controllers                      │
│  - Usa tenant_id del contexto           │
└─────────────────┬───────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────┐
│         Services                         │
│  - Lógica de negocio                    │
│  - Aplica tenant_id en operaciones      │
└─────────────────┬───────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────┐
│      Repositories (JPA)                  │
│  - Consultas filtradas por tenant_id    │
└─────────────────┬───────────────────────┘
                  │
                  ▼
┌─────────────────────────────────────────┐
│       Base de Datos PostgreSQL           │
│  - Datos segregados por tenant_id        │
└─────────────────────────────────────────┘
```

### Flujo de una Petición

1. **Cliente** envía request con `X-Tenant-ID` en header
2. **TenantInterceptor** captura el tenant_id
3. **TenantContext** almacena tenant_id en ThreadLocal
4. **Controllers/Services** acceden al tenant_id desde el contexto
5. **Repositories** filtran consultas por tenant_id
6. **TenantContext** se limpia al finalizar la petición

---

## 🗄️ Estructura de Base de Datos

### Tabla Tenant (Principal)

```sql
CREATE TABLE tenant (
    tenant_id VARCHAR(50) PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    nit VARCHAR(50) UNIQUE,
    razon_social VARCHAR(255),
    direccion VARCHAR(255),
    telefono VARCHAR(20),
    email VARCHAR(255),
    plan VARCHAR(20) DEFAULT 'BASIC',  -- BASIC, PREMIUM, ENTERPRISE
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_vencimiento TIMESTAMP,
    configuracion VARCHAR(500)
);
```

### Tablas con tenant_id

Todas las tablas principales incluyen `tenant_id`:

- ✅ `user` - Usuarios por tenant
- ✅ `pet` - Mascotas por tenant
- ✅ `service` - Servicios por tenant
- ✅ `product` - Productos por tenant
- ✅ `appointment` - Citas por tenant
- ✅ `invoice` - Facturas por tenant

### Constraints de Unicidad Compuestos

Para garantizar aislamiento, los campos únicos ahora incluyen `tenant_id`:

```sql
-- Ejemplo en tabla user
CONSTRAINT uq_user_tenant_correo UNIQUE (tenant_id, correo),
CONSTRAINT uq_user_tenant_ident UNIQUE (tenant_id, ident)

-- Ejemplo en tabla product
CONSTRAINT uq_product_tenant_codigo UNIQUE (tenant_id, codigo)

-- Ejemplo en tabla service
CONSTRAINT uq_service_tenant_codigo UNIQUE (tenant_id, codigo)
```

Esto permite que:
- Dos tenants diferentes puedan tener usuarios con el mismo email
- Dos tenants diferentes puedan tener productos con el mismo código
- Cada tenant mantiene su propio catálogo independiente

### Índices Optimizados

```sql
-- Índices compuestos para mejor rendimiento
CREATE INDEX idx_user_tenant_activo ON "user"(tenant_id, activo);
CREATE INDEX idx_product_tenant_activo ON product(tenant_id, activo);
CREATE INDEX idx_appointment_tenant_fecha ON appointment(tenant_id, fecha_hora);
```

---

## ⚙️ Configuración

### 1. TenantContext

```java
public class TenantContext {
    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();
    
    public static void setTenantId(String tenantId) {
        CURRENT_TENANT.set(tenantId);
    }
    
    public static String getTenantId() {
        return CURRENT_TENANT.get();
    }
    
    public static void clear() {
        CURRENT_TENANT.remove();
    }
}
```

### 2. TenantInterceptor

Captura el `tenant_id` de cada petición:

```java
@Component
public class TenantInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, ...) {
        String tenantId = request.getHeader("X-Tenant-ID");
        if (tenantId != null) {
            TenantContext.setTenantId(tenantId);
        }
        return true;
    }
    
    @Override
    public void afterCompletion(...) {
        TenantContext.clear();
    }
}
```

### 3. WebConfig

Registra el interceptor:

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Autowired
    private TenantInterceptor tenantInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tenantInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/tenants/**");
    }
}
```

---

## 🚀 Uso del Sistema

### Gestión de Tenants

#### 1. Crear un Tenant

```bash
POST http://localhost:8090/api/tenants/create
Content-Type: application/json

{
  "tenantId": "VET001",
  "nombre": "Veterinaria San Francisco",
  "nit": "900123456-1",
  "razonSocial": "Veterinaria San Francisco S.A.S",
  "direccion": "Calle 50 #30-20",
  "telefono": "3101234567",
  "email": "info@vetsanfrancisco.com",
  "plan": "PREMIUM",
  "fechaVencimiento": "2025-12-31T23:59:59"
}
```

#### 2. Listar Tenants

```bash
GET http://localhost:8090/api/tenants
```

#### 3. Obtener Tenant por ID

```bash
GET http://localhost:8090/api/tenants/VET001
```

---

## 📡 Ejemplos de Peticiones

### Patrón General

**Todas las peticiones a módulos de datos deben incluir el header `X-Tenant-ID`:**

```bash
X-Tenant-ID: VET001
```

### Ejemplo 1: Crear Usuario

```bash
POST http://localhost:8090/api/users/create
X-Tenant-ID: VET001
Content-Type: application/json

{
  "name": "Dr. Juan Pérez",
  "tipoId": "CC",
  "ident": "12345678",
  "correo": "juan.perez@vetsanfrancisco.com",
  "telefono": "3101234567",
  "direccion": "Calle 50 #30-20",
  "password": "password123",
  "rolId": "VET"
}
```

### Ejemplo 2: Crear Producto

```bash
POST http://localhost:8090/api/products/create
X-Tenant-ID: VET001
Content-Type: application/json

{
  "codigo": "PRD001",
  "nombre": "Croquetas Premium Perro",
  "descripcion": "Alimento balanceado",
  "presentacion": "10kg",
  "precio": 85000.00,
  "stock": 50,
  "stockMinimo": 10
}
```

### Ejemplo 3: Crear Cita

```bash
POST http://localhost:8090/api/appointments/create
X-Tenant-ID: VET001
Content-Type: application/json

{
  "petId": 1,
  "serviceId": 1,
  "userId": 2,
  "veterinarianId": 1,
  "fechaHora": "2025-10-25T10:00:00",
  "observaciones": "Control de rutina"
}
```

### Ejemplo 4: Obtener Dashboard

```bash
GET http://localhost:8090/api/dashboard/summary
X-Tenant-ID: VET001
```

### Ejemplo 5: Crear Factura

```bash
POST http://localhost:8090/api/invoices/create
X-Tenant-ID: VET001
Content-Type: application/json

{
  "clientId": 2,
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
}
```

---

## 🔐 Seguridad

### Aislamiento de Datos

1. **Por Query**: Todas las consultas filtran por `tenant_id`
2. **Por Constraint**: Las tablas tienen constraints de FK con tenant
3. **Por Índice**: Índices compuestos optimizan las búsquedas por tenant

### Validaciones Importantes

#### En el Backend (Recomendado)

Agregar validación en servicios:

```java
public PetResponseDto createPet(PetCreateDto petDto) {
    String currentTenant = TenantContext.getTenantId();
    
    if (currentTenant == null || currentTenant.isEmpty()) {
        throw new RuntimeException("Tenant ID requerido");
    }
    
    Pet pet = new Pet();
    pet.setTenantId(currentTenant);  // Asignar tenant del contexto
    // ... resto del código
}
```

#### En Login

El login debe retornar el `tenant_id` del usuario:

```java
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginDto data) {
    User user = userService.getUserByEmail(data.getCorreo());
    
    // Validar credenciales...
    
    LoginResponse response = new LoginResponse();
    response.setUserId(user.getUserId());
    response.setTenantId(user.getTenantId());  // ← Importante
    response.setToken(generateToken(user));
    
    return ResponseEntity.ok(response);
}
```

### Prevención de Acceso Cruzado

**NUNCA permitas que:**
- Un usuario de VET001 vea datos de VET002
- Se cambien datos sin validar el tenant_id
- Se ignoren las validaciones de tenant

---

## ✅ Mejores Prácticas

### 1. Siempre Usar el TenantContext

```java
// ✅ CORRECTO
String tenantId = TenantContext.getTenantId();
pet.setTenantId(tenantId);

// ❌ INCORRECTO
pet.setTenantId("VET001");  // Hardcoded
```

### 2. Filtrar Consultas por Tenant

```java
// ✅ CORRECTO
@Query("SELECT p FROM Pet p WHERE p.tenantId = :tenantId AND p.activo = true")
List<Pet> findByTenantId(@Param("tenantId") String tenantId);

// ❌ INCORRECTO (devuelve datos de todos los tenants)
@Query("SELECT p FROM Pet p WHERE p.activo = true")
List<Pet> findAll();
```

### 3. Validar Tenant en Actualizaciones

```java
public void updatePet(Integer petId, Pet petDetails) {
    String currentTenant = TenantContext.getTenantId();
    Pet pet = petRepository.findById(petId)
        .orElseThrow(() -> new RuntimeException("Pet no encontrado"));
    
    // ✅ Validar que el pet pertenezca al tenant actual
    if (!pet.getTenantId().equals(currentTenant)) {
        throw new RuntimeException("Acceso denegado");
    }
    
    // Continuar con la actualización...
}
```

### 4. Índices para Performance

```sql
-- Siempre crear índices compuestos con tenant_id primero
CREATE INDEX idx_pet_tenant_activo ON pet(tenant_id, activo);
CREATE INDEX idx_product_tenant_stock ON product(tenant_id, stock);
```

### 5. Testing por Tenant

```java
@Test
public void testCreatePet_withDifferentTenants() {
    // Configurar tenant VET001
    TenantContext.setTenantId("VET001");
    Pet pet1 = petService.createPet(petDto);
    
    // Configurar tenant VET002
    TenantContext.setTenantId("VET002");
    Pet pet2 = petService.createPet(petDto);
    
    // Verificar aislamiento
    TenantContext.setTenantId("VET001");
    List<Pet> petsVET001 = petService.getAllPets();
    
    assertFalse(petsVET001.contains(pet2));  // pet2 no debe aparecer
}
```

---

## 🔄 Migración de Datos Existentes

Si ya tienes datos sin tenant_id:

```sql
-- 1. Agregar columna tenant_id (si no existe)
ALTER TABLE "user" ADD COLUMN tenant_id VARCHAR(50);

-- 2. Crear un tenant por defecto
INSERT INTO tenant (tenant_id, nombre, activo) 
VALUES ('DEFAULT', 'Tenant Por Defecto', TRUE);

-- 3. Asignar datos existentes al tenant por defecto
UPDATE "user" SET tenant_id = 'DEFAULT' WHERE tenant_id IS NULL;
UPDATE pet SET tenant_id = 'DEFAULT' WHERE tenant_id IS NULL;
UPDATE product SET tenant_id = 'DEFAULT' WHERE tenant_id IS NULL;
UPDATE service SET tenant_id = 'DEFAULT' WHERE tenant_id IS NULL;
UPDATE appointment SET tenant_id = 'DEFAULT' WHERE tenant_id IS NULL;
UPDATE invoice SET tenant_id = 'DEFAULT' WHERE tenant_id IS NULL;

-- 4. Hacer tenant_id NOT NULL
ALTER TABLE "user" ALTER COLUMN tenant_id SET NOT NULL;
ALTER TABLE pet ALTER COLUMN tenant_id SET NOT NULL;
-- ... resto de tablas
```

---

## 📊 Consultas Útiles

### Estadísticas por Tenant

```sql
SELECT 
    t.tenant_id,
    t.nombre,
    t.plan,
    COUNT(DISTINCT u.user_id) as usuarios,
    COUNT(DISTINCT p.pet_id) as mascotas,
    COUNT(DISTINCT pr.product_id) as productos,
    COUNT(DISTINCT s.service_id) as servicios,
    COUNT(DISTINCT a.appointment_id) as citas,
    COUNT(DISTINCT i.invoice_id) as facturas
FROM tenant t
LEFT JOIN "user" u ON t.tenant_id = u.tenant_id
LEFT JOIN pet p ON t.tenant_id = p.tenant_id
LEFT JOIN product pr ON t.tenant_id = pr.tenant_id
LEFT JOIN service s ON t.tenant_id = s.tenant_id
LEFT JOIN appointment a ON t.tenant_id = a.tenant_id
LEFT JOIN invoice i ON t.tenant_id = i.tenant_id
WHERE t.activo = TRUE
GROUP BY t.tenant_id, t.nombre, t.plan
ORDER BY t.tenant_id;
```

### Ventas por Tenant

```sql
SELECT 
    i.tenant_id,
    t.nombre,
    DATE(i.fecha_emision) as fecha,
    COUNT(*) as num_facturas,
    SUM(i.total) as total_ventas
FROM invoice i
JOIN tenant t ON i.tenant_id = t.tenant_id
WHERE i.estado = 'PAGADA'
  AND i.activo = TRUE
GROUP BY i.tenant_id, t.nombre, DATE(i.fecha_emision)
ORDER BY i.tenant_id, fecha DESC;
```

---

## 🎓 Conclusión

El sistema Multi-Tenant de Pet Store permite:

✅ **Múltiples veterinarias** en una sola instancia  
✅ **Datos completamente aislados** entre tenants  
✅ **Escalabilidad** para agregar nuevos clientes  
✅ **Seguridad** mediante filtrado automático  
✅ **Performance optimizado** con índices compuestos  

### Próximos Pasos

1. Implementar Rate Limiting por tenant
2. Agregar métricas de uso por tenant
3. Implementar backup selectivo por tenant
4. Dashboard de administración de tenants
5. Facturación por uso (SaaS)

---

**Versión**: 2.0.0 (Multi-Tenant)  
**Fecha**: Octubre 2025  
**Framework**: Spring Boot 3.5.6

