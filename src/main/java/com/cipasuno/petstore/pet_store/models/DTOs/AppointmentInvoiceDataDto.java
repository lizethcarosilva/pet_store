package com.cipasuno.petstore.pet_store.models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO para obtener información de una cita para pre-cargar el carrito de compras/factura
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentInvoiceDataDto {
    private Integer appointmentId;
    private Integer clientId; // Dueño de la mascota
    private String clientName;
    private String clientIdent;
    private Integer petId;
    private String petName;
    private Integer serviceId;
    private String serviceName;
    private BigDecimal servicePrice;
    private String estado; // Estado de la cita
}

