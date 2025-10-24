-- ============================================================
-- SCRIPT COMPLETO DE BASE DE DATOS CON MULTI-TENANCY
-- Sistema de Gestión Veterinaria - Pet Store
-- ============================================================

-- Crear la base de datos
-- CREATE DATABASE petstore;
-- \c petstore;

-- ============================================================
-- TABLA PRINCIPAL: TENANT (EMPRESAS)
-- ============================================================
CREATE TABLE IF NOT EXISTS tenant (
    tenant_id VARCHAR(50) PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    nit VARCHAR(50) UNIQUE,
    razon_social VARCHAR(255),
    direccion VARCHAR(255),
    telefono VARCHAR(20),
    email VARCHAR(255),
    plan VARCHAR(20) DEFAULT 'BASIC',
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_vencimiento TIMESTAMP,
    configuracion VARCHAR(500),
    CONSTRAINT chk_plan CHECK (plan IN ('BASIC', 'PREMIUM', 'ENTERPRISE'))
);

-- Índices para tabla tenant
CREATE INDEX idx_tenant_activo ON tenant(activo);
CREATE INDEX idx_tenant_plan ON tenant(plan);
CREATE INDEX idx_tenant_nit ON tenant(nit);

COMMENT ON TABLE tenant IS 'Empresas/organizaciones que usan el sistema (Multi-tenancy)';
COMMENT ON COLUMN tenant.plan IS 'Plan de suscripción: BASIC, PREMIUM, ENTERPRISE';

