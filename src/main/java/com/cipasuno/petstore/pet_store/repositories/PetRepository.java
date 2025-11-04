package com.cipasuno.petstore.pet_store.repositories;

import com.cipasuno.petstore.pet_store.models.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PetRepository extends JpaRepository<Pet, Integer> {
    
    List<Pet> findByActivoTrue();
    
    List<Pet> findByNombreContainingIgnoreCase(String nombre);
    
    List<Pet> findByTipo(String tipo);
    
    @Query("SELECT p FROM Pet p JOIN p.owners po WHERE po.client.clientId = :clientId AND p.activo = true")
    List<Pet> findByOwnerId(Integer clientId);
    
    @Query("SELECT COUNT(p) FROM Pet p WHERE p.activo = true")
    long countActivePets();
    
    // Métodos con Tenant
    List<Pet> findByTenantId(String tenantId);
    
    Optional<Pet> findByPetIdAndTenantId(Integer petId, String tenantId);
    
    List<Pet> findByTenantIdAndActivoTrue(String tenantId);
    
    @Query("SELECT p FROM Pet p WHERE p.tenantId = :tenantId AND LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) AND p.activo = true")
    List<Pet> findByTenantIdAndNombreContainingIgnoreCase(@Param("tenantId") String tenantId, @Param("nombre") String nombre);
    
    // Queries optimizadas con JOIN FETCH para evitar problema N+1
    @Query("SELECT DISTINCT p FROM Pet p LEFT JOIN FETCH p.owners WHERE p.tenantId = :tenantId AND p.activo = true")
    List<Pet> findAllByTenantIdWithOwners(@Param("tenantId") String tenantId);
    
    @Query("SELECT p FROM Pet p WHERE p.tenantId = :tenantId")
    List<Pet> findAllByTenantId(@Param("tenantId") String tenantId);
}

