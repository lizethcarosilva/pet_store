package com.cipasuno.petstore.pet_store.repositories;

import com.cipasuno.petstore.pet_store.models.PetOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetOwnerRepository extends JpaRepository<PetOwner, PetOwner.PetOwnerId> {
    
    @Query("SELECT po FROM PetOwner po WHERE po.pet.petId = :petId")
    List<PetOwner> findByPetId(Integer petId);
    
    @Query("SELECT po FROM PetOwner po WHERE po.user.userId = :userId")
    List<PetOwner> findByUserId(Integer userId);
    
    @Query("DELETE FROM PetOwner po WHERE po.pet.petId = :petId")
    void deleteByPetId(Integer petId);
}

