package com.cipasuno.petstore.pet_store.models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VaccinationCreateDto {
    private Integer petId; // Mascota a vacunar
    private Integer medicalHistoryId; // Referencia al historial médico (opcional)
    private Integer veterinarianId; // Veterinario que aplica
    private String vaccineName; // Nombre de la vacuna
    private String vaccineType; // Tipo de vacuna
    private String manufacturer; // Fabricante
    private String batchNumber; // Lote
    private LocalDate applicationDate; // Fecha de aplicación
    private LocalDate nextDoseDate; // Fecha próxima dosis
    private Integer doseNumber; // Número de dosis
    private String applicationSite; // Sitio de aplicación
    private String observations; // Observaciones
    private Boolean requiresBooster; // Requiere refuerzo
    private Boolean isCompleted; // Esquema completo
}

