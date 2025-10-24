package com.cipasuno.petstore.pet_store.models.DTOs;

import java.time.LocalDateTime;

import lombok.Data;


@Data
public class UserResponseDto {
    private Integer user_id;
    private String name;
    private String tipo_id;
    private String ident;
    private String correo;
    private String telefono;
    private String direccion;
    private String rol_id;
    private LocalDateTime created_on;
    private Boolean activo;
    private String tenant_id;

}