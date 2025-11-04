package com.cipasuno.petstore.pet_store.repositories;

import com.cipasuno.petstore.pet_store.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {
    
    // Buscar por tenant
    List<Client> findByTenantId(String tenantId);
    
    // Buscar por identificación
    Optional<Client> findByIdent(String ident);
    
    // Buscar por correo
    Optional<Client> findByCorreo(String correo);
    
    // Buscar por identificación y tenant
    Optional<Client> findByIdentAndTenantId(String ident, String tenantId);
    
    // Buscar por correo y tenant
    Optional<Client> findByCorreoAndTenantId(String correo, String tenantId);
    
    // Buscar por nombre (contiene)
    @Query("SELECT c FROM Client c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Client> searchByName(@Param("name") String name);
    
    // Buscar activos
    @Query("SELECT c FROM Client c WHERE c.activo = true")
    List<Client> findAllActive();
    
    // Buscar activos por tenant
    @Query("SELECT c FROM Client c WHERE c.activo = true AND c.tenantId = :tenantId")
    List<Client> findActiveByTenantId(@Param("tenantId") String tenantId);
    
    // Contar activos por tenant
    @Query("SELECT COUNT(c) FROM Client c WHERE c.activo = true AND c.tenantId = :tenantId")
    long countActiveByTenantId(@Param("tenantId") String tenantId);
    
    // Verificar si existe por ident
    boolean existsByIdent(String ident);
    
    // Verificar si existe por correo
    boolean existsByCorreo(String correo);
}

