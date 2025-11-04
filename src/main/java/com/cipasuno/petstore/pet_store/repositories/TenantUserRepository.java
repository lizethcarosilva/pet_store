package com.cipasuno.petstore.pet_store.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cipasuno.petstore.pet_store.models.TenantUser;

@Repository
public interface TenantUserRepository extends JpaRepository<TenantUser, TenantUser.TenantUserId> {
    List<TenantUser> findByUserId(String userId);
}