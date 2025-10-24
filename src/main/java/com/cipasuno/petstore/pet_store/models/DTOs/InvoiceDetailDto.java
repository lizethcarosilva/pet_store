package com.cipasuno.petstore.pet_store.models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceDetailDto {
    private Integer detailId;
    private String tipo; // PRODUCTO o SERVICIO
    private Integer productId;
    private Integer serviceId;
    private String itemNombre;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal descuento;
    private BigDecimal subtotal;
}

