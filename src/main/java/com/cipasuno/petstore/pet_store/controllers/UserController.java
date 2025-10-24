package com.cipasuno.petstore.pet_store.controllers;

import org.springframework.web.bind.annotation.*;
import com.cipasuno.petstore.pet_store.config.TenantContext;
import com.cipasuno.petstore.pet_store.models.User;
import com.cipasuno.petstore.pet_store.models.DTOs.*;
import com.cipasuno.petstore.pet_store.security.RequiresRole;
import com.cipasuno.petstore.pet_store.services.LoginService;
import com.cipasuno.petstore.pet_store.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // Para permitir CORS desde React
@Tag(name = "User", description = "API para gestión de usuarios")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private LoginService loginService;


    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto data) {
        try {
            Optional<User> existingUser = userService.getUserByEmail(data.getCorreo());
            
            if (existingUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Usuario no encontrado");
            }
            
            LoginResponse loginResponse = loginService.login(data);
            return ResponseEntity.ok(loginResponse);
            
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error interno del servidor");
        }
    }
    


    // CREATE - Crear un nuevo usuario
    @PostMapping("/create")
    @Operation(summary = "Crear un nuevo usuario", description = "El password se encripta automáticamente")
    public ResponseEntity<?> createUser(@RequestBody UserCreateDto user) {
        try {
            String tenantId = TenantContext.getTenantId();
            
            // Si no hay tenantId en el contexto (usuario no autenticado), usar tenant por defecto
            if (tenantId == null || tenantId.isEmpty()) {
                tenantId = "1"; // Tenant por defecto para nuevos usuarios
            }
            
            // Validar que no exista un usuario con el mismo email en este tenant
            Optional<User> existingUser = userService.getUserByEmailAndTenant(user.getCorreo(), tenantId);
            if (existingUser.isPresent()) {
                return ResponseEntity.badRequest()
                    .body("Ya existe un usuario con este email: " + user.getCorreo());
            }

            // Validar que no exista un usuario con la misma identificación en este tenant
            List<User> usersWithSameIdent = userService.getUsersByIdentAndTenant(user.getIdent(), tenantId);
            if (!usersWithSameIdent.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body("Ya existe un usuario con esta identificación: " + user.getIdent());
            }

            UserResponseDto createdUser = userService.createUser(user, tenantId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al crear el usuario: " + e.getMessage());
        }
    }

    // READ - Obtener todos los usuarios (sin passwords)
    @GetMapping
    @Operation(summary = "Obtener todos los usuarios", description = "Retorna todos los usuarios sin passwords")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente"})
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        String tenantId = TenantContext.getTenantId();
        System.out.println("tenantId: " + tenantId);
        List<UserResponseDto> users = userService.getAllUsersByTenant(tenantId);
        System.out.println("users: " + users);
        return ResponseEntity.ok(users);
    }

    // READ - Obtener usuario por ID (sin password)
    @PostMapping("/getId")
    @Operation(summary = "Obtener usuario por ID", description = "Retorna un usuario sin password")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente"})
    public ResponseEntity<?> getUserById(@RequestBody IdRequest request) {
        try {
            String tenantId = TenantContext.getTenantId();
            Optional<UserResponseDto> user = userService.getUserByIdAndTenant(request.getId(), tenantId);
            if (user.isPresent()) {
                return ResponseEntity.ok(user.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/getIdTenant")
    @Operation(summary = "Obtener usuario por ID y tenant", description = "Retorna un usuario sin password")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Vendedor", "Cliente"})
    public ResponseEntity<?> getUserByIdTenant(@RequestBody TenantIdDto request) {
        try {
            String tenantId = TenantContext.getTenantId();
                Optional<UserResponseDto> user = userService.getUserByIdAndTenant(Integer.parseInt(request.getTenantId()), tenantId);
                if (user.isPresent()) {
                return ResponseEntity.ok(user.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    // READ - Obtener usuarios activos (sin passwords)
    @GetMapping("/active")
    @Operation(summary = "Obtener usuarios activos", description = "Retorna usuarios activos sin passwords")
    public ResponseEntity<List<UserResponseDto>> getActiveUsers() {
        List<UserResponseDto> users = userService.getActiveUsers();
        return ResponseEntity.ok(users);
    }

    // READ - Buscar usuarios por nombre (sin passwords)
    @GetMapping("/search")
    @Operation(summary = "Buscar usuarios por nombre")
    public ResponseEntity<List<UserResponseDto>> searchUsersByName(@RequestParam String name) {
        List<UserResponseDto> users = userService.searchUsersByName(name);
        return ResponseEntity.ok(users);
    }

    @PostMapping("/rolId")
    @Operation(summary = "Obtener usuarios por rol")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente"})
    public ResponseEntity<List<UserResponseDto>> getUsersByRole(@RequestBody RolesDto request) {
        String tenantId = TenantContext.getTenantId();
        List<UserResponseDto> users = userService.getUsersByRoleAndTenant(request.getRolId(), tenantId);
        return ResponseEntity.ok(users);
    }

    // UPDATE - Actualizar usuario
    @PutMapping("/update")
    @Operation(summary = "Actualizar usuario", description = "Si se envía password, se encripta automáticamente")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente"})
    public ResponseEntity<?> updateUser(@RequestBody UpdateUserRequest userDetails) {
        try {
            String tenantId = TenantContext.getTenantId();
            Optional<UserResponseDto> existingUser = userService.getUserByIdAndTenant(userDetails.getUserId(), tenantId);
            if (!existingUser.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            UserResponseDto updatedUser = userService.updateUser(userDetails.getUserId(), userDetails, tenantId);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al actualizar el usuario: " + e.getMessage());
        }
    }

    // DELETE - Eliminar usuario (soft delete)
    @DeleteMapping("/deleteUser")
    @Operation(summary = "Eliminar usuario (soft delete)")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente"})
    public ResponseEntity<?> deleteUser(@RequestBody IdRequest request) {
        try {
            String tenantId = TenantContext.getTenantId();
            Optional<UserResponseDto> user = userService.getUserByIdAndTenant(request.getId(), tenantId);
            if (!user.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            userService.deleteUser(request.getId(), tenantId);
            return ResponseEntity.ok("Usuario eliminado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al eliminar el usuario: " + e.getMessage());
        }
    }

    // DELETE - Eliminar usuario permanentemente
    @DeleteMapping("/userPermanent")
    @Operation(summary = "Eliminar usuario permanentemente")
    @RequiresRole({"SuperAdmin", "Admin"})
    public ResponseEntity<?> deleteUserPermanently(@RequestBody IdRequest request) {
        try {
            String tenantId = TenantContext.getTenantId();
            Optional<UserResponseDto> user = userService.getUserByIdAndTenant(request.getId(), tenantId);
            if (!user.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            userService.deleteUserPermanently(request.getId(), tenantId);
            return ResponseEntity.ok("Usuario eliminado permanentemente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al eliminar el usuario: " + e.getMessage());
        }
    }

    // ACTIVATE - Activar usuario
    @PutMapping("/userActivate")
    @Operation(summary = "Activar usuario")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente"})
    public ResponseEntity<?> activateUser(@RequestBody IdRequest request) {
        try {
            String tenantId = TenantContext.getTenantId();
            Optional<UserResponseDto> user = userService.getUserByIdAndTenant(request.getId(), tenantId);
            if (!user.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            UserResponseDto activatedUser = userService.activateUser(request.getId(), tenantId);
            return ResponseEntity.ok(activatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al activar el usuario: " + e.getMessage());
        }
    }

    // DEACTIVATE - Desactivar usuario
    @PutMapping("/userDeactivate")
    @Operation(summary = "Desactivar usuario")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente"})
    public ResponseEntity<?> deactivateUser(@RequestBody IdRequest request) {
        try {
            String tenantId = TenantContext.getTenantId();
            Optional<UserResponseDto> user = userService.getUserByIdAndTenant(request.getId(), tenantId);
            if (!user.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            UserResponseDto deactivatedUser = userService.deactivateUser(request.getId(), tenantId);
            return ResponseEntity.ok(deactivatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al desactivar el usuario: " + e.getMessage());
        }
    }

    // COUNT - Contar usuarios
    @GetMapping("/count")
    @Operation(summary = "Contar total de usuarios")
    public ResponseEntity<Long> countUsers() {
        long count = userService.countUsers();
        return ResponseEntity.ok(count);
    }

    // COUNT - Contar usuarios activos
    @GetMapping("/count/active")
    @Operation(summary = "Contar usuarios activos")
    public ResponseEntity<Long> countActiveUsers() {
        long count = userService.countActiveUsers();
        return ResponseEntity.ok(count);
    }
}