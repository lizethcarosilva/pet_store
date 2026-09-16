package com.cipasuno.petstore.pet_store.services;

import com.cipasuno.petstore.pet_store.models.Pet;
import com.cipasuno.petstore.pet_store.repositories.AppointmentRepository;
import com.cipasuno.petstore.pet_store.repositories.ClientRepository;
import com.cipasuno.petstore.pet_store.repositories.PetOwnerRepository;
import com.cipasuno.petstore.pet_store.repositories.PetRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas unitarias del servicio de mascotas (JUnit 5 + Mockito).
 * Tipo: unitarias | Enfoque: TDD / automatización
 */
@ExtendWith(MockitoExtension.class)
class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private PetOwnerRepository petOwnerRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private PetService petService;

    private Pet mascotaEjemplo(int id, String nombre) {
        Pet pet = new Pet();
        pet.setPetId(id);
        pet.setNombre(nombre);
        pet.setTipo("perro");
        pet.setRaza("Labrador");
        pet.setActivo(true);
        return pet;
    }

    private void mockSinPropietarios(int petId) {
        when(petRepository.existsById(petId)).thenReturn(true);
        when(petOwnerRepository.findOwnerDisplayRowsByPetIds(anyList()))
                .thenReturn(Collections.emptyList());
    }

    @Test
    @DisplayName("getPetById: retorna mascota cuando existe")
    void getPetById_cuandoExiste_retornaDto() {
        Pet pet = mascotaEjemplo(1, "Firulais");
        when(petRepository.findById(1)).thenReturn(Optional.of(pet));
        mockSinPropietarios(1);

        var result = petService.getPetById(1);

        assertTrue(result.isPresent());
        assertEquals("Firulais", result.get().getNombre());
        assertEquals("perro", result.get().getTipo());
    }

    @Test
    @DisplayName("getPetById: vacío cuando no existe")
    void getPetById_cuandoNoExiste_retornaVacio() {
        when(petRepository.findById(99)).thenReturn(Optional.empty());

        var result = petService.getPetById(99);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("deletePet: desactiva mascota (borrado lógico)")
    void deletePet_desactivaMascota() {
        Pet pet = mascotaEjemplo(2, "Michi");
        when(petRepository.findById(2)).thenReturn(Optional.of(pet));

        petService.deletePet(2);

        assertFalse(pet.getActivo());
        verify(petRepository).save(pet);
    }

    @Test
    @DisplayName("updatePet: actualiza nombre")
    void updatePet_actualizaNombre() {
        Pet pet = mascotaEjemplo(3, "Rex");
        Pet cambios = new Pet();
        cambios.setNombre("Rex Actualizado");

        when(petRepository.findById(3)).thenReturn(Optional.of(pet));
        when(petRepository.save(any(Pet.class))).thenAnswer(inv -> inv.getArgument(0));
        mockSinPropietarios(3);

        var dto = petService.updatePet(3, cambios);

        assertEquals("Rex Actualizado", dto.getNombre());
        verify(petRepository).save(pet);
    }

    @Test
    @DisplayName("countActivePets: delega al repositorio")
    void countActivePets_retornaTotal() {
        when(petRepository.countActivePets()).thenReturn(12L);

        assertEquals(12L, petService.countActivePets());
    }

    @Test
    @DisplayName("getAllPets: lista todas las mascotas")
    void getAllPets_retornaLista() {
        List<Pet> pets = List.of(mascotaEjemplo(1, "A"), mascotaEjemplo(2, "B"));
        when(petRepository.findAll()).thenReturn(pets);
        when(petOwnerRepository.findOwnerDisplayRowsByPetIds(anyList()))
                .thenReturn(Collections.emptyList());

        var result = petService.getAllPets();

        assertEquals(2, result.size());
        assertEquals("A", result.get(0).getNombre());
    }
}
