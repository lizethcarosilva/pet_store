package com.cipasuno.petstore.pet_store.services;

import com.cipasuno.petstore.pet_store.config.TenantContext;
import com.cipasuno.petstore.pet_store.models.Client;
import com.cipasuno.petstore.pet_store.models.Pet;
import com.cipasuno.petstore.pet_store.models.PetMedicalHistory;
import com.cipasuno.petstore.pet_store.models.PetOwner;
import com.cipasuno.petstore.pet_store.models.User;
import com.cipasuno.petstore.pet_store.models.Vaccination;
import com.cipasuno.petstore.pet_store.models.DTOs.VaccinationCreateDto;
import com.cipasuno.petstore.pet_store.models.DTOs.VaccinationInvoiceDataDto;
import com.cipasuno.petstore.pet_store.models.DTOs.VaccinationResponseDto;
import com.cipasuno.petstore.pet_store.repositories.PetMedicalHistoryRepository;
import com.cipasuno.petstore.pet_store.repositories.PetOwnerRepository;
import com.cipasuno.petstore.pet_store.repositories.PetRepository;
import com.cipasuno.petstore.pet_store.repositories.ProductRepository;
import com.cipasuno.petstore.pet_store.repositories.UserRepository;
import com.cipasuno.petstore.pet_store.repositories.VaccinationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
public class VaccinationService {

    @Autowired
    private VaccinationRepository vaccinationRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private PetMedicalHistoryRepository medicalHistoryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PetOwnerRepository petOwnerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public VaccinationResponseDto createVaccination(VaccinationCreateDto vaccinationDto, String tenantId) {
        // Validar mascota
        Pet pet = petRepository.findById(vaccinationDto.getPetId())
            .orElseThrow(() -> new RuntimeException("Mascota no encontrada con ID: " + vaccinationDto.getPetId()));

        // Validar veterinario
        User veterinarian = userRepository.findById(vaccinationDto.getVeterinarianId())
            .orElseThrow(() -> new RuntimeException("Veterinario no encontrado con ID: " + vaccinationDto.getVeterinarianId()));

        // Validar historial médico si se proporciona
        PetMedicalHistory medicalHistory = null;
        if (vaccinationDto.getMedicalHistoryId() != null) {
            medicalHistory = medicalHistoryRepository.findById(vaccinationDto.getMedicalHistoryId())
                .orElseThrow(() -> new RuntimeException("Historial médico no encontrado con ID: " + vaccinationDto.getMedicalHistoryId()));
            
            // Validar que el historial médico pertenece a la misma mascota
            if (!medicalHistory.getPet().getPetId().equals(vaccinationDto.getPetId())) {
                throw new RuntimeException("El historial médico (ID: " + vaccinationDto.getMedicalHistoryId() + 
                    ") pertenece a la mascota ID: " + medicalHistory.getPet().getPetId() + 
                    ", pero está intentando asociarlo con la mascota ID: " + vaccinationDto.getPetId());
            }
        }

        Vaccination vaccination = new Vaccination();
        vaccination.setTenantId(tenantId);
        vaccination.setPet(pet);
        vaccination.setMedicalHistory(medicalHistory);
        vaccination.setVeterinarian(veterinarian);
        vaccination.setVaccineName(vaccinationDto.getVaccineName());
        vaccination.setVaccineType(vaccinationDto.getVaccineType());
        vaccination.setManufacturer(vaccinationDto.getManufacturer());
        vaccination.setBatchNumber(vaccinationDto.getBatchNumber());
        vaccination.setApplicationDate(vaccinationDto.getApplicationDate() != null ? 
            vaccinationDto.getApplicationDate() : LocalDate.now());
        vaccination.setNextDoseDate(vaccinationDto.getNextDoseDate());
        vaccination.setDoseNumber(vaccinationDto.getDoseNumber());
        vaccination.setApplicationSite(vaccinationDto.getApplicationSite());
        vaccination.setObservations(vaccinationDto.getObservations());
        vaccination.setRequiresBooster(vaccinationDto.getRequiresBooster() != null ? 
            vaccinationDto.getRequiresBooster() : false);
        vaccination.setIsCompleted(vaccinationDto.getIsCompleted() != null ? 
            vaccinationDto.getIsCompleted() : false);

        Vaccination savedVaccination = vaccinationRepository.save(vaccination);
        return mapToResponseDto(savedVaccination);
    }

