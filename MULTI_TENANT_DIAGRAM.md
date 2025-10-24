# 📊 Diagrama del Sistema Multi-Tenant

## 🏗️ Arquitectura General

```
┌─────────────────────────────────────────────────────────────────────┐
│                          FRONTEND / CLIENTE                          │
│                                                                       │
│  ┌───────────┐  ┌───────────┐  ┌───────────┐                       │
│  │  VET001   │  │  VET002   │  │  VET003   │                       │
│  │ San Fran. │  │  Mascotas │  │  Hospital │                       │
│  │           │  │  Felices  │  │  Premium  │                       │
│  └─────┬─────┘  └─────┬─────┘  └─────┬─────┘                       │
│        │              │              │                               │
└────────┼──────────────┼──────────────┼───────────────────────────────┘
         │              │              │
         ▼              ▼              ▼
     Header:        Header:        Header:
 X-Tenant-ID:   X-Tenant-ID:   X-Tenant-ID:
    VET001         VET002         VET003
         │              │              │
         └──────────────┴──────────────┘
                        │
                        ▼
┌─────────────────────────────────────────────────────────────────────┐
│                      SPRING BOOT BACKEND                             │
│                                                                       │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │              TenantInterceptor                               │   │
│  │  1. Captura X-Tenant-ID del request                         │   │
│  │  2. Almacena en TenantContext (ThreadLocal)                 │   │
│  │  3. Limpia al finalizar request                             │   │
│  └──────────────────────┬──────────────────────────────────────┘   │
│                         │                                            │
│                         ▼                                            │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                   CONTROLLERS                                │   │
│  │  • TenantController    • UserController                      │   │
│  │  • PetController       • ServiceController                   │   │
│  │  • ProductController   • AppointmentController               │   │
│  │  • InvoiceController   • DashboardController                 │   │
│  └──────────────────────┬──────────────────────────────────────┘   │
│                         │                                            │
│                         ▼                                            │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                     SERVICES                                 │   │
│  │  • Aplica tenant_id en todas las operaciones                │   │
│  │  • Valida pertenencia al tenant                             │   │
│  │  • Lógica de negocio aislada por tenant                     │   │
│  └──────────────────────┬──────────────────────────────────────┘   │
│                         │                                            │
│                         ▼                                            │
│  ┌─────────────────────────────────────────────────────────────┐   │
│  │                   REPOSITORIES (JPA)                         │   │
│  │  • Consultas filtradas por tenant_id                        │   │
│  │  • Índices optimizados                                       │   │
│  └──────────────────────┬──────────────────────────────────────┘   │
│                         │                                            │
└─────────────────────────┼────────────────────────────────────────────┘
                          │
                          ▼
┌─────────────────────────────────────────────────────────────────────┐
│                     BASE DE DATOS POSTGRESQL                         │
│                                                                       │
│  ┌─────────────┐                                                    │
│  │   TENANT    │                                                    │
│  │  VET001 ────┼─┐                                                  │
│  │  VET002 ────┼─┼─┐                                                │
│  │  VET003 ────┼─┼─┼─┐                                              │
│  └─────────────┘ │ │ │                                              │
│                   │ │ │                                              │
│  ┌────────────────▼─▼─▼──────────────────────────────────────┐     │
│  │  DATOS SEGREGADOS POR TENANT_ID                           │     │
│  │  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐    │     │
│  │  │   USER   │ │   PET    │ │ PRODUCT  │ │ SERVICE  │    │     │
│  │  │          │ │          │ │          │ │          │    │     │
│  │  │ VET001:  │ │ VET001:  │ │ VET001:  │ │ VET001:  │    │     │
│  │  │  - Admin │ │  - Pet1  │ │  - Prod1 │ │  - Srv1  │    │     │
│  │  │  - User1 │ │  - Pet2  │ │  - Prod2 │ │  - Srv2  │    │     │
│  │  │          │ │          │ │          │ │          │    │     │
│  │  │ VET002:  │ │ VET002:  │ │ VET002:  │ │ VET002:  │    │     │
│  │  │  - Admin │ │  - Pet3  │ │  - Prod3 │ │  - Srv3  │    │     │
│  │  │  - User2 │ │  - Pet4  │ │  - Prod4 │ │  - Srv4  │    │     │
│  │  │          │ │          │ │          │ │          │    │     │
│  │  │ VET003:  │ │ VET003:  │ │ VET003:  │ │ VET003:  │    │     │
│  │  │  - Admin │ │  - Pet5  │ │  - Prod5 │ │  - Srv5  │    │     │
│  │  │  - User3 │ │  - Pet6  │ │  - Prod6 │ │  - Srv6  │    │     │
│  │  └──────────┘ └──────────┘ └──────────┘ └──────────┘    │     │
│  └─────────────────────────────────────────────────────────────┘     │
│                                                                       │
└───────────────────────────────────────────────────────────────────────┘
```

---

## 🔄 Flujo de una Petición

