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
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "No autenticado"));
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> details = (Map<String, Object>) authentication.getDetails();
        String userRole = (String) details.get("role");

        String[] allowedRoles = requiresRole.value();

        if (!Arrays.asList(allowedRoles).contains(userRole)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("error", "No tiene permisos para realizar esta acción"));
        }

        return joinPoint.proceed();
    }
}

