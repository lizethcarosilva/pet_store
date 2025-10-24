package com.cipasuno.petstore.pet_store.models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto {
    private Integer productId;
    private String codigo;
    private String nombre;
    private String descripcion;
    private String presentacion;
    private BigDecimal precio;
    private Integer stock;
    private Integer stockMinimo;
    private LocalDate fechaVencimiento;
    private Boolean activo;
    private LocalDateTime createdOn;
    private Boolean lowStock; // Calculado: stock < stockMinimo
    private Boolean nearExpiration; // Calculado: vence en menos de 30 días
}

