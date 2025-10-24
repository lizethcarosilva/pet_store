# 🔗 Guía de Integración Frontend - Pet Store Backend

## ✅ CORS Configurado

El backend ahora permite solicitudes desde:
- ✅ `http://localhost:5173` (Vite/React)
- ✅ `http://localhost:3000` (Create React App)
- ✅ `http://localhost:4200` (Angular)

---

## 📡 Cómo Hacer Requests desde el Frontend

### 1. Login (Sin Token)

```typescript
// api.ts o authService.ts
import axios from 'axios';

const API_URL = 'http://localhost:8090/api';

export const login = async (correo: string, password: string) => {
  const response = await axios.post(`${API_URL}/users/login`, {
    correo,
    password
  });
  
  return response.data;
  // Retorna: { token, userId, name, correo, rolId, tenantId }
};
```

### 2. Guardar Token

```typescript
// Después del login exitoso
const loginUser = async (correo: string, password: string) => {
  try {
    const data = await login(correo, password);
    
    // Guardar en localStorage
    localStorage.setItem('token', data.token);
    localStorage.setItem('userId', data.userId);
    localStorage.setItem('tenantId', data.tenantId);
    localStorage.setItem('rolId', data.rolId);
    localStorage.setItem('userName', data.name);
    
    return data;
  } catch (error) {
    console.error('Error en login:', error);
    throw error;
  }
};
```

### 3. Configurar Axios con Interceptor

```typescript
// axiosConfig.ts
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8090/api',
  headers: {
    'Content-Type': 'application/json'
  }
});

// Interceptor para agregar token automáticamente
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Interceptor para manejar errores
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      // Token inválido o expirado
      localStorage.clear();
      window.location.href = '/login';
    }
    
    if (error.response?.status === 403) {
      // Sin permisos
      alert('No tiene permisos para realizar esta acción');
    }
    
    return Promise.reject(error);
  }
);

export default api;
```

### 4. Usar la API

```typescript
// userService.ts
import api from './axiosConfig';

export const getAllUsers = async () => {
  const response = await api.get('/users');
  return response.data;
};

export const createUser = async (userData) => {
  const response = await api.post('/users/create', userData);
  return response.data;
};

export const updateUser = async (userId, userData) => {
  const response = await api.put('/users/update', {
    userId,
    ...userData
  });
  return response.data;
};

export const deleteUser = async (userId) => {
  const response = await api.delete('/users/deleteUser', {
    data: { id: userId }
  });
  return response.data;
};
```

---

## 🔒 Gestión de SUPERADMIN

### Regla Implementada:
- ✅ SUPERADMIN puede ver todos los tenants
- ✅ SUPERADMIN puede modificar cualquier cosa
- ✅ Para otros roles: NO pueden ver tenants

### Validación en Frontend:

```typescript
// Verificar rol del usuario
const userRole = localStorage.getItem('rolId');

// Mostrar opciones según rol
if (userRole === 'SUPERADMIN') {
  // Mostrar menú de gestión de tenants
  // Mostrar todos los usuarios de todos los tenants
} else if (userRole === 'ADMIN' || userRole === 'GERENTE') {
  // Mostrar solo datos de su tenant
  // NO mostrar gestión de tenants
} else if (userRole === 'VENDEDOR') {
  // Solo facturas, productos, servicios, citas
  // NO mostrar gestión de usuarios
} else if (userRole === 'CLIENTE') {
  // Solo sus propios datos
}
```

---

## 📋 Matriz de Permisos para Frontend

### SUPERADMIN
```typescript
const permissions = {
  canViewTenants: true,
  canCreateTenants: true,
  canEditTenants: true,
  canViewAllUsers: true,
  canCreateUsers: true,
  canEditUsers: true,
  canDeleteUsers: true,
  canViewAllPets: true,
  canManageAll: true
};
```

