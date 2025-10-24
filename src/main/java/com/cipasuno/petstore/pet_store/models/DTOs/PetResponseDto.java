package com.cipasuno.petstore.pet_store.models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PetResponseDto {
    private Integer petId;
    private String nombre;
    private String tipo;
    private String raza;
    private String cuidadosEspeciales;
    private Integer edad;
    private String sexo;
    private String color;
    private Boolean activo;
    private LocalDateTime createdOn;
    private List<OwnerInfoDto> owners; // Información de los dueños
}

