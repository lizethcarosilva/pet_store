package com.cipasuno.petstore.pet_store.models.DTOs;


import lombok.Data;


@Data
public class UserCreateDto {
    private String name;
    private String tipoId;
    private String ident;
    private String correo;
    private String password; 
    private String telefono;
    private String direccion;
    private String rolId;
    
}