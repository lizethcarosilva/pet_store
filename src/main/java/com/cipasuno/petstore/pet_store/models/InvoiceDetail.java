package com.cipasuno.petstore.pet_store.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "invoice_detail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "detail_id")
    private Integer detailId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    @JsonIgnoreProperties({"details", "client", "employee", "appointment", "hibernateLazyInitializer", "handler"})
    private Invoice invoice;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Product product;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Service service;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vaccination_id")
    @JsonIgnoreProperties({"pet", "veterinarian", "medicalHistory", "hibernateLazyInitializer", "handler"})
    private Vaccination vaccination; // Referencia a la vacunación que se está facturando
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    @JsonIgnoreProperties({"pet", "client", "service", "veterinarian", "hibernateLazyInitializer", "handler"})
    private Appointment appointment; // Referencia a la cita que se está facturando
    
    @Column(length = 15, nullable = false)
    private String tipo; // PRODUCTO, SERVICIO, VACUNACION, CITA
    
    @Column(nullable = false)
    private Integer cantidad = 1;
    
    @Column(name = "precio_unitario", precision = 10, scale = 2, nullable = false)
    private BigDecimal precioUnitario;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;
    
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal subtotal;
    
    @PrePersist
    protected void onCreate() {
        if (cantidad == null) {
            cantidad = 1;
        }
        if (descuento == null) {
            descuento = BigDecimal.ZERO;
        }
    }
}

