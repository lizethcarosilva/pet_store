-- Script para agregar columnas faltantes en la tabla roles
-- Ejecutar este script en la base de datos PostgreSQL

-- Agregar columna activo si no existe
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'roles' AND column_name = 'activo') THEN
        ALTER TABLE roles ADD COLUMN activo BOOLEAN NOT NULL DEFAULT TRUE;
    END IF;
END $$;

-- Agregar columna created_on si no existe
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns 
                   WHERE table_name = 'roles' AND column_name = 'created_on') THEN
        ALTER TABLE roles ADD COLUMN created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP;
    END IF;
END $$;

-- Verificar que las columnas se agregaron correctamente
SELECT column_name, data_type, is_nullable, column_default 
FROM information_schema.columns 
WHERE table_name = 'roles' 
ORDER BY ordinal_position;

