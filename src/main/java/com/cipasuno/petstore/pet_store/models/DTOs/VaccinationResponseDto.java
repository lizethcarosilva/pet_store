package com.cipasuno.petstore.pet_store.models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VaccinationResponseDto {
    private Integer vaccinationId;
    private Integer petId;
    private String petNombre;
    private Integer medicalHistoryId; // Opcional
    private Integer veterinarianId;
    private String veterinarianName;
    private String vaccineName;
    private String vaccineType;
    private String manufacturer;
    private String batchNumber;
    private LocalDate applicationDate;
    private LocalDate nextDoseDate;
    private Integer doseNumber;
    private String applicationSite;
    private String observations;
    private Boolean requiresBooster;
    private Boolean isCompleted;
    private String estado;
    private Boolean activo;
    private LocalDateTime createdOn;
}

