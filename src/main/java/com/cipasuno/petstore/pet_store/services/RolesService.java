package com.cipasuno.petstore.pet_store.services;

import com.cipasuno.petstore.pet_store.models.Roles;
import com.cipasuno.petstore.pet_store.repositories.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RolesService {

    @Autowired
    private RolesRepository rolesRepository;

    public List<Roles> getAllRoles() {
        // Filtrar SuperAdmin (rol_id = 1) para usuarios normales
        return rolesRepository.findAll()
                .stream()
                .filter(role -> !"1".equals(role.getRolId()))
                .collect(Collectors.toList());
    }

    public List<Roles> getActiveRoles() {
        return rolesRepository.findAll()
                .stream()
                .filter(role -> !"1".equals(role.getRolId()))  // Filtrar SuperAdmin (rol_id = 1)
                .collect(Collectors.toList());
    }

    public Roles getRoleById(String rolId) {
        return rolesRepository.findById(rolId).orElse(null);
    }

    public List<Roles> getAllRolesIncludingSuperAdmin() {
        return rolesRepository.findAll();
    }
}

