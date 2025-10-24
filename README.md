# 🏥 Pet Store - Sistema de Gestión Veterinaria (Multi-Tenant)

Sistema completo de gestión para clínicas veterinarias desarrollado con **Spring Boot** y **PostgreSQL** con soporte **Multi-Tenancy** para múltiples empresas.

## 📋 Descripción

Pet Store es una aplicación backend robusta que permite gestionar todos los aspectos de una clínica veterinaria, incluyendo:

- ✅ **Multi-Tenancy** - Múltiples empresas en una sola instancia
- ✅ Gestión de usuarios (empleados, administradores, clientes)
- ✅ Administración de mascotas con múltiples dueños
- ✅ Catálogo de servicios veterinarios
- ✅ Control de inventario de productos
- ✅ Sistema de agendamiento de citas
- ✅ Facturación de productos y servicios
- ✅ Dashboard con estadísticas en tiempo real
- ✅ Alertas de inventario y vencimientos
- ✅ Aislamiento completo de datos por empresa

## 🚀 Tecnologías

- **Java 25**
- **Spring Boot 3.5.6**
- **PostgreSQL 42.7.7**
- **Spring Data JPA**
- **Lombok**
- **Spring Security (Crypto & JWT)**
- **Swagger/OpenAPI 2.8.6**
- **Maven**
- **Multi-Tenancy Architecture**

## 📁 Estructura del Proyecto

```
pet_store/
├── src/main/java/com/cipasuno/petstore/pet_store/
│   ├── config/              # Configuraciones
│   │   ├── AppConfig.java
│   │   ├── EnvConfig.java
│   │   ├── JwtTokenProvider.java
│   │   └── PasswordConfig.java
│   │
│   ├── models/              # Entidades JPA
│   │   ├── User.java
│   │   ├── Pet.java
│   │   ├── PetOwner.java
│   │   ├── Service.java
│   │   ├── Product.java
│   │   ├── Appointment.java
│   │   ├── Invoice.java
│   │   ├── InvoiceDetail.java
│   │   └── DTOs/            # Data Transfer Objects
│   │
│   ├── repositories/        # Repositorios JPA
│   │   ├── UserRepository.java
│   │   ├── PetRepository.java
│   │   ├── PetOwnerRepository.java
│   │   ├── ServiceRepository.java
│   │   ├── ProductRepository.java
│   │   ├── AppointmentRepository.java
│   │   ├── InvoiceRepository.java
│   │   └── InvoiceDetailRepository.java
│   │
│   ├── services/            # Lógica de negocio
│   │   ├── UserService.java
│   │   ├── LoginService.java
│   │   ├── TokenService.java
│   │   ├── PetService.java
│   │   ├── VeterinaryService.java
│   │   ├── ProductService.java
│   │   ├── AppointmentService.java
│   │   └── InvoiceService.java
│   │
│   ├── controllers/         # Endpoints REST
│   │   ├── UserController.java
│   │   ├── PetController.java
│   │   ├── ServiceController.java
│   │   ├── ProductController.java
│   │   ├── AppointmentController.java
│   │   ├── InvoiceController.java
│   │   └── DashboardController.java
│   │
│   └── PetStoreApplication.java
│
├── src/main/resources/
│   ├── application.properties
│   └── static/
│
├── pom.xml
├── README.md
└── API_DOCUMENTATION.md
```

## 🔧 Instalación

### Prerrequisitos

- Java 25 o superior
- PostgreSQL 12 o superior
- Maven 3.8 o superior

### Pasos

1. **Clonar el repositorio**
```bash
git clone <repository-url>
cd pet_store
```

2. **Crear la base de datos**
```sql
CREATE DATABASE petstore;
```

3. **Configurar variables de entorno**

Crear archivo `.env` en la raíz:
```properties
DB_HOST=localhost
DB_PORT=5432
DB_NAME=petstore
DB_USERNAME=postgres
DB_PASSWORD=tu_password
DDL_AUTO=update
SHOW_SQL=true
DEBUG=false
```

4. **Compilar el proyecto**
```bash
./mvnw clean install
```

5. **Ejecutar la aplicación**
```bash
./mvnw spring-boot:run
```

