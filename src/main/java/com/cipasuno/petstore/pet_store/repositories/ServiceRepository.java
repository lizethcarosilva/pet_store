package com.cipasuno.petstore.pet_store.repositories;

import com.cipasuno.petstore.pet_store.models.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Integer> {
    
    List<Service> findByActivoTrue();
    
    List<Service> findByNombreContainingIgnoreCase(String nombre);
    
    Optional<Service> findByCodigo(String codigo);
    
    @Query("SELECT COUNT(s) FROM Service s WHERE s.activo = true")
    long countActiveServices();
}

