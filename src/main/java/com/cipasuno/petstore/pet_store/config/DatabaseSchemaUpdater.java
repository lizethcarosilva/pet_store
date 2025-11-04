package com.cipasuno.petstore.pet_store.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Componente para actualizar esquema de base de datos automáticamente
 * DESHABILITADO TEMPORALMENTE - Las tablas ya existen y están correctamente configuradas
 */
// @Component
public class DatabaseSchemaUpdater {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void updateSchema() {
        try {
            // Crear tabla client si no existe
            createClientTable();
            
            // Agregar columna appointment_id a invoice si no existe
            addAppointmentIdToInvoice();
            
            // Agregar columnas a product si no existen
            addProductColumns();
            
            // Crear tabla pet_medical_history si no existe
            createPetMedicalHistoryTable();
            
            // Crear tabla vaccination si no existe
            createVaccinationTable();
            
            // Actualizar pet_owner para soportar client_id
            updatePetOwnerTable();
            
            // Actualizar appointment para usar client_id en lugar de user_id
            updateAppointmentTable();
            
            // Actualizar invoice para que client_id apunte a la tabla client
            updateInvoiceClientReference();
            
            // Actualizar vaccination para agregar columna estado
            updateVaccinationTable();
            
            // Actualizar invoice_detail para soportar vacunaciones y citas
            updateInvoiceDetailTable();
            
            System.out.println("✅ Esquema de base de datos actualizado correctamente");
        } catch (Exception e) {
            System.err.println("❌ Error al actualizar esquema: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createClientTable() {
        try {
            jdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS client (" +
                "client_id SERIAL PRIMARY KEY, " +
                "tenant_id VARCHAR(50) NOT NULL, " +
                "name VARCHAR(200) NOT NULL, " +
                "tipo_id VARCHAR(10), " +
                "ident VARCHAR(50) UNIQUE NOT NULL, " +
                "correo VARCHAR(100) UNIQUE NOT NULL, " +
                "telefono VARCHAR(20), " +
                "direccion VARCHAR(300), " +
                "observaciones VARCHAR(500), " +
                "activo BOOLEAN NOT NULL DEFAULT true, " +
                "created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );
            
            // Crear índices
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_client_tenant_id ON client(tenant_id)");
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_client_ident ON client(ident)");
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_client_correo ON client(correo)");
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_client_activo ON client(activo)");
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_client_name ON client(name)");
            
            System.out.println("✅ Tabla 'client' verificada/creada");
        } catch (Exception e) {
            System.err.println("⚠️  Error con tabla client: " + e.getMessage());
        }
    }

    private void addAppointmentIdToInvoice() {
        try {
            jdbcTemplate.execute(
                "ALTER TABLE invoice ADD COLUMN IF NOT EXISTS appointment_id INTEGER"
            );
            jdbcTemplate.execute(
                "CREATE INDEX IF NOT EXISTS idx_invoice_appointment_id ON invoice(appointment_id)"
            );
            System.out.println("✅ Columna 'appointment_id' verificada en invoice");
        } catch (Exception e) {
            System.err.println("⚠️  Error con appointment_id en invoice: " + e.getMessage());
        }
    }

    private void addProductColumns() {
        try {
            jdbcTemplate.execute(
                "ALTER TABLE product ADD COLUMN IF NOT EXISTS es_vacuna BOOLEAN DEFAULT false"
            );
            jdbcTemplate.execute(
                "ALTER TABLE product ADD COLUMN IF NOT EXISTS lote VARCHAR(100)"
            );
            jdbcTemplate.execute(
                "ALTER TABLE product ADD COLUMN IF NOT EXISTS fabricante VARCHAR(200)"
            );
            jdbcTemplate.execute(
                "CREATE INDEX IF NOT EXISTS idx_product_es_vacuna ON product(es_vacuna)"
            );
            System.out.println("✅ Columnas de vacuna verificadas en product");
        } catch (Exception e) {
            System.err.println("⚠️  Error con columnas de product: " + e.getMessage());
        }
    }

    private void createPetMedicalHistoryTable() {
        try {
            jdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS pet_medical_history (" +
                "history_id SERIAL PRIMARY KEY, " +
                "tenant_id VARCHAR(50) NOT NULL, " +
                "pet_id INTEGER NOT NULL, " +
                "appointment_id INTEGER, " +
                "service_id INTEGER NOT NULL, " +
                "veterinarian_id INTEGER NOT NULL, " +
                "fecha_atencion TIMESTAMP NOT NULL, " +
                "tipo_procedimiento VARCHAR(100) NOT NULL, " +
                "diagnostico VARCHAR(2000), " +
                "observaciones VARCHAR(2000), " +
                "tratamiento VARCHAR(2000), " +
                "peso_kg NUMERIC(5,2), " +
                "temperatura_c NUMERIC(4,2), " +
                "notas_adicionales VARCHAR(2000), " +
                "activo BOOLEAN NOT NULL DEFAULT true, " +
                "created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );
            System.out.println("✅ Tabla 'pet_medical_history' verificada/creada");
        } catch (Exception e) {
            System.err.println("⚠️  Error con tabla pet_medical_history: " + e.getMessage());
        }
    }

    private void createVaccinationTable() {
        try {
            jdbcTemplate.execute(
                "CREATE TABLE IF NOT EXISTS vaccination (" +
                "vaccination_id SERIAL PRIMARY KEY, " +
                "tenant_id VARCHAR(50) NOT NULL, " +
                "pet_id INTEGER NOT NULL, " +
                "medical_history_id INTEGER, " +
                "veterinarian_id INTEGER NOT NULL, " +
                "vaccine_name VARCHAR(200) NOT NULL, " +
                "vaccine_type VARCHAR(100), " +
                "manufacturer VARCHAR(200), " +
                "batch_number VARCHAR(100), " +
                "application_date DATE NOT NULL, " +
                "next_dose_date DATE, " +
                "dose_number INTEGER, " +
                "application_site VARCHAR(100), " +
                "observations VARCHAR(1000), " +
                "requires_booster BOOLEAN DEFAULT false, " +
                "is_completed BOOLEAN DEFAULT false, " +
                "activo BOOLEAN NOT NULL DEFAULT true, " +
                "created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                ")"
            );
            System.out.println("✅ Tabla 'vaccination' verificada/creada");
        } catch (Exception e) {
            System.err.println("⚠️  Error con tabla vaccination: " + e.getMessage());
        }
    }

    private void updatePetOwnerTable() {
        try {
            // Verificar si user_id existe, si no, agregarla
            try {
                jdbcTemplate.execute(
                    "DO $$ " +
                    "BEGIN " +
                    "    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'pet_owner' AND column_name = 'user_id') THEN " +
                    "        ALTER TABLE pet_owner ADD COLUMN user_id INTEGER; " +
                    "        RAISE NOTICE 'Columna user_id agregada a pet_owner'; " +
                    "    END IF; " +
                    "END$$"
                );
            } catch (Exception e) {
                System.out.println("⚠️  Error agregando user_id: " + e.getMessage());
            }
            
            // Agregar columna client_id si no existe
            try {
                jdbcTemplate.execute(
                    "DO $$ " +
                    "BEGIN " +
                    "    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'pet_owner' AND column_name = 'client_id') THEN " +
                    "        ALTER TABLE pet_owner ADD COLUMN client_id INTEGER; " +
                    "        RAISE NOTICE 'Columna client_id agregada a pet_owner'; " +
                    "    END IF; " +
                    "END$$"
                );
            } catch (Exception e) {
                System.out.println("⚠️  Error agregando client_id: " + e.getMessage());
            }
            
            // Agregar created_on si no existe
            try {
                jdbcTemplate.execute(
                    "DO $$ " +
                    "BEGIN " +
                    "    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'pet_owner' AND column_name = 'created_on') THEN " +
                    "        ALTER TABLE pet_owner ADD COLUMN created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP; " +
                    "        RAISE NOTICE 'Columna created_on agregada a pet_owner'; " +
                    "    END IF; " +
                    "END$$"
                );
            } catch (Exception e) {
                System.out.println("⚠️  Error agregando created_on: " + e.getMessage());
            }
            
            // Hacer user_id nullable (puede fallar si ya lo es)
            try {
                jdbcTemplate.execute(
                    "ALTER TABLE pet_owner ALTER COLUMN user_id DROP NOT NULL"
                );
            } catch (Exception e) {
                // Ignorar si ya es nullable
            }
            
            // Crear índices
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_pet_owner_user_id ON pet_owner(user_id)");
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_pet_owner_client_id ON pet_owner(client_id)");
            
            // Intentar crear constraint de foreign key para client_id
            try {
                jdbcTemplate.execute(
                    "DO $$ " +
                    "BEGIN " +
                    "    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE constraint_name = 'fk_pet_owner_client') THEN " +
                    "        ALTER TABLE pet_owner ADD CONSTRAINT fk_pet_owner_client FOREIGN KEY (client_id) REFERENCES client(client_id) ON DELETE CASCADE; " +
                    "        RAISE NOTICE 'Constraint fk_pet_owner_client agregado'; " +
                    "    END IF; " +
                    "EXCEPTION " +
                    "    WHEN undefined_table THEN " +
                    "        RAISE NOTICE 'Tabla client aún no existe'; " +
                    "    WHEN OTHERS THEN " +
                    "        RAISE NOTICE 'Error creando constraint: %', SQLERRM; " +
                    "END$$"
                );
            } catch (Exception e) {
                System.out.println("⚠️  Constraint client será creado cuando exista la tabla client");
            }
            
            System.out.println("✅ Tabla 'pet_owner' actualizada para soportar clientes");
        } catch (Exception e) {
            System.err.println("⚠️  Error actualizando pet_owner: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void updateAppointmentTable() {
        try {
            // Agregar la columna client_id si no existe
            try {
                jdbcTemplate.execute(
                    "DO $$ " +
                    "BEGIN " +
                    "    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'appointment' AND column_name = 'client_id') THEN " +
                    "        ALTER TABLE appointment ADD COLUMN client_id INTEGER; " +
                    "        RAISE NOTICE 'Columna client_id agregada a appointment'; " +
                    "    END IF; " +
                    "END$$"
                );
            } catch (Exception e) {
                System.out.println("⚠️  Error agregando client_id: " + e.getMessage());
            }
            
            // Hacer user_id nullable (puede fallar si ya lo es)
            try {
                jdbcTemplate.execute(
                    "ALTER TABLE appointment ALTER COLUMN user_id DROP NOT NULL"
                );
            } catch (Exception e) {
                // Ignorar si ya es nullable
            }
            
            // Crear índice para client_id
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_appointment_client_id ON appointment(client_id)");
            
            // Intentar crear constraint de foreign key para client_id
            try {
                jdbcTemplate.execute(
                    "DO $$ " +
                    "BEGIN " +
                    "    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE constraint_name = 'fk_appointment_client') THEN " +
                    "        ALTER TABLE appointment ADD CONSTRAINT fk_appointment_client FOREIGN KEY (client_id) REFERENCES client(client_id) ON DELETE CASCADE; " +
                    "        RAISE NOTICE 'Constraint fk_appointment_client agregado'; " +
                    "    END IF; " +
                    "EXCEPTION " +
                    "    WHEN undefined_table THEN " +
                    "        RAISE NOTICE 'Tabla client aún no existe'; " +
                    "    WHEN OTHERS THEN " +
                    "        RAISE NOTICE 'Error creando constraint: %', SQLERRM; " +
                    "END$$"
                );
            } catch (Exception e) {
                System.out.println("⚠️  Constraint client será creado cuando exista la tabla client");
            }
            
            System.out.println("✅ Tabla 'appointment' actualizada para usar client_id");
        } catch (Exception e) {
            System.err.println("⚠️  Error actualizando appointment: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void updateInvoiceClientReference() {
        try {
            // La columna client_id ya existe en invoice, pero puede estar apuntando a la tabla 'user'
            // Necesitamos eliminar la constraint antigua y crear una nueva que apunte a 'client'
            
            // Paso 1: Eliminar constraint antigua si existe (puede fallar si no existe)
            try {
                jdbcTemplate.execute(
                    "ALTER TABLE invoice DROP CONSTRAINT IF EXISTS fk_invoice_client CASCADE"
                );
            } catch (Exception e) {
                // Ignorar si no existe
            }
            
            // Paso 2: Eliminar constraint antigua que apunte a user (puede tener otro nombre)
            try {
                jdbcTemplate.execute(
                    "DO $$ " +
                    "DECLARE " +
                    "    constraint_name TEXT; " +
                    "BEGIN " +
                    "    SELECT tc.constraint_name INTO constraint_name " +
                    "    FROM information_schema.table_constraints tc " +
                    "    JOIN information_schema.key_column_usage kcu ON tc.constraint_name = kcu.constraint_name " +
                    "    WHERE tc.table_name = 'invoice' AND kcu.column_name = 'client_id' AND tc.constraint_type = 'FOREIGN KEY'; " +
                    "    " +
                    "    IF constraint_name IS NOT NULL THEN " +
                    "        EXECUTE 'ALTER TABLE invoice DROP CONSTRAINT ' || constraint_name || ' CASCADE'; " +
                    "        RAISE NOTICE 'Constraint antigua % eliminada', constraint_name; " +
                    "    END IF; " +
                    "END$$"
                );
            } catch (Exception e) {
                System.out.println("⚠️  Error eliminando constraint antigua: " + e.getMessage());
            }
            
            // Paso 3: Crear nueva constraint que apunte a la tabla client
            try {
                jdbcTemplate.execute(
                    "DO $$ " +
                    "BEGIN " +
                    "    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE constraint_name = 'fk_invoice_client') THEN " +
                    "        ALTER TABLE invoice ADD CONSTRAINT fk_invoice_client FOREIGN KEY (client_id) REFERENCES client(client_id) ON DELETE CASCADE; " +
                    "        RAISE NOTICE 'Constraint fk_invoice_client agregado'; " +
                    "    END IF; " +
                    "EXCEPTION " +
                    "    WHEN undefined_table THEN " +
                    "        RAISE NOTICE 'Tabla client aún no existe'; " +
                    "    WHEN OTHERS THEN " +
                    "        RAISE NOTICE 'Error creando constraint: %', SQLERRM; " +
                    "END$$"
                );
            } catch (Exception e) {
                System.out.println("⚠️  Constraint fk_invoice_client será creado cuando exista la tabla client");
            }
            
            System.out.println("✅ Referencias de Invoice actualizadas para usar tabla client");
        } catch (Exception e) {
            System.err.println("⚠️  Error actualizando invoice references: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void updateVaccinationTable() {
        try {
            System.out.println("🔧 Verificando columna 'estado' en tabla vaccination...");
            
            // Verificar si la columna ya existe
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.columns WHERE table_name = 'vaccination' AND column_name = 'estado'",
                Integer.class
            );
            
            if (count != null && count > 0) {
                System.out.println("✅ Columna 'estado' ya existe en vaccination");
            } else {
                System.out.println("⚠️  Columna 'estado' NO existe, agregando...");
                // Agregar la columna estado si no existe
                jdbcTemplate.execute(
                    "DO $$ " +
                    "BEGIN " +
                    "    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'vaccination' AND column_name = 'estado') THEN " +
                    "        ALTER TABLE vaccination ADD COLUMN estado VARCHAR(20) NOT NULL DEFAULT 'APLICADA'; " +
                    "        RAISE NOTICE 'Columna estado agregada a vaccination'; " +
                    "    END IF; " +
                    "END$$"
                );
                System.out.println("✅ Columna 'estado' agregada a vaccination con valor por defecto 'APLICADA'");
            }
            
            // Crear índice para estado
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_vaccination_estado ON vaccination(estado)");
            System.out.println("✅ Índice idx_vaccination_estado creado/verificado");
            
            System.out.println("✅ Tabla 'vaccination' actualizada con columna estado");
        } catch (Exception e) {
            System.err.println("⚠️  Error actualizando vaccination: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void updateInvoiceDetailTable() {
        try {
            System.out.println("🔧 Actualizando tabla invoice_detail para soportar vacunaciones y citas...");
            
            // Agregar la columna vaccination_id si no existe
            try {
                jdbcTemplate.execute(
                    "DO $$ " +
                    "BEGIN " +
                    "    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'invoice_detail' AND column_name = 'vaccination_id') THEN " +
                    "        ALTER TABLE invoice_detail ADD COLUMN vaccination_id INTEGER; " +
                    "        RAISE NOTICE 'Columna vaccination_id agregada a invoice_detail'; " +
                    "    END IF; " +
                    "END$$"
                );
                System.out.println("✅ Columna vaccination_id verificada/agregada");
            } catch (Exception e) {
                System.out.println("⚠️  Error agregando vaccination_id: " + e.getMessage());
            }
            
            // Agregar la columna appointment_id si no existe
            try {
                jdbcTemplate.execute(
                    "DO $$ " +
                    "BEGIN " +
                    "    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'invoice_detail' AND column_name = 'appointment_id') THEN " +
                    "        ALTER TABLE invoice_detail ADD COLUMN appointment_id INTEGER; " +
                    "        RAISE NOTICE 'Columna appointment_id agregada a invoice_detail'; " +
                    "    END IF; " +
                    "END$$"
                );
                System.out.println("✅ Columna appointment_id verificada/agregada");
            } catch (Exception e) {
                System.out.println("⚠️  Error agregando appointment_id: " + e.getMessage());
            }
            
            // Crear índices
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_invoice_detail_vaccination_id ON invoice_detail(vaccination_id)");
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_invoice_detail_appointment_id ON invoice_detail(appointment_id)");
            
            // Agregar constraints de foreign key
            try {
                jdbcTemplate.execute(
                    "DO $$ " +
                    "BEGIN " +
                    "    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE constraint_name = 'fk_invoice_detail_vaccination') THEN " +
                    "        ALTER TABLE invoice_detail ADD CONSTRAINT fk_invoice_detail_vaccination FOREIGN KEY (vaccination_id) REFERENCES vaccination(vaccination_id) ON DELETE SET NULL; " +
                    "        RAISE NOTICE 'Constraint fk_invoice_detail_vaccination agregado'; " +
                    "    END IF; " +
                    "END$$"
                );
            } catch (Exception e) {
                System.out.println("⚠️  Error agregando constraint vaccination: " + e.getMessage());
            }
            
            try {
                jdbcTemplate.execute(
                    "DO $$ " +
                    "BEGIN " +
                    "    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE constraint_name = 'fk_invoice_detail_appointment') THEN " +
                    "        ALTER TABLE invoice_detail ADD CONSTRAINT fk_invoice_detail_appointment FOREIGN KEY (appointment_id) REFERENCES appointment(appointment_id) ON DELETE SET NULL; " +
                    "        RAISE NOTICE 'Constraint fk_invoice_detail_appointment agregado'; " +
                    "    END IF; " +
                    "END$$"
                );
            } catch (Exception e) {
                System.out.println("⚠️  Error agregando constraint appointment: " + e.getMessage());
            }
            
            System.out.println("✅ Tabla 'invoice_detail' actualizada para soportar vacunaciones y citas");
        } catch (Exception e) {
            System.err.println("⚠️  Error actualizando invoice_detail: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
