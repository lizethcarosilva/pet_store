package com.cipasuno.petstore.pet_store.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.cipasuno.petstore.pet_store.models.User;
import com.cipasuno.petstore.pet_store.models.DTOs.LoginDto;
import com.cipasuno.petstore.pet_store.models.DTOs.LoginResponse;

import com.cipasuno.petstore.pet_store.repositories.UserRepository;

@Service
public class LoginService {

    @Autowired
    private UserRepository loginRepository;

        @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    public LoginResponse login(LoginDto data) {
        // Buscar usuario por email
        Optional<User> userOptional = loginRepository.findByCorreo(data.getCorreo());
        
        if (userOptional.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }
        
        User user = userOptional.get();
        
        // Verificar si el usuario está activo
        if (!user.getActivo()) {
            throw new RuntimeException("Usuario inactivo");
        }
        
        // Verificar contraseña
        if (!passwordEncoder.matches(data.getPassword(), user.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }
        
        return mapToResponseLoginDto(user);
    }

    private LoginResponse mapToResponseLoginDto(User user) {
        LoginResponse dto = new LoginResponse();
        
        try {
            // Generar token JWT encriptado con los datos del usuario
            String token = tokenService.generateUserToken(user);
            
            dto.setToken(token);
            dto.setUserId(user.getUserId());
            dto.setName(user.getName());
            dto.setCorreo(user.getCorreo());
            dto.setRolId(user.getRolId());
            dto.setTenantId(user.getTenantId().toString());
            
        } catch (RuntimeException e) {
            // Manejar error específico de tenant no encontrado
            if (e.getMessage().contains("No se encontró tenant")) {
                throw new RuntimeException("Usuario no tiene un tenant asignado");
            }
            throw e;
        }
        
        return dto;
    }
}