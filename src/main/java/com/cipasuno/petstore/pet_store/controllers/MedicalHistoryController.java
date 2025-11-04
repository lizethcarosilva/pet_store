package com.cipasuno.petstore.pet_store.controllers;

import com.cipasuno.petstore.pet_store.config.TenantContext;
import com.cipasuno.petstore.pet_store.models.DTOs.IdRequest;
import com.cipasuno.petstore.pet_store.models.DTOs.MedicalHistoryCreateDto;
import com.cipasuno.petstore.pet_store.models.DTOs.MedicalHistoryResponseDto;
import com.cipasuno.petstore.pet_store.models.DTOs.UpdateMedicalHistoryRequest;
import com.cipasuno.petstore.pet_store.security.RequiresRole;
import com.cipasuno.petstore.pet_store.services.MedicalHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/medical-history")
@CrossOrigin(origins = "*")
@Tag(name = "Medical History", description = "API para gestión del historial médico de las mascotas")
public class MedicalHistoryController {

    @Autowired
    private MedicalHistoryService medicalHistoryService;

    @PostMapping("/create")
    @Operation(summary = "Crear un nuevo registro en el historial médico de una mascota")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Veterinario"})
    public ResponseEntity<?> createMedicalHistory(@RequestBody MedicalHistoryCreateDto history) {
        try {
            String tenantId = TenantContext.getTenantId();
            if (tenantId == null || tenantId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: tenantId es requerido. Verifique que el header X-Tenant-ID esté presente.");
            }

            MedicalHistoryResponseDto createdHistory = medicalHistoryService.createMedicalHistory(history, tenantId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdHistory);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al crear el historial médico: " + e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "Obtener todos los historiales médicos del tenant")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Veterinario"})
    public ResponseEntity<List<MedicalHistoryResponseDto>> getAllMedicalHistories() {
        String tenantId = TenantContext.getTenantId();
        List<MedicalHistoryResponseDto> histories = medicalHistoryService.getMedicalHistoriesByTenant(tenantId);
        return ResponseEntity.ok(histories);
    }

