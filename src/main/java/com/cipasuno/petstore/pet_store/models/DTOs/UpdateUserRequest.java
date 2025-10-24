package com.cipasuno.petstore.pet_store.models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {
    private Integer userId;
    private String name;
    private String tipoId;
    private String ident;
    private String correo;
    private String telefono;
    private String direccion;
    private String password;
    private String rolId;
    private Boolean activo;
}

