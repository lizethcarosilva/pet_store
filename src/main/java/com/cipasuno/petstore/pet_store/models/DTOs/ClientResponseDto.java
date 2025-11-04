package com.cipasuno.petstore.pet_store.models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO para responder con información de un cliente
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponseDto {
    private Integer clientId;
    private String name;
    private String tipoId;
    private String ident;
    private String correo;
    private String telefono;
    private String direccion;
    private String observaciones;
    private LocalDateTime createdOn;
    private Boolean activo;
    private String tenantId;
}

