package com.cipasuno.petstore.pet_store.repositories;

import com.cipasuno.petstore.pet_store.models.InvoiceDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface InvoiceDetailRepository extends JpaRepository<InvoiceDetail, Integer> {
    
    @Query("SELECT d FROM InvoiceDetail d WHERE d.invoice.invoiceId = :invoiceId")
    List<InvoiceDetail> findByInvoiceId(Integer invoiceId);
    
    @Query("SELECT d.product.nombre as nombre, SUM(d.cantidad) as cantidad " +
           "FROM InvoiceDetail d " +
           "WHERE d.tipo = 'PRODUCTO' AND d.product IS NOT NULL " +
           "GROUP BY d.product.productId, d.product.nombre " +
           "ORDER BY cantidad DESC")
    List<Map<String, Object>> findTopSellingProducts();
    
    @Query("SELECT d.service.nombre as nombre, COUNT(d) as cantidad " +
           "FROM InvoiceDetail d " +
           "WHERE d.tipo = 'SERVICIO' AND d.service IS NOT NULL " +
           "GROUP BY d.service.serviceId, d.service.nombre " +
           "ORDER BY cantidad DESC")
    List<Map<String, Object>> findTopSellingServices();
}

