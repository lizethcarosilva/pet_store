package com.cipasuno.petstore.pet_store.services;

import com.cipasuno.petstore.pet_store.models.Pet;
import com.cipasuno.petstore.pet_store.models.PetOwner;
import com.cipasuno.petstore.pet_store.models.User;
import com.cipasuno.petstore.pet_store.models.DTOs.OwnerInfoDto;
import com.cipasuno.petstore.pet_store.models.DTOs.PetCreateDto;
import com.cipasuno.petstore.pet_store.models.DTOs.PetResponseDto;
import com.cipasuno.petstore.pet_store.models.DTOs.UpdatePetRequest;
import com.cipasuno.petstore.pet_store.repositories.PetOwnerRepository;
import com.cipasuno.petstore.pet_store.repositories.PetRepository;
import com.cipasuno.petstore.pet_store.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private PetOwnerRepository petOwnerRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public PetResponseDto createPet(PetCreateDto petDto) {
        Pet pet = new Pet();
        pet.setNombre(petDto.getNombre());
        pet.setTipo(petDto.getTipo());
        pet.setRaza(petDto.getRaza());
        pet.setCuidadosEspeciales(petDto.getCuidadosEspeciales());
        pet.setEdad(petDto.getEdad());
        pet.setSexo(petDto.getSexo());
        pet.setColor(petDto.getColor());

        Pet savedPet = petRepository.save(pet);

        // Crear relaciones con los dueños
        if (petDto.getOwnerIds() != null && !petDto.getOwnerIds().isEmpty()) {
            for (Integer ownerId : petDto.getOwnerIds()) {
                User owner = userRepository.findById(ownerId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + ownerId));
                
                PetOwner petOwner = new PetOwner();
                petOwner.setPet(savedPet);
                petOwner.setUser(owner);
                petOwner.getId().setPetId(savedPet.getPetId());
                petOwner.getId().setUserId(ownerId);
                
                petOwnerRepository.save(petOwner);
            }
        }

        return mapToResponseDto(savedPet);
    }

    @Transactional
    public PetResponseDto createPet(PetCreateDto petDto, String tenantId) {
        Pet pet = new Pet();
        pet.setTenantId(tenantId);
        pet.setNombre(petDto.getNombre());
        pet.setTipo(petDto.getTipo());
        pet.setRaza(petDto.getRaza());
        pet.setCuidadosEspeciales(petDto.getCuidadosEspeciales());
        pet.setEdad(petDto.getEdad());
        pet.setSexo(petDto.getSexo());
        pet.setColor(petDto.getColor());

        Pet savedPet = petRepository.save(pet);

        // Crear relaciones con los dueños
        if (petDto.getOwnerIds() != null && !petDto.getOwnerIds().isEmpty()) {
            for (Integer ownerId : petDto.getOwnerIds()) {
                User owner = userRepository.findById(ownerId)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + ownerId));
                
                PetOwner petOwner = new PetOwner();
                petOwner.setPet(savedPet);
                petOwner.setUser(owner);
                petOwner.getId().setPetId(savedPet.getPetId());
                petOwner.getId().setUserId(ownerId);
                
                petOwnerRepository.save(petOwner);
            }
        }

        return mapToResponseDto(savedPet);
    }

    public List<PetResponseDto> getAllPets() {
        return petRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<PetResponseDto> getAllPetsByTenant(String tenantId) {
        return petRepository.findByTenantId(tenantId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<PetResponseDto> getActivePets() {
        return petRepository.findByActivoTrue()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public Optional<PetResponseDto> getPetById(Integer id) {
        return petRepository.findById(id)
                .map(this::mapToResponseDto);
    }

    public Optional<PetResponseDto> getPetByIdAndTenant(Integer id, String tenantId) {
        return petRepository.findByPetIdAndTenantId(id, tenantId)
                .map(this::mapToResponseDto);
    }

    public List<PetResponseDto> searchPetsByName(String nombre) {
        return petRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<PetResponseDto> getPetsByType(String tipo) {
        return petRepository.findByTipo(tipo)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<PetResponseDto> getPetsByOwnerId(Integer ownerId) {
        return petRepository.findByOwnerId(ownerId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public PetResponseDto updatePet(Integer id, Pet petDetails) {
        Pet pet = petRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Mascota no encontrada con ID: " + id));

        if (petDetails.getNombre() != null) {
            pet.setNombre(petDetails.getNombre());
        }
        if (petDetails.getTipo() != null) {
            pet.setTipo(petDetails.getTipo());
        }
        if (petDetails.getRaza() != null) {
            pet.setRaza(petDetails.getRaza());
        }
        if (petDetails.getCuidadosEspeciales() != null) {
            pet.setCuidadosEspeciales(petDetails.getCuidadosEspeciales());
        }
        if (petDetails.getEdad() != null) {
            pet.setEdad(petDetails.getEdad());
        }
        if (petDetails.getSexo() != null) {
            pet.setSexo(petDetails.getSexo());
        }
        if (petDetails.getColor() != null) {
            pet.setColor(petDetails.getColor());
        }
        if (petDetails.getActivo() != null) {
            pet.setActivo(petDetails.getActivo());
        }

        Pet updatedPet = petRepository.save(pet);
        return mapToResponseDto(updatedPet);
    }

    @Transactional
    public PetResponseDto updatePet(Integer id, UpdatePetRequest petDetails, String tenantId) {
        Pet pet = petRepository.findByPetIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new RuntimeException("Mascota no encontrada con ID: " + id));

        if (petDetails.getNombre() != null) {
            pet.setNombre(petDetails.getNombre());
        }
        if (petDetails.getTipo() != null) {
            pet.setTipo(petDetails.getTipo());
        }
        if (petDetails.getRaza() != null) {
            pet.setRaza(petDetails.getRaza());
        }
        if (petDetails.getCuidadosEspeciales() != null) {
            pet.setCuidadosEspeciales(petDetails.getCuidadosEspeciales());
        }
        if (petDetails.getEdad() != null) {
            pet.setEdad(petDetails.getEdad());
        }
        if (petDetails.getSexo() != null) {
            pet.setSexo(petDetails.getSexo());
        }
        if (petDetails.getColor() != null) {
            pet.setColor(petDetails.getColor());
        }
        if (petDetails.getActivo() != null) {
            pet.setActivo(petDetails.getActivo());
        }

        Pet updatedPet = petRepository.save(pet);
        return mapToResponseDto(updatedPet);
    }

    @Transactional
    public void addOwnerToPet(Integer petId, Integer ownerId) {
        Pet pet = petRepository.findById(petId)
            .orElseThrow(() -> new RuntimeException("Mascota no encontrada con ID: " + petId));
        
        User owner = userRepository.findById(ownerId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + ownerId));
        
        PetOwner petOwner = new PetOwner();
        petOwner.setPet(pet);
        petOwner.setUser(owner);
        petOwner.getId().setPetId(petId);
        petOwner.getId().setUserId(ownerId);
        
        petOwnerRepository.save(petOwner);
    }

    @Transactional
    public void removeOwnerFromPet(Integer petId, Integer ownerId) {
        PetOwner.PetOwnerId id = new PetOwner.PetOwnerId(petId, ownerId);
        petOwnerRepository.deleteById(id);
    }

    @Transactional
    public void deletePet(Integer id) {
        Pet pet = petRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Mascota no encontrada con ID: " + id));
        
        pet.setActivo(false);
        petRepository.save(pet);
    }

    @Transactional
    public void deletePet(Integer id, String tenantId) {
        Pet pet = petRepository.findByPetIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new RuntimeException("Mascota no encontrada con ID: " + id));
        
        pet.setActivo(false);
        petRepository.save(pet);
    }

    @Transactional
    public void deletePetPermanently(Integer id) {
        Pet pet = petRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Mascota no encontrada con ID: " + id));
        
        petRepository.delete(pet);
    }

    @Transactional
    public void deletePetPermanently(Integer id, String tenantId) {
        Pet pet = petRepository.findByPetIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new RuntimeException("Mascota no encontrada con ID: " + id));
        
        petRepository.delete(pet);
    }

    public long countActivePets() {
        return petRepository.countActivePets();
    }

    private PetResponseDto mapToResponseDto(Pet pet) {
        PetResponseDto dto = new PetResponseDto();
        dto.setPetId(pet.getPetId());
        dto.setNombre(pet.getNombre());
        dto.setTipo(pet.getTipo());
        dto.setRaza(pet.getRaza());
        dto.setCuidadosEspeciales(pet.getCuidadosEspeciales());
        dto.setEdad(pet.getEdad());
        dto.setSexo(pet.getSexo());
        dto.setColor(pet.getColor());
        dto.setActivo(pet.getActivo());
        dto.setCreatedOn(pet.getCreatedOn());

        // Mapear dueños
        List<OwnerInfoDto> owners = petOwnerRepository.findByPetId(pet.getPetId())
                .stream()
                .map(po -> {
                    User user = po.getUser();
                    OwnerInfoDto ownerDto = new OwnerInfoDto();
                    ownerDto.setUserId(user.getUserId());
                    ownerDto.setName(user.getName());
                    ownerDto.setIdent(user.getIdent());
                    ownerDto.setTelefono(user.getTelefono());
                    ownerDto.setCorreo(user.getCorreo());
                    return ownerDto;
                })
                .collect(Collectors.toList());
        
        dto.setOwners(owners);
        return dto;
    }
}

