package com.cipasuno.petstore.pet_store.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para depurar autenticación y roles
 * SOLO PARA DESARROLLO
 */
@RestController
@RequestMapping("/api/auth/debug")
@CrossOrigin(origins = "*")
@Tag(name = "Auth Debug", description = "Depuración de autenticación (SOLO DESARROLLO)")
public class AuthDebugController {

    @GetMapping("/whoami")
    @Operation(summary = "Ver información del usuario autenticado")
    public ResponseEntity<?> whoAmI() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            Map<String, Object> info = new HashMap<>();
            
            if (authentication == null) {
                info.put("authenticated", false);
                info.put("message", "No hay autenticación");
                return ResponseEntity.ok(info);
            }
            
            info.put("authenticated", authentication.isAuthenticated());
            info.put("principal", authentication.getPrincipal());
            info.put("authorities", authentication.getAuthorities());
            info.put("details", authentication.getDetails());
            info.put("name", authentication.getName());
            
            // Extraer el rol de los details
            if (authentication.getDetails() instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
                info.put("userRole", details.get("role"));
                info.put("userId", details.get("userId"));
                info.put("tenantId", details.get("tenantId"));
            }
            
            return ResponseEntity.ok(info);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body("Error: " + e.getMessage());
        }
    }
}

