package com.cipasuno.petstore.pet_store.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor para capturar el tenant_id de cada petición HTTP
 * El tenant_id puede venir en:
 * - Header: X-Tenant-ID
 * - Query param: tenant_id
 */
@Component
public class TenantInterceptor implements HandlerInterceptor {
    
    private static final String TENANT_HEADER = "X-Tenant-ID";
    private static final String TENANT_PARAM = "tenant_id";
    
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        String tenantId = extractTenantId(request);

        if (tenantId != null) {
            TenantContext.setTenantId(tenantId);
        }

        return true;
    }
    
    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, @Nullable Exception ex) {
        TenantContext.clear();
    }
    
    private String extractTenantId(HttpServletRequest request) {
        // Primero intenta obtenerlo del header
        String tenantIdStr = request.getHeader(TENANT_HEADER);

        // Si no está en el header, intenta obtenerlo del query param
        if (tenantIdStr == null || tenantIdStr.isEmpty()) {
            tenantIdStr = request.getParameter(TENANT_PARAM);
        }

        if (tenantIdStr == null || tenantIdStr.isEmpty()) {
            return null;
        }

        try {
            return tenantIdStr;
        } catch (NumberFormatException e) {
            return null; // O podrías lanzar una excepción, o manejar de otra forma según necesidad
        }
    }
}