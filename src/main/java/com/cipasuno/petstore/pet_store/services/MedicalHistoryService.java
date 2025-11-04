package com.cipasuno.petstore.pet_store.services;

import com.cipasuno.petstore.pet_store.models.Appointment;
import com.cipasuno.petstore.pet_store.models.Pet;
import com.cipasuno.petstore.pet_store.models.PetMedicalHistory;
import com.cipasuno.petstore.pet_store.models.Service;
import com.cipasuno.petstore.pet_store.models.User;
import com.cipasuno.petstore.pet_store.models.DTOs.MedicalHistoryCreateDto;
import com.cipasuno.petstore.pet_store.models.DTOs.MedicalHistoryResponseDto;
import com.cipasuno.petstore.pet_store.repositories.AppointmentRepository;
import com.cipasuno.petstore.pet_store.repositories.PetMedicalHistoryRepository;
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
public class MedicalHistoryService {

    @Autowired
    private PetMedicalHistoryRepository medicalHistoryRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public MedicalHistoryResponseDto createMedicalHistory(MedicalHistoryCreateDto historyDto, String tenantId) {
        // Validar mascota
        Pet pet = petRepository.findById(historyDto.getPetId())
            .orElseThrow(() -> new RuntimeException("Mascota no encontrada con ID: " + historyDto.getPetId()));

        // Validar servicio
        Service service = serviceRepository.findById(historyDto.getServiceId())
            .orElseThrow(() -> new RuntimeException("Servicio no encontrado con ID: " + historyDto.getServiceId()));

        // Validar veterinario
        User veterinarian = userRepository.findById(historyDto.getVeterinarianId())
            .orElseThrow(() -> new RuntimeException("Veterinario no encontrado con ID: " + historyDto.getVeterinarianId()));

        // Validar cita si se proporciona
        Appointment appointment = null;
        if (historyDto.getAppointmentId() != null) {
            appointment = appointmentRepository.findById(historyDto.getAppointmentId())
                .orElseThrow(() -> new RuntimeException("Cita no encontrada con ID: " + historyDto.getAppointmentId()));
            
            // Validar que la cita pertenece a la misma mascota
            if (!appointment.getPet().getPetId().equals(historyDto.getPetId())) {
                throw new RuntimeException("La cita no pertenece a la mascota especificada");
            }
        }

        PetMedicalHistory history = new PetMedicalHistory();
        history.setTenantId(tenantId);
        history.setPet(pet);
        history.setAppointment(appointment);
        history.setService(service);
        history.setVeterinarian(veterinarian);
        history.setFechaAtencion(historyDto.getFechaAtencion() != null ? historyDto.getFechaAtencion() : LocalDateTime.now());
        history.setTipoProcedimiento(historyDto.getTipoProcedimiento());
        history.setDiagnostico(historyDto.getDiagnostico());
        history.setObservaciones(historyDto.getObservaciones());
        history.setTratamiento(historyDto.getTratamiento());
        history.setPesoKg(historyDto.getPesoKg());
        history.setTemperaturaC(historyDto.getTemperaturaC());
        history.setNotasAdicionales(historyDto.getNotasAdicionales());

        PetMedicalHistory savedHistory = medicalHistoryRepository.save(history);
        
        // Si hay una cita relacionada, actualizar su estado a COMPLETADA y agregar diagnóstico
        if (appointment != null) {
            appointment.setEstado("COMPLETADA");
            if (historyDto.getDiagnostico() != null && !historyDto.getDiagnostico().isEmpty()) {
                appointment.setDiagnostico(historyDto.getDiagnostico());
            }
            appointmentRepository.save(appointment);
        }

        return mapToResponseDto(savedHistory);
    }

