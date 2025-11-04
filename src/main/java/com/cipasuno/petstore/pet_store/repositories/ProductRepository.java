package com.cipasuno.petstore.pet_store.repositories;

import com.cipasuno.petstore.pet_store.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    
    List<Product> findByActivoTrue();
    
    List<Product> findByNombreContainingIgnoreCase(String nombre);
    
    Optional<Product> findByCodigo(String codigo);
    
    @Query("SELECT p FROM Product p WHERE p.stock < p.stockMinimo AND p.activo = true")
    List<Product> findLowStockProducts();
    
    @Query("SELECT p FROM Product p WHERE p.fechaVencimiento <= :fecha AND p.activo = true ORDER BY p.fechaVencimiento")
    List<Product> findProductsExpiringBefore(@Param("fecha") LocalDate fecha);
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.activo = true")
    long countActiveProducts();
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.stock < p.stockMinimo AND p.activo = true")
    long countLowStockProducts();
    
    // Queries para vacunas
    @Query("SELECT p FROM Product p WHERE p.esVacuna = true AND p.activo = true ORDER BY p.nombre")
    List<Product> findVaccineProducts();
    
    @Query("SELECT p FROM Product p WHERE p.esVacuna = true AND p.stock > 0 AND p.activo = true ORDER BY p.nombre")
    List<Product> findAvailableVaccineProducts();
    
    @Query("SELECT COUNT(p) FROM Product p WHERE p.esVacuna = true AND p.activo = true")
    long countVaccineProducts();
}

