package com.cipasuno.petstore.pet_store.services;

import com.cipasuno.petstore.pet_store.models.Appointment;
import com.cipasuno.petstore.pet_store.models.Pet;
import com.cipasuno.petstore.pet_store.models.Service;
import com.cipasuno.petstore.pet_store.models.User;
import com.cipasuno.petstore.pet_store.models.DTOs.AppointmentCreateDto;
import com.cipasuno.petstore.pet_store.models.DTOs.AppointmentResponseDto;
import com.cipasuno.petstore.pet_store.repositories.AppointmentRepository;
import com.cipasuno.petstore.pet_store.repositories.PetRepository;
import com.cipasuno.petstore.pet_store.repositories.ServiceRepository;
import com.cipasuno.petstore.pet_store.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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
    private UserRepository userRepository;

    @Transactional
    public AppointmentResponseDto createAppointment(AppointmentCreateDto appointmentDto) {
        Appointment appointment = new Appointment();
        
        Pet pet = petRepository.findById(appointmentDto.getPetId())
            .orElseThrow(() -> new RuntimeException("Mascota no encontrada con ID: " + appointmentDto.getPetId()));
        
        Service service = serviceRepository.findById(appointmentDto.getServiceId())
            .orElseThrow(() -> new RuntimeException("Servicio no encontrado con ID: " + appointmentDto.getServiceId()));
        
        User user = userRepository.findById(appointmentDto.getUserId())
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + appointmentDto.getUserId()));
        
        appointment.setPet(pet);
        appointment.setService(service);
        appointment.setUser(user);
        appointment.setFechaHora(appointmentDto.getFechaHora());
        appointment.setObservaciones(appointmentDto.getObservaciones());
        
        if (appointmentDto.getVeterinarianId() != null) {
            User veterinarian = userRepository.findById(appointmentDto.getVeterinarianId())
                .orElseThrow(() -> new RuntimeException("Veterinario no encontrado con ID: " + appointmentDto.getVeterinarianId()));
            appointment.setVeterinarian(veterinarian);
        }

        Appointment savedAppointment = appointmentRepository.save(appointment);
        return mapToResponseDto(savedAppointment);
    }

    public List<AppointmentResponseDto> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<AppointmentResponseDto> getActiveAppointments() {
        return appointmentRepository.findByActivoTrue()
                .stream()
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

    public List<AppointmentResponseDto> getAppointmentsByUserId(Integer userId) {
        return appointmentRepository.findByUserId(userId)
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

    private AppointmentResponseDto mapToResponseDto(Appointment appointment) {
        AppointmentResponseDto dto = new AppointmentResponseDto();
        dto.setAppointmentId(appointment.getAppointmentId());
        dto.setPetId(appointment.getPet().getPetId());
        dto.setPetNombre(appointment.getPet().getNombre());
        dto.setServiceId(appointment.getService().getServiceId());
        dto.setServiceName(appointment.getService().getNombre());
        dto.setUserId(appointment.getUser().getUserId());
        dto.setUserName(appointment.getUser().getName());
        
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
}

