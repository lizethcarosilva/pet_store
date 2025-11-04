-- Verificar la estructura actual de la tabla pet_owner
SELECT column_name, data_type, is_nullable, column_default
FROM information_schema.columns
WHERE table_name = 'pet_owner'
ORDER BY ordinal_position;

