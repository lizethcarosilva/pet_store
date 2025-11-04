package com.cipasuno.petstore.pet_store.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pet_medical_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PetMedicalHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Integer historyId;
    
    @Column(name = "tenant_id", nullable = false)
    private String tenantId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    @JsonIgnoreProperties({"owners", "hibernateLazyInitializer", "handler"})
    private Pet pet;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    @JsonIgnoreProperties({"pet", "client", "service", "veterinarian", "hibernateLazyInitializer", "handler"})
    private Appointment appointment; // Opcional: puede haber historial sin cita
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Service service; // Tipo de servicio: consulta general, desparasitación, baño, etc.
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinarian_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User veterinarian; // Trabajador/veterinario que atendió
    
    @Column(name = "fecha_atencion", nullable = false)
    private LocalDateTime fechaAtencion;
    
    @Column(name = "tipo_procedimiento", length = 100, nullable = false)
    private String tipoProcedimiento; // Consulta General, Desparasitación, Baño, Limpieza de Pulgas, etc.
    
    @Column(name = "diagnostico", length = 2000)
    private String diagnostico;
    
    @Column(name = "observaciones", length = 2000)
    private String observaciones;
    
    @Column(name = "tratamiento", length = 2000)
    private String tratamiento; // Medicamentos o productos utilizados
    
    @Column(name = "peso_kg", precision = 5, scale = 2)
    private BigDecimal pesoKg; // Peso de la mascota en el momento de la atención
    
    @Column(name = "temperatura_c", precision = 4, scale = 2)
    private BigDecimal temperaturaC; // Temperatura corporal
    
    @Column(name = "notas_adicionales", length = 2000)
    private String notasAdicionales;
    
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
        if (fechaAtencion == null) {
            fechaAtencion = LocalDateTime.now();
        }
    }
}

