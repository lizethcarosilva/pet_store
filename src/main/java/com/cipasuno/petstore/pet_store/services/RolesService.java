package com.cipasuno.petstore.pet_store.services;

import com.cipasuno.petstore.pet_store.models.Roles;
import com.cipasuno.petstore.pet_store.models.DTOs.RoleResponseDto;
import com.cipasuno.petstore.pet_store.repositories.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RolesService {

    @Autowired
    private RolesRepository rolesRepository;

    public List<RoleResponseDto> getAllRoles() {
        // Filtrar SuperAdmin (rol_id = 1) para usuarios normales
        return rolesRepository.findAll()
                .stream()
                .filter(role -> !"1".equals(role.getRolId()))
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public List<RoleResponseDto> getActiveRoles() {
        // Por ahora devolvemos todos los roles excepto SuperAdmin
        // Si se necesita filtrar por activo, primero hay que agregar la columna a la BD
        return rolesRepository.findAll()
                .stream()
                .filter(role -> !"1".equals(role.getRolId()))  // Filtrar SuperAdmin (rol_id = 1)
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    public Optional<RoleResponseDto> getRoleById(String rolId) {
        return rolesRepository.findById(rolId)
                .map(this::mapToResponseDto);
    }

    public List<RoleResponseDto> getAllRolesIncludingSuperAdmin() {
        return rolesRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    private RoleResponseDto mapToResponseDto(Roles role) {
        RoleResponseDto dto = new RoleResponseDto();
        dto.setRolId(role.getRolId());
        dto.setNombre(role.getNombre());
        dto.setDescripcion(role.getDescripcion());
        return dto;
    }
}

