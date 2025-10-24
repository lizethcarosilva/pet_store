package com.cipasuno.petstore.pet_store.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tenant")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Tenant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tenant_id")
    private Integer tenantId;
    
    
    @Column(unique = true)
    private String nit;
    
    private String razonSocial;
    
    private String direccion;
    
    private String telefono;
    
    private String email;
    
    @Column(length = 20)
    private String plan; // BASIC, PREMIUM, ENTERPRISE
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;
    
    @Column(name = "fecha_vencimiento")
    private LocalDateTime fechaVencimiento;
    
    @Column(length = 500)
    private String configuracion; // JSON con configuraciones específicas
    
    @PrePersist
    protected void onCreate() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
        if (activo == null) {
            activo = true;
        }
    }
}

