-- ============================================================
-- TABLA: VACUNACIÓN DE MASCOTAS (CON TENANT)
-- ============================================================
CREATE TABLE IF NOT EXISTS vaccination (
    vaccination_id SERIAL PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    pet_id INTEGER NOT NULL,
    medical_history_id INTEGER,
    veterinarian_id INTEGER NOT NULL,
    vaccine_name VARCHAR(200) NOT NULL,
    vaccine_type VARCHAR(100),
    manufacturer VARCHAR(200),
    batch_number VARCHAR(100),
    application_date DATE NOT NULL DEFAULT CURRENT_DATE,
    next_dose_date DATE,
    dose_number INTEGER,
    application_site VARCHAR(100),
    observations VARCHAR(1000),
    requires_booster BOOLEAN NOT NULL DEFAULT FALSE,
    is_completed BOOLEAN NOT NULL DEFAULT FALSE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES tenant(tenant_id) ON DELETE CASCADE,
    FOREIGN KEY (pet_id) REFERENCES pet(pet_id) ON DELETE CASCADE,
    FOREIGN KEY (medical_history_id) REFERENCES pet_medical_history(history_id) ON DELETE SET NULL,
    FOREIGN KEY (veterinarian_id) REFERENCES "user"(user_id)
);

-- Índices para tabla vaccination
CREATE INDEX idx_vaccination_tenant ON vaccination(tenant_id);
CREATE INDEX idx_vaccination_pet ON vaccination(pet_id);
CREATE INDEX idx_vaccination_medical_history ON vaccination(medical_history_id);
CREATE INDEX idx_vaccination_veterinarian ON vaccination(veterinarian_id);
CREATE INDEX idx_vaccination_application_date ON vaccination(application_date);
CREATE INDEX idx_vaccination_next_dose_date ON vaccination(next_dose_date);
CREATE INDEX idx_vaccination_vaccine_name ON vaccination(vaccine_name);
CREATE INDEX idx_vaccination_activo ON vaccination(activo);
CREATE INDEX idx_vaccination_is_completed ON vaccination(is_completed);
CREATE INDEX idx_vaccination_tenant_pet ON vaccination(tenant_id, pet_id);
CREATE INDEX idx_vaccination_tenant_date ON vaccination(tenant_id, application_date);

COMMENT ON TABLE vaccination IS 'Registro de vacunación de mascotas con control de esquemas y refuerzos';
COMMENT ON COLUMN vaccination.tenant_id IS 'ID del tenant al que pertenece el registro';
COMMENT ON COLUMN vaccination.medical_history_id IS 'Referencia al historial médico (opcional)';
COMMENT ON COLUMN vaccination.vaccine_name IS 'Nombre de la vacuna (ej: Rabia, Parvovirus, Moquillo, Polivalente)';
COMMENT ON COLUMN vaccination.vaccine_type IS 'Tipo de vacuna: Viral, Bacteriana, Antiparasitaria';
COMMENT ON COLUMN vaccination.batch_number IS 'Número de lote para trazabilidad';
COMMENT ON COLUMN vaccination.next_dose_date IS 'Fecha programada para próxima dosis o refuerzo';
COMMENT ON COLUMN vaccination.dose_number IS 'Número de dosis en el esquema (1ra, 2da, 3ra, refuerzo)';
COMMENT ON COLUMN vaccination.requires_booster IS 'Indica si requiere refuerzo';
COMMENT ON COLUMN vaccination.is_completed IS 'Indica si el esquema de vacunación está completo';

