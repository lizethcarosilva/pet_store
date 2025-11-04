-- Agregar columna client_id a la tabla pet_owner para asociar mascotas con clientes
ALTER TABLE pet_owner ADD COLUMN IF NOT EXISTS client_id INTEGER;

-- Crear foreign key constraint
ALTER TABLE pet_owner 
ADD CONSTRAINT fk_pet_owner_client 
FOREIGN KEY (client_id) 
REFERENCES client(client_id) 
ON DELETE CASCADE;

-- Crear índice para mejorar rendimiento
CREATE INDEX IF NOT EXISTS idx_pet_owner_client_id ON pet_owner(client_id);

-- Ahora user_id puede ser nullable ya que puede tener client_id en su lugar
ALTER TABLE pet_owner ALTER COLUMN user_id DROP NOT NULL;

COMMENT ON COLUMN pet_owner.client_id IS 'ID del cliente propietario (alternativa a user_id)';

