package com.cipasuno.petstore.pet_store.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controlador temporal para ejecutar migraciones de base de datos
 * SOLO PARA DESARROLLO - Eliminar en producción
 */
@RestController
@RequestMapping("/api/admin/migrations")
@CrossOrigin(origins = "*")
@Tag(name = "Database Migrations", description = "Ejecutar migraciones de base de datos (SOLO DESARROLLO)")
public class DatabaseMigrationController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/debugRoles")
    @Operation(summary = "Ver todos los roles en la base de datos")
    public ResponseEntity<?> debugRoles() {
        try {
            String query = "SELECT rol_id, name, descripcion FROM roles ORDER BY rol_id";
            var roles = jdbcTemplate.queryForList(query);
            
            // También obtener información de usuarios
            String usersQuery = "SELECT user_id, name, rol_id, correo FROM \"user\" ORDER BY user_id LIMIT 10";
            var users = jdbcTemplate.queryForList(usersQuery);
            
            var response = new java.util.HashMap<String, Object>();
            response.put("roles", roles);
            response.put("sampleUsers", users);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body("Error consultando roles: " + e.getMessage());
        }
    }

    @GetMapping("/whoami")
    @Operation(summary = "Ver información de autenticación del usuario actual")
    public ResponseEntity<?> whoami() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            var result = new java.util.HashMap<String, Object>();
            result.put("authenticated", authentication != null && authentication.isAuthenticated());
            
            if (authentication != null) {
                result.put("principal", authentication.getPrincipal());
                result.put("authorities", authentication.getAuthorities().toString());
                
                // Extraer detalles del JWT
                if (authentication.getDetails() instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
                    result.put("userRole", details.get("role"));
                    result.put("userId", details.get("userId"));
                    result.put("tenantId", details.get("tenantId"));
                }
            }
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/fixOrphanedData")
    @Operation(summary = "Corregir datos huérfanos en la base de datos")
    public ResponseEntity<?> fixOrphanedData() {
        try {
            StringBuilder log = new StringBuilder();
            log.append("=== Corrigiendo datos huérfanos ===\n\n");
            
            // 1. Primero, eliminar historiales médicos y vacunaciones que referencian citas huérfanas
            log.append("1. Limpiando historiales médicos con citas huérfanas...\n");
            try {
                String deleteMedicalHistoryFromOrphanedAppointments = 
                    "DELETE FROM pet_medical_history WHERE appointment_id IS NOT NULL AND appointment_id NOT IN (SELECT appointment_id FROM appointment WHERE user_id IN (SELECT user_id FROM \"user\"))";
                int deletedMH = jdbcTemplate.update(deleteMedicalHistoryFromOrphanedAppointments);
                log.append(String.format("   ✅ %d historiales médicos con citas huérfanas eliminados\n\n", deletedMH));
            } catch (Exception e) {
                log.append("   ⚠️  Error: " + e.getMessage() + "\n\n");
            }
            
            // 2. Eliminar citas huérfanas (appointments que referencian users que no existen)
            log.append("2. Eliminando citas huérfanas...\n");
            try {
                String deleteOrphanedAppointments = 
                    "DELETE FROM appointment WHERE user_id NOT IN (SELECT user_id FROM \"user\")";
                int deletedAppointments = jdbcTemplate.update(deleteOrphanedAppointments);
                log.append(String.format("   ✅ %d citas huérfanas eliminadas\n\n", deletedAppointments));
            } catch (Exception e) {
                log.append("   ⚠️  Error: " + e.getMessage() + "\n\n");
            }
            
            // 3. Eliminar veterinarian_id huérfanos en appointments
            log.append("3. Corrigiendo veterinarios huérfanos en citas...\n");
            String fixOrphanedVeterinarians = 
                "UPDATE appointment SET veterinarian_id = NULL WHERE veterinarian_id IS NOT NULL AND veterinarian_id NOT IN (SELECT user_id FROM \"user\")";
            int fixedVeterinarians = jdbcTemplate.update(fixOrphanedVeterinarians);
            log.append(String.format("   ✅ %d referencias de veterinarios corregidas\n\n", fixedVeterinarians));
            
            // 4. Eliminar pet_owner huérfanos
            log.append("4. Eliminando relaciones pet_owner huérfanas...\n");
            try {
                String deleteOrphanedPetOwners = 
                    "DELETE FROM pet_owner WHERE pet_id NOT IN (SELECT pet_id FROM pet)";
                int deletedPetOwners = jdbcTemplate.update(deleteOrphanedPetOwners);
                log.append(String.format("   ✅ %d relaciones pet_owner huérfanas eliminadas\n\n", deletedPetOwners));
            } catch (Exception e) {
                log.append("   ⚠️  Error: " + e.getMessage() + "\n\n");
            }
            
            // 5. Eliminar historial médico huérfano (por pets)
            log.append("5. Eliminando historiales médicos huérfanos (por pets)...\n");
            try {
                String deleteOrphanedMedicalHistory = 
                    "DELETE FROM pet_medical_history WHERE pet_id NOT IN (SELECT pet_id FROM pet)";
                int deletedHistory = jdbcTemplate.update(deleteOrphanedMedicalHistory);
                log.append(String.format("   ✅ %d historiales médicos huérfanos eliminados\n\n", deletedHistory));
            } catch (Exception e) {
                log.append("   ⚠️  Tabla no existe o error: " + e.getMessage() + "\n\n");
            }
            
            // 6. Eliminar vacunaciones huérfanas
            log.append("6. Eliminando vacunaciones huérfanas...\n");
            try {
                String deleteOrphanedVaccinations = 
                    "DELETE FROM vaccination WHERE pet_id NOT IN (SELECT pet_id FROM pet)";
                int deletedVaccinations = jdbcTemplate.update(deleteOrphanedVaccinations);
                log.append(String.format("   ✅ %d vacunaciones huérfanas eliminadas\n\n", deletedVaccinations));
            } catch (Exception e) {
                log.append("   ⚠️  Tabla no existe o error: " + e.getMessage() + "\n\n");
            }
            
            // 7. Eliminar detalles de facturas huérfanas (facturas con clientes que no existen)
            log.append("7. Eliminando detalles de facturas huérfanas...\n");
            try {
                String deleteOrphanedInvoiceDetails = 
                    "DELETE FROM invoice_detail WHERE invoice_id IN (SELECT invoice_id FROM invoice WHERE client_id NOT IN (SELECT client_id FROM client))";
                int deletedInvoiceDetails = jdbcTemplate.update(deleteOrphanedInvoiceDetails);
                log.append(String.format("   ✅ %d detalles de facturas huérfanas eliminados\n\n", deletedInvoiceDetails));
            } catch (Exception e) {
                log.append("   ⚠️  Error: " + e.getMessage() + "\n\n");
            }
            
            // 8. Eliminar facturas huérfanas (facturas que referencian clientes que no existen)
            log.append("8. Eliminando facturas huérfanas...\n");
            try {
                String deleteOrphanedInvoices = 
                    "DELETE FROM invoice WHERE client_id NOT IN (SELECT client_id FROM client)";
                int deletedInvoices = jdbcTemplate.update(deleteOrphanedInvoices);
                log.append(String.format("   ✅ %d facturas huérfanas eliminadas\n\n", deletedInvoices));
            } catch (Exception e) {
                log.append("   ⚠️  Error: " + e.getMessage() + "\n\n");
            }
            
            log.append("=== ✅ Datos huérfanos corregidos ===");
            
            return ResponseEntity.ok(log.toString());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body("Error corrigiendo datos huérfanos: " + e.getMessage() + "\n" + getStackTrace(e));
        }
    }

    @GetMapping("/checkVaccinationEstados")
    @Operation(summary = "Ver estados actuales de todas las vacunaciones")
    public ResponseEntity<?> checkVaccinationEstados() {
        try {
            StringBuilder log = new StringBuilder();
            log.append("=== Estados de Vacunaciones ===\n\n");
            
            // Ver todas las vacunaciones con sus estados
            var vaccinations = jdbcTemplate.queryForList(
                "SELECT vaccination_id, pet_id, vaccine_name, estado, activo, created_on " +
                "FROM vaccination " +
                "ORDER BY created_on DESC LIMIT 50"
            );
            
            log.append("Últimas 50 vacunaciones:\n");
            for (var vac : vaccinations) {
                log.append(String.format("  ID: %d | Pet: %d | Vacuna: %s | Estado: '%s' | Activo: %b\n",
                    vac.get("vaccination_id"),
                    vac.get("pet_id"),
                    vac.get("vaccine_name"),
                    vac.get("estado"),
                    vac.get("activo")
                ));
            }
            log.append("\n");
            
            // Ver resumen por estados
            var resumen = jdbcTemplate.queryForList(
                "SELECT estado, COUNT(*) as cantidad FROM vaccination GROUP BY estado ORDER BY estado"
            );
            
            log.append("Resumen por estados:\n");
            for (var row : resumen) {
                log.append(String.format("  '%s': %d registros\n", row.get("estado"), row.get("cantidad")));
            }
            
            return ResponseEntity.ok(log.toString());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/fixVaccinationEstados")
    @Operation(summary = "Corregir estados de vacunaciones (normalizar a APLICADA, FACTURADA, PENDIENTE, CANCELADA)")
    public ResponseEntity<?> fixVaccinationEstados() {
        try {
            StringBuilder log = new StringBuilder();
            log.append("=== Corrigiendo estados de vacunaciones ===\n\n");
            
            // 1. Ver estados actuales
            log.append("1. Estados actuales en la tabla vaccination:\n");
            try {
                var estadosActuales = jdbcTemplate.queryForList(
                    "SELECT DISTINCT estado, COUNT(*) as cantidad FROM vaccination GROUP BY estado ORDER BY estado"
                );
                for (var row : estadosActuales) {
                    log.append(String.format("   - '%s': %d registros\n", row.get("estado"), row.get("cantidad")));
                }
                log.append("\n");
            } catch (Exception e) {
                log.append("   ⚠️  Error: " + e.getMessage() + "\n\n");
            }
            
            // 2. Normalizar estados inconsistentes
            log.append("2. Normalizando estados...\n");
            
            // Normalizar "Completo", "Completada", "COMPLETADA" -> "APLICADA"
            int updatedCompleto = jdbcTemplate.update(
                "UPDATE vaccination SET estado = 'APLICADA' " +
                "WHERE LOWER(estado) IN ('completo', 'completada', 'completado') " +
                "AND activo = true"
            );
            log.append(String.format("   ✅ %d registros cambiados de Completo/Completada -> APLICADA\n", updatedCompleto));
            
            // Normalizar "Facturado", "Facturada" -> "FACTURADA"
            int updatedFacturado = jdbcTemplate.update(
                "UPDATE vaccination SET estado = 'FACTURADA' " +
                "WHERE LOWER(estado) IN ('facturado', 'facturada') " +
                "AND activo = true"
            );
            log.append(String.format("   ✅ %d registros cambiados de Facturado/Facturada -> FACTURADA\n", updatedFacturado));
            
            // Normalizar "Pendiente" -> "PENDIENTE"
            int updatedPendiente = jdbcTemplate.update(
                "UPDATE vaccination SET estado = 'PENDIENTE' " +
                "WHERE LOWER(estado) = 'pendiente' AND estado != 'PENDIENTE' " +
                "AND activo = true"
            );
            log.append(String.format("   ✅ %d registros cambiados a PENDIENTE (mayúsculas)\n", updatedPendiente));
            
            // Normalizar "Cancelado", "Cancelada" -> "CANCELADA"
            int updatedCancelado = jdbcTemplate.update(
                "UPDATE vaccination SET estado = 'CANCELADA' " +
                "WHERE LOWER(estado) IN ('cancelado', 'cancelada') " +
                "AND activo = true"
            );
            log.append(String.format("   ✅ %d registros cambiados de Cancelado/Cancelada -> CANCELADA\n\n", updatedCancelado));
            
            // 3. Actualizar registros vacíos o NULL
            int updatedNull = jdbcTemplate.update(
                "UPDATE vaccination SET estado = 'APLICADA' " +
                "WHERE (estado IS NULL OR estado = '') " +
                "AND activo = true"
            );
            log.append(String.format("3. Estados vacíos o NULL actualizados: %d\n\n", updatedNull));
            
            // 4. Ver estados finales
            log.append("4. Estados finales después de la corrección:\n");
            var estadosFinales = jdbcTemplate.queryForList(
                "SELECT DISTINCT estado, COUNT(*) as cantidad FROM vaccination GROUP BY estado ORDER BY estado"
            );
            for (var row : estadosFinales) {
                log.append(String.format("   - '%s': %d registros\n", row.get("estado"), row.get("cantidad")));
            }
            log.append("\n");
            
            log.append("=== ✅ Estados normalizados correctamente ===\n");
            log.append("\nEstados válidos: PENDIENTE, APLICADA, FACTURADA, CANCELADA\n");
            
            return ResponseEntity.ok(log.toString());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body("Error corrigiendo estados: " + e.getMessage() + "\n" + getStackTrace(e));
        }
    }

    @PostMapping("/run")
    @Operation(summary = "Ejecutar todas las migraciones pendientes")
    public ResponseEntity<?> runMigrations() {
        try {
            StringBuilder log = new StringBuilder();
            log.append("=== Iniciando migraciones de base de datos ===\n\n");
            
            // 1. Crear tabla client
            log.append("1. Creando tabla client...\n");
            createClientTable();
            log.append("   ✅ Tabla client verificada\n\n");
            
            // 2. Actualizar pet_owner
            log.append("2. Actualizando tabla pet_owner...\n");
            updatePetOwnerTable();
            log.append("   ✅ Tabla pet_owner actualizada\n\n");
            
            // 3. Agregar appointment_id a invoice
            log.append("3. Actualizando tabla invoice...\n");
            addAppointmentIdToInvoice();
            log.append("   ✅ Tabla invoice actualizada\n\n");
            
            // 4. Actualizar product
            log.append("4. Actualizando tabla product...\n");
            addProductColumns();
            log.append("   ✅ Tabla product actualizada\n\n");
            
            // 5. Crear pet_medical_history
            log.append("5. Creando tabla pet_medical_history...\n");
            createPetMedicalHistoryTable();
            log.append("   ✅ Tabla pet_medical_history verificada\n\n");
            
            // 6. Crear vaccination
            log.append("6. Creando tabla vaccination...\n");
            createVaccinationTable();
            log.append("   ✅ Tabla vaccination verificada\n\n");
            
            log.append("=== ✅ Todas las migraciones completadas exitosamente ===");
            
            return ResponseEntity.ok(log.toString());
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body("Error ejecutando migraciones: " + e.getMessage() + "\n" + getStackTrace(e));
        }
    }

    private void createClientTable() {
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
        
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_client_tenant_id ON client(tenant_id)");
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_client_ident ON client(ident)");
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_client_correo ON client(correo)");
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_client_activo ON client(activo)");
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_client_name ON client(name)");
    }

    private void updatePetOwnerTable() {
        // Verificar si user_id existe, si no, agregarla
        jdbcTemplate.execute(
            "DO $$ " +
            "BEGIN " +
            "    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'pet_owner' AND column_name = 'user_id') THEN " +
            "        ALTER TABLE pet_owner ADD COLUMN user_id INTEGER; " +
            "    END IF; " +
            "END$$"
        );
        
        // Agregar client_id
        jdbcTemplate.execute(
            "DO $$ " +
            "BEGIN " +
            "    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'pet_owner' AND column_name = 'client_id') THEN " +
            "        ALTER TABLE pet_owner ADD COLUMN client_id INTEGER; " +
            "    END IF; " +
            "END$$"
        );
        
        // Agregar created_on
        jdbcTemplate.execute(
            "DO $$ " +
            "BEGIN " +
            "    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'pet_owner' AND column_name = 'created_on') THEN " +
            "        ALTER TABLE pet_owner ADD COLUMN created_on TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP; " +
            "    END IF; " +
            "END$$"
        );
        
        // Hacer user_id nullable
        try {
            jdbcTemplate.execute("ALTER TABLE pet_owner ALTER COLUMN user_id DROP NOT NULL");
        } catch (Exception e) {
            // Ignorar
        }
        
        // Crear índices
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_pet_owner_user_id ON pet_owner(user_id)");
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_pet_owner_client_id ON pet_owner(client_id)");
        
        // Constraint
        try {
            jdbcTemplate.execute(
                "DO $$ " +
                "BEGIN " +
                "    IF NOT EXISTS (SELECT 1 FROM information_schema.table_constraints WHERE constraint_name = 'fk_pet_owner_client') THEN " +
                "        ALTER TABLE pet_owner ADD CONSTRAINT fk_pet_owner_client FOREIGN KEY (client_id) REFERENCES client(client_id) ON DELETE CASCADE; " +
                "    END IF; " +
                "EXCEPTION WHEN OTHERS THEN " +
                "    NULL; " +
                "END$$"
            );
        } catch (Exception e) {
            // Ignorar
        }
    }

    private void addAppointmentIdToInvoice() {
        jdbcTemplate.execute("ALTER TABLE invoice ADD COLUMN IF NOT EXISTS appointment_id INTEGER");
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_invoice_appointment_id ON invoice(appointment_id)");
    }

    private void addProductColumns() {
        jdbcTemplate.execute("ALTER TABLE product ADD COLUMN IF NOT EXISTS es_vacuna BOOLEAN DEFAULT false");
        jdbcTemplate.execute("ALTER TABLE product ADD COLUMN IF NOT EXISTS lote VARCHAR(100)");
        jdbcTemplate.execute("ALTER TABLE product ADD COLUMN IF NOT EXISTS fabricante VARCHAR(200)");
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_product_es_vacuna ON product(es_vacuna)");
    }

    private void createPetMedicalHistoryTable() {
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
    }

    private void createVaccinationTable() {
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
    }

    private String getStackTrace(Exception e) {
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element : e.getStackTrace()) {
            sb.append(element.toString()).append("\n");
        }
        return sb.toString();
    }
}

