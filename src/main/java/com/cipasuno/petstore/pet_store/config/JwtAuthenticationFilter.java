package com.cipasuno.petstore.pet_store.config;

import com.cipasuno.petstore.pet_store.services.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = extractTokenFromRequest(request);

            if (token != null) {
                if (jwtTokenProvider.validateToken(token)) {
                    String email = jwtTokenProvider.getEmailFromToken(token);
                    Map<String, Object> claims = jwtTokenProvider.getAllClaims(token);

                    String role = (String) claims.get("role");
                    String tenantId = (String) claims.get("tenantId");
                    Integer userId = (Integer) claims.get("userId");

                    // Validar que los claims requeridos existan
                    if (email != null && role != null && userId != null) {
                        // Establecer contexto de seguridad
                        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role);
                        UsernamePasswordAuthenticationToken authentication = 
                            new UsernamePasswordAuthenticationToken(email, null, Collections.singletonList(authority));
                        
                        authentication.setDetails(Map.of(
                            "userId", userId,
                            "tenantId", tenantId != null ? tenantId : "",
                            "role", role
                        ));

                        SecurityContextHolder.getContext().setAuthentication(authentication);

                        // Establecer tenant en contexto
                        if (tenantId != null) {
                            TenantContext.setTenantId(tenantId);
                        }
                    } else {
                        logger.warn("Token JWT válido pero sin claims requeridos");
                    }
                } else {
                    logger.debug("Token JWT inválido o expirado");
                }
            }
        } catch (Exception e) {
            logger.error("Error al procesar el token JWT: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        
        // No filtrar requests OPTIONS (CORS preflight)
        if ("OPTIONS".equalsIgnoreCase(method)) {
            return true;
        }
        
        // No filtrar endpoints públicos
        return path.startsWith("/api/users/login") ||
               path.startsWith("/api/users/create") ||
               path.startsWith("/api/admin/migrations") ||
               path.startsWith("/api/auth/debug") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/api-docs") ||
               path.startsWith("/swagger-resources") ||
               path.startsWith("/webjars") ||
               path.equals("/favicon.ico");
    }
}

