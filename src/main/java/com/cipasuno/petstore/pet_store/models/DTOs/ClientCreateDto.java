package com.cipasuno.petstore.pet_store.models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO para crear un nuevo cliente
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientCreateDto {
    private String name;
    private String tipoId; // CC, TI, CE, NIT, etc.
    private String ident;
    private String correo;
    private String telefono;
    private String direccion;
    private String observaciones; // Notas adicionales
}

