package com.cipasuno.petstore.pet_store.security;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;

@Aspect
@Component
public class RolePermissionAspect {

    @Around("@annotation(requiresRole)")
    public Object checkRolePermission(ProceedingJoinPoint joinPoint, RequiresRole requiresRole) throws Throwable {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            System.out.println("🔴 No autenticado - Authentication is null or not authenticated");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "No autenticado"));
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
        String userRole = (String) details.get("role");

        String[] allowedRoles = requiresRole.value();

        // 🔍 LOG DE DEPURACIÓN
        System.out.println("🔍 Verificando permisos para: " + joinPoint.getSignature().getName());
        System.out.println("   👤 Rol del usuario: '" + userRole + "'");
        System.out.println("   ✅ Roles permitidos: " + Arrays.toString(allowedRoles));
        System.out.println("   🎯 ¿Tiene permiso? " + Arrays.asList(allowedRoles).contains(userRole));

        if (!Arrays.asList(allowedRoles).contains(userRole)) {
            System.out.println("🔴 ACCESO DENEGADO - Rol '" + userRole + "' no está en la lista de roles permitidos");
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "No tiene permisos para realizar esta acción", 
                             "userRole", userRole,
                             "allowedRoles", Arrays.toString(allowedRoles)));
        }

        System.out.println("✅ ACCESO PERMITIDO para " + userRole);
        return joinPoint.proceed();
    }
}

