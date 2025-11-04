-- ============================================================
-- AGREGAR COLUMNAS ADICIONALES A LA TABLA product
-- Para identificar productos que son vacunas, lote y fabricante
-- ============================================================

-- Agregar columnas si no existen
ALTER TABLE product 
ADD COLUMN IF NOT EXISTS es_vacuna BOOLEAN NOT NULL DEFAULT FALSE;

ALTER TABLE product 
ADD COLUMN IF NOT EXISTS lote VARCHAR(100);

ALTER TABLE product 
ADD COLUMN IF NOT EXISTS fabricante VARCHAR(200);

-- Crear índice para mejorar rendimiento de consultas
CREATE INDEX IF NOT EXISTS idx_product_es_vacuna 
ON product(es_vacuna);

-- Crear índice compuesto para buscar vacunas activas con stock
CREATE INDEX IF NOT EXISTS idx_product_vacuna_activo_stock 
ON product(es_vacuna, activo, stock) 
WHERE es_vacuna = TRUE;

COMMENT ON COLUMN product.es_vacuna IS 'Indica si el producto es una vacuna';
COMMENT ON COLUMN product.lote IS 'Número de lote del producto para trazabilidad';
COMMENT ON COLUMN product.fabricante IS 'Fabricante o marca del producto';

-- Ejemplos de actualización (opcional, descomentar si es necesario):
-- Marcar productos existentes como vacunas si el nombre contiene palabras clave
-- UPDATE product SET es_vacuna = TRUE 
-- WHERE LOWER(nombre) LIKE '%vacuna%' 
--    OR LOWER(nombre) LIKE '%vaccine%'
--    OR LOWER(nombre) LIKE '%inmuni%';

