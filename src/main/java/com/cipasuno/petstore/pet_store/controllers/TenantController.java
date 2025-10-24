package com.cipasuno.petstore.pet_store.controllers;

import com.cipasuno.petstore.pet_store.models.Tenant;
import com.cipasuno.petstore.pet_store.models.DTOs.TenantCreateDto;
import com.cipasuno.petstore.pet_store.models.DTOs.TenantIdDto;
import com.cipasuno.petstore.pet_store.models.DTOs.TenantResponseDto;
import com.cipasuno.petstore.pet_store.security.RequiresRole;
import com.cipasuno.petstore.pet_store.services.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/tenants")
@CrossOrigin(origins = "*")
@Tag(name = "Tenant", description = "API para gestión de empresas/tenants (Multi-tenancy)")
public class TenantController {

    @Autowired
    private TenantService tenantService;

    @PostMapping("/create")
    @RequiresRole({"SuperAdmin"})
    @Operation(summary = "Crear un nuevo tenant (empresa)")
    public ResponseEntity<?> createTenant(@RequestBody TenantCreateDto tenant) {
        try {
            // Validar que no exista un tenant con el mismo NIT
            if (tenant.getNit() != null && tenantService.existsByNit(tenant.getNit())) {
                return ResponseEntity.badRequest()
                    .body("Ya existe un tenant con este NIT: " + tenant.getNit());
            }

            TenantResponseDto createdTenant = tenantService.createTenant(tenant);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdTenant);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al crear el tenant: " + e.getMessage());
        }
    }

    @GetMapping
    @RequiresRole({"SuperAdmin"})
    @Operation(summary = "Obtener todos los tenants")
    public ResponseEntity<List<TenantResponseDto>> getAllTenants() {
        List<TenantResponseDto> tenants = tenantService.getAllTenants();
        return ResponseEntity.ok(tenants);
    }

    @GetMapping("/active")
    @RequiresRole({"SuperAdmin"})
    @Operation(summary = "Obtener tenants activos")
    public ResponseEntity<List<TenantResponseDto>> getActiveTenants() {
        List<TenantResponseDto> tenants = tenantService.getActiveTenants();
        return ResponseEntity.ok(tenants);
    }

    @PostMapping("/getById")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Vendedor", "Cliente"})
    @Operation(summary = "Obtener tenant por ID")
    public ResponseEntity<?> getTenantById(@RequestBody TenantIdDto request) {
        try {
            Optional<TenantResponseDto> tenant = tenantService.getTenantById(Integer.parseInt(request.getTenantId()));
            if (tenant.isPresent()) {
                return ResponseEntity.ok(tenant.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("ID de tenant inválido");
        }
    }

    @GetMapping("/nit/{nit}")
    @Operation(summary = "Obtener tenant por NIT")
    public ResponseEntity<?> getTenantByNit(@PathVariable String nit) {
        Optional<TenantResponseDto> tenant = tenantService.getTenantByNit(nit);
        if (tenant.isPresent()) {
            return ResponseEntity.ok(tenant.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
 /*
  * 
    @GetMapping("/search")
    @Operation(summary = "Buscar tenants por nombre")
    public ResponseEntity<List<TenantResponseDto>> searchTenantsByName(@RequestParam String name) {
        List<TenantResponseDto> tenants = tenantService.searchTenantsByName(name);
        return ResponseEntity.ok(tenants);
    }
  */


    @GetMapping("/plan/{plan}")
    @Operation(summary = "Obtener tenants por plan")
    public ResponseEntity<List<TenantResponseDto>> getTenantsByPlan(@PathVariable String plan) {
        List<TenantResponseDto> tenants = tenantService.getTenantsByPlan(plan);
        return ResponseEntity.ok(tenants);
    }

    @PutMapping("/update")
    @RequiresRole({"SuperAdmin"})
    @Operation(summary = "Actualizar tenant")
    public ResponseEntity<?> updateTenant(@RequestBody Tenant tenantDetails) {
        try {
            Optional<TenantResponseDto> existingTenant = tenantService.getTenantById(tenantDetails.getTenantId());
            if (!existingTenant.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            TenantResponseDto updatedTenant = tenantService.updateTenant(tenantDetails.getTenantId(), tenantDetails);
            return ResponseEntity.ok(updatedTenant);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al actualizar el tenant: " + e.getMessage());
        }
    }

    @PutMapping("/activate/{tenantId}")
    @Operation(summary = "Activar tenant")
    public ResponseEntity<?> activateTenant(@PathVariable Integer tenantId) {
        try {
            TenantResponseDto tenant = tenantService.activateTenant(tenantId);
            return ResponseEntity.ok(tenant);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al activar el tenant: " + e.getMessage());
        }
    }

    @PutMapping("/deactivate/{tenantId}")
    @Operation(summary = "Desactivar tenant")
    public ResponseEntity<?> deactivateTenant(@PathVariable Integer tenantId) {
        try {
            tenantService.deactivateTenant(tenantId);
            return ResponseEntity.ok("Tenant desactivado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al desactivar el tenant: " + e.getMessage());
        }
    }

    @GetMapping("/count")
    @Operation(summary = "Contar tenants activos")
    public ResponseEntity<Long> countActiveTenants() {
        long count = tenantService.countActiveTenants();
        return ResponseEntity.ok(count);
    }
}