-- ============================================================
-- TABLA: ROLES
-- ============================================================
CREATE TABLE IF NOT EXISTS roles (
    rol_id VARCHAR(20) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE roles IS 'Roles de usuarios en el sistema';

-- ============================================================
-- TABLA: USUARIOS (CON TENANT)
-- ============================================================
CREATE TABLE IF NOT EXISTS "user" (
    user_id SERIAL PRIMARY KEY,
    tenant_id VARCHAR(50),
    name VARCHAR(255) NOT NULL,
    tipo_id VARCHAR(10),
    ident VARCHAR(50) NOT NULL,
    correo VARCHAR(255) NOT NULL,
    telefono VARCHAR(20),
    direccion VARCHAR(255),
    password VARCHAR(255) NOT NULL,
    rol_id VARCHAR(20),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES tenant(tenant_id) ON DELETE CASCADE,
    CONSTRAINT uq_user_tenant_correo UNIQUE (tenant_id, correo),
    CONSTRAINT uq_user_tenant_ident UNIQUE (tenant_id, ident)
);

-- Índices para tabla user
CREATE INDEX idx_user_tenant ON "user"(tenant_id);
CREATE INDEX idx_user_correo ON "user"(correo);
CREATE INDEX idx_user_ident ON "user"(ident);
CREATE INDEX idx_user_activo ON "user"(activo);
CREATE INDEX idx_user_rol_id ON "user"(rol_id);
CREATE INDEX idx_user_tenant_activo ON "user"(tenant_id, activo);

COMMENT ON TABLE "user" IS 'Usuarios del sistema (empleados, clientes, veterinarios)';
COMMENT ON COLUMN "user".tenant_id IS 'ID del tenant al que pertenece el usuario';

-- ============================================================
-- TABLA: RELACIÓN TENANT-USER (Multi-tenant support)
-- ============================================================
CREATE TABLE IF NOT EXISTS tenant_user (
    tenant_id VARCHAR(50) NOT NULL,
    user_id INTEGER NOT NULL,
    created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (tenant_id, user_id),
    FOREIGN KEY (tenant_id) REFERENCES tenant(tenant_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES "user"(user_id) ON DELETE CASCADE
);

CREATE INDEX idx_tenant_user_tenant ON tenant_user(tenant_id);
CREATE INDEX idx_tenant_user_user ON tenant_user(user_id);

COMMENT ON TABLE tenant_user IS 'Relación muchos a muchos entre tenants y usuarios';

-- ============================================================
-- TABLA: MASCOTAS (CON TENANT)
-- ============================================================
CREATE TABLE IF NOT EXISTS pet (
    pet_id SERIAL PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    raza VARCHAR(100),
    cuidados_especiales VARCHAR(500),
    edad INTEGER,
    sexo VARCHAR(10),
    color VARCHAR(50),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES tenant(tenant_id) ON DELETE CASCADE
);

-- Índices para tabla pet
CREATE INDEX idx_pet_tenant ON pet(tenant_id);
CREATE INDEX idx_pet_nombre ON pet(nombre);
CREATE INDEX idx_pet_tipo ON pet(tipo);
CREATE INDEX idx_pet_activo ON pet(activo);
CREATE INDEX idx_pet_tenant_activo ON pet(tenant_id, activo);

COMMENT ON TABLE pet IS 'Mascotas registradas en el sistema';
COMMENT ON COLUMN pet.tenant_id IS 'ID del tenant al que pertenece la mascota';

-- ============================================================
-- TABLA: RELACIÓN MASCOTAS-DUEÑOS (Muchos a Muchos)
-- ============================================================
CREATE TABLE IF NOT EXISTS pet_owner (
    pet_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (pet_id, user_id),
    FOREIGN KEY (pet_id) REFERENCES pet(pet_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES "user"(user_id) ON DELETE CASCADE
);

-- Índices para tabla pet_owner
CREATE INDEX idx_pet_owner_pet ON pet_owner(pet_id);
CREATE INDEX idx_pet_owner_user ON pet_owner(user_id);

COMMENT ON TABLE pet_owner IS 'Relación muchos a muchos entre mascotas y dueños';

-- ============================================================
-- TABLA: SERVICIOS VETERINARIOS (CON TENANT)
-- ============================================================
CREATE TABLE IF NOT EXISTS service (
    service_id SERIAL PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    codigo VARCHAR(50) NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    descripcion VARCHAR(500),
    precio DECIMAL(10, 2) NOT NULL,
    duracion_minutos INTEGER,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES tenant(tenant_id) ON DELETE CASCADE,
    CONSTRAINT uq_service_tenant_codigo UNIQUE (tenant_id, codigo)
);

-- Índices para tabla service
CREATE INDEX idx_service_tenant ON service(tenant_id);
CREATE INDEX idx_service_codigo ON service(codigo);
CREATE INDEX idx_service_nombre ON service(nombre);
CREATE INDEX idx_service_activo ON service(activo);
CREATE INDEX idx_service_tenant_activo ON service(tenant_id, activo);

COMMENT ON TABLE service IS 'Servicios veterinarios ofrecidos por cada tenant';
COMMENT ON COLUMN service.tenant_id IS 'ID del tenant al que pertenece el servicio';

-- ============================================================
-- TABLA: PRODUCTOS (CON TENANT)
-- ============================================================
CREATE TABLE IF NOT EXISTS product (
    product_id SERIAL PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    codigo VARCHAR(50) NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    descripcion VARCHAR(500),
    presentacion VARCHAR(100),
    precio DECIMAL(10, 2) NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0,
    stock_minimo INTEGER DEFAULT 5,
    fecha_vencimiento DATE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES tenant(tenant_id) ON DELETE CASCADE,
    CONSTRAINT uq_product_tenant_codigo UNIQUE (tenant_id, codigo)
);

-- Índices para tabla product
CREATE INDEX idx_product_tenant ON product(tenant_id);
CREATE INDEX idx_product_codigo ON product(codigo);
CREATE INDEX idx_product_nombre ON product(nombre);
CREATE INDEX idx_product_activo ON product(activo);
CREATE INDEX idx_product_stock ON product(stock);
CREATE INDEX idx_product_fecha_vencimiento ON product(fecha_vencimiento);
CREATE INDEX idx_product_tenant_activo ON product(tenant_id, activo);
CREATE INDEX idx_product_tenant_stock ON product(tenant_id, stock);

COMMENT ON TABLE product IS 'Productos disponibles en inventario por tenant';
COMMENT ON COLUMN product.tenant_id IS 'ID del tenant al que pertenece el producto';

-- ============================================================
-- TABLA: CITAS (CON TENANT)
-- ============================================================
CREATE TABLE IF NOT EXISTS appointment (
    appointment_id SERIAL PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    pet_id INTEGER NOT NULL,
    service_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    veterinarian_id INTEGER,
    fecha_hora TIMESTAMP NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PROGRAMADA',
    observaciones VARCHAR(1000),
    diagnostico VARCHAR(1000),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES tenant(tenant_id) ON DELETE CASCADE,
    FOREIGN KEY (pet_id) REFERENCES pet(pet_id),
    FOREIGN KEY (service_id) REFERENCES service(service_id),
    FOREIGN KEY (user_id) REFERENCES "user"(user_id),
    FOREIGN KEY (veterinarian_id) REFERENCES "user"(user_id),
    CONSTRAINT chk_appointment_estado CHECK (estado IN ('PROGRAMADA', 'EN_PROCESO', 'COMPLETADA', 'CANCELADA'))
);

-- Índices para tabla appointment
CREATE INDEX idx_appointment_tenant ON appointment(tenant_id);
CREATE INDEX idx_appointment_pet ON appointment(pet_id);
CREATE INDEX idx_appointment_user ON appointment(user_id);
CREATE INDEX idx_appointment_fecha ON appointment(fecha_hora);
CREATE INDEX idx_appointment_estado ON appointment(estado);
CREATE INDEX idx_appointment_activo ON appointment(activo);
CREATE INDEX idx_appointment_tenant_fecha ON appointment(tenant_id, fecha_hora);
CREATE INDEX idx_appointment_tenant_estado ON appointment(tenant_id, estado);

COMMENT ON TABLE appointment IS 'Citas veterinarias programadas por tenant';
COMMENT ON COLUMN appointment.tenant_id IS 'ID del tenant al que pertenece la cita';

-- ============================================================
-- TABLA: FACTURAS (CON TENANT)
-- ============================================================
CREATE TABLE IF NOT EXISTS invoice (
    invoice_id SERIAL PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    numero VARCHAR(50) NOT NULL,
    client_id INTEGER NOT NULL,
    employee_id INTEGER,
    fecha_emision TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    subtotal DECIMAL(10, 2) NOT NULL DEFAULT 0,
    descuento DECIMAL(10, 2) DEFAULT 0,
    impuesto DECIMAL(10, 2) DEFAULT 0,
    total DECIMAL(10, 2) NOT NULL DEFAULT 0,
    estado VARCHAR(20) NOT NULL DEFAULT 'PAGADA',
    observaciones VARCHAR(500),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES tenant(tenant_id) ON DELETE CASCADE,
    FOREIGN KEY (client_id) REFERENCES "user"(user_id),
    FOREIGN KEY (employee_id) REFERENCES "user"(user_id),
    CONSTRAINT uq_invoice_tenant_numero UNIQUE (tenant_id, numero),
    CONSTRAINT chk_invoice_estado CHECK (estado IN ('PAGADA', 'PENDIENTE', 'ANULADA'))
);

-- Índices para tabla invoice
CREATE INDEX idx_invoice_tenant ON invoice(tenant_id);
CREATE INDEX idx_invoice_numero ON invoice(numero);
CREATE INDEX idx_invoice_client ON invoice(client_id);
CREATE INDEX idx_invoice_fecha ON invoice(fecha_emision);
CREATE INDEX idx_invoice_estado ON invoice(estado);
CREATE INDEX idx_invoice_activo ON invoice(activo);
CREATE INDEX idx_invoice_tenant_fecha ON invoice(tenant_id, fecha_emision);
CREATE INDEX idx_invoice_tenant_estado ON invoice(tenant_id, estado);

COMMENT ON TABLE invoice IS 'Facturas emitidas por ventas por tenant';
COMMENT ON COLUMN invoice.tenant_id IS 'ID del tenant al que pertenece la factura';

-- ============================================================
-- TABLA: DETALLES DE FACTURA
-- ============================================================
CREATE TABLE IF NOT EXISTS invoice_detail (
    detail_id SERIAL PRIMARY KEY,
    invoice_id INTEGER NOT NULL,
    product_id INTEGER,
    service_id INTEGER,
    tipo VARCHAR(10) NOT NULL,
    cantidad INTEGER NOT NULL DEFAULT 1,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    descuento DECIMAL(10, 2) DEFAULT 0,
    subtotal DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (invoice_id) REFERENCES invoice(invoice_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES product(product_id),
    FOREIGN KEY (service_id) REFERENCES service(service_id),
    CONSTRAINT chk_invoice_detail_tipo CHECK (tipo IN ('PRODUCTO', 'SERVICIO'))
);

-- Índices para tabla invoice_detail
CREATE INDEX idx_invoice_detail_invoice ON invoice_detail(invoice_id);
CREATE INDEX idx_invoice_detail_product ON invoice_detail(product_id);
CREATE INDEX idx_invoice_detail_service ON invoice_detail(service_id);
CREATE INDEX idx_invoice_detail_tipo ON invoice_detail(tipo);

COMMENT ON TABLE invoice_detail IS 'Detalles de cada factura';

-- ============================================================
-- DATOS INICIALES: ROLES
-- ============================================================
INSERT INTO roles (rol_id, nombre, descripcion, activo) VALUES
    ('ADMIN', 'Administrador', 'Acceso completo al sistema', TRUE),
    ('EMPLOYEE', 'Empleado', 'Acceso a operaciones diarias', TRUE),
    ('CLIENT', 'Cliente', 'Acceso limitado a consultas', TRUE),
    ('VET', 'Veterinario', 'Acceso a citas y diagnósticos', TRUE)
ON CONFLICT (rol_id) DO NOTHING;

-- ============================================================
-- DATOS INICIALES: TENANTS DE EJEMPLO
-- ============================================================
INSERT INTO tenant (tenant_id, nit, razon_social, direccion, telefono, email, plan, activo) VALUES
    ('VET001', 'Veterinaria San Francisco', '900123456-1', 'Veterinaria San Francisco S.A.S', 'Calle 50 #30-20', '3101234567', 'info@vetsanfrancisco.com', 'PREMIUM', TRUE),
    ('VET002', 'Clínica Veterinaria Mascotas Felices', '900654321-2', 'Mascotas Felices LTDA', 'Carrera 15 #80-45', '3207654321', 'contacto@mascotasfelices.com', 'BASIC', TRUE),
    ('VET003', 'Hospital Veterinario Premium', '900999888-3', 'Hospital Veterinario Premium S.A.S', 'Avenida 68 #100-50', '3159876543', 'admin@vetpremium.com', 'ENTERPRISE', TRUE)
ON CONFLICT (tenant_id) DO NOTHING;

-- ============================================================
-- DATOS INICIALES: USUARIOS POR TENANT
-- ============================================================
-- Usuario Admin para VET001
INSERT INTO "user" (tenant_id, name, tipo_id, ident, correo, telefono, direccion, password, rol_id, activo)
VALUES (
    'VET001',
    'Admin San Francisco',
    'CC',
    '10000001',
    'admin@vetsanfrancisco.com',
    '3101234567',
    'Calle 50 #30-20',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', -- admin123
    'ADMIN',
    TRUE
)
ON CONFLICT (tenant_id, correo) DO NOTHING;

-- Usuario Admin para VET002
INSERT INTO "user" (tenant_id, name, tipo_id, ident, correo, telefono, direccion, password, rol_id, activo)
VALUES (
    'VET002',
    'Admin Mascotas Felices',
    'CC',
    '20000001',
    'admin@mascotasfelices.com',
    '3207654321',
    'Carrera 15 #80-45',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', -- admin123
    'ADMIN',
    TRUE
)
ON CONFLICT (tenant_id, correo) DO NOTHING;

-- Usuario Admin para VET003
INSERT INTO "user" (tenant_id, name, tipo_id, ident, correo, telefono, direccion, password, rol_id, activo)
VALUES (
    'VET003',
    'Admin Hospital Premium',
    'CC',
    '30000001',
    'admin@vetpremium.com',
    '3159876543',
    'Avenida 68 #100-50',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', -- admin123
    'ADMIN',
    TRUE
)
ON CONFLICT (tenant_id, correo) DO NOTHING;

-- ============================================================
-- DATOS INICIALES: SERVICIOS POR TENANT (VET001)
-- ============================================================
INSERT INTO service (tenant_id, codigo, nombre, descripcion, precio, duracion_minutos, activo) VALUES
    ('VET001', 'SRV001', 'Consulta General', 'Consulta veterinaria general', 50000.00, 30, TRUE),
    ('VET001', 'SRV002', 'Vacunación', 'Aplicación de vacunas', 35000.00, 15, TRUE),
    ('VET001', 'SRV003', 'Desparasitación', 'Tratamiento antiparasitario', 25000.00, 15, TRUE),
    ('VET001', 'SRV004', 'Baño y Peluquería', 'Servicio completo de estética', 40000.00, 60, TRUE),
    ('VET001', 'SRV005', 'Cirugía Menor', 'Procedimientos quirúrgicos menores', 200000.00, 120, TRUE)
ON CONFLICT (tenant_id, codigo) DO NOTHING;

-- ============================================================
-- DATOS INICIALES: PRODUCTOS POR TENANT (VET001)
-- ============================================================
INSERT INTO product (tenant_id, codigo, nombre, descripcion, presentacion, precio, stock, stock_minimo, activo) VALUES
    ('VET001', 'PRD001', 'Croquetas Premium Perro', 'Alimento balanceado para perros adultos', '10kg', 85000.00, 50, 10, TRUE),
    ('VET001', 'PRD002', 'Croquetas Premium Gato', 'Alimento balanceado para gatos adultos', '5kg', 65000.00, 40, 10, TRUE),
    ('VET001', 'PRD003', 'Shampoo Antipulgas', 'Shampoo medicado contra pulgas y garrapatas', '500ml', 25000.00, 30, 5, TRUE),
    ('VET001', 'PRD004', 'Collar Antipulgas', 'Collar protector contra parásitos', 'Unitario', 35000.00, 25, 5, TRUE),
    ('VET001', 'PRD005', 'Vitaminas Multifuncionales', 'Suplemento vitamínico completo', '100 tabletas', 45000.00, 20, 5, TRUE)
ON CONFLICT (tenant_id, codigo) DO NOTHING;

-- ============================================================
-- VISTAS ÚTILES CON MULTI-TENANCY
-- ============================================================

-- Vista: Productos con stock bajo por tenant
CREATE OR REPLACE VIEW v_productos_stock_bajo_por_tenant AS
SELECT 
    p.tenant_id,
    t.nombre as tenant_nombre,
    p.product_id,
    p.codigo,
    p.nombre,
    p.stock,
    p.stock_minimo,
    p.precio,
    (p.stock_minimo - p.stock) as cantidad_faltante
FROM product p
JOIN tenant t ON p.tenant_id = t.tenant_id
WHERE p.stock < p.stock_minimo 
  AND p.activo = TRUE
  AND t.activo = TRUE
ORDER BY p.tenant_id, cantidad_faltante DESC;

-- Vista: Productos próximos a vencer por tenant
CREATE OR REPLACE VIEW v_productos_por_vencer_por_tenant AS
SELECT 
    p.tenant_id,
    t.nombre as tenant_nombre,
    p.product_id,
    p.codigo,
    p.nombre,
    p.fecha_vencimiento,
    p.stock,
    (p.fecha_vencimiento - CURRENT_DATE) as dias_para_vencer
FROM product p
JOIN tenant t ON p.tenant_id = t.tenant_id
WHERE p.fecha_vencimiento IS NOT NULL
  AND p.fecha_vencimiento <= CURRENT_DATE + INTERVAL '30 days'
  AND p.activo = TRUE
  AND t.activo = TRUE
ORDER BY p.tenant_id, p.fecha_vencimiento;

-- Vista: Citas del día por tenant
CREATE OR REPLACE VIEW v_citas_hoy_por_tenant AS
SELECT 
    a.tenant_id,
    t.nombre as tenant_nombre,
    a.appointment_id,
    a.fecha_hora,
    p.nombre as mascota,
    u.name as cliente,
    s.nombre as servicio,
    a.estado
FROM appointment a
JOIN tenant t ON a.tenant_id = t.tenant_id
JOIN pet p ON a.pet_id = p.pet_id
JOIN "user" u ON a.user_id = u.user_id
JOIN service s ON a.service_id = s.service_id
WHERE DATE(a.fecha_hora) = CURRENT_DATE
  AND a.activo = TRUE
  AND t.activo = TRUE
ORDER BY a.tenant_id, a.fecha_hora;

-- ============================================================
-- FUNCIONES ÚTILES CON MULTI-TENANCY
-- ============================================================

-- Función: Obtener total de ventas del día por tenant
CREATE OR REPLACE FUNCTION fn_ventas_dia_por_tenant(p_tenant_id VARCHAR(50))
RETURNS DECIMAL(10,2) AS $$
BEGIN
    RETURN COALESCE(
        (SELECT SUM(total) 
         FROM invoice 
         WHERE tenant_id = p_tenant_id
           AND DATE(fecha_emision) = CURRENT_DATE 
           AND estado = 'PAGADA' 
           AND activo = TRUE),
        0
    );
END;
$$ LANGUAGE plpgsql;

-- Función: Obtener total de ventas del mes por tenant
CREATE OR REPLACE FUNCTION fn_ventas_mes_por_tenant(p_tenant_id VARCHAR(50))
RETURNS DECIMAL(10,2) AS $$
BEGIN
    RETURN COALESCE(
        (SELECT SUM(total) 
         FROM invoice 
         WHERE tenant_id = p_tenant_id
           AND EXTRACT(YEAR FROM fecha_emision) = EXTRACT(YEAR FROM CURRENT_DATE)
           AND EXTRACT(MONTH FROM fecha_emision) = EXTRACT(MONTH FROM CURRENT_DATE)
           AND estado = 'PAGADA' 
           AND activo = TRUE),
        0
    );
END;
$$ LANGUAGE plpgsql;

-- Función: Contar mascotas activas por tenant
CREATE OR REPLACE FUNCTION fn_contar_mascotas_por_tenant(p_tenant_id VARCHAR(50))
RETURNS BIGINT AS $$
BEGIN
    RETURN (SELECT COUNT(*) 
            FROM pet 
            WHERE tenant_id = p_tenant_id 
              AND activo = TRUE);
END;
$$ LANGUAGE plpgsql;

-- ============================================================
-- CONSULTAS ÚTILES PARA VERIFICACIÓN
-- ============================================================

-- Verificar las tablas creadas
SELECT tablename FROM pg_tables WHERE schemaname = 'public' ORDER BY tablename;

-- Verificar tenants
SELECT tenant_id, nombre, plan, activo FROM tenant;

-- Verificar usuarios por tenant
SELECT tenant_id, user_id, name, correo, rol_id, activo FROM "user" ORDER BY tenant_id;

-- Verificar datos por tenant
SELECT 
    t.tenant_id,
    t.nombre,
    COUNT(DISTINCT u.user_id) as usuarios,
    COUNT(DISTINCT p.pet_id) as mascotas,
    COUNT(DISTINCT s.service_id) as servicios,
    COUNT(DISTINCT pr.product_id) as productos
FROM tenant t
LEFT JOIN "user" u ON t.tenant_id = u.tenant_id
LEFT JOIN pet p ON t.tenant_id = p.tenant_id
LEFT JOIN service s ON t.tenant_id = s.tenant_id
LEFT JOIN product pr ON t.tenant_id = pr.tenant_id
GROUP BY t.tenant_id, t.nombre;

-- ============================================================
-- FIN DEL SCRIPT
-- ============================================================

