package com.cipasuno.petstore.pet_store.controllers;

import com.cipasuno.petstore.pet_store.config.TenantContext;
import com.cipasuno.petstore.pet_store.models.Service;
import com.cipasuno.petstore.pet_store.models.DTOs.ServiceCreateDto;
import com.cipasuno.petstore.pet_store.models.DTOs.ServiceResponseDto;
import com.cipasuno.petstore.pet_store.services.VeterinaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/services")
@CrossOrigin(origins = "*")
@Tag(name = "Service", description = "API para gestión de servicios veterinarios")
public class ServiceController {

    @Autowired
    private VeterinaryService veterinaryService;

    @PostMapping("/create")
    @Operation(summary = "Crear un nuevo servicio")
    public ResponseEntity<?> createService(@RequestBody ServiceCreateDto service) {
        try {
            String tenantIdStr = TenantContext.getTenantId();
            if (tenantIdStr == null || tenantIdStr.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: tenantId es requerido. Verifique que el header X-Tenant-ID esté presente.");
            }

            Integer tenantId;
            try {
                tenantId = Integer.parseInt(tenantIdStr);
            } catch (NumberFormatException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: tenantId debe ser un número válido.");
            }

            // Validar código único
            Optional<ServiceResponseDto> existingService = veterinaryService.getServiceByCodigo(service.getCodigo());
            if (existingService.isPresent()) {
                return ResponseEntity.badRequest()
                    .body("Ya existe un servicio con este código: " + service.getCodigo());
            }

            ServiceResponseDto createdService = veterinaryService.createService(service, tenantId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdService);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al crear el servicio: " + e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "Obtener todos los servicios")
    public ResponseEntity<List<ServiceResponseDto>> getAllServices() {
        List<ServiceResponseDto> services = veterinaryService.getAllServices();
        return ResponseEntity.ok(services);
    }

    @GetMapping("/active")
    @Operation(summary = "Obtener servicios activos")
    public ResponseEntity<List<ServiceResponseDto>> getActiveServices() {
        List<ServiceResponseDto> services = veterinaryService.getActiveServices();
        return ResponseEntity.ok(services);
    }

    @GetMapping("/getId")
    @Operation(summary = "Obtener servicio por ID")
    public ResponseEntity<?> getServiceById(@RequestBody Integer id) {
        Optional<ServiceResponseDto> service = veterinaryService.getServiceById(id);
        if (service.isPresent()) {
            return ResponseEntity.ok(service.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/codigo")
    @Operation(summary = "Obtener servicio por código")
    public ResponseEntity<?> getServiceByCodigo(@RequestParam String codigo) {
        Optional<ServiceResponseDto> service = veterinaryService.getServiceByCodigo(codigo);
        if (service.isPresent()) {
            return ResponseEntity.ok(service.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar servicios por nombre")
    public ResponseEntity<List<ServiceResponseDto>> searchServicesByName(@RequestParam String name) {
        List<ServiceResponseDto> services = veterinaryService.searchServicesByName(name);
        return ResponseEntity.ok(services);
    }

    @PutMapping("/update")
    @Operation(summary = "Actualizar servicio")
    public ResponseEntity<?> updateService(@RequestBody Service serviceDetails) {
        try {
            Optional<ServiceResponseDto> existingService = veterinaryService.getServiceById(serviceDetails.getServiceId());
            if (!existingService.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            ServiceResponseDto updatedService = veterinaryService.updateService(serviceDetails.getServiceId(), serviceDetails);
            return ResponseEntity.ok(updatedService);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al actualizar el servicio: " + e.getMessage());
        }
    }

    @DeleteMapping("/deleteService")
    @Operation(summary = "Eliminar servicio (soft delete)")
    public ResponseEntity<?> deleteService(@RequestBody Integer serviceId) {
        try {
            Optional<ServiceResponseDto> service = veterinaryService.getServiceById(serviceId);
            if (!service.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            veterinaryService.deleteService(serviceId);
            return ResponseEntity.ok("Servicio eliminado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al eliminar el servicio: " + e.getMessage());
        }
    }

    @DeleteMapping("/servicePermanent")
    @Operation(summary = "Eliminar servicio permanentemente")
    public ResponseEntity<?> deleteServicePermanently(@RequestBody Integer id) {
        try {
            Optional<ServiceResponseDto> service = veterinaryService.getServiceById(id);
            if (!service.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            veterinaryService.deleteServicePermanently(id);
            return ResponseEntity.ok("Servicio eliminado permanentemente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al eliminar el servicio: " + e.getMessage());
        }
    }

    @GetMapping("/count")
    @Operation(summary = "Contar servicios activos")
    public ResponseEntity<Long> countActiveServices() {
        long count = veterinaryService.countActiveServices();
        return ResponseEntity.ok(count);
    }
}

