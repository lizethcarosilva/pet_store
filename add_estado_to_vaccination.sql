-- Script para agregar la columna estado a la tabla vaccination
-- Ejecutar solo si DatabaseSchemaUpdater no la agregó automáticamente

-- Agregar columna estado con valor por defecto 'APLICADA' para registros existentes
ALTER TABLE vaccination ADD COLUMN IF NOT EXISTS estado VARCHAR(20) NOT NULL DEFAULT 'APLICADA';

-- Comentario explicativo
COMMENT ON COLUMN vaccination.estado IS 'Estado de la vacunación: PENDIENTE, APLICADA, FACTURADA, CANCELADA';

-- Crear índice para mejorar búsquedas por estado
CREATE INDEX IF NOT EXISTS idx_vaccination_estado ON vaccination(estado);

-- Actualizar registros existentes que no tengan estado (por si acaso)
UPDATE vaccination SET estado = 'APLICADA' WHERE estado IS NULL OR estado = '';

-- Mostrar resultado
SELECT 
    'Columna estado agregada correctamente a la tabla vaccination' AS resultado,
    COUNT(*) as total_vaccinations,
    COUNT(CASE WHEN estado = 'APLICADA' THEN 1 END) as aplicadas,
    COUNT(CASE WHEN estado = 'FACTURADA' THEN 1 END) as facturadas,
    COUNT(CASE WHEN estado = 'PENDIENTE' THEN 1 END) as pendientes
FROM vaccination;

