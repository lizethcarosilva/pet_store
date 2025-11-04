-- Script para corregir la estructura de pet_owner
-- Este script intenta agregar las columnas faltantes si no existen

-- Verificar y agregar user_id si no existe
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'pet_owner' AND column_name = 'user_id'
    ) THEN
        ALTER TABLE pet_owner ADD COLUMN user_id INTEGER;
        RAISE NOTICE 'Columna user_id agregada a pet_owner';
    ELSE
        RAISE NOTICE 'Columna user_id ya existe en pet_owner';
    END IF;
END$$;

-- Verificar y agregar client_id si no existe
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'pet_owner' AND column_name = 'client_id'
    ) THEN
        ALTER TABLE pet_owner ADD COLUMN client_id INTEGER;
        RAISE NOTICE 'Columna client_id agregada a pet_owner';
    ELSE
        RAISE NOTICE 'Columna client_id ya existe en pet_owner';
    END IF;
END$$;

-- Verificar y agregar created_on si no existe
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'pet_owner' AND column_name = 'created_on'
    ) THEN
        ALTER TABLE pet_owner ADD COLUMN created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
        RAISE NOTICE 'Columna created_on agregada a pet_owner';
    ELSE
        RAISE NOTICE 'Columna created_on ya existe en pet_owner';
    END IF;
END$$;

-- Crear índices si no existen
CREATE INDEX IF NOT EXISTS idx_pet_owner_user_id ON pet_owner(user_id);
CREATE INDEX IF NOT EXISTS idx_pet_owner_client_id ON pet_owner(client_id);

-- Agregar constraint de foreign key para client_id si no existe
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.table_constraints 
        WHERE constraint_name = 'fk_pet_owner_client'
    ) THEN
        ALTER TABLE pet_owner 
        ADD CONSTRAINT fk_pet_owner_client 
        FOREIGN KEY (client_id) REFERENCES client(client_id) ON DELETE CASCADE;
        RAISE NOTICE 'Constraint fk_pet_owner_client agregado';
    ELSE
        RAISE NOTICE 'Constraint fk_pet_owner_client ya existe';
    END IF;
EXCEPTION
    WHEN undefined_table THEN
        RAISE NOTICE 'Tabla client aún no existe, constraint será creado después';
    WHEN OTHERS THEN
        RAISE NOTICE 'Error creando constraint: %', SQLERRM;
END$$;

