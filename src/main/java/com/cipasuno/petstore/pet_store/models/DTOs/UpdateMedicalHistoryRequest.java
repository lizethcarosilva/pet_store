package com.cipasuno.petstore.pet_store.models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateMedicalHistoryRequest {
    private Integer historyId;
    private Integer serviceId;
    private Integer veterinarianId;
    private LocalDateTime fechaAtencion;
    private String tipoProcedimiento;
    private String diagnostico;
    private String observaciones;
    private String tratamiento;
    private BigDecimal pesoKg;
    private BigDecimal temperaturaC;
    private String notasAdicionales;
}

