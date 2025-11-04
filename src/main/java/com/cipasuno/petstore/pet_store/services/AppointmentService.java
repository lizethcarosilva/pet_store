package com.cipasuno.petstore.pet_store.services;

import com.cipasuno.petstore.pet_store.models.Appointment;
import com.cipasuno.petstore.pet_store.models.Client;
import com.cipasuno.petstore.pet_store.models.Pet;
import com.cipasuno.petstore.pet_store.models.Service;
import com.cipasuno.petstore.pet_store.models.User;
import com.cipasuno.petstore.pet_store.models.DTOs.AppointmentCreateDto;
import com.cipasuno.petstore.pet_store.models.DTOs.AppointmentInvoiceDataDto;
import com.cipasuno.petstore.pet_store.models.DTOs.AppointmentResponseDto;
import com.cipasuno.petstore.pet_store.repositories.AppointmentRepository;
import com.cipasuno.petstore.pet_store.repositories.ClientRepository;
import com.cipasuno.petstore.pet_store.repositories.PetRepository;
import com.cipasuno.petstore.pet_store.repositories.ServiceRepository;
import com.cipasuno.petstore.pet_store.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public AppointmentResponseDto createAppointment(AppointmentCreateDto appointmentDto, String tenantId) {
        // Validar que el veterinario sea requerido
        if (appointmentDto.getVeterinarianId() == null) {
            throw new RuntimeException("El veterinario es requerido para agendar una cita");
        }
        
        Pet pet = petRepository.findById(appointmentDto.getPetId())
            .orElseThrow(() -> new RuntimeException("Mascota no encontrada con ID: " + appointmentDto.getPetId()));
        
        Service service = serviceRepository.findById(appointmentDto.getServiceId())
            .orElseThrow(() -> new RuntimeException("Servicio no encontrado con ID: " + appointmentDto.getServiceId()));
        
        Client client = clientRepository.findById(appointmentDto.getClientId())
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + appointmentDto.getClientId()));
        
        User veterinarian = userRepository.findById(appointmentDto.getVeterinarianId())
            .orElseThrow(() -> new RuntimeException("Veterinario no encontrado con ID: " + appointmentDto.getVeterinarianId()));
        
        // Validar que no exista una cita en el mismo horario para el mismo veterinario
        List<Appointment> existingAppointments = appointmentRepository.findByVeterinarianIdAndFechaHora(
            appointmentDto.getVeterinarianId(), 
            appointmentDto.getFechaHora()
        );
        
        if (!existingAppointments.isEmpty()) {
            throw new RuntimeException("Ya existe una cita programada para este veterinario en la fecha y hora seleccionada: " + appointmentDto.getFechaHora());
        }
        
        // Validar solapamiento considerando la duración del servicio
        Integer duracionServicio = service.getDuracionMinutos() != null ? service.getDuracionMinutos() : 30; // Default 30 minutos
        LocalDateTime finCita = appointmentDto.getFechaHora().plusMinutes(duracionServicio);
        
        // Obtener todas las citas del veterinario en el mismo día para verificar solapamientos
        List<Appointment> citasDelDia = appointmentRepository.findByVeterinarianIdAndDate(
            appointmentDto.getVeterinarianId(),
            appointmentDto.getFechaHora()
        );
        
        // Verificar solapamiento con cada cita existente
        for (Appointment existing : citasDelDia) {
            Integer duracionExistente = existing.getService().getDuracionMinutos() != null ? 
                                        existing.getService().getDuracionMinutos() : 30;
            LocalDateTime inicioExistente = existing.getFechaHora();
            LocalDateTime finExistente = inicioExistente.plusMinutes(duracionExistente);
            
            // Verificar si hay solapamiento: las citas se solapan si una empieza antes de que termine la otra
            boolean haySolapamiento = (appointmentDto.getFechaHora().isBefore(finExistente) && 
                                      finCita.isAfter(inicioExistente));
            
            if (haySolapamiento) {
                throw new RuntimeException("El horario seleccionado se solapa con otra cita del veterinario. " +
                    "Cita existente: " + inicioExistente + " - " + finExistente + 
                    ". Su cita: " + appointmentDto.getFechaHora() + " - " + finCita);
            }
        }
        
        Appointment appointment = new Appointment();
        appointment.setTenantId(tenantId);
        appointment.setPet(pet);
        appointment.setService(service);
        appointment.setClient(client);
        appointment.setVeterinarian(veterinarian);
        appointment.setFechaHora(appointmentDto.getFechaHora());
        appointment.setObservaciones(appointmentDto.getObservaciones());

        Appointment savedAppointment = appointmentRepository.save(appointment);
        return mapToResponseDto(savedAppointment);
    }

    public List<AppointmentResponseDto> getAllAppointments() {
        return appointmentRepository.findAllWithRelations()
                .stream()
                .filter(appointment -> {
                    try {
                        // Verificar que el cliente existe (evitar datos huérfanos)
                        if (appointment.getClient() != null) {
                            appointment.getClient().getName(); // Esto lanza exception si el client no existe
                            return true;
                        }
                        return false;
                    } catch (Exception e) {
                        // Si hay error al acceder al cliente, filtrar esta cita
                        return false;
                    }
                })
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<AppointmentResponseDto> getActiveAppointments() {
        return appointmentRepository.findByActivoTrue()
                .stream()
                .filter(appointment -> {
                    try {
                        // Verificar que el cliente existe (evitar datos huérfanos)
                        if (appointment.getClient() != null) {
                            appointment.getClient().getName(); // Esto lanza exception si el client no existe
                            return true;
                        }
                        return false;
                    } catch (Exception e) {
                        // Si hay error al acceder al cliente, filtrar esta cita
                        return false;
                    }
                })
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public Optional<AppointmentResponseDto> getAppointmentById(Integer id) {
        return appointmentRepository.findById(id)
                .map(this::mapToResponseDto);
    }

    public List<AppointmentResponseDto> getAppointmentsByPetId(Integer petId) {
        return appointmentRepository.findByPetId(petId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<AppointmentResponseDto> getAppointmentsByClientId(Integer clientId) {
        return appointmentRepository.findByClientId(clientId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<AppointmentResponseDto> getAppointmentsByEstado(String estado) {
        return appointmentRepository.findByEstado(estado)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<AppointmentResponseDto> getAppointmentsByDateRange(LocalDateTime inicio, LocalDateTime fin) {
        return appointmentRepository.findByFechaHoraBetween(inicio, fin)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<AppointmentResponseDto> getAppointmentsToday() {
        return appointmentRepository.findAppointmentsToday()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public AppointmentResponseDto updateAppointment(Integer id, Appointment appointmentDetails) {
        Appointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cita no encontrada con ID: " + id));

        if (appointmentDetails.getFechaHora() != null) {
            appointment.setFechaHora(appointmentDetails.getFechaHora());
        }
        if (appointmentDetails.getEstado() != null) {
            appointment.setEstado(appointmentDetails.getEstado());
        }
        if (appointmentDetails.getObservaciones() != null) {
            appointment.setObservaciones(appointmentDetails.getObservaciones());
        }
        if (appointmentDetails.getDiagnostico() != null) {
            appointment.setDiagnostico(appointmentDetails.getDiagnostico());
        }
        if (appointmentDetails.getActivo() != null) {
            appointment.setActivo(appointmentDetails.getActivo());
        }

        Appointment updatedAppointment = appointmentRepository.save(appointment);
        return mapToResponseDto(updatedAppointment);
    }

    @Transactional
    public AppointmentResponseDto completeAppointment(Integer id, String diagnostico) {
        Appointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cita no encontrada con ID: " + id));
        
        appointment.setEstado("COMPLETADA");
        appointment.setDiagnostico(diagnostico);
        
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        return mapToResponseDto(updatedAppointment);
    }

    @Transactional
    public AppointmentResponseDto cancelAppointment(Integer id) {
        Appointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cita no encontrada con ID: " + id));
        
        appointment.setEstado("CANCELADA");
        
        Appointment updatedAppointment = appointmentRepository.save(appointment);
        return mapToResponseDto(updatedAppointment);
    }

    @Transactional
    public void deleteAppointment(Integer id) {
        Appointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cita no encontrada con ID: " + id));
        
        appointment.setActivo(false);
        appointmentRepository.save(appointment);
    }

    @Transactional
    public void deleteAppointmentPermanently(Integer id) {
        Appointment appointment = appointmentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cita no encontrada con ID: " + id));
        
        appointmentRepository.delete(appointment);
    }

    public long countAppointmentsToday() {
        return appointmentRepository.countAppointmentsToday();
    }

    /**
     * Obtiene las citas programadas de un veterinario para una fecha específica
     */
    public List<AppointmentResponseDto> getAppointmentsByVeterinarianAndDate(Integer veterinarianId, LocalDate fecha) {
        LocalDateTime fechaInicio = fecha.atStartOfDay();
        List<Appointment> appointments = appointmentRepository.findByVeterinarianIdAndDate(
            veterinarianId, 
            fechaInicio
        );
        return appointments.stream()
            .map(this::mapToResponseDto)
            .collect(Collectors.toList());
    }

    /**
     * Calcula los horarios disponibles para un veterinario en una fecha específica
     * Considera las citas existentes y la duración de los servicios
     * 
     * @param veterinarianId ID del veterinario
     * @param fecha Fecha para la cual se quieren ver los horarios disponibles
     * @param horaInicio Hora de inicio del horario laboral (ej: 08:00)
     * @param horaFin Hora de fin del horario laboral (ej: 17:00)
     * @param intervaloMinutos Intervalo entre citas disponibles (ej: 30 minutos)
     * @return Lista de LocalDateTime con los horarios disponibles
     */
    public Map<String, Object> getAvailableTimeSlots(Integer veterinarianId, LocalDate fecha, 
                                                     LocalTime horaInicio, LocalTime horaFin, 
                                                     Integer intervaloMinutos) {
        // Validar que el veterinario exista
        User veterinarian = userRepository.findById(veterinarianId)
            .orElseThrow(() -> new RuntimeException("Veterinario no encontrado con ID: " + veterinarianId));
        
        // Obtener todas las citas del veterinario en la fecha
        LocalDateTime fechaInicio = fecha.atStartOfDay();
        List<Appointment> citasDelDia = appointmentRepository.findByVeterinarianIdAndDate(
            veterinarianId, 
            fechaInicio
        );
        
        // Crear lista de horarios ocupados con su duración
        List<Map<String, LocalDateTime>> horariosOcupados = new ArrayList<>();
        for (Appointment cita : citasDelDia) {
            Integer duracion = cita.getService().getDuracionMinutos() != null ? 
                             cita.getService().getDuracionMinutos() : 30;
            LocalDateTime fin = cita.getFechaHora().plusMinutes(duracion);
            
            Map<String, LocalDateTime> ocupado = new HashMap<>();
            ocupado.put("inicio", cita.getFechaHora());
            ocupado.put("fin", fin);
            horariosOcupados.add(ocupado);
        }
        
        // Generar horarios disponibles
        List<LocalDateTime> horariosDisponibles = new ArrayList<>();
        LocalDateTime horaActual = fecha.atTime(horaInicio);
        LocalDateTime finDia = fecha.atTime(horaFin);
        
        while (horaActual.plusMinutes(intervaloMinutos).isBefore(finDia) || 
               horaActual.plusMinutes(intervaloMinutos).equals(finDia)) {
            
            LocalDateTime finSlot = horaActual.plusMinutes(intervaloMinutos);
            
            // Verificar si este horario está disponible (no se solapa con ninguna cita)
            boolean disponible = true;
            for (Map<String, LocalDateTime> ocupado : horariosOcupados) {
                LocalDateTime inicioOcupado = ocupado.get("inicio");
                LocalDateTime finOcupado = ocupado.get("fin");
                
                // El horario está ocupado si se solapa con alguna cita
                if ((horaActual.isBefore(finOcupado) && finSlot.isAfter(inicioOcupado))) {
                    disponible = false;
                    break;
                }
            }
            
            if (disponible) {
                horariosDisponibles.add(horaActual);
            }
            
            horaActual = horaActual.plusMinutes(intervaloMinutos);
        }
        
        // Preparar respuesta
        Map<String, Object> resultado = new HashMap<>();
        resultado.put("veterinarianId", veterinarianId);
        resultado.put("veterinarianName", veterinarian.getName());
        resultado.put("fecha", fecha);
        resultado.put("citasProgramadas", citasDelDia.size());
        resultado.put("horariosDisponibles", horariosDisponibles);
        resultado.put("horariosOcupados", horariosOcupados);
        
        return resultado;
    }

    /**
     * Obtiene información de una cita para pre-cargar en la factura/carrito de compras
     * Este método devuelve información del cliente (dueño de la mascota), la mascota, y el servicio
     */
    public AppointmentInvoiceDataDto getAppointmentForInvoice(Integer appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> new RuntimeException("Cita no encontrada con ID: " + appointmentId));
        
        AppointmentInvoiceDataDto dto = new AppointmentInvoiceDataDto();
        dto.setAppointmentId(appointment.getAppointmentId());
        dto.setClientId(appointment.getClient().getClientId());
        dto.setClientName(appointment.getClient().getName());
        dto.setClientIdent(appointment.getClient().getIdent());
        dto.setPetId(appointment.getPet().getPetId());
        dto.setPetName(appointment.getPet().getNombre());
        dto.setServiceId(appointment.getService().getServiceId());
        dto.setServiceName(appointment.getService().getNombre());
        dto.setServicePrice(appointment.getService().getPrecio());
        dto.setEstado(appointment.getEstado());
        
        return dto;
    }

    private AppointmentResponseDto mapToResponseDto(Appointment appointment) {
        AppointmentResponseDto dto = new AppointmentResponseDto();
        dto.setAppointmentId(appointment.getAppointmentId());
        dto.setPetId(appointment.getPet().getPetId());
        dto.setPetNombre(appointment.getPet().getNombre());
        dto.setServiceId(appointment.getService().getServiceId());
        dto.setServiceName(appointment.getService().getNombre());
        dto.setClientId(appointment.getClient().getClientId());
        dto.setClientName(appointment.getClient().getName());
        
        if (appointment.getVeterinarian() != null) {
            dto.setVeterinarianId(appointment.getVeterinarian().getUserId());
            dto.setVeterinarianName(appointment.getVeterinarian().getName());
        }
        
        dto.setFechaHora(appointment.getFechaHora());
        dto.setEstado(appointment.getEstado());
        dto.setObservaciones(appointment.getObservaciones());
        dto.setDiagnostico(appointment.getDiagnostico());
        dto.setActivo(appointment.getActivo());
        dto.setCreatedOn(appointment.getCreatedOn());
        
        return dto;
    }

    @Transactional
    public AppointmentResponseDto markAsInvoiced(Integer appointmentId) {
        System.out.println("🟢 SERVICE - Buscando appointmentId: " + appointmentId);
        
        Appointment appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow(() -> {
                System.out.println("🔴 SERVICE - NO ENCONTRADA appointmentId: " + appointmentId);
                return new RuntimeException("Cita no encontrada con ID: " + appointmentId);
            });
        
        System.out.println("🟢 SERVICE - Encontrada. Estado ANTES: " + appointment.getEstado());
        
        appointment.setEstado("FACTURADA");
        System.out.println("🟢 SERVICE - Estado cambiado a: " + appointment.getEstado());
        
        Appointment updated = appointmentRepository.save(appointment);
        System.out.println("🟢 SERVICE - Guardado. Estado DESPUÉS del save: " + updated.getEstado());
        
        // Verificar que realmente se guardó
        Appointment verificacion = appointmentRepository.findById(appointmentId).get();
        System.out.println("🟢 SERVICE - VERIFICACIÓN - Estado en BD: " + verificacion.getEstado());
        
        AppointmentResponseDto dto = mapToResponseDto(updated);
        System.out.println("🟢 SERVICE - DTO estado: " + dto.getEstado());
        
        return dto;
    }
}

