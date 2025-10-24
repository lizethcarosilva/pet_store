package com.cipasuno.petstore.pet_store.services;


import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cipasuno.petstore.pet_store.config.JwtTokenProvider;
import com.cipasuno.petstore.pet_store.models.Roles;
import com.cipasuno.petstore.pet_store.models.User;
import com.cipasuno.petstore.pet_store.repositories.RolesRepository;

@Service
public class TokenService {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private RolesRepository rolesRepository;

    public String generateToken(String email) {
        return jwtTokenProvider.generateToken(email);
    }

    public String generateToken(String email, Integer userId, String role, String tenantId) {
        return jwtTokenProvider.generateToken(email, userId, role, tenantId);
    }

    public String generateUserToken(User user) {
        // Obtener el nombre del rol desde la tabla roles
        String roleName = getRoleNameById(user.getRolId());
        
        return generateToken(
            user.getCorreo(),
            user.getUserId(),
            roleName,  // Ahora guarda el nombre del rol, no el ID
            user.getTenantId()
        );
    }

    private String getRoleNameById(String rolId) {
        return rolesRepository.findById(rolId)
            .map(Roles::getNombre)  // Obtiene el campo "nombre" de la tabla roles
            .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + rolId));
    }

    public Map<String, Object> validateToken(String token) {
        if (jwtTokenProvider.validateToken(token)) {
            return jwtTokenProvider.getAllClaims(token);
        }
        throw new RuntimeException("Token inválido");
    }

    public String getEmailFromToken(String token) {
        return jwtTokenProvider.getEmailFromToken(token);
    }
}
