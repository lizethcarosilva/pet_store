package com.cipasuno.petstore.pet_store.controllers;

import com.cipasuno.petstore.pet_store.config.TenantContext;
import com.cipasuno.petstore.pet_store.models.DTOs.IdRequest;
import com.cipasuno.petstore.pet_store.models.DTOs.UpdateVaccinationRequest;
import com.cipasuno.petstore.pet_store.models.DTOs.VaccinationCreateDto;
import com.cipasuno.petstore.pet_store.models.DTOs.VaccinationInvoiceDataDto;
import com.cipasuno.petstore.pet_store.models.DTOs.VaccinationResponseDto;
import com.cipasuno.petstore.pet_store.security.RequiresRole;
import com.cipasuno.petstore.pet_store.services.VaccinationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/vaccinations")
@CrossOrigin(origins = "*")
@Tag(name = "Vaccination", description = "API para gestión de vacunación de mascotas")
public class VaccinationController {

    @Autowired
    private VaccinationService vaccinationService;

    @PostMapping("/create")
    @Operation(summary = "Registrar una nueva vacunación")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Veterinario"})
    public ResponseEntity<?> createVaccination(@RequestBody VaccinationCreateDto vaccination) {
        try {
            String tenantId = TenantContext.getTenantId();
            if (tenantId == null || tenantId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: tenantId es requerido. Verifique que el header X-Tenant-ID esté presente.");
            }

            VaccinationResponseDto createdVaccination = vaccinationService.createVaccination(vaccination, tenantId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdVaccination);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al registrar la vacunación: " + e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "Obtener todas las vacunaciones del tenant")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Veterinario"})
    public ResponseEntity<List<VaccinationResponseDto>> getAllVaccinations() {
        String tenantId = TenantContext.getTenantId();
        List<VaccinationResponseDto> vaccinations = vaccinationService.getVaccinationsByTenant(tenantId);
        return ResponseEntity.ok(vaccinations);
    }

