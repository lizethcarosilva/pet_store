package com.cipasuno.petstore.pet_store.models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePetRequest {
    private Integer petId;
    private String nombre;
    private String tipo;
    private String raza;
    private String cuidadosEspeciales;
    private Integer edad;
    private String sexo;
    private String color;
    private Boolean activo;
}

