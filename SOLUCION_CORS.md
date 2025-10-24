# ✅ Solución Completa del Error CORS

## 🐛 Problema Original

```
Access to XMLHttpRequest at 'http://localhost:8090/api/users' from origin 'http://localhost:5173' 
has been blocked by CORS policy: No 'Access-Control-Allow-Origin' header is present
```

**Causa**: Spring Security bloqueaba las solicitudes CORS del frontend React.

---

## ✅ Solución Implementada

### 1. Creado `CorsConfig.java`

```java
@Configuration
public class CorsConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Permitir orígenes del frontend
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:5173",  // Vite/React
            "http://localhost:3000",  // Create React App
            "http://localhost:4200"   // Angular
        ));
        
        // Permitir métodos
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));
        
        // Permitir headers
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization",
            "Content-Type",
            "X-Tenant-ID"
        ));
        
        // Permitir credenciales
        configuration.setAllowCredentials(true);
        
        return source;
    }
}
```

### 2. Actualizado `SecurityConfig.java`

```java
@Autowired
private CorsConfigurationSource corsConfigurationSource;

@Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) {
    http
        .csrf(csrf -> csrf.disable())
        .cors(cors -> cors.configurationSource(corsConfigurationSource))  // ← ACTIVADO
        // ... resto
}
```

### 3. Actualizado `JwtAuthenticationFilter.java`

Agregado método `shouldNotFilter()` para no filtrar:
- Swagger UI
- OpenAPI docs
- Login
- Registro

---

## 🚀 Cómo Aplicar la Solución

### Paso 1: REINICIAR el Backend

**CRÍTICO**: Los cambios de CORS solo aplican al reiniciar.

```bash
# Detener el backend actual (Ctrl + C)

# Iniciar de nuevo
./mvnw.cmd spring-boot:run
```

### Paso 2: Verificar en Consola

Al iniciar deberías ver:
```
Started PetStoreApplication in X seconds
```

### Paso 3: Probar desde Frontend

```typescript
// Desde tu frontend React
const response = await fetch('http://localhost:8090/api/users/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    correo: 'admin@vetsanfrancisco.com',
    password: 'admin123'
  })
});

const data = await response.json();
console.log(data);  // Debe funcionar SIN error CORS
```

---

## 🔒 Reglas de SUPERADMIN

### Implementación Recomendada

El SUPERADMIN solo debe ser modificado por sí mismo. Esto se puede validar en el backend:

```java
// En UserService.updateUser()
public UserResponseDto updateUser(Integer id, UpdateUserRequest details, String tenantId) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    
    // Obtener usuario actual del contexto de seguridad
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    Map<String, Object> authDetails = (Map<String, Object>) auth.getDetails();
    Integer currentUserId = (Integer) authDetails.get("userId");
    String currentRole = (String) authDetails.get("role");
    
    // Si el usuario a editar es SUPERADMIN y el usuario actual NO es el mismo
    if ("SUPERADMIN".equals(user.getRolId()) && !user.getUserId().equals(currentUserId)) {
        throw new RuntimeException("Solo el SUPERADMIN puede modificarse a sí mismo");
    }
    
    // Resto de la lógica...
}
```

---

## 📊 Validación en Frontend

```typescript
// En el componente de edición de usuarios
const canEditUser = (targetUser) => {
  const currentUserId = localStorage.getItem('userId');
  const currentRole = localStorage.getItem('rolId');
  
  // Si el usuario target es SUPERADMIN
  if (targetUser.rolId === 'SUPERADMIN') {
    // Solo puede editarlo si ES el mismo usuario
    return targetUser.userId.toString() === currentUserId;
  }
  
  // Para otros roles, validar permisos normales
  return ['SUPERADMIN', 'ADMIN', 'GERENTE'].includes(currentRole);
};

// Uso en UI
{users.map(user => (
  <div key={user.userId}>
    <span>{user.name}</span>
    {canEditUser(user) && (
      <button onClick={() => editUser(user)}>Editar</button>
    )}
  </div>
))}
```

---

## ✅ Verificación

Después de reiniciar el backend, verifica:

### 1. CORS funciona:
```bash
curl -X OPTIONS http://localhost:8090/api/users \
  -H "Origin: http://localhost:5173" \
  -H "Access-Control-Request-Method: GET" \
  -H "Access-Control-Request-Headers: Authorization"
```

**Debe retornar**:
- Status: 200 OK
- Header: `Access-Control-Allow-Origin: http://localhost:5173`

### 2. Login funciona:
```bash
curl -X POST http://localhost:8090/api/users/login \
  -H "Content-Type: application/json" \
  -H "Origin: http://localhost:5173" \
  -d '{"correo":"admin@vetsanfrancisco.com","password":"admin123"}'
```

**Debe retornar**:
- Token JWT
- Sin error CORS

### 3. Endpoint protegido funciona:
```bash
curl -X GET http://localhost:8090/api/users \
  -H "Authorization: Bearer <TOKEN>" \
  -H "Origin: http://localhost:5173"
```

**Debe retornar**:
- Lista de usuarios
- Header: `Access-Control-Allow-Origin`

---

## 🎯 Checklist Final

- ✅ CorsConfig.java creado
- ✅ SecurityConfig.java actualizado
- ✅ JwtAuthenticationFilter.java actualizado
- ✅ Compilación exitosa
- ⏳ **REINICIAR BACKEND** (pendiente)
- ⏳ Probar desde frontend

---

## 🚨 IMPORTANTE

### DEBES REINICIAR EL BACKEND

Los cambios de configuración Spring Boot solo aplican al reiniciar la aplicación.

```bash
# 1. DETENER (Ctrl + C en terminal del servidor)
# 2. INICIAR de nuevo
./mvnw.cmd spring-boot:run
```

**Sin reiniciar, seguirás viendo el error CORS** ⚠️

---

## 📞 Si Aún Hay Problemas

### Verificar:
1. ✅ Backend reiniciado
2. ✅ Frontend en puerto 5173
3. ✅ Token incluido en header Authorization
4. ✅ Content-Type: application/json

### Debugging:
```javascript
// En tu frontend, verifica los headers
const config = {
  headers: {
    'Authorization': `Bearer ${token}`,
    'Content-Type': 'application/json'
  }
};

console.log('Request config:', config);
```

---

**CORS está configurado correctamente. Solo necesitas REINICIAR el backend.** ✅

**Versión**: 2.1.1  
**Fecha**: Octubre 2025  
**Estado**: ✅ CORS CONFIGURADO

