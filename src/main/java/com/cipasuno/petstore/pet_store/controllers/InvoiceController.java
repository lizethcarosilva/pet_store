package com.cipasuno.petstore.pet_store.controllers;

import com.cipasuno.petstore.pet_store.models.DTOs.InvoiceCreateDto;
import com.cipasuno.petstore.pet_store.models.DTOs.InvoiceResponseDto;
import com.cipasuno.petstore.pet_store.services.InvoiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "*")
@Tag(name = "Invoice", description = "API para gestión de facturas y ventas")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @PostMapping("/create")
    @Operation(summary = "Crear una nueva factura")
    public ResponseEntity<?> createInvoice(@RequestBody InvoiceCreateDto invoice) {
        try {
            InvoiceResponseDto createdInvoice = invoiceService.createInvoice(invoice);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdInvoice);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al crear la factura: " + e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "Obtener todas las facturas")
    public ResponseEntity<List<InvoiceResponseDto>> getAllInvoices() {
        List<InvoiceResponseDto> invoices = invoiceService.getAllInvoices();
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/active")
    @Operation(summary = "Obtener facturas activas")
    public ResponseEntity<List<InvoiceResponseDto>> getActiveInvoices() {
        List<InvoiceResponseDto> invoices = invoiceService.getActiveInvoices();
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/getId")
    @Operation(summary = "Obtener factura por ID")
    public ResponseEntity<?> getInvoiceById(@RequestBody Integer id) {
        Optional<InvoiceResponseDto> invoice = invoiceService.getInvoiceById(id);
        if (invoice.isPresent()) {
            return ResponseEntity.ok(invoice.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/numero")
    @Operation(summary = "Obtener factura por número")
    public ResponseEntity<?> getInvoiceByNumero(@RequestParam String numero) {
        Optional<InvoiceResponseDto> invoice = invoiceService.getInvoiceByNumero(numero);
        if (invoice.isPresent()) {
            return ResponseEntity.ok(invoice.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/client")
    @Operation(summary = "Obtener facturas por cliente")
    public ResponseEntity<List<InvoiceResponseDto>> getInvoicesByClientId(@RequestParam Integer clientId) {
        List<InvoiceResponseDto> invoices = invoiceService.getInvoicesByClientId(clientId);
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/estado")
    @Operation(summary = "Obtener facturas por estado")
    public ResponseEntity<List<InvoiceResponseDto>> getInvoicesByEstado(@RequestParam String estado) {
        List<InvoiceResponseDto> invoices = invoiceService.getInvoicesByEstado(estado);
        return ResponseEntity.ok(invoices);
    }

    @GetMapping("/dateRange")
    @Operation(summary = "Obtener facturas en un rango de fechas")
    public ResponseEntity<?> getInvoicesByDateRange(
            @RequestParam String inicio, 
            @RequestParam String fin) {
        try {
            LocalDateTime inicioDate = LocalDateTime.parse(inicio);
            LocalDateTime finDate = LocalDateTime.parse(fin);
            
            List<InvoiceResponseDto> invoices = invoiceService.getInvoicesByDateRange(inicioDate, finDate);
            return ResponseEntity.ok(invoices);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body("Formato de fecha inválido. Use ISO-8601: yyyy-MM-ddTHH:mm:ss");
        }
    }

    @GetMapping("/sales/today")
    @Operation(summary = "Obtener total de ventas del día")
    public ResponseEntity<BigDecimal> getTotalSalesToday() {
        BigDecimal total = invoiceService.getTotalSalesToday();
        return ResponseEntity.ok(total);
    }

    @GetMapping("/sales/month")
    @Operation(summary = "Obtener total de ventas del mes")
    public ResponseEntity<BigDecimal> getTotalSalesThisMonth() {
        BigDecimal total = invoiceService.getTotalSalesThisMonth();
        return ResponseEntity.ok(total);
    }

    @GetMapping("/topProducts")
    @Operation(summary = "Obtener productos más vendidos")
    public ResponseEntity<List<Map<String, Object>>> getTopSellingProducts() {
        List<Map<String, Object>> topProducts = invoiceService.getTopSellingProducts();
        return ResponseEntity.ok(topProducts);
    }

    @GetMapping("/topServices")
    @Operation(summary = "Obtener servicios más solicitados")
    public ResponseEntity<List<Map<String, Object>>> getTopSellingServices() {
        List<Map<String, Object>> topServices = invoiceService.getTopSellingServices();
        return ResponseEntity.ok(topServices);
    }

    @PutMapping("/updateStatus")
    @Operation(summary = "Actualizar estado de factura")
    public ResponseEntity<?> updateInvoiceStatus(
            @RequestParam Integer invoiceId, 
            @RequestParam String estado) {
        try {
            InvoiceResponseDto invoice = invoiceService.updateInvoiceStatus(invoiceId, estado);
            return ResponseEntity.ok(invoice);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al actualizar el estado: " + e.getMessage());
        }
    }

    @PutMapping("/cancel")
    @Operation(summary = "Anular factura (devuelve stock)")
    public ResponseEntity<?> cancelInvoice(@RequestParam Integer invoiceId) {
        try {
            invoiceService.cancelInvoice(invoiceId);
            return ResponseEntity.ok("Factura anulada exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al anular la factura: " + e.getMessage());
        }
    }

    @DeleteMapping("/deleteInvoice")
    @Operation(summary = "Eliminar factura (soft delete)")
    public ResponseEntity<?> deleteInvoice(@RequestBody Integer invoiceId) {
        try {
            Optional<InvoiceResponseDto> invoice = invoiceService.getInvoiceById(invoiceId);
            if (!invoice.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            invoiceService.deleteInvoice(invoiceId);
            return ResponseEntity.ok("Factura eliminada exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al eliminar la factura: " + e.getMessage());
        }
    }

    @GetMapping("/count/today")
    @Operation(summary = "Contar facturas del día")
    public ResponseEntity<Long> countInvoicesToday() {
        long count = invoiceService.countInvoicesToday();
        return ResponseEntity.ok(count);
    }
}