### ADMIN / GERENTE
```typescript
const permissions = {
  canViewTenants: false,  // NO puede ver tenants
  canCreateTenants: false,
  canEditTenants: false,
  canViewAllUsers: true,  // Solo de su tenant
  canCreateUsers: true,
  canEditUsers: true,
  canDeleteUsers: true,
  canViewAllPets: true,
  canManageAll: true  // Pero solo de su tenant
};
```

### VENDEDOR
```typescript
const permissions = {
  canViewUsers: false,
  canCreateUsers: false,
  canManageInvoices: true,
  canManageProducts: true,
  canViewServices: true,
  canManageAppointments: true,
  canViewPets: true
};
```

### CLIENTE
```typescript
const permissions = {
  canViewOwnPets: true,
  canViewOwnAppointments: true,
  canViewOwnInvoices: true,
  canViewDashboard: true  // Dashboard limitado
};
```

---

## 🎯 Ejemplos Completos

### Ejemplo 1: Página de Usuarios

```typescript
// Users.tsx
import { useEffect, useState } from 'react';
import api from '../services/axiosConfig';

const Users = () => {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const userRole = localStorage.getItem('rolId');
  
  useEffect(() => {
    loadUsers();
  }, []);
  
  const loadUsers = async () => {
    try {
      setLoading(true);
      const response = await api.get('/users');
      setUsers(response.data);
    } catch (error) {
      console.error('Error loading users:', error);
      if (error.response?.status === 403) {
        alert('No tiene permisos para ver usuarios');
      }
    } finally {
      setLoading(false);
    }
  };
  
  const handleCreate = async (userData) => {
    try {
      await api.post('/users/create', userData);
      loadUsers(); // Recargar lista
      alert('Usuario creado exitosamente');
    } catch (error) {
      console.error('Error creating user:', error);
      alert(error.response?.data || 'Error al crear usuario');
    }
  };
  
  const handleDelete = async (userId) => {
    if (!confirm('¿Está seguro de eliminar este usuario?')) return;
    
    try {
      await api.delete('/users/deleteUser', {
        data: { id: userId }
      });
      loadUsers();
      alert('Usuario eliminado exitosamente');
    } catch (error) {
      console.error('Error deleting user:', error);
      alert('Error al eliminar usuario');
    }
  };
  
  // Renderizado según permisos
  if (userRole !== 'SUPERADMIN' && userRole !== 'ADMIN' && userRole !== 'GERENTE') {
    return <div>No tiene permisos para ver esta página</div>;
  }
  
  return (
    <div>
      {/* Tu UI aquí */}
    </div>
  );
};
```

### Ejemplo 2: Crear Mascota

```typescript
// Pets.tsx
const createPet = async (petData) => {
  try {
    const response = await api.post('/pets/create', {
      nombre: petData.nombre,
      tipo: petData.tipo,
      raza: petData.raza,
      edad: petData.edad,
      sexo: petData.sexo,
      color: petData.color,
      cuidadosEspeciales: petData.cuidados,
      ownerIds: petData.ownerIds  // Array de IDs de dueños
    });
    
    // El backend automáticamente asigna el tenant_id del token
    return response.data;
  } catch (error) {
    throw error;
  }
};
```

### Ejemplo 3: Crear Factura

```typescript
// Invoices.tsx
const createInvoice = async (invoiceData) => {
  try {
    const response = await api.post('/invoices/create', {
      clientId: invoiceData.clientId,
      employeeId: invoiceData.employeeId,
      descuento: invoiceData.descuento || 0,
      impuesto: invoiceData.impuesto || 0,
      observaciones: invoiceData.observaciones,
      details: [
        {
          tipo: 'PRODUCTO',
          productId: 1,
          cantidad: 2,
          descuento: 0
        },
        {
          tipo: 'SERVICIO',
          serviceId: 1,
          cantidad: 1,
          descuento: 0
        }
      ]
    });
    
    // El backend:
    // - Reduce stock automáticamente
    // - Calcula totales
    // - Genera número de factura
    // - Asigna tenant_id del token
    
    return response.data;
  } catch (error) {
    if (error.response?.data) {
      alert(error.response.data);
    }
    throw error;
  }
};
```

