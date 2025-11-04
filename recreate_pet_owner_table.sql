-- Primero, verificar qué columnas tiene actualmente la tabla pet_owner
-- Si la tabla existe pero con estructura incorrecta, la vamos a recrear

-- Eliminar tabla si existe (PRECAUCIÓN: esto borrará datos)
-- DROP TABLE IF EXISTS pet_owner CASCADE;

-- Crear tabla pet_owner con estructura correcta
CREATE TABLE IF NOT EXISTS pet_owner (
    pet_id INTEGER NOT NULL,
    user_id INTEGER,
    client_id INTEGER,
    created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    PRIMARY KEY (pet_id, user_id),
    
    CONSTRAINT fk_pet_owner_pet FOREIGN KEY (pet_id) REFERENCES pet(pet_id) ON DELETE CASCADE,
    CONSTRAINT fk_pet_owner_user FOREIGN KEY (user_id) REFERENCES "user"(user_id) ON DELETE CASCADE,
    CONSTRAINT fk_pet_owner_client FOREIGN KEY (client_id) REFERENCES client(client_id) ON DELETE CASCADE
);

-- Crear índices
CREATE INDEX IF NOT EXISTS idx_pet_owner_pet_id ON pet_owner(pet_id);
CREATE INDEX IF NOT EXISTS idx_pet_owner_user_id ON pet_owner(user_id);
CREATE INDEX IF NOT EXISTS idx_pet_owner_client_id ON pet_owner(client_id);

-- Comentarios
COMMENT ON TABLE pet_owner IS 'Relación entre mascotas y sus propietarios (usuarios o clientes)';
COMMENT ON COLUMN pet_owner.pet_id IS 'ID de la mascota';
COMMENT ON COLUMN pet_owner.user_id IS 'ID del usuario propietario (de la tabla user)';
COMMENT ON COLUMN pet_owner.client_id IS 'ID del cliente propietario (de la tabla client)';
COMMENT ON COLUMN pet_owner.created_on IS 'Fecha de creación de la relación';

