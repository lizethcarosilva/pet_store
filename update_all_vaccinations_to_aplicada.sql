-- Script para actualizar TODAS las vacunaciones existentes al estado correcto
-- Ejecutar ANTES de probar la funcionalidad de facturación

-- 1. Ver el estado actual de TODAS las vacunaciones
SELECT 
    vaccination_id, 
    pet_id, 
    vaccine_name, 
    estado,
    activo,
    created_on
FROM vaccination
ORDER BY created_on DESC;

-- 2. Actualizar TODAS las vacunaciones que no estén facturadas a "APLICADA"
-- Esto asegura que tengan un estado consistente
UPDATE vaccination 
SET estado = 'APLICADA' 
WHERE activo = true 
  AND (
    estado IS NULL 
    OR estado = '' 
    OR estado IN ('PENDIENTE', 'Pendiente', 'pendiente')
    OR estado IN ('Completo', 'Completada', 'Completado', 'COMPLETADA', 'completada')
  );

-- 3. Ver resultado
SELECT 
    'Estados actualizados' AS mensaje,
    COUNT(*) as total_vaccinations,
    COUNT(CASE WHEN estado = 'APLICADA' THEN 1 END) as aplicadas,
    COUNT(CASE WHEN estado = 'FACTURADA' THEN 1 END) as facturadas,
    COUNT(CASE WHEN estado = 'PENDIENTE' THEN 1 END) as pendientes,
    COUNT(CASE WHEN estado = 'CANCELADA' THEN 1 END) as canceladas
FROM vaccination;

-- 4. Ver todas las vacunaciones con sus nuevos estados
SELECT 
    vaccination_id, 
    pet_id, 
    vaccine_name, 
    estado,
    activo
FROM vaccination
ORDER BY vaccination_id;

