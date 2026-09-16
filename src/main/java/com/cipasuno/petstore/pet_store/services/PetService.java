package com.cipasuno.petstore.pet_store.services;

import com.cipasuno.petstore.pet_store.models.Client;
import com.cipasuno.petstore.pet_store.models.Pet;
import com.cipasuno.petstore.pet_store.models.PetOwner;
import com.cipasuno.petstore.pet_store.models.DTOs.OwnerInfoDto;
import com.cipasuno.petstore.pet_store.models.DTOs.PetCreateDto;
import com.cipasuno.petstore.pet_store.models.DTOs.PetResponseDto;
import com.cipasuno.petstore.pet_store.models.DTOs.UpdatePetRequest;
import com.cipasuno.petstore.pet_store.repositories.AppointmentRepository;
import com.cipasuno.petstore.pet_store.repositories.ClientRepository;
import com.cipasuno.petstore.pet_store.repositories.PetOwnerRepository;
import com.cipasuno.petstore.pet_store.repositories.PetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PetService {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private PetOwnerRepository petOwnerRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

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

        // Crear relaciones con los dueños (ahora usando Client)
        if (petDto.getOwnerIds() != null && !petDto.getOwnerIds().isEmpty()) {
            for (Integer clientId : petDto.getOwnerIds()) {
                Client client = clientRepository.findById(clientId)
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + clientId));
                
                PetOwner petOwner = new PetOwner();
                petOwner.setPet(savedPet);
                petOwner.setClient(client);
                petOwner.getId().setPetId(savedPet.getPetId());
                petOwner.getId().setClientId(clientId);
                
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

        // Crear relaciones con los dueños (ahora usando Client)
        if (petDto.getOwnerIds() != null && !petDto.getOwnerIds().isEmpty()) {
            for (Integer clientId : petDto.getOwnerIds()) {
                Client client = clientRepository.findById(clientId)
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + clientId));
                
                PetOwner petOwner = new PetOwner();
                petOwner.setPet(savedPet);
                petOwner.setClient(client);
                petOwner.getId().setPetId(savedPet.getPetId());
                petOwner.getId().setClientId(clientId);
                
                petOwnerRepository.save(petOwner);
            }
        }

        return mapToResponseDto(savedPet);
    }

    public List<PetResponseDto> getAllPets() {
        List<Pet> pets = petRepository.findAll();
        Map<Integer, List<OwnerInfoDto>> ownersByPet = buildOwnersMapByPetIds(pets);
        return pets.stream()
                .map(p -> mapToResponseDto(p, ownersByPet))
                .collect(Collectors.toList());
    }

    public List<PetResponseDto> getAllPetsByTenant(String tenantId) {
        List<Pet> pets = petRepository.findAllByTenantId(tenantId);
        Map<Integer, List<OwnerInfoDto>> ownersByPet = buildOwnersMapByPetIds(pets);
        return pets.stream()
                .map(p -> mapToResponseDto(p, ownersByPet))
                .collect(Collectors.toList());
    }

    public List<PetResponseDto> getActivePets() {
        List<Pet> pets = petRepository.findByActivoTrue();
        Map<Integer, List<OwnerInfoDto>> ownersByPet = buildOwnersMapByPetIds(pets);
        return pets.stream()
                .map(p -> mapToResponseDto(p, ownersByPet))
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
        List<Pet> pets = petRepository.findByNombreContainingIgnoreCase(nombre);
        Map<Integer, List<OwnerInfoDto>> ownersByPet = buildOwnersMapByPetIds(pets);
        return pets.stream()
                .map(p -> mapToResponseDto(p, ownersByPet))
                .collect(Collectors.toList());
    }

    public List<PetResponseDto> getPetsByType(String tipo) {
        List<Pet> pets = petRepository.findByTipo(tipo);
        Map<Integer, List<OwnerInfoDto>> ownersByPet = buildOwnersMapByPetIds(pets);
        return pets.stream()
                .map(p -> mapToResponseDto(p, ownersByPet))
                .collect(Collectors.toList());
    }

    public List<PetResponseDto> getPetsByOwnerId(Integer ownerId) {
        List<Pet> pets = petRepository.findByOwnerId(ownerId);
        Map<Integer, List<OwnerInfoDto>> ownersByPet = buildOwnersMapByPetIds(pets);
        return pets.stream()
                .map(p -> mapToResponseDto(p, ownersByPet))
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
    public void addOwnerToPet(Integer petId, Integer clientId) {
        Pet pet = petRepository.findById(petId)
            .orElseThrow(() -> new RuntimeException("Mascota no encontrada con ID: " + petId));
        
        Client client = clientRepository.findById(clientId)
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + clientId));
        
        PetOwner petOwner = new PetOwner();
        petOwner.setPet(pet);
        petOwner.setClient(client);
        petOwner.getId().setPetId(petId);
        petOwner.getId().setClientId(clientId);
        
        petOwnerRepository.save(petOwner);
    }

    @Transactional
    public void removeOwnerFromPet(Integer petId, Integer clientId) {
        PetOwner.PetOwnerId id = new PetOwner.PetOwnerId(petId, clientId);
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

    /**
     * Obtiene todos los propietarios de una mascota por su ID
     */
    public List<OwnerInfoDto> getOwnersByPetId(Integer petId) {
        if (!petRepository.existsById(petId)) {
            throw new RuntimeException("Mascota no encontrada con ID: " + petId);
        }
        List<OwnerInfoDto> fromPo = loadOwnersFromPetOwnerRows(Collections.singletonList(petId));
        if (!fromPo.isEmpty()) {
            return fromPo;
        }
        return loadOwnersFromAppointmentFallback(Collections.singletonList(petId));
    }

    private List<OwnerInfoDto> loadOwnersFromPetOwnerRows(List<Integer> petIds) {
        if (petIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<Object[]> rows = petOwnerRepository.findOwnerDisplayRowsByPetIds(petIds);
        return rows.stream()
                .map(this::ownerRowToDto)
                .filter(this::isOwnerDtoMeaningful)
                .collect(Collectors.toList());
    }

    private List<OwnerInfoDto> loadOwnersFromAppointmentFallback(List<Integer> petIds) {
        if (petIds.isEmpty()) {
            return Collections.emptyList();
        }
        try {
            List<Object[]> rows = appointmentRepository.findLatestClientRowByPetIds(petIds);
            return rows.stream()
                    .map(this::ownerRowToDto)
                    .filter(this::isOwnerDtoMeaningful)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    private OwnerInfoDto ownerRowToDto(Object[] r) {
        OwnerInfoDto d = new OwnerInfoDto();
        Integer cid = r.length > 1 ? numberToInt(r[1]) : null;
        Integer uid = r.length > 2 ? numberToInt(r[2]) : null;
        d.setUserId(cid != null ? cid : uid);
        d.setIdent(strColumn(r.length > 3 ? r[3] : null));
        d.setName(strColumn(r.length > 4 ? r[4] : null));
        d.setTelefono(strColumn(r.length > 5 ? r[5] : null));
        d.setCorreo(strColumn(r.length > 6 ? r[6] : null));
        return d;
    }

    private static Integer numberToInt(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof Number) {
            return ((Number) o).intValue();
        }
        return null;
    }

    private static String strColumn(Object o) {
        return o == null ? "" : o.toString().trim();
    }

    private boolean isOwnerDtoMeaningful(OwnerInfoDto d) {
        if (d == null) {
            return false;
        }
        if (d.getUserId() != null) {
            return true;
        }
        return !d.getIdent().isEmpty() || !d.getName().isEmpty() || !d.getTelefono().isEmpty();
    }

    /**
     * Dueños desde pet_owner (client y/o user legacy) y, si falta, último cliente en cita.
     */
    private Map<Integer, List<OwnerInfoDto>> buildOwnersMapByPetIds(List<Pet> pets) {
        if (pets == null || pets.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Integer> petIds = pets.stream()
                .map(Pet::getPetId)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
        if (petIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Object[]> rows = petOwnerRepository.findOwnerDisplayRowsByPetIds(petIds);
        Map<Integer, List<OwnerInfoDto>> map = new HashMap<>();
        for (Object[] r : rows) {
            if (r == null || r.length < 7 || r[0] == null) {
                continue;
            }
            OwnerInfoDto dto = ownerRowToDto(r);
            if (!isOwnerDtoMeaningful(dto)) {
                continue;
            }
            int pid = ((Number) r[0]).intValue();
            map.computeIfAbsent(pid, k -> new ArrayList<>()).add(dto);
        }
        List<Integer> stillMissing = petIds.stream()
                .filter(id -> !map.containsKey(id) || map.get(id).isEmpty())
                .collect(Collectors.toList());
        if (!stillMissing.isEmpty()) {
            try {
                List<Object[]> fb = appointmentRepository.findLatestClientRowByPetIds(stillMissing);
                for (Object[] r : fb) {
                    if (r == null || r.length < 7 || r[0] == null) {
                        continue;
                    }
                    OwnerInfoDto dto = ownerRowToDto(r);
                    if (!isOwnerDtoMeaningful(dto)) {
                        continue;
                    }
                    int pid = ((Number) r[0]).intValue();
                    map.computeIfAbsent(pid, k -> new ArrayList<>()).add(dto);
                }
            } catch (Exception ignored) {
                // DISTINCT ON / sintaxis solo PostgreSQL
            }
        }
        return map;
    }

    private PetResponseDto mapToResponseDto(Pet pet) {
        PetResponseDto dto = mapToResponseDtoWithoutOwners(pet);
        if (pet.getPetId() == null) {
            dto.setOwners(new ArrayList<>());
        } else {
            dto.setOwners(getOwnersByPetId(pet.getPetId()));
        }
        return dto;
    }

    private PetResponseDto mapToResponseDto(Pet pet, Map<Integer, List<OwnerInfoDto>> ownersByPetId) {
        PetResponseDto dto = mapToResponseDtoWithoutOwners(pet);
        List<OwnerInfoDto> owners = ownersByPetId.getOrDefault(pet.getPetId(), Collections.emptyList());
        dto.setOwners(new ArrayList<>(owners));
        return dto;
    }

    private PetResponseDto mapToResponseDtoWithoutOwners(Pet pet) {
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
        return dto;
    }
}