    @PostMapping("/getByPet")
    @Operation(summary = "Obtener historial médico completo de una mascota")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Veterinario", "Vendedor", "Cliente"})
    public ResponseEntity<?> getMedicalHistoriesByPetId(@RequestBody IdRequest request) {
        try {
            String tenantId = TenantContext.getTenantId();
            List<MedicalHistoryResponseDto> histories = medicalHistoryService.getMedicalHistoriesByPetIdAndTenant(
                request.getId(), tenantId);
            return ResponseEntity.ok(histories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/getByAppointment")
    @Operation(summary = "Obtener historial médico relacionado con una cita")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Veterinario"})
    public ResponseEntity<?> getMedicalHistoriesByAppointmentId(@RequestBody IdRequest request) {
        try {
            List<MedicalHistoryResponseDto> histories = medicalHistoryService.getMedicalHistoriesByAppointmentId(request.getId());
            return ResponseEntity.ok(histories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/getByVeterinarian")
    @Operation(summary = "Obtener historiales médicos atendidos por un veterinario")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Veterinario"})
    public ResponseEntity<?> getMedicalHistoriesByVeterinarianId(@RequestBody IdRequest request) {
        try {
            List<MedicalHistoryResponseDto> histories = medicalHistoryService.getMedicalHistoriesByVeterinarianId(request.getId());
            return ResponseEntity.ok(histories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/getByService")
    @Operation(summary = "Obtener historiales médicos por tipo de servicio")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Veterinario"})
    public ResponseEntity<?> getMedicalHistoriesByServiceId(@RequestBody IdRequest request) {
        try {
            List<MedicalHistoryResponseDto> histories = medicalHistoryService.getMedicalHistoriesByServiceId(request.getId());
            return ResponseEntity.ok(histories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/getByProcedureType")
    @Operation(summary = "Obtener historiales médicos de una mascota por tipo de procedimiento")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Veterinario", "Vendedor", "Cliente"})
    public ResponseEntity<?> getMedicalHistoriesByPetIdAndTipoProcedimiento(
            @RequestBody MedicalHistoryCreateDto request) {
        try {
            if (request.getPetId() == null || request.getTipoProcedimiento() == null) {
                return ResponseEntity.badRequest()
                    .body("Error: petId y tipoProcedimiento son requeridos");
            }
            
            List<MedicalHistoryResponseDto> histories = medicalHistoryService.getMedicalHistoriesByPetIdAndTipoProcedimiento(
                request.getPetId(), request.getTipoProcedimiento());
            return ResponseEntity.ok(histories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/getByDateRange")
    @Operation(summary = "Obtener historiales médicos de una mascota en un rango de fechas")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Veterinario", "Vendedor", "Cliente"})
    public ResponseEntity<?> getMedicalHistoriesByDateRange(
            @RequestParam Integer petId,
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin) {
        try {
            LocalDateTime inicio = LocalDateTime.parse(fechaInicio);
            LocalDateTime fin = LocalDateTime.parse(fechaFin);
            
            List<MedicalHistoryResponseDto> histories = medicalHistoryService.getMedicalHistoriesByPetIdAndDateRange(
                petId, inicio, fin);
            return ResponseEntity.ok(histories);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Error: " + e.getMessage() + ". Formato de fecha esperado: yyyy-MM-ddTHH:mm:ss");
        }
    }

    @PostMapping("/getId")
    @Operation(summary = "Obtener historial médico por ID")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Veterinario", "Vendedor", "Cliente"})
    public ResponseEntity<?> getMedicalHistoryById(@RequestBody IdRequest request) {
        try {
            Optional<MedicalHistoryResponseDto> history = medicalHistoryService.getMedicalHistoryById(request.getId());
            if (history.isPresent()) {
                return ResponseEntity.ok(history.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/update")
    @Operation(summary = "Actualizar historial médico")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Veterinario"})
    public ResponseEntity<?> updateMedicalHistory(@RequestBody UpdateMedicalHistoryRequest historyDetails) {
        try {
            String tenantId = TenantContext.getTenantId();
            if (tenantId == null || tenantId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: tenantId es requerido.");
            }

            if (historyDetails.getHistoryId() == null) {
                return ResponseEntity.badRequest()
                    .body("Error: historyId es requerido.");
            }

            // Convertir UpdateMedicalHistoryRequest a MedicalHistoryCreateDto
            MedicalHistoryCreateDto updateDto = new MedicalHistoryCreateDto();
            updateDto.setServiceId(historyDetails.getServiceId());
            updateDto.setVeterinarianId(historyDetails.getVeterinarianId());
            updateDto.setFechaAtencion(historyDetails.getFechaAtencion());
            updateDto.setTipoProcedimiento(historyDetails.getTipoProcedimiento());
            updateDto.setDiagnostico(historyDetails.getDiagnostico());
            updateDto.setObservaciones(historyDetails.getObservaciones());
            updateDto.setTratamiento(historyDetails.getTratamiento());
            updateDto.setPesoKg(historyDetails.getPesoKg());
            updateDto.setTemperaturaC(historyDetails.getTemperaturaC());
            updateDto.setNotasAdicionales(historyDetails.getNotasAdicionales());

            MedicalHistoryResponseDto updatedHistory = medicalHistoryService.updateMedicalHistory(
                historyDetails.getHistoryId(), updateDto, tenantId);
            return ResponseEntity.ok(updatedHistory);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al actualizar el historial médico: " + e.getMessage());
        }
    }

    @DeleteMapping("/deleteHistory")
    @Operation(summary = "Eliminar historial médico (soft delete)")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente"})
    public ResponseEntity<?> deleteMedicalHistory(@RequestBody IdRequest request) {
        try {
            String tenantId = TenantContext.getTenantId();
            if (tenantId == null || tenantId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: tenantId es requerido.");
            }

            medicalHistoryService.deleteMedicalHistory(request.getId(), tenantId);
            return ResponseEntity.ok("Historial médico eliminado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al eliminar el historial médico: " + e.getMessage());
        }
    }

    @DeleteMapping("/deletePermanent")
    @Operation(summary = "Eliminar historial médico permanentemente")
    @RequiresRole({"SuperAdmin", "Admin"})
    public ResponseEntity<?> deleteMedicalHistoryPermanently(@RequestBody IdRequest request) {
        try {
            String tenantId = TenantContext.getTenantId();
            if (tenantId == null || tenantId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: tenantId es requerido.");
            }

            medicalHistoryService.deleteMedicalHistoryPermanently(request.getId(), tenantId);
            return ResponseEntity.ok("Historial médico eliminado permanentemente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al eliminar el historial médico: " + e.getMessage());
        }
    }
}

