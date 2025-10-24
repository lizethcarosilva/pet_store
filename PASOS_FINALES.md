# 🎯 PASOS FINALES PARA QUE TODO FUNCIONE

## ⚠️ ACCIÓN INMEDIATA REQUERIDA

---

## 1️⃣ REINICIAR EL BACKEND (OBLIGATORIO)

Los cambios de CORS **NO aplican** hasta que reinicies Spring Boot.

### Paso 1: Detener el Backend
Si está corriendo, presiona:
```
Ctrl + C
```

### Paso 2: Iniciar de Nuevo
```bash
./mvnw.cmd spring-boot:run
```

### Paso 3: Esperar el Mensaje
```
Started PetStoreApplication in X.X seconds
```

**✅ Después de esto, CORS funcionará**

---

## 2️⃣ VERIFICAR QUE CORS FUNCIONA

### Desde tu Frontend React:

```typescript
// Prueba rápida en consola del navegador
fetch('http://localhost:8090/api/users/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    correo: 'admin@vetsanfrancisco.com',
    password: 'admin123'
  })
})
.then(res => res.json())
.then(data => console.log('✅ CORS funciona!', data))
.catch(err => console.error('❌ Error:', err));
```

**Resultado Esperado**:
```json
{
  "token": "eyJhbGci...",
  "userId": 1,
  "name": "Admin San Francisco",
  "correo": "admin@vetsanfrancisco.com",
  "rolId": "ADMIN",
  "tenantId": "VET001"
}
```

---

## 3️⃣ CONFIGURAR TU FRONTEND

### Crear archivo de configuración de Axios:

```typescript
// src/services/axiosConfig.ts
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8090/api',
  headers: {
    'Content-Type': 'application/json'
  }
});

// Agregar token automáticamente
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Manejar errores
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.clear();
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
```

### Usar en tus componentes:

```typescript
// Users.tsx
import api from '../services/axiosConfig';

const getUsers = async () => {
  const response = await api.get('/users');
  return response.data;
};
```

---

## 4️⃣ FLUJO DE LOGIN EN FRONTEND

```typescript
// LoginPage.tsx
import { useState } from 'react';
import api from '../services/axiosConfig';

const LoginPage = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  
  const handleLogin = async (e) => {
    e.preventDefault();
    
    try {
      // Login SIN token (endpoint público)
      const response = await axios.post('http://localhost:8090/api/users/login', {
        correo: email,
        password: password
      });
      
      const data = response.data;
      
      // Guardar datos
      localStorage.setItem('token', data.token);
      localStorage.setItem('userId', data.userId);
      localStorage.setItem('tenantId', data.tenantId);
      localStorage.setItem('rolId', data.rolId);
      localStorage.setItem('userName', data.name);
      
      // Redirect al dashboard
      window.location.href = '/dashboard';
      
    } catch (error) {
      alert('Credenciales inválidas');
    }
  };
  
  return (
    <form onSubmit={handleLogin}>
      <input 
        type="email" 
        value={email} 
        onChange={(e) => setEmail(e.target.value)}
        placeholder="Correo"
      />
      <input 
        type="password" 
        value={password} 
        onChange={(e) => setPassword(e.target.value)}
        placeholder="Contraseña"
      />
      <button type="submit">Ingresar</button>
    </form>
  );
};
```

---

## 5️⃣ REGLAS DE SUPERADMIN

### El SUPERADMIN:
- ✅ Puede ver y gestionar todos los tenants
- ✅ Solo puede ser modificado por sí mismo
- ✅ No es visible en la lista de roles para usuarios normales

### Validación en Frontend:

```typescript
// En componente de edición de usuario
const canEditUser = (targetUser) => {
  const currentUserId = parseInt(localStorage.getItem('userId'));
  const currentRole = localStorage.getItem('rolId');
  
  // Si el target es SUPERADMIN
  if (targetUser.rolId === 'SUPERADMIN') {
    // Solo puede editarlo si ES el mismo usuario
    if (currentUserId !== targetUser.userId) {
      return false;  // NO puede editar otro SUPERADMIN
    }
  }
  
  // Para otros usuarios
  return ['SUPERADMIN', 'ADMIN', 'GERENTE'].includes(currentRole);
};
```

### Ocultar SUPERADMIN en Lista de Roles:

```typescript
// RoleSelector.tsx
const getRoles = async () => {
  const response = await api.get('/roles');
  return response.data;
  // El backend ya filtra SUPERADMIN para usuarios normales
};

// En el select
<select name="rolId">
  {roles.map(role => (
    <option key={role.rolId} value={role.rolId}>
      {role.nombre}
    </option>
  ))}
</select>
```

