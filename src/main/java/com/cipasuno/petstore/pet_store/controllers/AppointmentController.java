package com.cipasuno.petstore.pet_store.controllers;

import com.cipasuno.petstore.pet_store.models.Appointment;
import com.cipasuno.petstore.pet_store.models.DTOs.AppointmentCreateDto;
import com.cipasuno.petstore.pet_store.models.DTOs.AppointmentResponseDto;
import com.cipasuno.petstore.pet_store.services.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
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
    public ResponseEntity<?> createAppointment(@RequestBody AppointmentCreateDto appointment) {
        try {
            AppointmentResponseDto createdAppointment = appointmentService.createAppointment(appointment);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAppointment);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error al crear la cita: " + e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "Obtener todas las citas")
    public ResponseEntity<List<AppointmentResponseDto>> getAllAppointments() {
        List<AppointmentResponseDto> appointments = appointmentService.getAllAppointments();
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/active")
    @Operation(summary = "Obtener citas activas")
    public ResponseEntity<List<AppointmentResponseDto>> getActiveAppointments() {
        List<AppointmentResponseDto> appointments = appointmentService.getActiveAppointments();
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/getId")
    @Operation(summary = "Obtener cita por ID")
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
    public ResponseEntity<List<AppointmentResponseDto>> getAppointmentsByPetId(@RequestParam Integer petId) {
        List<AppointmentResponseDto> appointments = appointmentService.getAppointmentsByPetId(petId);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/user")
    @Operation(summary = "Obtener citas por usuario")
    public ResponseEntity<List<AppointmentResponseDto>> getAppointmentsByUserId(@RequestParam Integer userId) {
        List<AppointmentResponseDto> appointments = appointmentService.getAppointmentsByUserId(userId);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/estado")
    @Operation(summary = "Obtener citas por estado")
    public ResponseEntity<List<AppointmentResponseDto>> getAppointmentsByEstado(@RequestParam String estado) {
        List<AppointmentResponseDto> appointments = appointmentService.getAppointmentsByEstado(estado);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/today")
    @Operation(summary = "Obtener citas del día")
    public ResponseEntity<List<AppointmentResponseDto>> getAppointmentsToday() {
        List<AppointmentResponseDto> appointments = appointmentService.getAppointmentsToday();
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/dateRange")
    @Operation(summary = "Obtener citas en un rango de fechas")
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
    public ResponseEntity<Long> countAppointmentsToday() {
        long count = appointmentService.countAppointmentsToday();
        return ResponseEntity.ok(count);
    }
}

