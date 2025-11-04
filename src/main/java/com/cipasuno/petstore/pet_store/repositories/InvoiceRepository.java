package com.cipasuno.petstore.pet_store.repositories;

import com.cipasuno.petstore.pet_store.models.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Integer> {
    
    @Query("SELECT i FROM Invoice i LEFT JOIN FETCH i.client LEFT JOIN FETCH i.employee WHERE i.activo = true")
    List<Invoice> findByActivoTrue();
    
    Optional<Invoice> findByNumero(String numero);
    
    @Query("SELECT i FROM Invoice i LEFT JOIN FETCH i.client LEFT JOIN FETCH i.employee WHERE i.estado = :estado")
    List<Invoice> findByEstado(@Param("estado") String estado);
    
    @Query("SELECT i FROM Invoice i WHERE i.client.clientId = :clientId AND i.activo = true ORDER BY i.fechaEmision DESC")
    List<Invoice> findByClientId(Integer clientId);
    
    @Query("SELECT i FROM Invoice i WHERE i.fechaEmision BETWEEN :inicio AND :fin AND i.activo = true ORDER BY i.fechaEmision DESC")
    List<Invoice> findByFechaEmisionBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
    
    @Query("SELECT COALESCE(SUM(i.total), 0) FROM Invoice i WHERE CAST(i.fechaEmision AS date) = CURRENT_DATE AND i.estado = 'PAGADA' AND i.activo = true")
    BigDecimal getTotalSalesToday();
    
    @Query("SELECT COALESCE(SUM(i.total), 0) FROM Invoice i WHERE YEAR(i.fechaEmision) = YEAR(CURRENT_DATE) AND MONTH(i.fechaEmision) = MONTH(CURRENT_DATE) AND i.estado = 'PAGADA' AND i.activo = true")
    BigDecimal getTotalSalesThisMonth();
    
    @Query("SELECT COUNT(i) FROM Invoice i WHERE CAST(i.fechaEmision AS date) = CURRENT_DATE AND i.activo = true")
    long countInvoicesToday();
    
    @Query("SELECT MAX(CAST(SUBSTRING(i.numero, LENGTH(i.numero) - 5, 6) AS integer)) FROM Invoice i")
    Integer findMaxInvoiceNumber();
    
    // Query optimizada con JOIN FETCH para evitar LazyInitializationException
    @Query("SELECT i FROM Invoice i LEFT JOIN FETCH i.client LEFT JOIN FETCH i.employee LEFT JOIN FETCH i.appointment")
    List<Invoice> findAllWithRelations();
}

