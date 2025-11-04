package com.cipasuno.petstore.pet_store.controllers;

import com.cipasuno.petstore.pet_store.config.TenantContext;
import com.cipasuno.petstore.pet_store.models.DTOs.ClientCreateDto;
import com.cipasuno.petstore.pet_store.models.DTOs.ClientResponseDto;
import com.cipasuno.petstore.pet_store.models.DTOs.IdRequest;
import com.cipasuno.petstore.pet_store.models.DTOs.UpdateClientRequest;
import com.cipasuno.petstore.pet_store.security.RequiresRole;
import com.cipasuno.petstore.pet_store.services.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/clients")
@CrossOrigin(origins = "*")
@Tag(name = "Client", description = "API para gestión de clientes")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @PostMapping("/create")
    @Operation(summary = "Crear un nuevo cliente")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Empleado"})
    public ResponseEntity<?> createClient(@RequestBody ClientCreateDto client) {
        try {
            String tenantId = TenantContext.getTenantId();
            if (tenantId == null || tenantId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: tenantId es requerido.");
            }

            ClientResponseDto createdClient = clientService.createClient(client, tenantId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdClient);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al crear el cliente: " + e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "Obtener todos los clientes")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Empleado", "Veterinario"})
    public ResponseEntity<List<ClientResponseDto>> getAllClients() {
        String tenantId = TenantContext.getTenantId();
        List<ClientResponseDto> clients;
        
        if (tenantId != null && !tenantId.isEmpty()) {
            clients = clientService.getAllClientsByTenant(tenantId);
        } else {
            clients = clientService.getAllClients();
        }
        
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/active")
    @Operation(summary = "Obtener clientes activos")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Empleado", "Veterinario"})
    public ResponseEntity<List<ClientResponseDto>> getActiveClients() {
        String tenantId = TenantContext.getTenantId();
        List<ClientResponseDto> clients;
        
        if (tenantId != null && !tenantId.isEmpty()) {
            clients = clientService.getActiveClientsByTenant(tenantId);
        } else {
            clients = clientService.getActiveClients();
        }
        
        return ResponseEntity.ok(clients);
    }

    @PostMapping("/getById")
    @Operation(summary = "Obtener cliente por ID")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Empleado", "Veterinario"})
    public ResponseEntity<?> getClientById(@RequestBody IdRequest request) {
        try {
            Optional<ClientResponseDto> client = clientService.getClientById(request.getId());
            if (client.isPresent()) {
                return ResponseEntity.ok(client.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Cliente no encontrado con ID: " + request.getId());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/searchByName")
    @Operation(summary = "Buscar clientes por nombre")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Empleado", "Veterinario"})
    public ResponseEntity<List<ClientResponseDto>> searchClientsByName(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        List<ClientResponseDto> clients = clientService.searchClientsByName(name);
        return ResponseEntity.ok(clients);
    }

    @PostMapping("/getByIdent")
    @Operation(summary = "Buscar cliente por identificación")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Empleado", "Veterinario"})
    public ResponseEntity<?> getClientByIdent(@RequestBody Map<String, String> request) {
        try {
            String ident = request.get("ident");
            Optional<ClientResponseDto> client = clientService.getClientByIdent(ident);
            if (client.isPresent()) {
                return ResponseEntity.ok(client.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Cliente no encontrado con identificación: " + ident);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/getByCorreo")
    @Operation(summary = "Buscar cliente por correo electrónico")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Empleado", "Veterinario"})
    public ResponseEntity<?> getClientByCorreo(@RequestBody Map<String, String> request) {
        try {
            String correo = request.get("correo");
            Optional<ClientResponseDto> client = clientService.getClientByCorreo(correo);
            if (client.isPresent()) {
                return ResponseEntity.ok(client.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Cliente no encontrado con correo: " + correo);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    @PutMapping("/update")
    @Operation(summary = "Actualizar información de un cliente")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Empleado"})
    public ResponseEntity<?> updateClient(@RequestBody UpdateClientRequest clientDetails) {
        try {
            String tenantId = TenantContext.getTenantId();
            if (tenantId == null || tenantId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: tenantId es requerido.");
            }

            ClientResponseDto updatedClient = clientService.updateClient(clientDetails, tenantId);
            return ResponseEntity.ok(updatedClient);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al actualizar el cliente: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Eliminar cliente (soft delete)")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente"})
    public ResponseEntity<?> deleteClient(@RequestBody IdRequest request) {
        try {
            String tenantId = TenantContext.getTenantId();
            if (tenantId == null || tenantId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: tenantId es requerido.");
            }

            clientService.deleteClient(request.getId(), tenantId);
            return ResponseEntity.ok("Cliente eliminado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al eliminar el cliente: " + e.getMessage());
        }
    }

    @DeleteMapping("/deletePermanent")
    @Operation(summary = "Eliminar cliente permanentemente")
    @RequiresRole({"SuperAdmin", "Admin"})
    public ResponseEntity<?> deleteClientPermanently(@RequestBody IdRequest request) {
        try {
            String tenantId = TenantContext.getTenantId();
            if (tenantId == null || tenantId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: tenantId es requerido.");
            }

            clientService.deleteClientPermanently(request.getId(), tenantId);
            return ResponseEntity.ok("Cliente eliminado permanentemente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al eliminar el cliente: " + e.getMessage());
        }
    }

    @GetMapping("/count")
    @Operation(summary = "Contar clientes activos")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente"})
    public ResponseEntity<?> countActiveClients() {
        try {
            String tenantId = TenantContext.getTenantId();
            if (tenantId == null || tenantId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: tenantId es requerido.");
            }

            long count = clientService.countActiveClientsByTenant(tenantId);
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }
}