    public List<MedicalHistoryResponseDto> getAllMedicalHistories() {
        return medicalHistoryRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<MedicalHistoryResponseDto> getMedicalHistoriesByTenant(String tenantId) {
        return medicalHistoryRepository.findByTenantId(tenantId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<MedicalHistoryResponseDto> getMedicalHistoriesByPetId(Integer petId) {
        return medicalHistoryRepository.findByPetId(petId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<MedicalHistoryResponseDto> getMedicalHistoriesByPetIdAndTenant(Integer petId, String tenantId) {
        return medicalHistoryRepository.findByPetIdAndTenantId(petId, tenantId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<MedicalHistoryResponseDto> getMedicalHistoriesByAppointmentId(Integer appointmentId) {
        return medicalHistoryRepository.findByAppointmentId(appointmentId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<MedicalHistoryResponseDto> getMedicalHistoriesByVeterinarianId(Integer veterinarianId) {
        return medicalHistoryRepository.findByVeterinarianId(veterinarianId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<MedicalHistoryResponseDto> getMedicalHistoriesByServiceId(Integer serviceId) {
        return medicalHistoryRepository.findByServiceId(serviceId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<MedicalHistoryResponseDto> getMedicalHistoriesByPetIdAndTipoProcedimiento(Integer petId, String tipoProcedimiento) {
        return medicalHistoryRepository.findByPetIdAndTipoProcedimiento(petId, tipoProcedimiento)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<MedicalHistoryResponseDto> getMedicalHistoriesByPetIdAndDateRange(Integer petId, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return medicalHistoryRepository.findByPetIdAndDateRange(petId, fechaInicio, fechaFin)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public Optional<MedicalHistoryResponseDto> getMedicalHistoryById(Integer historyId) {
        return medicalHistoryRepository.findById(historyId)
                .map(this::mapToResponseDto);
    }

    @Transactional
    public MedicalHistoryResponseDto updateMedicalHistory(Integer historyId, MedicalHistoryCreateDto historyDto, String tenantId) {
        PetMedicalHistory history = medicalHistoryRepository.findById(historyId)
            .orElseThrow(() -> new RuntimeException("Historial médico no encontrado con ID: " + historyId));

        // Validar tenant
        if (!history.getTenantId().equals(tenantId)) {
            throw new RuntimeException("El historial médico no pertenece al tenant actual");
        }

        // Actualizar campos
        if (historyDto.getServiceId() != null) {
            Service service = serviceRepository.findById(historyDto.getServiceId())
                .orElseThrow(() -> new RuntimeException("Servicio no encontrado con ID: " + historyDto.getServiceId()));
            history.setService(service);
        }

        if (historyDto.getVeterinarianId() != null) {
            User veterinarian = userRepository.findById(historyDto.getVeterinarianId())
                .orElseThrow(() -> new RuntimeException("Veterinario no encontrado con ID: " + historyDto.getVeterinarianId()));
            history.setVeterinarian(veterinarian);
        }

        if (historyDto.getFechaAtencion() != null) {
            history.setFechaAtencion(historyDto.getFechaAtencion());
        }

        if (historyDto.getTipoProcedimiento() != null) {
            history.setTipoProcedimiento(historyDto.getTipoProcedimiento());
        }

        if (historyDto.getDiagnostico() != null) {
            history.setDiagnostico(historyDto.getDiagnostico());
        }

        if (historyDto.getObservaciones() != null) {
            history.setObservaciones(historyDto.getObservaciones());
        }

        if (historyDto.getTratamiento() != null) {
            history.setTratamiento(historyDto.getTratamiento());
        }

        if (historyDto.getPesoKg() != null) {
            history.setPesoKg(historyDto.getPesoKg());
        }

        if (historyDto.getTemperaturaC() != null) {
            history.setTemperaturaC(historyDto.getTemperaturaC());
        }

        if (historyDto.getNotasAdicionales() != null) {
            history.setNotasAdicionales(historyDto.getNotasAdicionales());
        }

        PetMedicalHistory updatedHistory = medicalHistoryRepository.save(history);
        return mapToResponseDto(updatedHistory);
    }

    @Transactional
    public void deleteMedicalHistory(Integer historyId, String tenantId) {
        PetMedicalHistory history = medicalHistoryRepository.findById(historyId)
            .orElseThrow(() -> new RuntimeException("Historial médico no encontrado con ID: " + historyId));

        if (!history.getTenantId().equals(tenantId)) {
            throw new RuntimeException("El historial médico no pertenece al tenant actual");
        }

        history.setActivo(false);
        medicalHistoryRepository.save(history);
    }

    @Transactional
    public void deleteMedicalHistoryPermanently(Integer historyId, String tenantId) {
        PetMedicalHistory history = medicalHistoryRepository.findById(historyId)
            .orElseThrow(() -> new RuntimeException("Historial médico no encontrado con ID: " + historyId));

        if (!history.getTenantId().equals(tenantId)) {
            throw new RuntimeException("El historial médico no pertenece al tenant actual");
        }

        medicalHistoryRepository.delete(history);
    }

    private MedicalHistoryResponseDto mapToResponseDto(PetMedicalHistory history) {
        MedicalHistoryResponseDto dto = new MedicalHistoryResponseDto();
        dto.setHistoryId(history.getHistoryId());
        dto.setPetId(history.getPet().getPetId());
        dto.setPetNombre(history.getPet().getNombre());
        
        if (history.getAppointment() != null) {
            dto.setAppointmentId(history.getAppointment().getAppointmentId());
        }
        
        dto.setServiceId(history.getService().getServiceId());
        dto.setServiceName(history.getService().getNombre());
        dto.setVeterinarianId(history.getVeterinarian().getUserId());
        dto.setVeterinarianName(history.getVeterinarian().getName());
        dto.setFechaAtencion(history.getFechaAtencion());
        dto.setTipoProcedimiento(history.getTipoProcedimiento());
        dto.setDiagnostico(history.getDiagnostico());
        dto.setObservaciones(history.getObservaciones());
        dto.setTratamiento(history.getTratamiento());
        dto.setPesoKg(history.getPesoKg());
        dto.setTemperaturaC(history.getTemperaturaC());
        dto.setNotasAdicionales(history.getNotasAdicionales());
        dto.setActivo(history.getActivo());
        dto.setCreatedOn(history.getCreatedOn());
        
        return dto;
    }
}

