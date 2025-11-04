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
public class MedicalHistoryCreateDto {
    private Integer petId;
    private Integer appointmentId; // Opcional: puede ser null si no está relacionado con una cita
    private Integer serviceId; // Tipo de servicio
    private Integer veterinarianId; // Trabajador que atiende
    private LocalDateTime fechaAtencion;
    private String tipoProcedimiento; // Consulta General, Desparasitación, Baño, Limpieza de Pulgas, etc.
    private String diagnostico;
    private String observaciones;
    private String tratamiento; // Medicamentos o productos utilizados
    private BigDecimal pesoKg;
    private BigDecimal temperaturaC;
    private String notasAdicionales;
}

