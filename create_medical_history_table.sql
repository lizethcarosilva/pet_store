-- ============================================================
-- TABLA: HISTORIAL MÉDICO DE MASCOTAS (CON TENANT)
-- ============================================================
CREATE TABLE IF NOT EXISTS pet_medical_history (
    history_id SERIAL PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    pet_id INTEGER NOT NULL,
    appointment_id INTEGER,
    service_id INTEGER NOT NULL,
    veterinarian_id INTEGER NOT NULL,
    fecha_atencion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tipo_procedimiento VARCHAR(100) NOT NULL,
    diagnostico VARCHAR(2000),
    observaciones VARCHAR(2000),
    tratamiento VARCHAR(2000),
    peso_kg DECIMAL(5, 2),
    temperatura_c DECIMAL(4, 2),
    notas_adicionales VARCHAR(2000),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES tenant(tenant_id) ON DELETE CASCADE,
    FOREIGN KEY (pet_id) REFERENCES pet(pet_id) ON DELETE CASCADE,
    FOREIGN KEY (appointment_id) REFERENCES appointment(appointment_id) ON DELETE SET NULL,
    FOREIGN KEY (service_id) REFERENCES service(service_id),
    FOREIGN KEY (veterinarian_id) REFERENCES "user"(user_id)
);

-- Índices para tabla pet_medical_history
CREATE INDEX idx_medical_history_tenant ON pet_medical_history(tenant_id);
CREATE INDEX idx_medical_history_pet ON pet_medical_history(pet_id);
CREATE INDEX idx_medical_history_appointment ON pet_medical_history(appointment_id);
CREATE INDEX idx_medical_history_service ON pet_medical_history(service_id);
CREATE INDEX idx_medical_history_veterinarian ON pet_medical_history(veterinarian_id);
CREATE INDEX idx_medical_history_fecha ON pet_medical_history(fecha_atencion);
CREATE INDEX idx_medical_history_tipo_procedimiento ON pet_medical_history(tipo_procedimiento);
CREATE INDEX idx_medical_history_activo ON pet_medical_history(activo);
CREATE INDEX idx_medical_history_tenant_pet ON pet_medical_history(tenant_id, pet_id);
CREATE INDEX idx_medical_history_tenant_fecha ON pet_medical_history(tenant_id, fecha_atencion);

COMMENT ON TABLE pet_medical_history IS 'Historial médico completo de las mascotas (hoja de vida veterinaria)';
COMMENT ON COLUMN pet_medical_history.tenant_id IS 'ID del tenant al que pertenece el historial médico';
COMMENT ON COLUMN pet_medical_history.appointment_id IS 'ID de la cita relacionada (opcional, puede ser NULL si no hay cita asociada)';
COMMENT ON COLUMN pet_medical_history.tipo_procedimiento IS 'Tipo de procedimiento realizado: Consulta General, Desparasitación, Baño, Limpieza de Pulgas, etc.';
COMMENT ON COLUMN pet_medical_history.diagnostico IS 'Diagnóstico realizado por el veterinario';
COMMENT ON COLUMN pet_medical_history.observaciones IS 'Observaciones generales de la atención';
COMMENT ON COLUMN pet_medical_history.tratamiento IS 'Tratamiento, medicamentos o productos utilizados';

