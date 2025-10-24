package com.cipasuno.petstore.pet_store.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointment")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    private Integer appointmentId;
    
    @Column(name = "tenant_id", nullable = false)
    private Integer tenantId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false) // Usuario que agenda (cliente)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinarian_id") // Veterinario asignado (opcional)
    private User veterinarian;
    
    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;
    
    @Column(length = 20, nullable = false)
    private String estado = "PROGRAMADA"; // PROGRAMADA, COMPLETADA, CANCELADA, EN_PROCESO
    
    @Column(length = 1000)
    private String observaciones;
    
    @Column(length = 1000)
    private String diagnostico;
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    @Column(name = "created_on", nullable = false, updatable = false)
    private LocalDateTime createdOn;
    
    @PrePersist
    protected void onCreate() {
        if (createdOn == null) {
            createdOn = LocalDateTime.now();
        }
        if (activo == null) {
            activo = true;
        }
        if (estado == null) {
            estado = "PROGRAMADA";
        }
    }
}

