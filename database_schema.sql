-- ============================================================
-- SCRIPT DE CREACIÓN DE BASE DE DATOS - PET STORE
-- Sistema de Gestión Veterinaria
-- ============================================================

-- Crear la base de datos (ejecutar primero como superusuario)
-- CREATE DATABASE petstore;
-- \c petstore;

-- ============================================================
-- TABLA: USUARIOS
-- ============================================================
CREATE TABLE IF NOT EXISTS "user" (
    user_id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    tipo_id VARCHAR(10),
    ident VARCHAR(50) NOT NULL UNIQUE,
    correo VARCHAR(255) NOT NULL UNIQUE,
    telefono VARCHAR(20),
    direccion VARCHAR(255),
    password VARCHAR(255) NOT NULL,
    rol_id VARCHAR(20),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices para tabla user
CREATE INDEX idx_user_correo ON "user"(correo);
CREATE INDEX idx_user_ident ON "user"(ident);
CREATE INDEX idx_user_activo ON "user"(activo);
CREATE INDEX idx_user_rol_id ON "user"(rol_id);

-- ============================================================
-- TABLA: MASCOTAS
-- ============================================================
CREATE TABLE IF NOT EXISTS pet (
    pet_id SERIAL PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    raza VARCHAR(100),
    cuidados_especiales VARCHAR(500),
    edad INTEGER,
    sexo VARCHAR(10),
    color VARCHAR(50),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices para tabla pet
CREATE INDEX idx_pet_nombre ON pet(nombre);
CREATE INDEX idx_pet_tipo ON pet(tipo);
CREATE INDEX idx_pet_activo ON pet(activo);

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

-- ============================================================
-- TABLA: SERVICIOS VETERINARIOS
-- ============================================================
CREATE TABLE IF NOT EXISTS service (
    service_id SERIAL PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(255) NOT NULL,
    descripcion VARCHAR(500),
    precio DECIMAL(10, 2) NOT NULL,
    duracion_minutos INTEGER,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices para tabla service
CREATE INDEX idx_service_codigo ON service(codigo);
CREATE INDEX idx_service_nombre ON service(nombre);
CREATE INDEX idx_service_activo ON service(activo);

-- ============================================================
-- TABLA: PRODUCTOS
-- ============================================================
CREATE TABLE IF NOT EXISTS product (
    product_id SERIAL PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(255) NOT NULL,
    descripcion VARCHAR(500),
    presentacion VARCHAR(100),
    precio DECIMAL(10, 2) NOT NULL,
    stock INTEGER NOT NULL DEFAULT 0,
    stock_minimo INTEGER DEFAULT 5,
    fecha_vencimiento DATE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Índices para tabla product
CREATE INDEX idx_product_codigo ON product(codigo);
CREATE INDEX idx_product_nombre ON product(nombre);
CREATE INDEX idx_product_activo ON product(activo);
CREATE INDEX idx_product_stock ON product(stock);
CREATE INDEX idx_product_fecha_vencimiento ON product(fecha_vencimiento);

-- ============================================================
-- TABLA: CITAS
-- ============================================================
CREATE TABLE IF NOT EXISTS appointment (
    appointment_id SERIAL PRIMARY KEY,
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
    FOREIGN KEY (pet_id) REFERENCES pet(pet_id),
    FOREIGN KEY (service_id) REFERENCES service(service_id),
    FOREIGN KEY (user_id) REFERENCES "user"(user_id),
    FOREIGN KEY (veterinarian_id) REFERENCES "user"(user_id)
);

-- Índices para tabla appointment
CREATE INDEX idx_appointment_pet ON appointment(pet_id);
CREATE INDEX idx_appointment_user ON appointment(user_id);
CREATE INDEX idx_appointment_fecha ON appointment(fecha_hora);
CREATE INDEX idx_appointment_estado ON appointment(estado);
CREATE INDEX idx_appointment_activo ON appointment(activo);

-- ============================================================
-- TABLA: FACTURAS
-- ============================================================
CREATE TABLE IF NOT EXISTS invoice (
    invoice_id SERIAL PRIMARY KEY,
    numero VARCHAR(50) NOT NULL UNIQUE,
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
    FOREIGN KEY (client_id) REFERENCES "user"(user_id),
    FOREIGN KEY (employee_id) REFERENCES "user"(user_id)
);

-- Índices para tabla invoice
CREATE INDEX idx_invoice_numero ON invoice(numero);
CREATE INDEX idx_invoice_client ON invoice(client_id);
CREATE INDEX idx_invoice_fecha ON invoice(fecha_emision);
CREATE INDEX idx_invoice_estado ON invoice(estado);
CREATE INDEX idx_invoice_activo ON invoice(activo);

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
    CHECK (tipo IN ('PRODUCTO', 'SERVICIO'))
);

-- Índices para tabla invoice_detail
CREATE INDEX idx_invoice_detail_invoice ON invoice_detail(invoice_id);
CREATE INDEX idx_invoice_detail_product ON invoice_detail(product_id);
CREATE INDEX idx_invoice_detail_service ON invoice_detail(service_id);
CREATE INDEX idx_invoice_detail_tipo ON invoice_detail(tipo);

-- ============================================================
-- TABLA ADICIONAL: ROLES (Opcional, para gestión de roles)
-- ============================================================
CREATE TABLE IF NOT EXISTS roles (
    rol_id VARCHAR(20) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- TABLA ADICIONAL: TENANT_USER (Si existe en el proyecto)
-- ============================================================
CREATE TABLE IF NOT EXISTS tenant_user (
    tenant_id VARCHAR(50) NOT NULL,
    user_id INTEGER NOT NULL,
    created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (tenant_id, user_id),
    FOREIGN KEY (user_id) REFERENCES "user"(user_id) ON DELETE CASCADE
);

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
-- DATOS INICIALES: USUARIO ADMINISTRADOR
-- ============================================================
-- Contraseña: admin123 (ya encriptada con BCrypt)
INSERT INTO "user" (name, tipo_id, ident, correo, telefono, direccion, password, rol_id, activo)
VALUES (
    'Administrador Sistema',
    'CC',
    '00000000',
    'admin@petstore.com',
    '3000000000',
    'Oficina Central',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', -- admin123
    'ADMIN',
    TRUE
)
ON CONFLICT (correo) DO NOTHING;

-- ============================================================
-- DATOS INICIALES: SERVICIOS COMUNES
-- ============================================================
INSERT INTO service (codigo, nombre, descripcion, precio, duracion_minutos, activo) VALUES
    ('SRV001', 'Consulta General', 'Consulta veterinaria general', 50000.00, 30, TRUE),
    ('SRV002', 'Vacunación', 'Aplicación de vacunas', 35000.00, 15, TRUE),
    ('SRV003', 'Desparasitación', 'Tratamiento antiparasitario', 25000.00, 15, TRUE),
    ('SRV004', 'Baño y Peluquería', 'Servicio completo de estética', 40000.00, 60, TRUE),
    ('SRV005', 'Cirugía Menor', 'Procedimientos quirúrgicos menores', 200000.00, 120, TRUE),
    ('SRV006', 'Control de Salud', 'Revisión preventiva completa', 60000.00, 45, TRUE),
    ('SRV007', 'Limpieza Dental', 'Profilaxis dental', 80000.00, 60, TRUE),
    ('SRV008', 'Esterilización', 'Cirugía de esterilización', 150000.00, 90, TRUE)
ON CONFLICT (codigo) DO NOTHING;

-- ============================================================
-- DATOS INICIALES: PRODUCTOS COMUNES
-- ============================================================
INSERT INTO product (codigo, nombre, descripcion, presentacion, precio, stock, stock_minimo, activo) VALUES
    ('PRD001', 'Croquetas Premium Perro', 'Alimento balanceado para perros adultos', '10kg', 85000.00, 50, 10, TRUE),
    ('PRD002', 'Croquetas Premium Gato', 'Alimento balanceado para gatos adultos', '5kg', 65000.00, 40, 10, TRUE),
    ('PRD003', 'Shampoo Antipulgas', 'Shampoo medicado contra pulgas y garrapatas', '500ml', 25000.00, 30, 5, TRUE),
    ('PRD004', 'Collar Antipulgas', 'Collar protector contra parásitos', 'Unitario', 35000.00, 25, 5, TRUE),
    ('PRD005', 'Vitaminas Multifuncionales', 'Suplemento vitamínico completo', '100 tabletas', 45000.00, 20, 5, TRUE),
    ('PRD006', 'Desparasitante Interno', 'Tratamiento antiparasitario oral', '10 tabletas', 30000.00, 35, 10, TRUE),
    ('PRD007', 'Juguete Interactivo', 'Juguete para estimulación mental', 'Unitario', 20000.00, 15, 3, TRUE),
    ('PRD008', 'Cama para Mascota', 'Cama acolchada tamaño mediano', 'Unitario', 75000.00, 10, 2, TRUE)
ON CONFLICT (codigo) DO NOTHING;

-- ============================================================
-- VISTAS ÚTILES
-- ============================================================

-- Vista: Resumen de productos con stock bajo
CREATE OR REPLACE VIEW v_productos_stock_bajo AS
SELECT 
    p.product_id,
    p.codigo,
    p.nombre,
    p.stock,
    p.stock_minimo,
    p.precio,
    (p.stock_minimo - p.stock) as cantidad_faltante
FROM product p
WHERE p.stock < p.stock_minimo 
  AND p.activo = TRUE
ORDER BY cantidad_faltante DESC;

-- Vista: Productos próximos a vencer
CREATE OR REPLACE VIEW v_productos_por_vencer AS
SELECT 
    p.product_id,
    p.codigo,
    p.nombre,
    p.fecha_vencimiento,
    p.stock,
    (p.fecha_vencimiento - CURRENT_DATE) as dias_para_vencer
FROM product p
WHERE p.fecha_vencimiento IS NOT NULL
  AND p.fecha_vencimiento <= CURRENT_DATE + INTERVAL '30 days'
  AND p.activo = TRUE
ORDER BY p.fecha_vencimiento;

-- Vista: Citas del día
CREATE OR REPLACE VIEW v_citas_hoy AS
SELECT 
    a.appointment_id,
    a.fecha_hora,
    p.nombre as mascota,
    u.name as cliente,
    s.nombre as servicio,
    a.estado
FROM appointment a
JOIN pet p ON a.pet_id = p.pet_id
JOIN "user" u ON a.user_id = u.user_id
JOIN service s ON a.service_id = s.service_id
WHERE DATE(a.fecha_hora) = CURRENT_DATE
  AND a.activo = TRUE
ORDER BY a.fecha_hora;

-- Vista: Top productos vendidos
CREATE OR REPLACE VIEW v_top_productos_vendidos AS
SELECT 
    p.product_id,
    p.codigo,
    p.nombre,
    SUM(id.cantidad) as total_vendido,
    SUM(id.subtotal) as total_ingresos
FROM invoice_detail id
JOIN product p ON id.product_id = p.product_id
JOIN invoice i ON id.invoice_id = i.invoice_id
WHERE id.tipo = 'PRODUCTO'
  AND i.estado = 'PAGADA'
  AND i.activo = TRUE
GROUP BY p.product_id, p.codigo, p.nombre
ORDER BY total_vendido DESC;

-- ============================================================
-- FUNCIONES ÚTILES
-- ============================================================

-- Función: Obtener total de ventas del día
CREATE OR REPLACE FUNCTION fn_ventas_dia()
RETURNS DECIMAL(10,2) AS $$
BEGIN
    RETURN COALESCE(
        (SELECT SUM(total) 
         FROM invoice 
         WHERE DATE(fecha_emision) = CURRENT_DATE 
           AND estado = 'PAGADA' 
           AND activo = TRUE),
        0
    );
END;
$$ LANGUAGE plpgsql;

-- Función: Obtener total de ventas del mes
CREATE OR REPLACE FUNCTION fn_ventas_mes()
RETURNS DECIMAL(10,2) AS $$
BEGIN
    RETURN COALESCE(
        (SELECT SUM(total) 
         FROM invoice 
         WHERE EXTRACT(YEAR FROM fecha_emision) = EXTRACT(YEAR FROM CURRENT_DATE)
           AND EXTRACT(MONTH FROM fecha_emision) = EXTRACT(MONTH FROM CURRENT_DATE)
           AND estado = 'PAGADA' 
           AND activo = TRUE),
        0
    );
END;
$$ LANGUAGE plpgsql;

-- ============================================================
-- TRIGGERS
-- ============================================================

-- Trigger: Actualizar stock al crear detalle de factura
CREATE OR REPLACE FUNCTION fn_actualizar_stock_producto()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.tipo = 'PRODUCTO' AND NEW.product_id IS NOT NULL THEN
        UPDATE product 
        SET stock = stock - NEW.cantidad
        WHERE product_id = NEW.product_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- No crear el trigger si ya existe
DROP TRIGGER IF EXISTS trg_actualizar_stock ON invoice_detail;
-- CREATE TRIGGER trg_actualizar_stock
-- AFTER INSERT ON invoice_detail
-- FOR EACH ROW
-- EXECUTE FUNCTION fn_actualizar_stock_producto();

-- ============================================================
-- COMENTARIOS EN TABLAS
-- ============================================================
COMMENT ON TABLE "user" IS 'Usuarios del sistema (empleados, clientes, veterinarios)';
COMMENT ON TABLE pet IS 'Mascotas registradas en el sistema';
COMMENT ON TABLE pet_owner IS 'Relación muchos a muchos entre mascotas y dueños';
COMMENT ON TABLE service IS 'Servicios veterinarios ofrecidos';
COMMENT ON TABLE product IS 'Productos disponibles en inventario';
COMMENT ON TABLE appointment IS 'Citas veterinarias programadas';
COMMENT ON TABLE invoice IS 'Facturas emitidas por ventas';
COMMENT ON TABLE invoice_detail IS 'Detalles de cada factura';

-- ============================================================
-- FIN DEL SCRIPT
-- ============================================================

-- Verificar las tablas creadas
SELECT tablename FROM pg_tables WHERE schemaname = 'public' ORDER BY tablename;

-- Verificar los datos iniciales
SELECT 'Users:', COUNT(*) FROM "user" UNION ALL
SELECT 'Roles:', COUNT(*) FROM roles UNION ALL
SELECT 'Services:', COUNT(*) FROM service UNION ALL
SELECT 'Products:', COUNT(*) FROM product;