    @PostMapping("/getByPet")
    @Operation(summary = "Obtener historial de vacunación completo de una mascota")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Veterinario", "Vendedor", "Cliente"})
    public ResponseEntity<?> getVaccinationsByPetId(@RequestBody IdRequest request) {
        try {
            String tenantId = TenantContext.getTenantId();
            List<VaccinationResponseDto> vaccinations = vaccinationService.getVaccinationsByPetIdAndTenant(
                request.getId(), tenantId);
            return ResponseEntity.ok(vaccinations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/getByMedicalHistory")
    @Operation(summary = "Obtener vacunaciones relacionadas con un historial médico")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Veterinario"})
    public ResponseEntity<?> getVaccinationsByMedicalHistoryId(@RequestBody IdRequest request) {
        try {
            List<VaccinationResponseDto> vaccinations = vaccinationService.getVaccinationsByMedicalHistoryId(request.getId());
            return ResponseEntity.ok(vaccinations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/getByVeterinarian")
    @Operation(summary = "Obtener vacunaciones aplicadas por un veterinario")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Veterinario"})
    public ResponseEntity<?> getVaccinationsByVeterinarianId(@RequestBody IdRequest request) {
        try {
            List<VaccinationResponseDto> vaccinations = vaccinationService.getVaccinationsByVeterinarianId(request.getId());
            return ResponseEntity.ok(vaccinations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/getByVaccine")
    @Operation(summary = "Obtener historial de una vacuna específica de una mascota")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Veterinario", "Vendedor", "Cliente"})
    public ResponseEntity<?> getVaccinationsByPetIdAndVaccineName(
            @RequestParam Integer petId,
            @RequestParam String vaccineName) {
        try {
            List<VaccinationResponseDto> vaccinations = vaccinationService.getVaccinationsByPetIdAndVaccineName(
                petId, vaccineName);
            return ResponseEntity.ok(vaccinations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Obtener vacunaciones próximas en un rango de fechas")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Veterinario"})
    public ResponseEntity<?> getUpcomingVaccinations(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            
            List<VaccinationResponseDto> vaccinations = vaccinationService.getUpcomingVaccinations(start, end);
            return ResponseEntity.ok(vaccinations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Error: " + e.getMessage() + ". Formato de fecha esperado: yyyy-MM-dd");
        }
    }

    @PostMapping("/overdue")
    @Operation(summary = "Obtener vacunaciones vencidas de una mascota")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Veterinario", "Vendedor", "Cliente"})
    public ResponseEntity<?> getOverdueVaccinationsByPetId(@RequestBody IdRequest request) {
        try {
            List<VaccinationResponseDto> vaccinations = vaccinationService.getOverdueVaccinationsByPetId(request.getId());
            return ResponseEntity.ok(vaccinations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/pending")
    @Operation(summary = "Obtener vacunaciones pendientes de una mascota")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Veterinario", "Vendedor", "Cliente"})
    public ResponseEntity<?> getPendingVaccinationsByPetId(@RequestBody IdRequest request) {
        try {
            List<VaccinationResponseDto> vaccinations = vaccinationService.getPendingVaccinationsByPetId(request.getId());
            return ResponseEntity.ok(vaccinations);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/getId")
    @Operation(summary = "Obtener vacunación por ID")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Veterinario", "Vendedor", "Cliente"})
    public ResponseEntity<?> getVaccinationById(@RequestBody IdRequest request) {
        try {
            Optional<VaccinationResponseDto> vaccination = vaccinationService.getVaccinationById(request.getId());
            if (vaccination.isPresent()) {
                return ResponseEntity.ok(vaccination.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/count")
    @Operation(summary = "Contar vacunaciones de una mascota")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Veterinario", "Vendedor", "Cliente"})
    public ResponseEntity<?> countVaccinationsByPetId(@RequestBody IdRequest request) {
        try {
            long count = vaccinationService.countVaccinationsByPetId(request.getId());
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/update")
    @Operation(summary = "Actualizar vacunación")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Veterinario"})
    public ResponseEntity<?> updateVaccination(@RequestBody UpdateVaccinationRequest vaccinationDetails) {
        try {
            String tenantId = TenantContext.getTenantId();
            if (tenantId == null || tenantId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: tenantId es requerido.");
            }

            if (vaccinationDetails.getVaccinationId() == null) {
                return ResponseEntity.badRequest()
                    .body("Error: vaccinationId es requerido.");
            }

            // Convertir UpdateVaccinationRequest a VaccinationCreateDto
            VaccinationCreateDto updateDto = new VaccinationCreateDto();
            updateDto.setVeterinarianId(vaccinationDetails.getVeterinarianId());
            updateDto.setVaccineName(vaccinationDetails.getVaccineName());
            updateDto.setVaccineType(vaccinationDetails.getVaccineType());
            updateDto.setManufacturer(vaccinationDetails.getManufacturer());
            updateDto.setBatchNumber(vaccinationDetails.getBatchNumber());
            updateDto.setApplicationDate(vaccinationDetails.getApplicationDate());
            updateDto.setNextDoseDate(vaccinationDetails.getNextDoseDate());
            updateDto.setDoseNumber(vaccinationDetails.getDoseNumber());
            updateDto.setApplicationSite(vaccinationDetails.getApplicationSite());
            updateDto.setObservations(vaccinationDetails.getObservations());
            updateDto.setRequiresBooster(vaccinationDetails.getRequiresBooster());
            updateDto.setIsCompleted(vaccinationDetails.getIsCompleted());

            VaccinationResponseDto updatedVaccination = vaccinationService.updateVaccination(
                vaccinationDetails.getVaccinationId(), updateDto, tenantId);
            return ResponseEntity.ok(updatedVaccination);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al actualizar la vacunación: " + e.getMessage());
        }
    }

    @DeleteMapping("/deleteVaccination")
    @Operation(summary = "Eliminar vacunación (soft delete)")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente"})
    public ResponseEntity<?> deleteVaccination(@RequestBody IdRequest request) {
        try {
            String tenantId = TenantContext.getTenantId();
            if (tenantId == null || tenantId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: tenantId es requerido.");
            }

            vaccinationService.deleteVaccination(request.getId(), tenantId);
            return ResponseEntity.ok("Vacunación eliminada exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al eliminar la vacunación: " + e.getMessage());
        }
    }

    @DeleteMapping("/deletePermanent")
    @Operation(summary = "Eliminar vacunación permanentemente")
    @RequiresRole({"SuperAdmin", "Admin"})
    public ResponseEntity<?> deleteVaccinationPermanently(@RequestBody IdRequest request) {
        try {
            String tenantId = TenantContext.getTenantId();
            if (tenantId == null || tenantId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: tenantId es requerido.");
            }

            vaccinationService.deleteVaccinationPermanently(request.getId(), tenantId);
            return ResponseEntity.ok("Vacunación eliminada permanentemente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al eliminar la vacunación: " + e.getMessage());
        }
    }

    @PostMapping("/forInvoice")
    @Operation(summary = "Obtener datos de vacunación para agregar al carrito de compras/factura")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Veterinario", "Empleado"})
    public ResponseEntity<?> getVaccinationForInvoice(@RequestBody IdRequest request) {
        try {
            VaccinationInvoiceDataDto vaccinationData = vaccinationService.getVaccinationForInvoice(request.getId());
            return ResponseEntity.ok(vaccinationData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Error al obtener datos de la vacunación: " + e.getMessage());
        }
    }

    @GetMapping("/completed/pet/{petId}")
    @Operation(summary = "Obtener vacunaciones COMPLETADAS (no facturadas) de una mascota para facturar")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Empleado", "Veterinario"})
    public ResponseEntity<List<VaccinationResponseDto>> getCompletedVaccinationsByPet(@PathVariable Integer petId) {
        System.out.println("🔵 CONTROLLER - Buscando vacunaciones COMPLETADAS para petId: " + petId);
        List<VaccinationResponseDto> vaccinations = vaccinationService.getCompletedVaccinationsByPet(petId);
        System.out.println("🔵 CONTROLLER - Devolviendo " + vaccinations.size() + " vacunaciones completadas");
        return ResponseEntity.ok(vaccinations);
    }

    @PutMapping("/markAsInvoiced")
    @Operation(summary = "Marcar vacunación como facturada")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Empleado"})
    public ResponseEntity<?> markVaccinationAsInvoiced(@RequestParam Integer vaccinationId) {
        System.out.println("🔵 CONTROLLER - Recibido vaccinationId: " + vaccinationId);
        try {
            VaccinationResponseDto updated = vaccinationService.markAsInvoiced(vaccinationId);
            System.out.println("🔵 CONTROLLER - Devolviendo estado: " + updated.getEstado());
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            System.out.println("🔴 CONTROLLER - ERROR: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al marcar vacunación como facturada: " + e.getMessage());
        }
    }
}

