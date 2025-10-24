package com.cipasuno.petstore.pet_store.models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PetCreateDto {
    private String nombre;
    private String tipo;
    private String raza;
    private String cuidadosEspeciales;
    private Integer edad;
    private String sexo;
    private String color;
    private List<Integer> ownerIds; // IDs de los dueños
}