---

## 🚦 Manejo de Errores

```typescript
// errorHandler.ts
export const handleApiError = (error: any) => {
  if (error.response) {
    // El servidor respondió con un código de error
    switch (error.response.status) {
      case 401:
        // No autenticado
        localStorage.clear();
        window.location.href = '/login';
        return 'Sesión expirada. Por favor inicie sesión nuevamente.';
        
      case 403:
        // Sin permisos
        return 'No tiene permisos para realizar esta acción.';
        
      case 404:
        return 'Recurso no encontrado.';
        
      case 500:
        return 'Error interno del servidor.';
        
      default:
        return error.response.data || 'Error desconocido.';
    }
  } else if (error.request) {
    // La solicitud se hizo pero no hubo respuesta
    return 'No se pudo conectar con el servidor.';
  } else {
    // Algo sucedió al configurar la solicitud
    return error.message;
  }
};
```

---

## 🔐 Protección de Rutas en Frontend

```typescript
// ProtectedRoute.tsx
import { Navigate } from 'react-router-dom';

const ProtectedRoute = ({ children, requiredRoles }) => {
  const token = localStorage.getItem('token');
  const userRole = localStorage.getItem('rolId');
  
  if (!token) {
    return <Navigate to="/login" />;
  }
  
  if (requiredRoles && !requiredRoles.includes(userRole)) {
    return <Navigate to="/unauthorized" />;
  }
  
  return children;
};

// Uso en Routes
<Route 
  path="/users" 
  element={
    <ProtectedRoute requiredRoles={['SUPERADMIN', 'ADMIN', 'GERENTE']}>
      <Users />
    </ProtectedRoute>
  } 
/>

<Route 
  path="/tenants" 
  element={
    <ProtectedRoute requiredRoles={['SUPERADMIN']}>
      <Tenants />
    </ProtectedRoute>
  } 
/>
```

---

## 📊 Dashboard según Rol

```typescript
// Dashboard.tsx
import { useEffect, useState } from 'react';
import api from '../services/axiosConfig';

const Dashboard = () => {
  const [stats, setStats] = useState(null);
  const userRole = localStorage.getItem('rolId');
  const userName = localStorage.getItem('userName');
  const tenantId = localStorage.getItem('tenantId');
  
  useEffect(() => {
    loadDashboard();
  }, []);
  
  const loadDashboard = async () => {
    try {
      const response = await api.get('/dashboard/summary');
      setStats(response.data);
      // El backend automáticamente filtra por el tenant del token
    } catch (error) {
      console.error('Error loading dashboard:', error);
    }
  };
  
  return (
    <div>
      <h1>Dashboard - {userName}</h1>
      <p>Tenant: {tenantId}</p>
      <p>Rol: {userRole}</p>
      
      {stats && (
        <div className="stats-grid">
          <div className="stat-card">
            <h3>Citas Hoy</h3>
            <p>{stats.citasHoy}</p>
          </div>
          
          <div className="stat-card">
            <h3>Total Productos</h3>
            <p>{stats.totalProductos}</p>
          </div>
          
          <div className="stat-card">
            <h3>Ventas Hoy</h3>
            <p>${stats.ventasHoy}</p>
          </div>
          
          <div className="stat-card">
            <h3>Ventas Mes</h3>
            <p>${stats.ventasMes}</p>
          </div>
        </div>
      )}
    </div>
  );
};
```

---

## 🔄 IMPORTANTE: Reiniciar el Backend

Después de configurar CORS, **DEBES REINICIAR** la aplicación Spring Boot:

```bash
# Detener la aplicación (Ctrl + C)
# Luego iniciar de nuevo:
./mvnw.cmd spring-boot:run
```

---

## ✅ Checklist de Integración Frontend

### Headers Requeridos:
- ✅ `Authorization: Bearer <token>` (en todos los endpoints excepto login)
- ✅ `Content-Type: application/json`

