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
public class TenantCreateDto {


    private String nit;
    private String razonSocial;
    private String direccion;
    private String telefono;
    private String email;
    private String plan; 
    private LocalDateTime fechaVencimiento;
    private String configuracion;
}

