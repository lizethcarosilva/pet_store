-- Script para cambiar la tabla appointment de user_id a client_id
-- Esto permite que las citas se asocien con la tabla client en lugar de user

-- Paso 1: Agregar la columna client_id (nullable temporalmente)
ALTER TABLE appointment ADD COLUMN IF NOT EXISTS client_id INTEGER;

-- Paso 2: Eliminar la restricción NOT NULL de user_id (temporalmente)
ALTER TABLE appointment ALTER COLUMN user_id DROP NOT NULL;

-- Paso 3: Crear el índice para client_id
CREATE INDEX IF NOT EXISTS idx_appointment_client_id ON appointment(client_id);

-- Paso 4: Agregar la constraint de foreign key
ALTER TABLE appointment 
ADD CONSTRAINT IF NOT EXISTS fk_appointment_client 
FOREIGN KEY (client_id) REFERENCES client(client_id) ON DELETE CASCADE;

-- Paso 5: IMPORTANTE - Si tienes datos existentes, necesitarás migrarlos manualmente
-- Este script NO migra datos automáticamente porque user_id apunta a la tabla 'user'
-- y client_id apunta a la tabla 'client', que son diferentes.

-- Nota: Para limpiar datos huérfanos de citas con user_id que ya no existe:
-- DELETE FROM appointment WHERE user_id NOT IN (SELECT user_id FROM "user");

-- Una vez que hayas migrado todos los datos:
-- ALTER TABLE appointment DROP COLUMN user_id;

