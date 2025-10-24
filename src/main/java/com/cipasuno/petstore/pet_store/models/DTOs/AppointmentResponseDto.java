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
public class AppointmentResponseDto {
    private Integer appointmentId;
    private Integer petId;
    private String petNombre;
    private Integer serviceId;
    private String serviceName;
    private Integer userId;
    private String userName;
    private Integer veterinarianId;
    private String veterinarianName;
    private LocalDateTime fechaHora;
    private String estado;
    private String observaciones;
    private String diagnostico;
    private Boolean activo;
    private LocalDateTime createdOn;
}

