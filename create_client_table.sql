-- Crear tabla de clientes separada de usuarios
CREATE TABLE IF NOT EXISTS client (
    client_id SERIAL PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    name VARCHAR(200) NOT NULL,
    tipo_id VARCHAR(10),
    ident VARCHAR(50) UNIQUE NOT NULL,
    correo VARCHAR(100) UNIQUE NOT NULL,
    telefono VARCHAR(20),
    direccion VARCHAR(300),
    observaciones VARCHAR(500),
    activo BOOLEAN NOT NULL DEFAULT true,
    created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT client_ident_unique UNIQUE (ident),
    CONSTRAINT client_correo_unique UNIQUE (correo)
);

-- Crear índices para mejorar el rendimiento
CREATE INDEX idx_client_tenant_id ON client(tenant_id);
CREATE INDEX idx_client_ident ON client(ident);
CREATE INDEX idx_client_correo ON client(correo);
CREATE INDEX idx_client_activo ON client(activo);
CREATE INDEX idx_client_name ON client(name);

-- Comentarios de la tabla
COMMENT ON TABLE client IS 'Tabla de clientes separada de usuarios';
COMMENT ON COLUMN client.client_id IS 'ID único del cliente';
COMMENT ON COLUMN client.tenant_id IS 'ID del tenant al que pertenece';
COMMENT ON COLUMN client.name IS 'Nombre completo del cliente';
COMMENT ON COLUMN client.tipo_id IS 'Tipo de identificación: CC, TI, CE, NIT, etc.';
COMMENT ON COLUMN client.ident IS 'Número de identificación único';
COMMENT ON COLUMN client.correo IS 'Correo electrónico único';
COMMENT ON COLUMN client.telefono IS 'Número de teléfono';
COMMENT ON COLUMN client.direccion IS 'Dirección física';
COMMENT ON COLUMN client.observaciones IS 'Notas adicionales sobre el cliente';
COMMENT ON COLUMN client.activo IS 'Estado del cliente (true=activo, false=inactivo)';
COMMENT ON COLUMN client.created_on IS 'Fecha de creación del registro';