```
1. Cliente hace request
   ↓
   POST /api/pets/create
   Header: X-Tenant-ID: VET001
   Body: { "nombre": "Firulais", ... }

2. TenantInterceptor.preHandle()
   ↓
   Captura tenant_id del header
   TenantContext.setTenantId("VET001")

3. PetController.createPet()
   ↓
   Recibe el request

4. PetService.createPet()
   ↓
   String tenantId = TenantContext.getTenantId()
   pet.setTenantId(tenantId)  // VET001

5. PetRepository.save()
   ↓
   INSERT INTO pet (tenant_id, nombre, ...) 
   VALUES ('VET001', 'Firulais', ...)

6. Base de Datos
   ↓
   Almacena con tenant_id = 'VET001'

7. TenantInterceptor.afterCompletion()
   ↓
   TenantContext.clear()

8. Response al Cliente
   ↓
   { "petId": 1, "tenantId": "VET001", ... }
```

---

## 🗄️ Modelo de Base de Datos

```
┌─────────────────┐
│     TENANT      │
├─────────────────┤
│ tenant_id (PK)  │◄─────┐
│ nombre          │      │
│ nit (UNIQUE)    │      │
│ plan            │      │
│ activo          │      │
└─────────────────┘      │
                         │
        ┌────────────────┼────────────────┬────────────────┐
        │                │                │                │
┌───────▼─────┐  ┌───────▼─────┐  ┌──────▼──────┐  ┌─────▼──────┐
│    USER     │  │     PET     │  │   PRODUCT   │  │  SERVICE   │
├─────────────┤  ├─────────────┤  ├─────────────┤  ├────────────┤
│ user_id     │  │ pet_id      │  │ product_id  │  │ service_id │
│ tenant_id   │  │ tenant_id   │  │ tenant_id   │  │ tenant_id  │
│ name        │  │ nombre      │  │ codigo      │  │ codigo     │
│ correo      │  │ tipo        │  │ nombre      │  │ nombre     │
│ password    │  │ raza        │  │ precio      │  │ precio     │
│ rol_id      │  │ activo      │  │ stock       │  │ activo     │
│ activo      │  └─────────────┘  │ activo      │  └────────────┘
└───────┬─────┘                   └─────────────┘
        │                                 │
        ├────────────┐           ┌────────┘
        │            │           │
┌───────▼──────┐ ┌──▼───────────▼───┐
│  PET_OWNER   │ │   APPOINTMENT    │
├──────────────┤ ├──────────────────┤
│ pet_id       │ │ appointment_id   │
│ user_id      │ │ tenant_id        │
│ created_on   │ │ pet_id           │
└──────────────┘ │ service_id       │
                 │ user_id          │
                 │ veterinarian_id  │
                 │ fecha_hora       │
                 │ estado           │
                 └──────────────────┘
                          │
                 ┌────────▼────────┐
                 │     INVOICE     │
                 ├─────────────────┤
                 │ invoice_id      │
                 │ tenant_id       │
                 │ numero          │
                 │ client_id       │
                 │ employee_id     │
                 │ total           │
                 │ estado          │
                 └────────┬────────┘
                          │
                 ┌────────▼──────────┐
                 │  INVOICE_DETAIL   │
                 ├───────────────────┤
                 │ detail_id         │
                 │ invoice_id        │
                 │ product_id        │
                 │ service_id        │
                 │ tipo              │
                 │ cantidad          │
                 │ precio_unitario   │
                 └───────────────────┘
```

---

## 🔒 Constraints de Unicidad

```
┌─────────────────────────────────────────────────────────┐
│ ANTES (Sin Multi-Tenancy)                               │
├─────────────────────────────────────────────────────────┤
│ user.correo UNIQUE                                      │
│ product.codigo UNIQUE                                   │
│ service.codigo UNIQUE                                   │
│                                                         │
│ Problema: Solo una empresa puede usar admin@email.com  │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│ AHORA (Con Multi-Tenancy)                               │
├─────────────────────────────────────────────────────────┤
│ (tenant_id, correo) UNIQUE                              │
│ (tenant_id, codigo) UNIQUE                              │
│ (tenant_id, codigo) UNIQUE                              │
│                                                         │
│ ✅ VET001 puede tener admin@email.com                   │
│ ✅ VET002 puede tener admin@email.com                   │
│ ✅ VET003 puede tener admin@email.com                   │
│                                                         │
│ Cada tenant es independiente!                           │
└─────────────────────────────────────────────────────────┘
```

---

## 📊 Índices Optimizados

```
┌──────────────────────────────────────────────────────┐
│ Índices Simples                                      │
├──────────────────────────────────────────────────────┤
│ • idx_user_tenant ON user(tenant_id)                 │
│ • idx_pet_tenant ON pet(tenant_id)                   │
│ • idx_product_tenant ON product(tenant_id)           │
│ • idx_service_tenant ON service(tenant_id)           │
│ • idx_appointment_tenant ON appointment(tenant_id)   │
│ • idx_invoice_tenant ON invoice(tenant_id)           │
└──────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────┐
│ Índices Compuestos (Optimización)                   │
├──────────────────────────────────────────────────────┤
│ • idx_user_tenant_activo                             │
│   ON user(tenant_id, activo)                         │
│                                                      │
│ • idx_product_tenant_activo                          │
│   ON product(tenant_id, activo)                      │
│                                                      │
│ • idx_product_tenant_stock                           │
│   ON product(tenant_id, stock)                       │
│                                                      │
│ • idx_appointment_tenant_fecha                       │
│   ON appointment(tenant_id, fecha_hora)              │
│                                                      │
│ • idx_invoice_tenant_fecha                           │
│   ON invoice(tenant_id, fecha_emision)               │
└──────────────────────────────────────────────────────┘

Ventaja: Las queries por tenant son extremadamente rápidas!
```

