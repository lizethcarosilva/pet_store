package com.cipasuno.petstore.pet_store.repositories;

import com.cipasuno.petstore.pet_store.models.PetOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetOwnerRepository extends JpaRepository<PetOwner, PetOwner.PetOwnerId> {
    
    @Query("SELECT po FROM PetOwner po WHERE po.pet.petId = :petId")
    List<PetOwner> findByPetId(Integer petId);

    /**
     * Dueños por filas de pet_owner: usa client_id y/o user_id (legacy).
     * Evita JPA con clave (pet_id, client_id) cuando en BD solo está user_id.
     * Columnas: pet_id, client_id, user_id, ident, name, telefono, correo
     */
    @Query(value = """
        SELECT po.pet_id,
               c.client_id,
               u.user_id,
               COALESCE(NULLIF(TRIM(c.ident), ''), NULLIF(TRIM(u.ident), '')) AS owner_ident,
               COALESCE(NULLIF(TRIM(c.name), ''), NULLIF(TRIM(u.name), '')) AS owner_name,
               COALESCE(NULLIF(TRIM(c.telefono), ''), NULLIF(TRIM(u.telefono), '')) AS owner_phone,
               COALESCE(NULLIF(TRIM(c.correo), ''), NULLIF(TRIM(u.correo), '')) AS owner_correo
        FROM pet_owner po
        LEFT JOIN client c ON po.client_id = c.client_id
        LEFT JOIN "user" u ON po.user_id = u.user_id
        WHERE po.pet_id IN (:petIds)
        """, nativeQuery = true)
    List<Object[]> findOwnerDisplayRowsByPetIds(@Param("petIds") List<Integer> petIds);
    
    @Query("SELECT po FROM PetOwner po WHERE po.client.clientId = :clientId")
    List<PetOwner> findByClientId(Integer clientId);
    
    // Método legacy mantenido por compatibilidad (busca por client_id)
    @Query("SELECT po FROM PetOwner po WHERE po.client.clientId = :userId")
    List<PetOwner> findByUserId(Integer userId);
    
    @Query("DELETE FROM PetOwner po WHERE po.pet.petId = :petId")
    void deleteByPetId(Integer petId);
}

