# Resumen de Datos Insertados en la Base de Datos

## ✅ Inserción Completada Exitosamente

Se han insertado datos de prueba en la base de datos **Pet Store** de manera exitosa.

### 📊 Estadísticas de Registros Insertados

| Tabla | Cantidad de Registros |
|-------|----------------------|
| **Usuarios** | 10 |
| **Clientes** | 80 |
| **Mascotas** | 100 |
| **Servicios** | 20 |
| **Productos** | 50 |
| **Citas** | 150 |
| **Vacunaciones** | 100 |
| **Historias Médicas** | 150 |
| **Facturas** | 200 |
| **Detalles de Facturas** | ~300 (variable) |

### 📝 Descripción de los Datos

#### 1. **Usuarios (10 registros)**
- Roles: ADMIN, VETERINARIO, RECEPCIONISTA, ASISTENTE
- Incluyen: nombre, correo, teléfono, dirección, identificación
- Contraseña por defecto: `hashed_password_123`

#### 2. **Clientes (80 registros)**
- Tipos de documento: CC, TI, CE, NIT
- Datos completos de contacto
- Relaciones con mascotas a través de la tabla `pet_owner`

#### 3. **Mascotas (100 registros)**
- Tipos: perro, gato, ave, conejo, hamster, pez, tortuga, iguana
- Razas específicas según el tipo
- Edades entre 0 y 15 años
- Colores y cuidados especiales
- Cada mascota está asociada a un cliente

#### 4. **Servicios (20 registros)**
- Consultas generales, vacunaciones, desparasitación
- Baño y peluquería, cirugías, hospitalizaciones
- Servicios especializados: radiografías, ecografías, fisioterapia
- Precios entre $15,000 y $200,000 COP
- Duraciones entre 15 y 1440 minutos

#### 5. **Productos (50 registros)**
- Categorías: Alimento, Higiene, Medicamentos, Accesorios, Juguetes
- Incluyen: fabricante, lote, fecha de vencimiento
- Stock entre 10 y 200 unidades
- Marcas: Purina, Royal Canin, Hill's, Pedigree, Whiskas, ProPlan

#### 6. **Citas (150 registros)**
- Rango de fechas: últimos 3 meses y próximos 3 meses
- Estados: PROGRAMADA (30%), COMPLETADA (50%), CANCELADA (10%), EN_PROCESO (5%), FACTURADA (5%)
- Incluyen veterinario asignado, observaciones y diagnósticos
- Horarios entre 8:00 AM y 6:00 PM

#### 7. **Vacunaciones (100 registros)**
- Vacunas: Parvovirus, Moquillo, Rabia, Leptospirosis, Hepatitis, Triple Felina, etc.
- Tipos: Viral, Bacteriana
- Fabricantes: Zoetis, Boehringer Ingelheim, Virbac, MSD Animal Health, Elanco, Bayer, Merial
- Incluyen: sitio de aplicación, número de lote, número de dosis
- Estados: APLICADA (30%), FACTURADA (60%), PENDIENTE (10%)
- Fechas de próxima dosis programadas

#### 8. **Historias Médicas (150 registros)**
- Tipos de procedimiento: Consulta, Vacunación, Desparasitación, Baño, Cirugía, Emergencia, Control
- Relacionadas con citas (70% de los casos)
- Incluyen diagnóstico y tratamiento detallados
- Rango de fechas: último año

#### 9. **Facturas (200 registros)**
- Números consecutivos: FAC-1000 a FAC-1199
- 70% relacionadas con citas, 30% ventas directas
- Estados: PAGADA (85%), PENDIENTE (10%), ANULADA (5%)
- Incluyen: subtotal, descuento, impuestos (19% IVA), total
- Rango de fechas: último año

#### 10. **Detalles de Facturas (~300 registros)**
- Tipos: PRODUCTO, SERVICIO, CITA
- Incluyen cantidad, precio unitario, descuentos
- Relacionados con productos y servicios de las citas

---

## 🔍 Consultas SQL para Verificar los Datos

### Contar registros por tabla:
```sql
SELECT 
    'Usuarios' AS tabla, COUNT(*) AS cantidad FROM "user"
UNION ALL SELECT 'Clientes', COUNT(*) FROM client
UNION ALL SELECT 'Mascotas', COUNT(*) FROM pet
UNION ALL SELECT 'Servicios', COUNT(*) FROM service
UNION ALL SELECT 'Productos', COUNT(*) FROM product
UNION ALL SELECT 'Citas', COUNT(*) FROM appointment
UNION ALL SELECT 'Vacunaciones', COUNT(*) FROM vaccination
UNION ALL SELECT 'Historias Médicas', COUNT(*) FROM pet_medical_history
UNION ALL SELECT 'Facturas', COUNT(*) FROM invoice
UNION ALL SELECT 'Detalles Factura', COUNT(*) FROM invoice_detail;
```

