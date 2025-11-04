package com.cipasuno.petstore.pet_store.controllers;

import com.cipasuno.petstore.pet_store.config.TenantContext;
import com.cipasuno.petstore.pet_store.models.Appointment;
import com.cipasuno.petstore.pet_store.models.DTOs.AppointmentCreateDto;
import com.cipasuno.petstore.pet_store.models.DTOs.AppointmentInvoiceDataDto;
import com.cipasuno.petstore.pet_store.models.DTOs.AppointmentResponseDto;
import com.cipasuno.petstore.pet_store.models.DTOs.IdRequest;
import com.cipasuno.petstore.pet_store.security.RequiresRole;
import com.cipasuno.petstore.pet_store.services.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*")
@Tag(name = "Appointment", description = "API para gestión de citas veterinarias")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @PostMapping("/create")
    @Operation(summary = "Crear una nueva cita")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Empleado", "Cliente"})
    public ResponseEntity<?> createAppointment(@RequestBody AppointmentCreateDto appointment) {
        try {
            String tenantId = TenantContext.getTenantId();
            if (tenantId == null || tenantId.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: tenantId es requerido. Verifique que el header X-Tenant-ID esté presente.");
            }

            AppointmentResponseDto createdAppointment = appointmentService.createAppointment(appointment, tenantId);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAppointment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al crear la cita: " + e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "Obtener todas las citas")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Veterinario", "Empleado", "Cliente"})
    public ResponseEntity<List<AppointmentResponseDto>> getAllAppointments() {
        List<AppointmentResponseDto> appointments = appointmentService.getAllAppointments();
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/active")
    @Operation(summary = "Obtener citas activas")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Veterinario", "Empleado", "Cliente"})
    public ResponseEntity<List<AppointmentResponseDto>> getActiveAppointments() {
        List<AppointmentResponseDto> appointments = appointmentService.getActiveAppointments();
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/getId")
    @Operation(summary = "Obtener cita por ID")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Veterinario", "Empleado", "Cliente"})
    public ResponseEntity<?> getAppointmentById(@RequestBody Integer id) {
        Optional<AppointmentResponseDto> appointment = appointmentService.getAppointmentById(id);
        if (appointment.isPresent()) {
            return ResponseEntity.ok(appointment.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/pet")
    @Operation(summary = "Obtener citas por mascota")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Veterinario", "Empleado", "Cliente"})
    public ResponseEntity<List<AppointmentResponseDto>> getAppointmentsByPetId(@RequestParam Integer petId) {
        List<AppointmentResponseDto> appointments = appointmentService.getAppointmentsByPetId(petId);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/client")
    @Operation(summary = "Obtener citas por cliente")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Veterinario", "Empleado", "Cliente"})
    public ResponseEntity<List<AppointmentResponseDto>> getAppointmentsByClientId(@RequestParam Integer clientId) {
        List<AppointmentResponseDto> appointments = appointmentService.getAppointmentsByClientId(clientId);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/estado")
    @Operation(summary = "Obtener citas por estado")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Veterinario", "Empleado"})
    public ResponseEntity<List<AppointmentResponseDto>> getAppointmentsByEstado(@RequestParam String estado) {
        List<AppointmentResponseDto> appointments = appointmentService.getAppointmentsByEstado(estado);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/today")
    @Operation(summary = "Obtener citas del día")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Veterinario", "Empleado"})
    public ResponseEntity<List<AppointmentResponseDto>> getAppointmentsToday() {
        List<AppointmentResponseDto> appointments = appointmentService.getAppointmentsToday();
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/dateRange")
    @Operation(summary = "Obtener citas en un rango de fechas")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Veterinario", "Empleado"})
    public ResponseEntity<?> getAppointmentsByDateRange(
            @RequestParam String inicio, 
            @RequestParam String fin) {
        try {
            LocalDateTime inicioDate = LocalDateTime.parse(inicio);
            LocalDateTime finDate = LocalDateTime.parse(fin);
            
            List<AppointmentResponseDto> appointments = appointmentService.getAppointmentsByDateRange(inicioDate, finDate);
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body("Formato de fecha inválido. Use ISO-8601: yyyy-MM-ddTHH:mm:ss");
        }
    }

    @PutMapping("/update")
    @Operation(summary = "Actualizar cita")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Veterinario", "Empleado"})
    public ResponseEntity<?> updateAppointment(@RequestBody Appointment appointmentDetails) {
        try {
            Optional<AppointmentResponseDto> existingAppointment = appointmentService.getAppointmentById(appointmentDetails.getAppointmentId());
            if (!existingAppointment.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            AppointmentResponseDto updatedAppointment = appointmentService.updateAppointment(
                appointmentDetails.getAppointmentId(), appointmentDetails);
            return ResponseEntity.ok(updatedAppointment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al actualizar la cita: " + e.getMessage());
        }
    }

    @PutMapping("/complete")
    @Operation(summary = "Completar cita con diagnóstico")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Veterinario"})
    public ResponseEntity<?> completeAppointment(
            @RequestParam Integer appointmentId, 
            @RequestParam String diagnostico) {
        try {
            AppointmentResponseDto appointment = appointmentService.completeAppointment(appointmentId, diagnostico);
            return ResponseEntity.ok(appointment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al completar la cita: " + e.getMessage());
        }
    }

    @PutMapping("/cancel")
    @Operation(summary = "Cancelar cita")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Veterinario", "Empleado", "Cliente"})
    public ResponseEntity<?> cancelAppointment(@RequestParam Integer appointmentId) {
        try {
            AppointmentResponseDto appointment = appointmentService.cancelAppointment(appointmentId);
            return ResponseEntity.ok(appointment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al cancelar la cita: " + e.getMessage());
        }
    }

    @DeleteMapping("/deleteAppointment")
    @Operation(summary = "Eliminar cita (soft delete)")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente"})
    public ResponseEntity<?> deleteAppointment(@RequestBody Integer appointmentId) {
        try {
            Optional<AppointmentResponseDto> appointment = appointmentService.getAppointmentById(appointmentId);
            if (!appointment.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            appointmentService.deleteAppointment(appointmentId);
            return ResponseEntity.ok("Cita eliminada exitosamente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al eliminar la cita: " + e.getMessage());
        }
    }

    @DeleteMapping("/appointmentPermanent")
    @Operation(summary = "Eliminar cita permanentemente")
    @RequiresRole({"SuperAdmin", "Admin"})
    public ResponseEntity<?> deleteAppointmentPermanently(@RequestBody Integer id) {
        try {
            Optional<AppointmentResponseDto> appointment = appointmentService.getAppointmentById(id);
            if (!appointment.isPresent()) {
                return ResponseEntity.notFound().build();
            }

            appointmentService.deleteAppointmentPermanently(id);
            return ResponseEntity.ok("Cita eliminada permanentemente");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al eliminar la cita: " + e.getMessage());
        }
    }

    @GetMapping("/count/today")
    @Operation(summary = "Contar citas del día")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Veterinario", "Empleado"})
    public ResponseEntity<Long> countAppointmentsToday() {
        long count = appointmentService.countAppointmentsToday();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/veterinarian")
    @Operation(summary = "Obtener citas de un veterinario para una fecha")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Veterinario", "Empleado"})
    public ResponseEntity<?> getAppointmentsByVeterinarianAndDate(
            @RequestParam Integer veterinarianId,
            @RequestParam String fecha) {
        try {
            LocalDate fechaDate = LocalDate.parse(fecha);
            List<AppointmentResponseDto> appointments = appointmentService.getAppointmentsByVeterinarianAndDate(
                veterinarianId, 
                fechaDate
            );
            return ResponseEntity.ok(appointments);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Error: " + e.getMessage() + ". Formato de fecha esperado: yyyy-MM-dd");
        }
    }

    @GetMapping("/availableSlots")
    @Operation(summary = "Obtener horarios disponibles de un veterinario para una fecha")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Veterinario", "Empleado", "Cliente"})
    public ResponseEntity<?> getAvailableTimeSlots(
            @RequestParam Integer veterinarianId,
            @RequestParam String fecha,
            @RequestParam(required = false, defaultValue = "08:00") String horaInicio,
            @RequestParam(required = false, defaultValue = "17:00") String horaFin,
            @RequestParam(required = false, defaultValue = "30") Integer intervaloMinutos) {
        try {
            LocalDate fechaDate = LocalDate.parse(fecha);
            LocalTime inicioTime = LocalTime.parse(horaInicio);
            LocalTime finTime = LocalTime.parse(horaFin);
            
            Map<String, Object> availableSlots = appointmentService.getAvailableTimeSlots(
                veterinarianId,
                fechaDate,
                inicioTime,
                finTime,
                intervaloMinutos
            );
            
            return ResponseEntity.ok(availableSlots);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("Error: " + e.getMessage() + ". Formato de fecha esperado: yyyy-MM-dd. Formato de hora: HH:mm");
        }
    }

    @PostMapping("/forInvoice")
    @Operation(summary = "Obtener datos de cita para agregar al carrito de compras/factura")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Veterinario", "Empleado"})
    public ResponseEntity<?> getAppointmentForInvoice(@RequestBody IdRequest request) {
        try {
            AppointmentInvoiceDataDto appointmentData = appointmentService.getAppointmentForInvoice(request.getId());
            return ResponseEntity.ok(appointmentData);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Error al obtener datos de la cita: " + e.getMessage());
        }
    }

    @PutMapping("/markAsInvoiced")
    @Operation(summary = "Marcar cita como facturada")
    @RequiresRole({"SuperAdmin", "Admin", "Gerente", "Empleado"})
    public ResponseEntity<?> markAppointmentAsInvoiced(@RequestParam Integer appointmentId) {
        System.out.println("🔵 CONTROLLER - Recibido appointmentId: " + appointmentId);
        try {
            AppointmentResponseDto updated = appointmentService.markAsInvoiced(appointmentId);
            System.out.println("🔵 CONTROLLER - Devolviendo estado: " + updated.getEstado());
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            System.out.println("🔴 CONTROLLER - ERROR: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al marcar cita como facturada: " + e.getMessage());
        }
    }
}

