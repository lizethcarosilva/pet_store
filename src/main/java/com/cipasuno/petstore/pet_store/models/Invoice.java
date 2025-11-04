package com.cipasuno.petstore.pet_store.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "invoice")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "invoice_id")
    private Integer invoiceId;
    
    @Column(name = "tenant_id", nullable = false)
    private String tenantId;
    
    @Column(nullable = false)
    private String numero; // Número de factura
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false) // Cliente
    @JsonIgnoreProperties({"pets", "hibernateLazyInitializer", "handler"})
    private Client client;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id") // Empleado que realiza la venta
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User employee;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id") // Cita agendada relacionada (opcional)
    @JsonIgnoreProperties({"pet", "client", "service", "veterinarian", "hibernateLazyInitializer", "handler"})
    private Appointment appointment; // Solo para servicios agendados (cita, baño, desparasitación, etc.)
    
    @Column(name = "fecha_emision", nullable = false)
    private LocalDateTime fechaEmision;
    
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal subtotal = BigDecimal.ZERO;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal impuesto = BigDecimal.ZERO; // IVA u otros
    
    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal total = BigDecimal.ZERO;
    
    @Column(length = 20, nullable = false)
    private String estado = "PAGADA"; // PAGADA, PENDIENTE, ANULADA
    
    @Column(length = 500)
    private String observaciones;
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    @Column(name = "created_on", nullable = false, updatable = false)
    private LocalDateTime createdOn;
    
    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"invoice", "hibernateLazyInitializer", "handler"})
    private List<InvoiceDetail> details = new ArrayList<>();
    
    @PrePersist
    protected void onCreate() {
        if (createdOn == null) {
            createdOn = LocalDateTime.now();
        }
        if (fechaEmision == null) {
            fechaEmision = LocalDateTime.now();
        }
        if (activo == null) {
            activo = true;
        }
        if (estado == null) {
            estado = "PAGADA";
        }
        if (subtotal == null) {
            subtotal = BigDecimal.ZERO;
        }
        if (descuento == null) {
            descuento = BigDecimal.ZERO;
        }
        if (impuesto == null) {
            impuesto = BigDecimal.ZERO;
        }
        if (total == null) {
            total = BigDecimal.ZERO;
        }
    }
}

