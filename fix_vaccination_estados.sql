-- Script para corregir estados de vacunaciones existentes
-- Este script asegura que todas las vacunaciones tengan el estado correcto

-- 1. Ver el estado actual de todas las vacunaciones
SELECT 
    vaccination_id, 
    pet_id, 
    vaccine_name, 
    estado,
    activo,
    created_on
FROM vaccination
ORDER BY created_on DESC;

-- 2. Actualizar registros que tengan estados incorrectos o vacíos
-- Establecer "APLICADA" como estado por defecto para vacunaciones existentes que no estén facturadas

UPDATE vaccination 
SET estado = 'APLICADA' 
WHERE (estado IS NULL OR estado = '' OR estado = 'PENDIENTE' OR estado = 'Completo' OR estado = 'Completada')
  AND activo = true;

-- 3. Verificar que no haya estados inconsistentes
-- Este query muestra todos los estados únicos que existen
SELECT DISTINCT estado, COUNT(*) as cantidad
FROM vaccination
GROUP BY estado
ORDER BY estado;

-- 4. Resultado final - Mostrar todas las vacunaciones con sus estados actualizados
SELECT 
    vaccination_id, 
    pet_id, 
    vaccine_name, 
    estado,
    activo,
    'Estado actualizado correctamente' as mensaje
FROM vaccination
ORDER BY vaccination_id;

