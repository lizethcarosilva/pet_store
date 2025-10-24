package com.cipasuno.petstore.pet_store.controllers;

import com.cipasuno.petstore.pet_store.models.DTOs.AppointmentResponseDto;
import com.cipasuno.petstore.pet_store.models.DTOs.ProductResponseDto;
import com.cipasuno.petstore.pet_store.services.AppointmentService;
import com.cipasuno.petstore.pet_store.services.InvoiceService;
import com.cipasuno.petstore.pet_store.services.PetService;
import com.cipasuno.petstore.pet_store.services.ProductService;
import com.cipasuno.petstore.pet_store.services.UserService;
import com.cipasuno.petstore.pet_store.services.VeterinaryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
@Tag(name = "Dashboard", description = "API para estadísticas y métricas del dashboard")
public class DashboardController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private ProductService productService;

    @Autowired
    private InvoiceService invoiceService;

    @Autowired
    private UserService userService;

    @Autowired
    private PetService petService;

    @Autowired
    private VeterinaryService veterinaryService;

    @GetMapping("/summary")
    @Operation(summary = "Obtener resumen general del dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardSummary() {
        Map<String, Object> summary = new HashMap<>();

        try {
            // Citas del día
            long citasHoy = appointmentService.countAppointmentsToday();
            summary.put("citasHoy", citasHoy);

            // Total de productos
            long totalProductos = productService.countActiveProducts();
            summary.put("totalProductos", totalProductos);

            // Ventas del día
            BigDecimal ventasHoy = invoiceService.getTotalSalesToday();
            summary.put("ventasHoy", ventasHoy);

            // Ventas del mes
            BigDecimal ventasMes = invoiceService.getTotalSalesThisMonth();
            summary.put("ventasMes", ventasMes);

            // Total de usuarios activos
            long totalUsuarios = userService.countActiveUsers();
            summary.put("totalUsuarios", totalUsuarios);

            // Total de mascotas activas
            long totalMascotas = petService.countActivePets();
            summary.put("totalMascotas", totalMascotas);

            // Total de servicios activos
            long totalServicios = veterinaryService.countActiveServices();
            summary.put("totalServicios", totalServicios);

            // Productos con stock bajo
            long productosStockBajo = productService.countLowStockProducts();
            summary.put("productosStockBajo", productosStockBajo);

            // Facturas del día
            long facturasHoy = invoiceService.countInvoicesToday();
            summary.put("facturasHoy", facturasHoy);

            return ResponseEntity.ok(summary);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error al obtener el resumen: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/appointments/today")
    @Operation(summary = "Obtener citas programadas para hoy")
    public ResponseEntity<?> getAppointmentsToday() {
        try {
            List<AppointmentResponseDto> appointments = appointmentService.getAppointmentsToday();
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error al obtener las citas del día: " + e.getMessage());
        }
    }

    @GetMapping("/products/lowStock")
    @Operation(summary = "Obtener productos con inventario bajo")
    public ResponseEntity<?> getLowStockProducts() {
        try {
            List<ProductResponseDto> products = productService.getLowStockProducts();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error al obtener productos con stock bajo: " + e.getMessage());
        }
    }

    @GetMapping("/products/expiringSoon")
    @Operation(summary = "Obtener productos próximos a vencer (30 días)")
    public ResponseEntity<?> getProductsExpiringSoon() {
        try {
            List<ProductResponseDto> products = productService.getProductsExpiringSoon();
            return ResponseEntity.ok(products);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error al obtener productos por vencer: " + e.getMessage());
        }
    }

    @GetMapping("/alerts")
    @Operation(summary = "Obtener todas las alertas (stock bajo y vencimientos)")
    public ResponseEntity<Map<String, Object>> getAlerts() {
        Map<String, Object> alerts = new HashMap<>();

        try {
            // Productos con stock bajo
            List<ProductResponseDto> lowStockProducts = productService.getLowStockProducts();
            alerts.put("productosStockBajo", lowStockProducts);
            alerts.put("countStockBajo", lowStockProducts.size());

            // Productos próximos a vencer
            List<ProductResponseDto> expiringProducts = productService.getProductsExpiringSoon();
            alerts.put("productosProximosVencer", expiringProducts);
            alerts.put("countProximosVencer", expiringProducts.size());

            // Total de alertas
            alerts.put("totalAlertas", lowStockProducts.size() + expiringProducts.size());

            return ResponseEntity.ok(alerts);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error al obtener alertas: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/sales/stats")
    @Operation(summary = "Obtener estadísticas de ventas")
    public ResponseEntity<Map<String, Object>> getSalesStats() {
        Map<String, Object> stats = new HashMap<>();

        try {
            // Ventas del día
            BigDecimal ventasHoy = invoiceService.getTotalSalesToday();
            stats.put("ventasHoy", ventasHoy);

            // Ventas del mes
            BigDecimal ventasMes = invoiceService.getTotalSalesThisMonth();
            stats.put("ventasMes", ventasMes);

            // Facturas del día
            long facturasHoy = invoiceService.countInvoicesToday();
            stats.put("facturasHoy", facturasHoy);

            // Productos más vendidos
            List<Map<String, Object>> topProducts = invoiceService.getTopSellingProducts();
            stats.put("productosTopVentas", topProducts);

            // Servicios más solicitados
            List<Map<String, Object>> topServices = invoiceService.getTopSellingServices();
            stats.put("serviciosTopVentas", topServices);

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error al obtener estadísticas de ventas: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/products/stats")
    @Operation(summary = "Obtener estadísticas de productos")
    public ResponseEntity<Map<String, Object>> getProductStats() {
        Map<String, Object> stats = new HashMap<>();

        try {
            // Total de productos activos
            long totalProductos = productService.countActiveProducts();
            stats.put("totalProductos", totalProductos);

            // Productos con stock bajo
            long productosStockBajo = productService.countLowStockProducts();
            stats.put("productosStockBajo", productosStockBajo);

            // Productos próximos a vencer
            List<ProductResponseDto> expiringProducts = productService.getProductsExpiringSoon();
            stats.put("productosProximosVencer", expiringProducts.size());

            // Lista de productos con stock bajo
            List<ProductResponseDto> lowStockProducts = productService.getLowStockProducts();
            stats.put("listaStockBajo", lowStockProducts);

            // Productos más vendidos
            List<Map<String, Object>> topProducts = invoiceService.getTopSellingProducts();
            stats.put("productosMasVendidos", topProducts);

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error al obtener estadísticas de productos: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/appointments/stats")
    @Operation(summary = "Obtener estadísticas de citas")
    public ResponseEntity<Map<String, Object>> getAppointmentStats() {
        Map<String, Object> stats = new HashMap<>();

        try {
            // Citas del día
            long citasHoy = appointmentService.countAppointmentsToday();
            stats.put("citasHoy", citasHoy);

            // Lista de citas del día
            List<AppointmentResponseDto> appointmentsToday = appointmentService.getAppointmentsToday();
            stats.put("listaCitasHoy", appointmentsToday);

            // Citas programadas
            List<AppointmentResponseDto> citasProgramadas = appointmentService.getAppointmentsByEstado("PROGRAMADA");
            stats.put("citasProgramadas", citasProgramadas.size());

            // Citas completadas
            List<AppointmentResponseDto> citasCompletadas = appointmentService.getAppointmentsByEstado("COMPLETADA");
            stats.put("citasCompletadas", citasCompletadas.size());

            // Citas canceladas
            List<AppointmentResponseDto> citasCanceladas = appointmentService.getAppointmentsByEstado("CANCELADA");
            stats.put("citasCanceladas", citasCanceladas.size());

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error al obtener estadísticas de citas: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/users/stats")
    @Operation(summary = "Obtener estadísticas de usuarios")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        Map<String, Object> stats = new HashMap<>();

        try {
            // Total de usuarios
            long totalUsuarios = userService.countUsers();
            stats.put("totalUsuarios", totalUsuarios);

            // Usuarios activos
            long usuariosActivos = userService.countActiveUsers();
            stats.put("usuariosActivos", usuariosActivos);

            // Total de mascotas
            long totalMascotas = petService.countActivePets();
            stats.put("totalMascotas", totalMascotas);

            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error al obtener estadísticas de usuarios: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }

    @GetMapping("/topProducts")
    @Operation(summary = "Obtener productos más vendidos (top 10)")
    public ResponseEntity<?> getTopProducts() {
        try {
            List<Map<String, Object>> topProducts = invoiceService.getTopSellingProducts();
            // Limitar a top 10
            if (topProducts.size() > 10) {
                topProducts = topProducts.subList(0, 10);
            }
            return ResponseEntity.ok(topProducts);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error al obtener productos más vendidos: " + e.getMessage());
        }
    }

    @GetMapping("/topServices")
    @Operation(summary = "Obtener servicios más solicitados (top 10)")
    public ResponseEntity<?> getTopServices() {
        try {
            List<Map<String, Object>> topServices = invoiceService.getTopSellingServices();
            // Limitar a top 10
            if (topServices.size() > 10) {
                topServices = topServices.subList(0, 10);
            }
            return ResponseEntity.ok(topServices);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body("Error al obtener servicios más solicitados: " + e.getMessage());
        }
    }

    @GetMapping("/performance")
    @Operation(summary = "Obtener producto con mejor y peor rendimiento")
    public ResponseEntity<Map<String, Object>> getProductPerformance() {
        Map<String, Object> performance = new HashMap<>();

        try {
            List<Map<String, Object>> topProducts = invoiceService.getTopSellingProducts();
            
            if (!topProducts.isEmpty()) {
                // Mejor rendimiento (primero de la lista)
                performance.put("mejorProducto", topProducts.get(0));
                
                // Peor rendimiento (último de la lista)
                performance.put("peorProducto", topProducts.get(topProducts.size() - 1));
            } else {
                performance.put("mensaje", "No hay datos de ventas disponibles");
            }

            return ResponseEntity.ok(performance);
        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Error al obtener rendimiento de productos: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}

