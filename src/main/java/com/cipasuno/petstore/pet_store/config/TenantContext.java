package com.cipasuno.petstore.pet_store.config;

/**
 * Contexto de Tenant - Almacena el tenant_id del usuario actual en el hilo de ejecución
 * Utiliza ThreadLocal para mantener el contexto por cada petición HTTP
 */
public class TenantContext {
    
    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();
    
    public static void setTenantId(String tenantId) {
        CURRENT_TENANT.set(tenantId);
    }
    
    public static String getTenantId() {
        return CURRENT_TENANT.get();
    }
    
    public static void clear() {
        CURRENT_TENANT.remove();
    }
}

