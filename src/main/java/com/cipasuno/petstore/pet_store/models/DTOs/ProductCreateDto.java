package com.cipasuno.petstore.pet_store.models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateDto {
    private String codigo;
    private String nombre;
    private String descripcion;
    private String presentacion;
    private BigDecimal precio;
    private Integer stock;
    private Integer stockMinimo;
    private LocalDate fechaVencimiento;
}

