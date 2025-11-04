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
public class UpdateProductRequest {
    private Integer productId;
    private String codigo;
    private String nombre;
    private String descripcion;
    private String presentacion;
    private BigDecimal precio;
    private Integer stock;
    private Integer stockMinimo;
    private LocalDate fechaVencimiento;
    private String lote; // Número de lote
    private String fabricante; // Fabricante o marca
    private Boolean esVacuna; // Indica si es una vacuna
    private Boolean activo;
}

