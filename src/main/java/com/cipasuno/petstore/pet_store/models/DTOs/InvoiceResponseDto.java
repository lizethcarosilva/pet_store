package com.cipasuno.petstore.pet_store.models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponseDto {
    private Integer invoiceId;
    private String numero;
    private Integer clientId;
    private String clientName;
    private Integer employeeId;
    private String employeeName;
    private Integer appointmentId; // ID de la cita agendada (opcional)
    private LocalDateTime fechaEmision;
    private BigDecimal subtotal;
    private BigDecimal descuento;
    private BigDecimal impuesto;
    private BigDecimal total;
    private String estado;
    private String observaciones;
    private Boolean activo;
    private LocalDateTime createdOn;
    private List<InvoiceDetailDto> details;
}

