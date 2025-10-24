package com.cipasuno.petstore.pet_store.repositories;

import com.cipasuno.petstore.pet_store.models.Tenant;

import jakarta.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, Integer> {
    
    List<Tenant> findByActivoTrue();
    
    Optional<Tenant> findByNit(String nit);
    
    
    List<Tenant> findByPlan(String plan);

    @Query("Insert into Tenant (nit, razonSocial, direccion, telefono, email, plan, fechaVencimiento, configuracion) values (:nit, :razonSocial, :direccion, :telefono, :email, :plan, :fechaVencimiento, :configuracion)")
    @Modifying
    @Transactional
    void insertTenant(String nit, String razonSocial, String direccion, String telefono, String email, String plan, Boolean activo);
    
    @Query("SELECT COUNT(t) FROM Tenant t WHERE t.activo = true")
    long countActiveTenants();
}

