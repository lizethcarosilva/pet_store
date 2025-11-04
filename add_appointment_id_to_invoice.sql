-- ============================================================
-- AGREGAR CAMPO appointment_id A LA TABLA invoice
-- Para referenciar citas agendadas cuando se facturan servicios
-- ============================================================

-- Agregar columna appointment_id si no existe
ALTER TABLE invoice 
ADD COLUMN IF NOT EXISTS appointment_id INTEGER;

-- Agregar foreign key constraint
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint 
        WHERE conname = 'fk_invoice_appointment'
    ) THEN
        ALTER TABLE invoice 
        ADD CONSTRAINT fk_invoice_appointment 
        FOREIGN KEY (appointment_id) 
        REFERENCES appointment(appointment_id) 
        ON DELETE SET NULL;
    END IF;
END $$;

-- Crear índice para mejorar rendimiento de consultas
CREATE INDEX IF NOT EXISTS idx_invoice_appointment 
ON invoice(appointment_id);

COMMENT ON COLUMN invoice.appointment_id IS 'Referencia a la cita agendada cuando la factura es por un servicio agendado (cita, baño, desparasitación, etc.)';