---

## 🔐 Seguridad y Aislamiento

```
┌─────────────────────────────────────────────────────────┐
│ Nivel 1: Validación en Request (TenantInterceptor)     │
├─────────────────────────────────────────────────────────┤
│ • Captura tenant_id del header                          │
│ • Valida que el tenant exista                           │
│ • Almacena en ThreadLocal                               │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│ Nivel 2: Validación en Servicios                       │
├─────────────────────────────────────────────────────────┤
│ • Asigna tenant_id a las entidades                      │
│ • Valida que los datos pertenezcan al tenant            │
│ • Previene acceso cruzado                               │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│ Nivel 3: Filtrado en Queries                           │
├─────────────────────────────────────────────────────────┤
│ • WHERE tenant_id = :tenantId                           │
│ • Índices optimizados con tenant_id                     │
│ • Join con verificación de tenant                       │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│ Nivel 4: Constraints de Base de Datos                  │
├─────────────────────────────────────────────────────────┤
│ • Foreign Keys con tenant_id                            │
│ • Unique Constraints compuestos                         │
│ • Check Constraints                                     │
└─────────────────────────────────────────────────────────┘
```

---

## 🎯 Ejemplo de Aislamiento

```
┌──────────────────────────────────────────────────────────┐
│ VET001: Veterinaria San Francisco                       │
├──────────────────────────────────────────────────────────┤
│ Usuarios:                                                │
│  • admin@vetsanfrancisco.com                             │
│  • juan.perez@vetsanfrancisco.com                        │
│                                                          │
│ Mascotas:                                                │
│  • Firulais (Perro)                                      │
│  • Michi (Gato)                                          │
│                                                          │
│ Productos:                                               │
│  • PRD001 - Croquetas (Stock: 50)                        │
│  • PRD002 - Shampoo (Stock: 30)                          │
└──────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────┐
│ VET002: Mascotas Felices                                │
├──────────────────────────────────────────────────────────┤
│ Usuarios:                                                │
│  • admin@mascotasfelices.com                             │
│  • maria.garcia@mascotasfelices.com                      │
│                                                          │
│ Mascotas:                                                │
│  • Rex (Perro)                                           │
│  • Luna (Gato)                                           │
│                                                          │
│ Productos:                                               │
│  • PRD001 - Croquetas (Stock: 100) ← Mismo código       │
│  • PRD003 - Vitaminas (Stock: 20)                        │
└──────────────────────────────────────────────────────────┘

✅ Los datos están completamente separados
✅ Cada tenant puede usar los mismos códigos
✅ No hay posibilidad de acceso cruzado
```

---

## 📈 Consultas por Tenant

```sql
-- ✅ CORRECTO: Filtrado por tenant
SELECT * FROM product 
WHERE tenant_id = 'VET001' 
  AND activo = TRUE;

-- Resultado: Solo productos de VET001

-- ❌ INCORRECTO: Sin filtro de tenant
SELECT * FROM product 
WHERE activo = TRUE;

-- Resultado: Productos de TODOS los tenants (problema de seguridad!)


-- ✅ CORRECTO: Join con validación de tenant
SELECT p.nombre, u.name as dueño
FROM pet p
JOIN pet_owner po ON p.pet_id = po.pet_id
JOIN user u ON po.user_id = u.user_id
WHERE p.tenant_id = 'VET001'
  AND u.tenant_id = 'VET001'
  AND p.activo = TRUE;

-- Resultado: Solo mascotas y dueños de VET001
```

---

## 🚀 Escalabilidad

```
┌────────────────────────────────────────────────────────┐
│ Agregar Nuevo Tenant es Fácil!                        │
├────────────────────────────────────────────────────────┤
│                                                        │
│ 1. Crear registro en tabla tenant                     │
│    INSERT INTO tenant VALUES ('VET004', ...)          │
│                                                        │
│ 2. Crear usuario administrador                        │
│    INSERT INTO user VALUES (..., 'VET004', ...)       │
│                                                        │
│ 3. ¡Listo! El tenant puede empezar a usar el sistema  │
│                                                        │
│ NO se necesita:                                        │
│  ✗ Crear nueva base de datos                          │
│  ✗ Desplegar nueva instancia                          │
│  ✗ Configurar nuevos servidores                       │
│  ✗ Modificar código                                   │
│                                                        │
└────────────────────────────────────────────────────────┘

Capacidad: ✅ Cientos de tenants en una sola instancia
```

---

**Sistema Multi-Tenant Completo y Funcional**  
**Versión**: 2.0.0  
**Fecha**: Octubre 2025