---

## 📊 Permisos por Módulo

### SUPERADMIN
```typescript
const permissions = {
  tenants: 'full',      // Crear, editar, eliminar
  users: 'full',        // Todos los tenants
  pets: 'full',
  products: 'full',
  services: 'full',
  appointments: 'full',
  invoices: 'full',
  dashboard: 'full'
};
```

### ADMIN / GERENTE
```typescript
const permissions = {
  tenants: 'none',      // NO puede ver/editar tenants
  users: 'full',        // Solo su tenant
  pets: 'full',         // Solo su tenant
  products: 'full',     // Solo su tenant
  services: 'full',     // Solo su tenant
  appointments: 'full', // Solo su tenant
  invoices: 'full',     // Solo su tenant
  dashboard: 'full'     // Solo su tenant
};
```

### VENDEDOR
```typescript
const permissions = {
  tenants: 'none',
  users: 'none',        // NO puede gestionar usuarios
  pets: 'read',         // Solo ver
  products: 'full',     // Gestionar inventario
  services: 'read',     // Solo ver
  appointments: 'full', // Gestionar citas
  invoices: 'full',     // Gestionar facturas
  dashboard: 'read'     // Solo ver estadísticas
};
```

### CLIENTE
```typescript
const permissions = {
  tenants: 'none',
  users: 'none',
  pets: 'own',          // Solo sus mascotas
  products: 'read',     // Solo ver catálogo
  services: 'read',     // Solo ver servicios
  appointments: 'own',  // Solo sus citas
  invoices: 'own',      // Solo sus facturas
  dashboard: 'limited'  // Dashboard personal
};
```

---

## 🔄 Flujo Correcto Frontend ↔ Backend

```
┌─────────────────────────────────────────┐
│  FRONTEND (localhost:5173)              │
│  React/Vue/Angular                      │
└────────────┬────────────────────────────┘
             │
             │ 1. Login
             │ POST /api/users/login
             │ { correo, password }
             │
             ▼
┌─────────────────────────────────────────┐
│  BACKEND (localhost:8090)               │
│  Spring Boot + Security + JWT           │
│                                         │
│  ✅ CORS configurado                     │
│  ✅ Permite origin: localhost:5173       │
└────────────┬────────────────────────────┘
             │
             │ 2. Retorna Token
             │ { token, userId, tenantId, rolId }
             │
             ▼
┌─────────────────────────────────────────┐
│  FRONTEND guarda en localStorage        │
│  - token                                │
│  - tenantId                             │
│  - rolId                                │
└────────────┬────────────────────────────┘
             │
             │ 3. Request con Token
             │ GET /api/users
             │ Header: Authorization: Bearer <token>
             │
             ▼
┌─────────────────────────────────────────┐
│  BACKEND valida y responde              │
│  ✅ Valida token                         │
│  ✅ Extrae tenant_id del token           │
│  ✅ Filtra datos por tenant              │
│  ✅ Headers CORS incluidos               │
└─────────────────────────────────────────┘
```

---

## ✅ CHECKLIST COMPLETO

### Backend:
- ✅ CorsConfig creado
- ✅ SecurityConfig actualizado
- ✅ Compilación exitosa
- ⏳ **REINICIAR APLICACIÓN** ← HACER AHORA

### Frontend:
- ⏳ Configurar axios con interceptor
- ⏳ Guardar token del login
- ⏳ Incluir Authorization header
- ⏳ Manejar errores 401/403

---

## 🚀 SIGUIENTE PASO INMEDIATO

### ¡REINICIA EL BACKEND AHORA!

```bash
# En la terminal del backend:
# Ctrl + C (detener)

./mvnw.cmd spring-boot:run
```

**Después de reiniciar:**
- ✅ CORS funcionará
- ✅ Frontend podrá hacer requests
- ✅ Login funcionará
- ✅ Todos los endpoints accesibles con token

---

## 📚 Documentos de Ayuda

1. **INTEGRACION_FRONTEND.md** - Ejemplos completos de código
2. **SOLUCION_CORS.md** - Detalles técnicos de CORS
3. **SECURITY_ROLES_PERMISSIONS.md** - Matriz de permisos
4. **QUICK_START_TESTING.md** - Comandos de prueba

---

**¡REINICIA EL BACKEND Y TODO FUNCIONARÁ!** 🎉

**Estado**: ✅ CORS CONFIGURADO  
**Acción Pendiente**: ⏳ REINICIAR BACKEND  
**Tiempo Estimado**: 30 segundos

