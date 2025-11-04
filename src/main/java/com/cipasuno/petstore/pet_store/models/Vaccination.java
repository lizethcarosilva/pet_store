package com.cipasuno.petstore.pet_store.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "vaccination")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Vaccination {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vaccination_id")
    private Integer vaccinationId;
    
    @Column(name = "tenant_id", nullable = false)
    private String tenantId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    @JsonIgnoreProperties({"owners", "hibernateLazyInitializer", "handler"})
    private Pet pet; // Mascota vacunada
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medical_history_id")
    @JsonIgnoreProperties({"pet", "appointment", "service", "veterinarian", "hibernateLazyInitializer", "handler"})
    private PetMedicalHistory medicalHistory; // Referencia al historial médico (opcional)
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veterinarian_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private User veterinarian; // Veterinario que aplicó la vacuna
    
    @Column(name = "vaccine_name", length = 200, nullable = false)
    private String vaccineName; // Nombre de la vacuna (ej: Rabia, Parvovirus, Moquillo)
    
    @Column(name = "vaccine_type", length = 100)
    private String vaccineType; // Tipo: Viral, Bacteriana, Antiparasitaria
    
    @Column(name = "manufacturer", length = 200)
    private String manufacturer; // Fabricante/laboratorio
    
    @Column(name = "batch_number", length = 100)
    private String batchNumber; // Número de lote
    
    @Column(name = "application_date", nullable = false)
    private LocalDate applicationDate; // Fecha de aplicación
    
    @Column(name = "next_dose_date")
    private LocalDate nextDoseDate; // Fecha de próxima dosis (si requiere refuerzo)
    
    @Column(name = "dose_number")
    private Integer doseNumber; // Número de dosis (1ra, 2da, 3ra, refuerzo)
    
    @Column(name = "application_site", length = 100)
    private String applicationSite; // Sitio de aplicación (Subcutánea, Intramuscular, etc.)
    
    @Column(name = "observations", length = 1000)
    private String observations; // Observaciones y reacciones adversas
    
    @Column(name = "requires_booster", nullable = false)
    private Boolean requiresBooster = false; // Si requiere refuerzo
    
    @Column(name = "is_completed", nullable = false)
    private Boolean isCompleted = false; // Si el esquema está completo
    
    @Column(length = 20, nullable = false)
    private String estado = "APLICADA"; // PENDIENTE, APLICADA, FACTURADA, CANCELADA
    
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
        if (requiresBooster == null) {
            requiresBooster = false;
        }
        if (isCompleted == null) {
            isCompleted = false;
        }
        if (applicationDate == null) {
            applicationDate = LocalDate.now();
        }
        if (estado == null || estado.isEmpty()) {
            estado = "APLICADA"; // Por defecto, cuando se crea ya está aplicada
        }
    }
}

