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
public class UpdateVaccinationRequest {
    private Integer vaccinationId;
    private Integer veterinarianId;
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
}

