-- Script para agregar columnas vaccination_id y appointment_id a invoice_detail
-- Esto permite que los detalles de factura puedan referenciar vacunaciones y citas específicas

-- Paso 1: Agregar columna vaccination_id
ALTER TABLE invoice_detail ADD COLUMN IF NOT EXISTS vaccination_id INTEGER;

-- Paso 2: Agregar columna appointment_id
ALTER TABLE invoice_detail ADD COLUMN IF NOT EXISTS appointment_id INTEGER;

-- Paso 3: Crear índices para mejorar el rendimiento
CREATE INDEX IF NOT EXISTS idx_invoice_detail_vaccination_id ON invoice_detail(vaccination_id);
CREATE INDEX IF NOT EXISTS idx_invoice_detail_appointment_id ON invoice_detail(appointment_id);

-- Paso 4: Agregar foreign keys
ALTER TABLE invoice_detail 
ADD CONSTRAINT IF NOT EXISTS fk_invoice_detail_vaccination 
FOREIGN KEY (vaccination_id) REFERENCES vaccination(vaccination_id) ON DELETE SET NULL;

ALTER TABLE invoice_detail 
ADD CONSTRAINT IF NOT EXISTS fk_invoice_detail_appointment 
FOREIGN KEY (appointment_id) REFERENCES appointment(appointment_id) ON DELETE SET NULL;

-- Paso 5: Comentarios explicativos
COMMENT ON COLUMN invoice_detail.vaccination_id IS 'ID de la vacunación que se está facturando (se marca automáticamente como FACTURADA)';
COMMENT ON COLUMN invoice_detail.appointment_id IS 'ID de la cita que se está facturando (se marca automáticamente como FACTURADA)';

-- Verificación: Ver estructura actualizada
SELECT 
    column_name, 
    data_type, 
    is_nullable,
    column_default
FROM information_schema.columns 
WHERE table_name = 'invoice_detail'
ORDER BY ordinal_position;

-- Resultado esperado
SELECT 'Columnas agregadas correctamente a invoice_detail' AS resultado;