### Datos del Login a Guardar:
- ✅ `token` - Para autenticación
- ✅ `userId` - ID del usuario
- ✅ `tenantId` - Empresa del usuario (IMPORTANTE)
- ✅ `rolId` - Rol del usuario (para permisos)
- ✅ `name` - Nombre del usuario
- ✅ `correo` - Email del usuario

### Validaciones en Frontend:
- ✅ Si no hay token → Redirect a /login
- ✅ Si rol no tiene permisos → Mostrar mensaje
- ✅ Si error 401 → Limpiar localStorage y redirect a /login
- ✅ Si error 403 → Mostrar "Sin permisos"

---

## 🎯 Endpoints Principales

### Autenticación
```typescript
POST /api/users/login
Body: { correo, password }
```

### Usuarios (ADMIN, GERENTE, SUPERADMIN)
```typescript
GET /api/users
POST /api/users/create
POST /api/users/getId { id: number }
PUT /api/users/update { userId, ...campos }
DELETE /api/users/deleteUser { id: number }
```

### Mascotas (Todos pueden ver, solo ADMIN/GERENTE crear/modificar)
```typescript
GET /api/pets
POST /api/pets/create
POST /api/pets/getId { id: number }
PUT /api/pets/update { petId, ...campos }
```

### Productos (ADMIN, GERENTE, VENDEDOR)
```typescript
GET /api/products
POST /api/products/create
GET /api/products/lowStock
```

### Facturas (ADMIN, GERENTE, VENDEDOR)
```typescript
POST /api/invoices/create
GET /api/invoices
GET /api/invoices/sales/today
GET /api/invoices/topProducts
```

### Dashboard (Todos)
```typescript
GET /api/dashboard/summary
GET /api/dashboard/alerts
GET /api/dashboard/sales/stats
```

### Tenants (Solo SUPERADMIN)
```typescript
GET /api/tenants
POST /api/tenants/create
GET /api/tenants/{tenantId}
```

---

## ⚠️ IMPORTANTE

### El backend automáticamente:
1. ✅ Extrae `tenant_id` del token JWT
2. ✅ Filtra todos los datos por ese tenant
3. ✅ Valida permisos por rol
4. ✅ Previene acceso cruzado entre tenants

### El frontend NO necesita:
- ❌ Enviar tenant_id en requests (viene del token)
- ❌ Filtrar datos por tenant (el backend lo hace)
- ❌ Validar acceso cruzado (el backend lo previene)

### El frontend SÍ necesita:
- ✅ Incluir `Authorization: Bearer <token>` en headers
- ✅ Enviar datos en formato JSON
- ✅ Manejar errores 401 (no autenticado) y 403 (sin permisos)
- ✅ Mostrar/ocultar opciones según rol del usuario

---

## 🧪 Probar Integración

### 1. Reinicia el Backend
```bash
./mvnw.cmd spring-boot:run
```

### 2. Desde tu Frontend, prueba:
```javascript
// Test de login
const testLogin = async () => {
  try {
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
    console.log('Login exitoso:', data);
    console.log('Token:', data.token);
    console.log('Tenant ID:', data.tenantId);
  } catch (error) {
    console.error('Error:', error);
  }
};

testLogin();
```

### 3. Probar con Token:
```javascript
const testGetUsers = async () => {
  const token = 'TU_TOKEN_DEL_LOGIN';
  
  try {
    const response = await fetch('http://localhost:8090/api/users', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });
    
    const data = await response.json();
    console.log('Usuarios:', data);
  } catch (error) {
    console.error('Error:', error);
  }
};
```

---

## 🎨 Componente de Ejemplo Completo

