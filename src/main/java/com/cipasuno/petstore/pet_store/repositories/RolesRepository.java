package com.cipasuno.petstore.pet_store.repositories;

import com.cipasuno.petstore.pet_store.models.Roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RolesRepository extends JpaRepository<Roles, String> {
    // La tabla roles solo tiene: rol_id y name
}

