package com.cipasuno.petstore.pet_store.models.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * DTO para obtener información de una vacunación para pre-cargar el carrito de compras/factura
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VaccinationInvoiceDataDto {
    private Integer vaccinationId;
    private Integer clientId; // Dueño de la mascota
    private String clientName;
    private String clientIdent;
    private Integer petId;
    private String petName;
    private String vaccineName;
    private String vaccineType;
    private String manufacturer;
    private String batchNumber;
    // Si la vacuna está en inventario como producto
    private Integer productId;
    private String productName;
    private BigDecimal productPrice;
    private Integer medicalHistoryId; // Opcional
}

