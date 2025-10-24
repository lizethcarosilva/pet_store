package com.cipasuno.petstore.pet_store.models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAppointmentRequest {
    private Integer appointmentId;
    private LocalDateTime fechaHora;
    private String estado;
    private String observaciones;
    private String diagnostico;
    private Boolean activo;
}