```typescript
// CreateUserModal.tsx
import { useState } from 'react';
import api from '../services/axiosConfig';

interface CreateUserModalProps {
  onClose: () => void;
  onSuccess: () => void;
}

const CreateUserModal: React.FC<CreateUserModalProps> = ({ onClose, onSuccess }) => {
  const [formData, setFormData] = useState({
    name: '',
    tipoId: 'CC',
    ident: '',
    correo: '',
    telefono: '',
    direccion: '',
    password: '',
    rolId: 'CLIENTE'
  });
  
  const [loading, setLoading] = useState(false);
  
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    
    try {
      await api.post('/users/create', formData);
      alert('Usuario creado exitosamente');
      onSuccess();
      onClose();
    } catch (error: any) {
      alert(error.response?.data || 'Error al crear usuario');
    } finally {
      setLoading(false);
    }
  };
  
  return (
    <div className="modal">
      <form onSubmit={handleSubmit}>
        <h2>Crear Usuario</h2>
        
        <input
          type="text"
          placeholder="Nombre"
          value={formData.name}
          onChange={(e) => setFormData({...formData, name: e.target.value})}
          required
        />
        
        <input
          type="text"
          placeholder="Identificación"
          value={formData.ident}
          onChange={(e) => setFormData({...formData, ident: e.target.value})}
          required
        />
        
        <input
          type="email"
          placeholder="Correo"
          value={formData.correo}
          onChange={(e) => setFormData({...formData, correo: e.target.value})}
          required
        />
        
        <input
          type="password"
          placeholder="Contraseña"
          value={formData.password}
          onChange={(e) => setFormData({...formData, password: e.target.value})}
          required
        />
        
        <select
          value={formData.rolId}
          onChange={(e) => setFormData({...formData, rolId: e.target.value})}
        >
          <option value="CLIENTE">Cliente</option>
          <option value="VENDEDOR">Vendedor</option>
          <option value="VET">Veterinario</option>
          <option value="GERENTE">Gerente</option>
          <option value="ADMIN">Administrador</option>
        </select>
        
        <button type="submit" disabled={loading}>
          {loading ? 'Creando...' : 'Crear Usuario'}
        </button>
        <button type="button" onClick={onClose}>
          Cancelar
        </button>
      </form>
    </div>
  );
};
```

---

## ✨ Beneficios de esta Arquitectura

### Seguridad:
- ✅ Token en el header (no en URL)
- ✅ tenant_id viene del token (no modificable)
- ✅ Backend valida todo automáticamente
- ✅ Frontend solo muestra/oculta según rol

### Simplicidad:
- ✅ Un solo axios config
- ✅ Token se agrega automáticamente
- ✅ No necesitas enviar tenant_id manualmente
- ✅ El backend filtra todo

### Mantenibilidad:
- ✅ Lógica de permisos en un solo lugar
- ✅ Fácil agregar nuevos endpoints
- ✅ Validación centralizada

---

## 🔄 Flujo Completo Frontend → Backend

```
1. Usuario hace login en frontend
   ↓
   POST /api/users/login
   { correo, password }

2. Backend valida y retorna token
   ↓
   { token: "eyJhbGci...", tenantId: "VET001", rolId: "ADMIN" }

3. Frontend guarda en localStorage
   ↓
   localStorage.setItem('token', data.token)

4. Frontend hace request con token
   ↓
   GET /api/users
   Headers: { Authorization: "Bearer eyJhbGci..." }

5. Backend valida token
   ↓
   - JwtAuthenticationFilter extrae tenant_id = "VET001"
   - @RequiresRole valida que sea ADMIN
   - TenantContext.setTenantId("VET001")

6. Backend consulta datos
   ↓
   SELECT * FROM user WHERE tenant_id = 'VET001'

7. Frontend recibe solo datos de VET001
   ↓
   - No puede ver datos de VET002
   - Aislamiento total garantizado
```

---

## 🎉 CORS ESTÁ LISTO

**Ahora tu frontend puede:**
- ✅ Hacer requests al backend
- ✅ Enviar credenciales
- ✅ Recibir datos
- ✅ Sin errores CORS

**Solo necesitas:**
1. Reiniciar el backend Spring Boot
2. Usar axios con el interceptor configurado
3. Guardar el token del login
4. Incluir Authorization header en requests

---

**¡Frontend listo para conectar!** 🚀

