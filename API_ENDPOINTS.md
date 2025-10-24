# API Endpoints para User CRUD

## Base URL
```
http://localhost:8080/api/users
```

## Endpoints Disponibles

### 1. CREATE - Crear Usuario
- **Método:** `POST`
- **URL:** `/api/users`
- **Body:**
```json
{
  "name": "Juan Pérez",
  "tipo_id": "CC",
  "ident": "12345678",
  "correo": "juan@example.com",
  "telefono": "3001234567",
  "direccion": "Calle 123 #45-67",
  "rol_id": "ADMIN"
}
```
- **Respuesta:** Usuario creado con ID generado automáticamente

### 2. READ - Obtener Todos los Usuarios
- **Método:** `GET`
- **URL:** `/api/users`
- **Respuesta:** Array de todos los usuarios

### 3. READ - Obtener Usuario por ID
- **Método:** `GET`
- **URL:** `/api/users/{id}`
- **Ejemplo:** `/api/users/1`
- **Respuesta:** Usuario específico o 404 si no existe

### 4. READ - Obtener Usuarios Activos
- **Método:** `GET`
- **URL:** `/api/users/active`
- **Respuesta:** Array de usuarios activos únicamente

### 5. READ - Buscar Usuarios por Nombre
- **Método:** `GET`
- **URL:** `/api/users/search?name={nombre}`
- **Ejemplo:** `/api/users/search?name=Juan`
- **Respuesta:** Array de usuarios que contengan el nombre

### 6. READ - Obtener Usuarios por Rol
- **Método:** `GET`
- **URL:** `/api/users/role/{rolId}`
- **Ejemplo:** `/api/users/role/ADMIN`
- **Respuesta:** Array de usuarios con el rol especificado

### 7. UPDATE - Actualizar Usuario
- **Método:** `PUT`
- **URL:** `/api/users/{id}`
- **Body:** (solo los campos que quieres actualizar)
```json
{
  "name": "Juan Carlos Pérez",
  "telefono": "3009876543",
  "direccion": "Nueva dirección 456"
}
```
- **Respuesta:** Usuario actualizado

### 8. DELETE - Eliminar Usuario (Soft Delete)
- **Método:** `DELETE`
- **URL:** `/api/users/{id}`
- **Respuesta:** Mensaje de confirmación

### 9. DELETE - Eliminar Usuario Permanentemente
- **Método:** `DELETE`
- **URL:** `/api/users/{id}/permanent`
- **Respuesta:** Mensaje de confirmación

### 10. ACTIVATE - Activar Usuario
- **Método:** `PUT`
- **URL:** `/api/users/{id}/activate`
- **Respuesta:** Usuario activado

### 11. DEACTIVATE - Desactivar Usuario
- **Método:** `PUT`
- **URL:** `/api/users/{id}/deactivate`
- **Respuesta:** Usuario desactivado

### 12. COUNT - Contar Todos los Usuarios
- **Método:** `GET`
- **URL:** `/api/users/count`
- **Respuesta:** Número total de usuarios

### 13. COUNT - Contar Usuarios Activos
- **Método:** `GET`
- **URL:** `/api/users/count/active`
- **Respuesta:** Número de usuarios activos

## Códigos de Respuesta HTTP

- **200 OK:** Operación exitosa
- **201 Created:** Usuario creado exitosamente
- **400 Bad Request:** Error en la validación (email duplicado, etc.)
- **404 Not Found:** Usuario no encontrado
- **500 Internal Server Error:** Error del servidor

## Ejemplo de Uso desde React

```javascript
// Crear usuario
const createUser = async (userData) => {
  const response = await fetch('http://localhost:8080/api/users', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(userData)
  });
  return response.json();
};

// Obtener todos los usuarios
const getUsers = async () => {
  const response = await fetch('http://localhost:8080/api/users');
  return response.json();
};

// Actualizar usuario
const updateUser = async (id, userData) => {
  const response = await fetch(`http://localhost:8080/api/users/${id}`, {
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(userData)
  });
  return response.json();
};

// Eliminar usuario
const deleteUser = async (id) => {
  const response = await fetch(`http://localhost:8080/api/users/${id}`, {
    method: 'DELETE'
  });
  return response.json();
};
```

## Estructura del Modelo User

```json
{
  "user_id": 1,
  "name": "Juan Pérez",
  "tipo_id": "CC",
  "ident": "12345678",
  "correo": "juan@example.com",
  "telefono": "3001234567",
  "direccion": "Calle 123 #45-67",
  "rol_id": "ADMIN",
  "created_on": "2024-01-15T10:30:00",
  "activo": true
}
```

## Notas Importantes

1. **CORS habilitado:** El controlador tiene `@CrossOrigin(origins = "*")` para permitir peticiones desde React
2. **Validaciones:** Se valida que no existan usuarios duplicados por email o identificación
3. **Soft Delete:** Por defecto, la eliminación es lógica (activo = false)
4. **Timestamps:** Se establece automáticamente la fecha de creación
5. **Campos opcionales:** En las actualizaciones, solo se actualizan los campos que no son null
