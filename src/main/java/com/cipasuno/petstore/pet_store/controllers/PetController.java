package com.cipasuno.petstore.pet_store.controllers;

import com.cipasuno.petstore.pet_store.config.TenantContext;
import com.cipasuno.petstore.pet_store.models.DTOs.*;
import com.cipasuno.petstore.pet_store.security.RequiresRole;
import com.cipasuno.petstore.pet_store.services.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/pets")
@CrossOrigin(origins = "*")
@Tag(name = "Pet", description = "API para gestión de mascotas")
public class PetController {

    @Autowired
    private PetService petService;

    @PostMapping("/create")
    @Operation(summary = "Crear una nueva mascota")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente"})
    public ResponseEntity<?> createPet(@RequestBody PetCreateDto pet) {
        try {
            String tenantId = TenantContext.getTenantId();
            PetResponseDto createdPet = petService.createPet(pet, tenantId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdPet);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al crear la mascota: " + e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "Obtener todas las mascotas")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Vendedor", "Cliente"})
    public ResponseEntity<List<PetResponseDto>> getAllPets() {
        String tenantId = TenantContext.getTenantId();
        List<PetResponseDto> pets = petService.getAllPetsByTenant(tenantId);
        return ResponseEntity.ok(pets);
    }

    @GetMapping("/active")
    @Operation(summary = "Obtener mascotas activas")
    public ResponseEntity<List<PetResponseDto>> getActivePets() {
        List<PetResponseDto> pets = petService.getActivePets();
        return ResponseEntity.ok(pets);
    }

    @PostMapping("/getId")
    @Operation(summary = "Obtener mascota por ID")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Vendedor", "Cliente"})
    public ResponseEntity<?> getPetById(@RequestBody IdRequest request) {
        try {
            String tenantId = TenantContext.getTenantId();
            Optional<PetResponseDto> pet = petService.getPetByIdAndTenant(request.getId(), tenantId);
            if (pet.isPresent()) {
                return ResponseEntity.ok(pet.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Buscar mascotas por nombre")
    public ResponseEntity<List<PetResponseDto>> searchPetsByName(@RequestParam String name) {
        List<PetResponseDto> pets = petService.searchPetsByName(name);
        return ResponseEntity.ok(pets);
    }

    @GetMapping("/type")
    @Operation(summary = "Obtener mascotas por tipo")
    public ResponseEntity<List<PetResponseDto>> getPetsByType(@RequestParam String tipo) {
        List<PetResponseDto> pets = petService.getPetsByType(tipo);
        return ResponseEntity.ok(pets);
    }

    @GetMapping("/owner")
    @Operation(summary = "Obtener mascotas por dueño")
    public ResponseEntity<List<PetResponseDto>> getPetsByOwnerId(@RequestParam Integer ownerId) {
        List<PetResponseDto> pets = petService.getPetsByOwnerId(ownerId);
        return ResponseEntity.ok(pets);
    }

    @PostMapping("/getOwners")
    @Operation(summary = "Obtener propietarios de una mascota por ID de mascota")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Vendedor", "Cliente"})
    public ResponseEntity<?> getOwnersByPetId(@RequestBody IdRequest request) {
        try {
            List<OwnerInfoDto> owners = petService.getOwnersByPetId(request.getId());
            return ResponseEntity.ok(owners);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al obtener los propietarios: " + e.getMessage());
        }
    }

    @PutMapping("/update")
    @Operation(summary = "Actualizar mascota")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente"})
    public ResponseEntity<?> updatePet(@RequestBody UpdatePetRequest petDetails) {
        try {
            String tenantId = TenantContext.getTenantId();
            Optional<PetResponseDto> existingPet = petService.getPetByIdAndTenant(petDetails.getPetId(), tenantId);
            if (!existingPet.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            PetResponseDto updatedPet = petService.updatePet(petDetails.getPetId(), petDetails, tenantId);
            return ResponseEntity.ok(updatedPet);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al actualizar la mascota: " + e.getMessage());
        }
    }

    @PostMapping("/addOwner")
    @Operation(summary = "Agregar dueño a mascota")
    public ResponseEntity<?> addOwnerToPet(@RequestParam Integer petId, @RequestParam Integer ownerId) {
        try {
            petService.addOwnerToPet(petId, ownerId);
            return ResponseEntity.ok("Dueño agregado exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al agregar dueño: " + e.getMessage());
        }
    }

    @DeleteMapping("/removeOwner")
    @Operation(summary = "Remover dueño de mascota")
    public ResponseEntity<?> removeOwnerFromPet(@RequestParam Integer petId, @RequestParam Integer ownerId) {
        try {
            petService.removeOwnerFromPet(petId, ownerId);
            return ResponseEntity.ok("Dueño removido exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al remover dueño: " + e.getMessage());
        }
    }

    @DeleteMapping("/deletePet")
    @Operation(summary = "Eliminar mascota (soft delete)")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente"})
    public ResponseEntity<?> deletePet(@RequestBody IdRequest request) {
        try {
            String tenantId = TenantContext.getTenantId();
            Optional<PetResponseDto> pet = petService.getPetByIdAndTenant(request.getId(), tenantId);
            if (!pet.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            petService.deletePet(request.getId(), tenantId);
            return ResponseEntity.ok("Mascota eliminada exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al eliminar la mascota: " + e.getMessage());
        }
    }

    @DeleteMapping("/petPermanent")
    @Operation(summary = "Eliminar mascota permanentemente")
    @RequiresRole({"SuperAdmin", "Admin"})
    public ResponseEntity<?> deletePetPermanently(@RequestBody IdRequest request) {
        try {
            String tenantId = TenantContext.getTenantId();
            Optional<PetResponseDto> pet = petService.getPetByIdAndTenant(request.getId(), tenantId);
            if (!pet.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            petService.deletePetPermanently(request.getId(), tenantId);
            return ResponseEntity.ok("Mascota eliminada permanentemente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al eliminar la mascota: " + e.getMessage());
        }
    }

    @GetMapping("/count")
    @Operation(summary = "Contar mascotas activas")
    public ResponseEntity<Long> countActivePets() {
        long count = petService.countActivePets();
        return ResponseEntity.ok(count);
    }
}

