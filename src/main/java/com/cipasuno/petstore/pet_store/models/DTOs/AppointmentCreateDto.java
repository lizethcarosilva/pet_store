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
public class AppointmentCreateDto {
    private Integer petId;
    private Integer serviceId;
    private Integer userId; // Cliente que agenda
    private Integer veterinarianId; // Opcional
    private LocalDateTime fechaHora;
    private String observaciones;
}

