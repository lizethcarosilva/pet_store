package com.cipasuno.petstore.pet_store.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cipasuno.petstore.pet_store.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // Buscar todos los usuarios activos
    List<User> findByActivoTrue();
    

    
    // Buscar usuarios por rol
    List<User> findByRolId(String rolId);
    
    // Buscar usuarios por nombre (case insensitive)
    List<User> findByNameContainingIgnoreCase(String name);
    
    // Buscar usuarios por identificación
    List<User> findByIdent(String ident);
    
    // Consulta personalizada para obtener usuarios con información específica
    @Query("SELECT u FROM User u WHERE u.activo = true ORDER BY u.createdOn ASC")
    List<User> findAllActiveUsersOrdered();

    @Query("SELECT u FROM User u WHERE u.correo = :correo AND u.activo = true")
    Optional<User> findByCorreo(@Param("correo") String correo);

    // Métodos con Tenant
    List<User> findByTenantId(String tenantId);

    Optional<User> findByUserIdAndTenantId(Integer userId, String tenantId);

    Optional<User> findByCorreoAndTenantId(String correo, String tenantId);

    List<User> findByIdentAndTenantId(String ident, String tenantId);

    List<User> findByRolIdAndTenantId(String rolId, String tenantId);

    List<User> findByTenantIdAndActivoTrue(String tenantId);

    @Query("SELECT u FROM User u WHERE u.tenantId = :tenantId AND u.activo = true ORDER BY u.createdOn DESC")
    List<User> findAllActiveUsersByTenantOrdered(@Param("tenantId") String tenantId);
}