package com.cipasuno.petstore.pet_store.controllers;

import com.cipasuno.petstore.pet_store.models.DTOs.RolesDto;
import com.cipasuno.petstore.pet_store.models.DTOs.RoleResponseDto;
import com.cipasuno.petstore.pet_store.services.RolesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/roles")
@CrossOrigin(origins = "*")
@Tag(name = "Roles", description = "API para gestión de roles del sistema")
public class RolesController {

    @Autowired
    private RolesService rolesService;

    @GetMapping
    @Operation(summary = "Obtener todos los roles (excepto SUPERADMIN para usuarios normales)")
    public ResponseEntity<List<RoleResponseDto>> getAllRoles() {
        List<RoleResponseDto> roles = rolesService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/active")
    @Operation(summary = "Obtener roles activos")
    public ResponseEntity<List<RoleResponseDto>> getActiveRoles() {
        List<RoleResponseDto> roles = rolesService.getActiveRoles();
        return ResponseEntity.ok(roles);
    }

    @PostMapping("/getById")
    @Operation(summary = "Obtener rol por ID")
    public ResponseEntity<?> getRoleById(@RequestBody RolesDto request) {
        try {
            Optional<RoleResponseDto> role = rolesService.getRoleById(request.getRolId());
            if (role.isPresent()) {
                return ResponseEntity.ok(role.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}

