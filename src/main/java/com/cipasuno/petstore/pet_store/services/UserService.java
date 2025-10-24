package com.cipasuno.petstore.pet_store.services;


import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.cipasuno.petstore.pet_store.models.User;
import com.cipasuno.petstore.pet_store.models.DTOs.UpdateUserRequest;
import com.cipasuno.petstore.pet_store.models.DTOs.UserCreateDto;
import com.cipasuno.petstore.pet_store.models.DTOs.UserResponseDto;
import com.cipasuno.petstore.pet_store.repositories.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // CREATE - Crear un nuevo usuario con password encriptado
    public UserResponseDto createUser(UserCreateDto userDto) {
        User user = new User();
        
        // Copiar datos del DTO
        user.setName(userDto.getName());
        user.setTipoId(userDto.getTipoId());
        user.setIdent(userDto.getIdent());
        user.setCorreo(userDto.getCorreo());
        user.setTelefono(userDto.getTelefono());
        user.setDireccion(userDto.getDireccion());
        user.setRolId(userDto.getRolId());
        
        // ENCRIPTAR EL PASSWORD
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        

        User savedUser = userRepository.save(user);
        return mapToResponseDto(savedUser);
    }

    // CREATE - Crear usuario con tenant
    public UserResponseDto createUser(UserCreateDto userDto, String tenantId) {
        User user = new User();
        
        user.setTenantId(tenantId);
        user.setName(userDto.getName());
        user.setTipoId(userDto.getTipoId());
        user.setIdent(userDto.getIdent());
        user.setCorreo(userDto.getCorreo());
        user.setTelefono(userDto.getTelefono());
        user.setDireccion(userDto.getDireccion());
        user.setRolId(userDto.getRolId());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));

        User savedUser = userRepository.save(user);
        return mapToResponseDto(savedUser);
    }

    // READ - Obtener todos los usuarios (sin password)
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    // READ - Obtener usuario por ID (sin password)
    public Optional<UserResponseDto> getUserById(Integer id) {
        return userRepository.findById(id)
                .map(this::mapToResponseDto);
    }

    // READ - Obtener todos los usuarios activos (sin password)
    public List<UserResponseDto> getActiveUsers() {
        return userRepository.findByActivoTrue()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    // READ - Obtener usuarios activos ordenados (sin password)
    public List<UserResponseDto> getActiveUsersOrdered() {
        return userRepository.findAllActiveUsersOrdered()
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    // READ - Buscar usuarios por nombre (sin password)
    public List<UserResponseDto> searchUsersByName(String name) {
        return userRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    // READ - Buscar usuarios por rol (sin password)
    public List<UserResponseDto> getUsersByRole(String rolId) {
        return userRepository.findByRolId(rolId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    // READ - Obtener usuario por email (para validaciones internas)
    public Optional<User> getUserByEmail(String correo) {
        return userRepository.findByCorreo(correo);
    }

    // READ - Buscar usuarios por identificación (para validaciones internas)
    public List<User> getUsersByIdent(String ident) {
        return userRepository.findByIdent(ident);
    }

    // READ - Obtener usuarios por tenant
    public List<UserResponseDto> getAllUsersByTenant(String tenantId) {
        return userRepository.findByTenantId(tenantId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    // READ - Obtener usuario por ID y tenant
    public Optional<UserResponseDto> getUserByIdAndTenant(Integer id, String tenantId) {
        return userRepository.findByUserIdAndTenantId(id, tenantId)
                .map(this::mapToResponseDto);
    }

    // READ - Obtener usuario por email y tenant
    public Optional<User> getUserByEmailAndTenant(String correo, String tenantId) {
        return userRepository.findByCorreoAndTenantId(correo, tenantId);
    }

    // READ - Obtener usuarios por ident y tenant
    public List<User> getUsersByIdentAndTenant(String ident, String tenantId) {
        return userRepository.findByIdentAndTenantId(ident, tenantId);
    }

    // READ - Obtener usuarios por rol y tenant
    public List<UserResponseDto> getUsersByRoleAndTenant(String rolId, String tenantId) {
        return userRepository.findByRolIdAndTenantId(rolId, tenantId)
                .stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    // UPDATE - Actualizar usuario
    public UserResponseDto updateUser(Integer id, User userDetails) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        // Actualizar solo los campos que no son nulos
        if (userDetails.getName() != null) {
            user.setName(userDetails.getName());
        }
        if (userDetails.getTipoId() != null) {
            user.setTipoId(userDetails.getTipoId());
        }
        if (userDetails.getIdent() != null) {
            user.setIdent(userDetails.getIdent());
        }
        if (userDetails.getCorreo() != null) {
            user.setCorreo(userDetails.getCorreo());
        }
        if (userDetails.getTelefono() != null) {
            user.setTelefono(userDetails.getTelefono());
        }
        if (userDetails.getDireccion() != null) {
            user.setDireccion(userDetails.getDireccion());
        }
        if (userDetails.getRolId() != null) {
            user.setRolId(userDetails.getRolId());
        }
        if (userDetails.getActivo() != null) {
            user.setActivo(userDetails.getActivo());
        }
        // Si se proporciona un nuevo password, encriptarlo
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }

        User updatedUser = userRepository.save(user);
        return mapToResponseDto(updatedUser);
    }

    // UPDATE - Actualizar usuario con tenant
    public UserResponseDto updateUser(Integer id, UpdateUserRequest userDetails, String tenantId) {
        User user = userRepository.findByUserIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));

        if (userDetails.getName() != null) {
            user.setName(userDetails.getName());
        }
        if (userDetails.getTipoId() != null) {
            user.setTipoId(userDetails.getTipoId());
        }
        if (userDetails.getIdent() != null) {
            user.setIdent(userDetails.getIdent());
        }
        if (userDetails.getCorreo() != null) {
            user.setCorreo(userDetails.getCorreo());
        }
        if (userDetails.getTelefono() != null) {
            user.setTelefono(userDetails.getTelefono());
        }
        if (userDetails.getDireccion() != null) {
            user.setDireccion(userDetails.getDireccion());
        }
        if (userDetails.getRolId() != null) {
            user.setRolId(userDetails.getRolId());
        }
        if (userDetails.getActivo() != null) {
            user.setActivo(userDetails.getActivo());
        }
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
        }

        User updatedUser = userRepository.save(user);
        return mapToResponseDto(updatedUser);
    }

    // DELETE - Eliminar usuario (soft delete)
    public void deleteUser(Integer id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        
        user.setActivo(false);
        userRepository.save(user);
    }

    // DELETE - Eliminar usuario con tenant
    public void deleteUser(Integer id, String tenantId) {
        User user = userRepository.findByUserIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        
        user.setActivo(false);
        userRepository.save(user);
    }

    // DELETE - Eliminar usuario permanentemente
    public void deleteUserPermanently(Integer id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        
        userRepository.delete(user);
    }

    // DELETE - Eliminar usuario permanentemente con tenant
    public void deleteUserPermanently(Integer id, String tenantId) {
        User user = userRepository.findByUserIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        
        userRepository.delete(user);
    }

    // ACTIVATE - Activar usuario
    public UserResponseDto activateUser(Integer id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        
        user.setActivo(true);
        User savedUser = userRepository.save(user);
        return mapToResponseDto(savedUser);
    }

    // ACTIVATE - Activar usuario con tenant
    public UserResponseDto activateUser(Integer id, String tenantId) {
        User user = userRepository.findByUserIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        
        user.setActivo(true);
        User savedUser = userRepository.save(user);
        return mapToResponseDto(savedUser);
    }

    // DEACTIVATE - Desactivar usuario
    public UserResponseDto deactivateUser(Integer id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        
        user.setActivo(false);
        User savedUser = userRepository.save(user);
        return mapToResponseDto(savedUser);
    }

    // DEACTIVATE - Desactivar usuario con tenant
    public UserResponseDto deactivateUser(Integer id, String tenantId) {
        User user = userRepository.findByUserIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        
        user.setActivo(false);
        User savedUser = userRepository.save(user);
        return mapToResponseDto(savedUser);
    }

    // COUNT - Contar total de usuarios
    public long countUsers() {
        return userRepository.count();
    }

    // COUNT - Contar usuarios activos
    public long countActiveUsers() {
        return userRepository.findByActivoTrue().size();
    }

    // VALIDATION - Verificar si existe usuario por email
    public boolean existsByEmail(String correo) {
        return userRepository.findByCorreo(correo).isPresent();
    }

    // VALIDATION - Verificar si existe usuario por identificación
    public boolean existsByIdent(String ident) {
        return !userRepository.findByIdent(ident).isEmpty();
    }
    
    // MÉTODO AUXILIAR - Convertir User a UserResponseDto (sin password)
    private UserResponseDto mapToResponseDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setUser_id(user.getUserId());
        dto.setName(user.getName());
        dto.setTipo_id(user.getTipoId());
        dto.setIdent(user.getIdent());
        dto.setCorreo(user.getCorreo());
        dto.setTelefono(user.getTelefono());
        dto.setDireccion(user.getDireccion());
        dto.setRol_id(user.getRolId());
        dto.setActivo(user.getActivo());
        dto.setCreated_on(user.getCreatedOn());
        // NO se incluye el password
        return dto;
    }
}