La aplicación estará disponible en: `http://localhost:8090`

## 📚 Documentación API

### Swagger UI
Accede a la documentación interactiva de la API:
```
http://localhost:8090/swagger-ui.html
```

### Documentación Completa
- Ver [API_DOCUMENTATION.md](./API_DOCUMENTATION.md) para guía completa de todos los endpoints
- Ver [MULTI_TENANT_DOCUMENTATION.md](./MULTI_TENANT_DOCUMENTATION.md) para arquitectura Multi-Tenant

### Scripts de Base de Datos
- Ver [database_schema.sql](./database_schema.sql) para schema básico
- Ver [MULTI_TENANT_SCHEMA.sql](./MULTI_TENANT_SCHEMA.sql) para schema con Multi-Tenancy **(RECOMENDADO)**

## 🎯 Módulos Principales

### 1. 👥 Gestión de Usuarios
- CRUD completo de usuarios
- Sistema de roles (Admin, Empleado, Cliente)
- Autenticación con JWT
- Encriptación de contraseñas con BCrypt

**Endpoint base**: `/api/users`

### 2. 🐾 Gestión de Mascotas
- Registro de mascotas con información detallada
- Relación muchos a muchos con dueños
- Una mascota puede tener múltiples dueños
- Búsqueda por tipo, nombre, dueño

**Endpoint base**: `/api/pets`

### 3. 🏥 Servicios Veterinarios
- Catálogo de servicios (consultas, baños, vacunas, etc.)
- Precio y duración estimada
- Gestión de servicios activos/inactivos

**Endpoint base**: `/api/services`

### 4. 📦 Inventario de Productos
- Control de stock en tiempo real
- Alertas de stock bajo
- Alertas de productos próximos a vencer
- Gestión de precios y presentaciones

**Endpoint base**: `/api/products`

### 5. 📅 Sistema de Citas
- Agendamiento de citas veterinarias
- Estados: Programada, En Proceso, Completada, Cancelada
- Asignación de veterinarios
- Registro de diagnósticos
- Historial de citas por mascota

**Endpoint base**: `/api/appointments`

### 6. 💰 Facturación
- Facturación de productos y servicios
- Cálculo automático de totales, descuentos e impuestos
- Reducción automática de stock
- Numeración automática de facturas
- Estadísticas de ventas
- Top productos y servicios más vendidos

**Endpoint base**: `/api/invoices`

### 7. 📊 Dashboard
- Resumen general de la clínica
- Citas del día
- Ventas diarias y mensuales
- Alertas de inventario
- Productos más vendidos
- Estadísticas en tiempo real

**Endpoint base**: `/api/dashboard`

## 🔐 Seguridad

- **Encriptación de contraseñas**: BCrypt
- **Tokens JWT**: Para autenticación
- **CORS configurado**: Ajustar para producción
- **Soft Delete**: Preserva datos históricos

## 📊 Base de Datos

### Diagrama ER Simplificado

```
┌─────────┐        ┌───────────┐        ┌─────┐
│  User   │◄──────►│ PetOwner  │◄──────►│ Pet │
└─────────┘        └───────────┘        └─────┘
     │                                      │
     │                                      │
     ▼                                      ▼
┌──────────────┐                    ┌─────────────┐
│ Appointment  │                    │ Appointment │
└──────────────┘                    └─────────────┘
     │                                      │
     ▼                                      ▼
┌─────────┐                          ┌─────────┐
│ Service │                          │ Service │
└─────────┘                          └─────────┘

┌─────────┐        ┌────────────────┐        ┌─────────┐
│ Invoice │◄───────│ InvoiceDetail  │───────►│ Product │
└─────────┘        └────────────────┘        └─────────┘
     │                     │
     │                     └───────────────────►┌─────────┐
     │                                          │ Service │
     ▼                                          └─────────┘
┌─────────┐
│  User   │
│(Cliente)│
└─────────┘
```

### Tablas Principales

- `user` - Usuarios del sistema
- `pet` - Mascotas registradas
- `pet_owner` - Relación mascotas-dueños
- `service` - Servicios veterinarios
- `product` - Productos en inventario
- `appointment` - Citas programadas
- `invoice` - Facturas emitidas
- `invoice_detail` - Detalles de facturas