### Ver los servicios más utilizados:
```sql
SELECT 
    s.nombre AS servicio,
    COUNT(a.appointment_id) AS total_citas,
    COUNT(CASE WHEN a.estado = 'COMPLETADA' THEN 1 END) AS completadas
FROM appointment a
JOIN service s ON a.service_id = s.service_id
WHERE a.activo = true
GROUP BY s.nombre
ORDER BY total_citas DESC
LIMIT 10;
```

### Ver tipos de mascotas registradas:
```sql
SELECT 
    tipo,
    COUNT(*) AS cantidad,
    ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM pet), 2) AS porcentaje
FROM pet
WHERE activo = true
GROUP BY tipo
ORDER BY cantidad DESC;
```

### Ver facturación por mes:
```sql
SELECT 
    TO_CHAR(fecha_emision, 'YYYY-MM') AS mes,
    COUNT(*) AS total_facturas,
    SUM(total) AS total_facturado,
    COUNT(CASE WHEN estado = 'PAGADA' THEN 1 END) AS pagadas
FROM invoice
WHERE activo = true
GROUP BY TO_CHAR(fecha_emision, 'YYYY-MM')
ORDER BY mes DESC;
```

### Ver productos más vendidos:
```sql
SELECT 
    p.nombre AS producto,
    COUNT(id.detail_id) AS veces_vendido,
    SUM(id.cantidad) AS cantidad_total
FROM invoice_detail id
JOIN product p ON id.product_id = p.product_id
WHERE id.tipo = 'PRODUCTO'
GROUP BY p.nombre
ORDER BY cantidad_total DESC
LIMIT 10;
```

### Ver veterinarios más activos:
```sql
SELECT 
    u.name AS veterinario,
    COUNT(DISTINCT a.appointment_id) AS citas_atendidas,
    COUNT(DISTINCT v.vaccination_id) AS vacunas_aplicadas
FROM "user" u
LEFT JOIN appointment a ON u.user_id = a.veterinarian_id
LEFT JOIN vaccination v ON u.user_id = v.veterinarian_id
WHERE u.rol_id = 'VETERINARIO'
GROUP BY u.name
ORDER BY citas_atendidas DESC;
```

---

## 📂 Archivos Generados

1. **`insert_test_data.py`** - Script principal para insertar los datos
   - Contiene todas las funciones de inserción
   - Genera datos realistas usando la librería Faker
   - Maneja transacciones y errores correctamente

---

## 🚀 Cómo Ejecutar Nuevamente

Si necesitas insertar más datos o repetir el proceso:

```bash
# Ejecutar el script
py insert_test_data.py
```

**Nota:** El script insertará datos adicionales cada vez que se ejecute. Si deseas limpiar la base de datos primero, deberás ejecutar DELETE statements manualmente.

---

## 🗑️ Cómo Limpiar los Datos (Opcional)

Si deseas eliminar todos los datos insertados:

```sql
-- CUIDADO: Esto eliminará TODOS los datos de las tablas
TRUNCATE TABLE invoice_detail CASCADE;
TRUNCATE TABLE invoice CASCADE;
TRUNCATE TABLE pet_medical_history CASCADE;
TRUNCATE TABLE vaccination CASCADE;
TRUNCATE TABLE appointment CASCADE;
TRUNCATE TABLE pet_owner CASCADE;
TRUNCATE TABLE pet CASCADE;
TRUNCATE TABLE product CASCADE;
TRUNCATE TABLE service CASCADE;
TRUNCATE TABLE client CASCADE;
TRUNCATE TABLE "user" CASCADE;
```

---

## 📊 Uso para Análisis con IA

Estos datos están listos para:

1. **Dashboard de Analytics**
   - Análisis de tendencias de citas por día/hora
   - Servicios más demandados
   - Tipos de mascotas más comunes
   - Ingresos y facturación

2. **Chatbot con IA**
   - Historial médico de mascotas
   - Consultas sobre servicios disponibles
   - Información de productos
   - Estado de citas y facturas

3. **Red Neuronal / Machine Learning**
   - Predicción de demanda de servicios
   - Clasificación de tipos de mascota por servicio
   - Análisis de patrones de vacunación
   - Predicción de ingresos

---

## ✅ Validación de Integridad

Todos los datos insertados tienen:
- ✓ Relaciones correctas entre tablas (FK)
- ✓ Fechas coherentes y realistas
- ✓ Estados válidos según el modelo de negocio
- ✓ Precios y cantidades realistas
- ✓ Tenant ID consistente: `PET001`

---

## 📞 Soporte

Para cualquier problema o consulta sobre los datos insertados, revisar:
- `DATABASE_DOCUMENTATION_FOR_PYTHON.md` - Documentación completa de la base de datos
- `insert_test_data.py` - Código fuente del script de inserción

---

**Fecha de generación:** 3 de Noviembre, 2025
**Base de datos:** PostgreSQL (Railway)
**Tenant ID:** PET001