    public List<VaccinationResponseDto> getAllVaccinations() {
        return vaccinationRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<VaccinationResponseDto> getVaccinationsByTenant(String tenantId) {
        return vaccinationRepository.findByTenantId(tenantId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<VaccinationResponseDto> getVaccinationsByPetId(Integer petId) {
        List<Vaccination> vaccinations = vaccinationRepository.findByPetId(petId);
        System.out.println("📋 getVaccinationsByPetId - Total vacunaciones para petId " + petId + ": " + vaccinations.size());
        vaccinations.forEach(v -> System.out.println("   - ID: " + v.getVaccinationId() + ", Estado: " + v.getEstado()));
        
        return vaccinations.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<VaccinationResponseDto> getVaccinationsByPetIdAndTenant(Integer petId, String tenantId) {
        return vaccinationRepository.findByPetIdAndTenantId(petId, tenantId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<VaccinationResponseDto> getVaccinationsByMedicalHistoryId(Integer medicalHistoryId) {
        return vaccinationRepository.findByMedicalHistoryId(medicalHistoryId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<VaccinationResponseDto> getVaccinationsByVeterinarianId(Integer veterinarianId) {
        return vaccinationRepository.findByVeterinarianId(veterinarianId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<VaccinationResponseDto> getVaccinationsByPetIdAndVaccineName(Integer petId, String vaccineName) {
        return vaccinationRepository.findByPetIdAndVaccineName(petId, vaccineName)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<VaccinationResponseDto> getUpcomingVaccinations(LocalDate startDate, LocalDate endDate) {
        return vaccinationRepository.findUpcomingVaccinations(startDate, endDate)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<VaccinationResponseDto> getOverdueVaccinationsByPetId(Integer petId) {
        return vaccinationRepository.findOverdueVaccinationsByPetId(petId, LocalDate.now())
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<VaccinationResponseDto> getPendingVaccinationsByPetId(Integer petId) {
        return vaccinationRepository.findPendingVaccinationsByPetId(petId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public Optional<VaccinationResponseDto> getVaccinationById(Integer vaccinationId) {
        return vaccinationRepository.findById(vaccinationId)
                .map(this::mapToResponseDto);
    }

    public long countVaccinationsByPetId(Integer petId) {
        return vaccinationRepository.countVaccinationsByPetId(petId);
    }

    @Transactional
    public VaccinationResponseDto updateVaccination(Integer vaccinationId, VaccinationCreateDto vaccinationDto, String tenantId) {
        Vaccination vaccination = vaccinationRepository.findById(vaccinationId)
            .orElseThrow(() -> new RuntimeException("Vacunación no encontrada con ID: " + vaccinationId));

        // Validar tenant
        if (!vaccination.getTenantId().equals(tenantId)) {
            throw new RuntimeException("La vacunación no pertenece al tenant actual");
        }

        // Actualizar campos
        if (vaccinationDto.getVeterinarianId() != null) {
            User veterinarian = userRepository.findById(vaccinationDto.getVeterinarianId())
                .orElseThrow(() -> new RuntimeException("Veterinario no encontrado con ID: " + vaccinationDto.getVeterinarianId()));
            vaccination.setVeterinarian(veterinarian);
        }

        if (vaccinationDto.getVaccineName() != null) {
            vaccination.setVaccineName(vaccinationDto.getVaccineName());
        }

        if (vaccinationDto.getVaccineType() != null) {
            vaccination.setVaccineType(vaccinationDto.getVaccineType());
        }

        if (vaccinationDto.getManufacturer() != null) {
            vaccination.setManufacturer(vaccinationDto.getManufacturer());
        }

        if (vaccinationDto.getBatchNumber() != null) {
            vaccination.setBatchNumber(vaccinationDto.getBatchNumber());
        }

        if (vaccinationDto.getApplicationDate() != null) {
            vaccination.setApplicationDate(vaccinationDto.getApplicationDate());
        }

        if (vaccinationDto.getNextDoseDate() != null) {
            vaccination.setNextDoseDate(vaccinationDto.getNextDoseDate());
        }

        if (vaccinationDto.getDoseNumber() != null) {
            vaccination.setDoseNumber(vaccinationDto.getDoseNumber());
        }

        if (vaccinationDto.getApplicationSite() != null) {
            vaccination.setApplicationSite(vaccinationDto.getApplicationSite());
        }

        if (vaccinationDto.getObservations() != null) {
            vaccination.setObservations(vaccinationDto.getObservations());
        }

        if (vaccinationDto.getRequiresBooster() != null) {
            vaccination.setRequiresBooster(vaccinationDto.getRequiresBooster());
        }

        if (vaccinationDto.getIsCompleted() != null) {
            vaccination.setIsCompleted(vaccinationDto.getIsCompleted());
        }

        Vaccination updatedVaccination = vaccinationRepository.save(vaccination);
        return mapToResponseDto(updatedVaccination);
    }

    @Transactional
    public void deleteVaccination(Integer vaccinationId, String tenantId) {
        Vaccination vaccination = vaccinationRepository.findById(vaccinationId)
            .orElseThrow(() -> new RuntimeException("Vacunación no encontrada con ID: " + vaccinationId));

        if (!vaccination.getTenantId().equals(tenantId)) {
            throw new RuntimeException("La vacunación no pertenece al tenant actual");
        }

        vaccination.setActivo(false);
        vaccinationRepository.save(vaccination);
    }

    @Transactional
    public void deleteVaccinationPermanently(Integer vaccinationId, String tenantId) {
        Vaccination vaccination = vaccinationRepository.findById(vaccinationId)
            .orElseThrow(() -> new RuntimeException("Vacunación no encontrada con ID: " + vaccinationId));

        if (!vaccination.getTenantId().equals(tenantId)) {
            throw new RuntimeException("La vacunación no pertenece al tenant actual");
        }

        vaccinationRepository.delete(vaccination);
    }

    private VaccinationResponseDto mapToResponseDto(Vaccination vaccination) {
        VaccinationResponseDto dto = new VaccinationResponseDto();
        dto.setVaccinationId(vaccination.getVaccinationId());
        dto.setPetId(vaccination.getPet().getPetId());
        dto.setPetNombre(vaccination.getPet().getNombre());
        
        if (vaccination.getMedicalHistory() != null) {
            dto.setMedicalHistoryId(vaccination.getMedicalHistory().getHistoryId());
        }
        
        dto.setVeterinarianId(vaccination.getVeterinarian().getUserId());
        dto.setVeterinarianName(vaccination.getVeterinarian().getName());
        dto.setVaccineName(vaccination.getVaccineName());
        dto.setVaccineType(vaccination.getVaccineType());
        dto.setManufacturer(vaccination.getManufacturer());
        dto.setBatchNumber(vaccination.getBatchNumber());
        dto.setApplicationDate(vaccination.getApplicationDate());
        dto.setNextDoseDate(vaccination.getNextDoseDate());
        dto.setDoseNumber(vaccination.getDoseNumber());
        dto.setApplicationSite(vaccination.getApplicationSite());
        dto.setObservations(vaccination.getObservations());
        dto.setRequiresBooster(vaccination.getRequiresBooster());
        dto.setIsCompleted(vaccination.getIsCompleted());
        dto.setEstado(vaccination.getEstado());
        dto.setActivo(vaccination.getActivo());
        dto.setCreatedOn(vaccination.getCreatedOn());
        
        return dto;
    }

    /**
     * Obtiene información de una vacunación para pre-cargar en la factura/carrito de compras
     * Este método devuelve información del cliente (dueño de la mascota), la mascota, y la vacuna
     */
    public VaccinationInvoiceDataDto getVaccinationForInvoice(Integer vaccinationId) {
        Vaccination vaccination = vaccinationRepository.findById(vaccinationId)
            .orElseThrow(() -> new RuntimeException("Vacunación no encontrada con ID: " + vaccinationId));
        
        // Obtener el primer propietario de la mascota
        List<PetOwner> petOwners = petOwnerRepository.findByPetId(vaccination.getPet().getPetId());
        if (petOwners.isEmpty()) {
            throw new RuntimeException("La mascota no tiene propietarios registrados");
        }
        
        Client client = petOwners.get(0).getClient(); // Primer propietario
        
        VaccinationInvoiceDataDto dto = new VaccinationInvoiceDataDto();
        dto.setVaccinationId(vaccination.getVaccinationId());
        dto.setClientId(client.getClientId());
        dto.setClientName(client.getName());
        dto.setClientIdent(client.getIdent());
        dto.setPetId(vaccination.getPet().getPetId());
        dto.setPetName(vaccination.getPet().getNombre());
        dto.setVaccineName(vaccination.getVaccineName());
        dto.setVaccineType(vaccination.getVaccineType());
        dto.setManufacturer(vaccination.getManufacturer());
        dto.setBatchNumber(vaccination.getBatchNumber());
        
        if (vaccination.getMedicalHistory() != null) {
            dto.setMedicalHistoryId(vaccination.getMedicalHistory().getHistoryId());
        }
        
        // Intentar encontrar el producto de la vacuna en el inventario
        // Buscar por nombre de vacuna, fabricante y lote
        productRepository.findAll().stream()
            .filter(p -> p.getEsVacuna() != null && p.getEsVacuna())
            .filter(p -> p.getNombre().equalsIgnoreCase(vaccination.getVaccineName()) 
                || (p.getLote() != null && p.getLote().equals(vaccination.getBatchNumber())))
            .findFirst()
            .ifPresent(product -> {
                dto.setProductId(product.getProductId());
                dto.setProductName(product.getNombre());
                dto.setProductPrice(product.getPrecio());
            });
        
        return dto;
    }

    /**
     * Obtiene vacunaciones COMPLETADAS (no facturadas) de una mascota
     * Este método filtra solo las vacunaciones con estado "COMPLETADA", "Completo" o "APLICADA"
     * EXCLUYENDO las que ya fueron facturadas
     */
    @Transactional(readOnly = true)
    public List<VaccinationResponseDto> getCompletedVaccinationsByPet(Integer petId) {
        System.out.println("🔍 Buscando vacunaciones COMPLETADAS (sin facturar) para petId: " + petId);
        
        String tenantIdStr = TenantContext.getTenantId();
        
        // Obtener TODAS las vacunaciones activas de la mascota
        List<Vaccination> allVaccinations = vaccinationRepository
            .findByPetIdAndTenantIdAndActivo(petId, tenantIdStr, true);
        
        System.out.println("📋 Total vacunaciones encontradas: " + allVaccinations.size());
        
        // Filtrar SOLO las que están "Completada", "Completo" o "APLICADA"
        // EXCLUIR "FACTURADA", "FACTURADO", "Pendiente", "Cancelada", etc.
        List<Vaccination> completedOnly = allVaccinations.stream()
            .filter(v -> {
                String estado = v.getEstado();
                System.out.println("   - ID " + v.getVaccinationId() + ": estado = '" + estado + "'");
                
                // Solo incluir si el estado es "Completada", "Completo" o "APLICADA"
                // NO incluir "FACTURADA" ni ningún otro estado
                return "Completada".equalsIgnoreCase(estado) ||
                       "Completo".equalsIgnoreCase(estado) ||
                       "APLICADA".equalsIgnoreCase(estado);
            })
            .collect(Collectors.toList());
        
        System.out.println("✅ Vacunaciones COMPLETADAS (sin facturar): " + completedOnly.size());
        
        return completedOnly.stream()
            .map(this::mapToResponseDto)
            .collect(Collectors.toList());
    }

    @Transactional
    public VaccinationResponseDto markAsInvoiced(Integer vaccinationId) {
        System.out.println("🟢 SERVICE - Buscando vaccinationId: " + vaccinationId);
        
        Vaccination vaccination = vaccinationRepository.findById(vaccinationId)
            .orElseThrow(() -> {
                System.out.println("🔴 SERVICE - NO ENCONTRADA vaccinationId: " + vaccinationId);
                return new RuntimeException("Vacunación no encontrada con ID: " + vaccinationId);
            });
        
        System.out.println("🟢 SERVICE - Encontrada. Estado ANTES: " + vaccination.getEstado());
        
        vaccination.setEstado("FACTURADA");
        System.out.println("🟢 SERVICE - Estado cambiado a: " + vaccination.getEstado());
        
        Vaccination updated = vaccinationRepository.save(vaccination);
        System.out.println("🟢 SERVICE - Guardado. Estado DESPUÉS del save: " + updated.getEstado());
        
        // Verificar que realmente se guardó
        Vaccination verificacion = vaccinationRepository.findById(vaccinationId).get();
        System.out.println("🟢 SERVICE - VERIFICACIÓN - Estado en BD: " + verificacion.getEstado());
        
        VaccinationResponseDto dto = mapToResponseDto(updated);
        System.out.println("🟢 SERVICE - DTO estado: " + dto.getEstado());
        
        return dto;
    }
}