## 🎨 Características Especiales

### 1. Gestión Inteligente de Stock
- Reducción automática al facturar
- Restauración al anular facturas
- Alertas configurables de stock mínimo

### 2. Sistema de Alertas
- **Stock Bajo**: Productos debajo del mínimo
- **Vencimientos**: Productos que vencen en 30 días
- Dashboard centralizado de alertas

### 3. Facturación Avanzada
- Soporte para productos y servicios en una misma factura
- Descuentos por ítem y generales
- Cálculo automático de impuestos
- Numeración secuencial automática

### 4. Historial Completo
- Todas las citas de una mascota
- Diagnósticos y tratamientos
- Historial de compras por cliente

### 5. Estadísticas en Tiempo Real
- Ventas diarias y mensuales
- Productos más vendidos
- Servicios más solicitados
- Métricas de rendimiento

## 🧪 Testing

### Endpoints de Prueba

#### 1. Crear Usuario Administrador
```bash
POST http://localhost:8090/api/users/create
Content-Type: application/json

{
  "name": "Admin Sistema",
  "tipoId": "CC",
  "ident": "12345678",
  "correo": "admin@petstore.com",
  "telefono": "3001234567",
  "direccion": "Calle 123 #45-67",
  "password": "admin123",
  "rolId": "ADMIN"
}
```

#### 2. Login
```bash
POST http://localhost:8090/api/users/login
Content-Type: application/json

{
  "correo": "admin@petstore.com",
  "password": "admin123"
}
```

#### 3. Crear Mascota
```bash
POST http://localhost:8090/api/pets/create
Content-Type: application/json

{
  "nombre": "Firulais",
  "tipo": "Perro",
  "raza": "Labrador",
  "edad": 3,
  "sexo": "Macho",
  "color": "Dorado",
  "cuidadosEspeciales": "Alergias a ciertos alimentos",
  "ownerIds": [1]
}
```

#### 4. Ver Dashboard
```bash
GET http://localhost:8090/api/dashboard/summary
```

## 📈 Roadmap

### Versión Actual (1.0.0)
- ✅ CRUD completo de todos los módulos
- ✅ Sistema de autenticación
- ✅ Facturación básica
- ✅ Dashboard con estadísticas
- ✅ Alertas de inventario

### Futuras Mejoras (2.0.0)
- [ ] Spring Security completo con interceptores
- [ ] Paginación en listados
- [ ] Filtros avanzados
- [ ] Reportes en PDF
- [ ] Notificaciones por email
- [ ] Sistema de recordatorios de citas
- [ ] Exportación de datos a Excel
- [ ] Backup automático
- [ ] Auditoría completa
- [ ] Dashboard con gráficos más avanzados

## 🤝 Contribución

Este proyecto es parte de un trabajo académico. Para contribuir:

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📝 Notas Importantes

### Configuración de Producción

Para producción, asegúrate de:

1. Cambiar `DDL_AUTO=validate` o `none`
2. Configurar CORS específico
3. Implementar rate limiting
4. Usar HTTPS
5. Configurar backups automáticos
6. Implementar monitoreo
7. Configurar logs apropiados

### Seguridad

- Las contraseñas se encriptan con BCrypt
- Los tokens JWT tienen expiración configurable
- Se implementa soft delete para preservar datos
- Validaciones básicas en todos los endpoints

## 🐛 Problemas Conocidos

Actualmente no hay problemas conocidos. Si encuentras alguno, por favor repórtalo en la sección de Issues.

## 📞 Contacto

**Universidad**: CIPASUNO  
**Proyecto**: Sistema de Gestión Veterinaria  
**Fecha**: Octubre 2025

## 📄 Licencia

Este proyecto es parte de un trabajo académico de la Universidad CIPASUNO.

---

## 🎓 Información Académica

**Materia**: Aprendizaje Automatizado  
**Semestre**: Séptimo  
**Año**: 2025

---

**⭐ Si este proyecto te fue útil, considera darle una estrella en GitHub!**

