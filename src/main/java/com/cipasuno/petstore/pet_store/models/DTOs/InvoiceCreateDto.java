package com.cipasuno.petstore.pet_store.models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceCreateDto {
    private Integer clientId;
    private Integer employeeId;
    private Integer appointmentId; // ID de la cita agendada (opcional, solo para servicios agendados)
    private BigDecimal descuento;
    private BigDecimal impuesto;
    private String observaciones;
    private List<InvoiceDetailDto> details;
}

