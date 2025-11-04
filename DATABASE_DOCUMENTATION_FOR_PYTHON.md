# 📊 DOCUMENTACIÓN BASE DE DATOS - PET STORE

## 🎯 Para implementar: Chatbot + Dashboard con Red Neuronal

---

## 📌 **Información de Conexión**

```python
# PostgreSQL Database Configuration
DB_HOST = "gondola.proxy.rlwy.net"
DB_PORT = 22967
DB_NAME = "railway"
DB_USER = "postgres"
DB_PASSWORD = "LpEGFItXIhiOLcvpeWczptlFPxYnxhhI"

# Ejemplo de conexión con psycopg2
import psycopg2

conn = psycopg2.connect(
    host=DB_HOST,
    port=DB_PORT,
    database=DB_NAME,
    user=DB_USER,
    password=DB_PASSWORD,
    sslmode="prefer"
)

# Ejemplo con SQLAlchemy
from sqlalchemy import create_engine

DATABASE_URL = f"postgresql://{DB_USER}:{DB_PASSWORD}@{DB_HOST}:{DB_PORT}/{DB_NAME}?sslmode=prefer"
engine = create_engine(DATABASE_URL)
```

---

## 🗂️ **ESTRUCTURA DE TABLAS**

### **1. TABLA: `client` - Clientes/Propietarios**

Almacena información de los clientes que son dueños de mascotas.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `client_id` | INTEGER (PK) | ID único del cliente |
| `tenant_id` | VARCHAR | ID del tenant (multi-tenancy) |
| `name` | VARCHAR(200) | Nombre completo del cliente |
| `tipo_id` | VARCHAR(10) | Tipo de documento (CC, TI, CE, NIT) |
| `ident` | VARCHAR(50) | Número de identificación (UNIQUE) |
| `correo` | VARCHAR(100) | Email del cliente (UNIQUE) |
| `telefono` | VARCHAR(20) | Número telefónico |
| `direccion` | VARCHAR(300) | Dirección física |
| `observaciones` | VARCHAR(500) | Notas adicionales |
| `activo` | BOOLEAN | Si el cliente está activo |
| `created_on` | TIMESTAMP | Fecha de registro |

**Uso para IA:**
- Segmentación de clientes
- Frecuencia de visitas por cliente
- Clientes con más mascotas

---

### **2. TABLA: `pet` - Mascotas**

Información de las mascotas registradas.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `pet_id` | INTEGER (PK) | ID único de la mascota |
| `tenant_id` | VARCHAR | ID del tenant |
| `nombre` | VARCHAR | Nombre de la mascota |
| `tipo` | VARCHAR | **Tipo de mascota** (perro, gato, ave, conejo, etc.) |
| `raza` | VARCHAR | Raza específica |
| `cuidados_especiales` | VARCHAR(500) | Cuidados especiales requeridos |
| `edad` | INTEGER | Edad en años |
| `sexo` | VARCHAR(10) | Macho/Hembra |
| `color` | VARCHAR | Color predominante |
| `activo` | BOOLEAN | Si está activa en el sistema |
| `created_on` | TIMESTAMP | Fecha de registro |

**⭐ USO CLAVE PARA RED NEURONAL:**
- **Campo `tipo`**: Analizar qué tipo de mascota recibe más servicios
- **Campo `raza`**: Razas más comunes por servicio
- **Campo `edad`**: Correlación edad-servicios

---

### **3. TABLA: `pet_owner` - Relación Cliente-Mascota**

Tabla intermedia que conecta clientes con sus mascotas.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `pet_id` | INTEGER (PK, FK) | ID de la mascota |
| `client_id` | INTEGER (PK, FK) | ID del cliente propietario |
| `created_on` | TIMESTAMP | Fecha de asociación |

---

### **4. TABLA: `service` - Servicios Ofrecidos**

Catálogo de servicios disponibles (consultas, baños, desparasitación, etc.)

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `service_id` | INTEGER (PK) | ID único del servicio |
| `tenant_id` | INTEGER | ID del tenant |
| `codigo` | VARCHAR | Código del servicio |
| `nombre` | VARCHAR | **Nombre del servicio** |
| `descripcion` | VARCHAR(500) | Descripción detallada |
| `precio` | DECIMAL(10,2) | Precio del servicio |
| `duracion_minutos` | INTEGER | Duración aproximada |
| `activo` | BOOLEAN | Si está disponible |
| `created_on` | TIMESTAMP | Fecha de creación |

**⭐ USO CLAVE PARA RED NEURONAL:**
- **Campo `nombre`**: Identificar servicios más solicitados
- Correlación servicio-tipo de mascota
- Análisis de precios vs demanda

---

### **5. TABLA: `appointment` - Citas Agendadas**

Registro de todas las citas agendadas (programadas, completadas, canceladas).

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `appointment_id` | INTEGER (PK) | ID único de la cita |
| `tenant_id` | INTEGER | ID del tenant |
| `pet_id` | INTEGER (FK) | Mascota que recibe el servicio |
| `service_id` | INTEGER (FK) | Servicio agendado |
| `client_id` | INTEGER (FK) | Cliente que agenda |
| `veterinarian_id` | INTEGER (FK) | Veterinario asignado (opcional) |
| `fecha_hora` | TIMESTAMP | **Fecha y hora de la cita** |
| `estado` | VARCHAR(20) | PROGRAMADA, COMPLETADA, CANCELADA, EN_PROCESO, FACTURADA |
| `observaciones` | VARCHAR(1000) | Notas del cliente |
| `diagnostico` | VARCHAR(1000) | Diagnóstico del veterinario |
| `activo` | BOOLEAN | Si está activa |
| `created_on` | TIMESTAMP | Fecha de creación |

**⭐ USO CLAVE PARA RED NEURONAL:**
- **Campo `fecha_hora`**: 
  - Extraer día de la semana → Días con más atención
  - Extraer hora → Horas pico
  - Extraer mes → Temporadas de alta demanda
- **Campo `estado`**: Tasa de asistencia vs cancelación
- Relación con `service_id` → Servicios más agendados
- Relación con `pet_id` + `tipo` → Tipos de mascota por día

---

### **6. TABLA: `vaccination` - Vacunaciones Aplicadas**

Historial de vacunas aplicadas a las mascotas.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `vaccination_id` | INTEGER (PK) | ID único de la vacunación |
| `tenant_id` | VARCHAR | ID del tenant |
| `pet_id` | INTEGER (FK) | Mascota vacunada |
| `veterinarian_id` | INTEGER (FK) | Veterinario que aplicó |
| `vaccine_name` | VARCHAR(200) | Nombre de la vacuna |
| `vaccine_type` | VARCHAR(100) | Tipo (Viral, Bacteriana, Antiparasitaria) |
| `manufacturer` | VARCHAR(200) | Fabricante/laboratorio |
| `application_date` | DATE | **Fecha de aplicación** |
| `next_dose_date` | DATE | Fecha de próxima dosis |
| `estado` | VARCHAR(20) | APLICADA, FACTURADA, CANCELADA |
| `activo` | BOOLEAN | Si está activa |
| `created_on` | TIMESTAMP | Fecha de registro |

**⭐ USO PARA RED NEURONAL:**
- Vacunas más aplicadas
- Temporadas de vacunación
- Relación mascota-vacuna

---

### **7. TABLA: `pet_medical_history` - Historial Médico**

Registro completo de atenciones médicas.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `history_id` | INTEGER (PK) | ID único del historial |
| `tenant_id` | VARCHAR | ID del tenant |
| `pet_id` | INTEGER (FK) | Mascota atendida |
| `appointment_id` | INTEGER (FK) | Cita relacionada (opcional) |
| `service_id` | INTEGER (FK) | Servicio realizado |
| `veterinarian_id` | INTEGER (FK) | Veterinario que atendió |
| `fecha_atencion` | TIMESTAMP | **Fecha de la atención** |
| `tipo_procedimiento` | VARCHAR(100) | Consulta, Desparasitación, Baño, etc. |
| `diagnostico` | VARCHAR(2000) | Diagnóstico médico |
| `tratamiento` | VARCHAR(2000) | Tratamiento aplicado |
| `activo` | BOOLEAN | Si está activo |
| `created_on` | TIMESTAMP | Fecha de registro |

**⭐ USO PARA CHATBOT:**
- Historial completo de la mascota
- Diagnósticos comunes
- Tratamientos frecuentes

---

### **8. TABLA: `invoice` - Facturas Emitidas**

Registro de todas las facturas generadas.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `invoice_id` | INTEGER (PK) | ID único de la factura |
| `tenant_id` | INTEGER | ID del tenant |
| `numero` | VARCHAR | Número de factura |
| `client_id` | INTEGER (FK) | Cliente facturado |
| `employee_id` | INTEGER (FK) | Empleado que facturó |
| `fecha_emision` | TIMESTAMP | **Fecha de emisión** |
| `subtotal` | DECIMAL(10,2) | Subtotal |
| `descuento` | DECIMAL(10,2) | Descuento aplicado |
| `impuesto` | DECIMAL(10,2) | IVA u otros |
| `total` | DECIMAL(10,2) | **Total facturado** |
| `estado` | VARCHAR(20) | PAGADA, PENDIENTE, ANULADA |
| `activo` | BOOLEAN | Si está activa |
| `created_on` | TIMESTAMP | Fecha de creación |

**⭐ USO PARA RED NEURONAL:**
- Ingresos por período
- Tickets promedio
- Tendencias de facturación

---

### **9. TABLA: `invoice_detail` - Detalles de Factura**

Líneas de detalle de cada factura (productos, servicios, vacunaciones, citas).

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `detail_id` | INTEGER (PK) | ID único del detalle |
| `invoice_id` | INTEGER (FK) | Factura asociada |
| `product_id` | INTEGER (FK) | Producto vendido (si aplica) |
| `service_id` | INTEGER (FK) | Servicio facturado (si aplica) |
| `vaccination_id` | INTEGER (FK) | Vacunación facturada (si aplica) |
| `appointment_id` | INTEGER (FK) | Cita facturada (si aplica) |
| `tipo` | VARCHAR(15) | **PRODUCTO, SERVICIO, VACUNACION, CITA** |
| `cantidad` | INTEGER | Cantidad |
| `precio_unitario` | DECIMAL(10,2) | Precio unitario |
| `descuento` | DECIMAL(10,2) | Descuento |
| `subtotal` | DECIMAL(10,2) | Subtotal del detalle |

**⭐ USO CLAVE PARA RED NEURONAL:**
- **Campo `tipo`**: Contar qué tipo de items se facturan más
- Relación con `service_id` → Servicios más rentables
- Análisis de productos vs servicios

---

### **10. TABLA: `product` - Productos del Inventario**

Catálogo de productos disponibles (alimentos, medicinas, accesorios).

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `product_id` | INTEGER (PK) | ID único del producto |
| `tenant_id` | INTEGER | ID del tenant |
| `codigo` | VARCHAR | Código del producto |
| `nombre` | VARCHAR | Nombre del producto |
| `descripcion` | VARCHAR(500) | Descripción |
| `precio` | DECIMAL(10,2) | Precio de venta |
| `stock` | INTEGER | Stock actual |
| `es_vacuna` | BOOLEAN | Si es una vacuna |
| `activo` | BOOLEAN | Si está activo |
| `created_on` | TIMESTAMP | Fecha de creación |

**⭐ USO PARA RED NEURONAL:**
- Productos más vendidos
- Gestión de inventario
- Predicción de demanda

---

### **11. TABLA: `user` - Usuarios del Sistema**

Empleados, veterinarios, administradores.

| Campo | Tipo | Descripción |
|-------|------|-------------|
| `user_id` | INTEGER (PK) | ID único del usuario |
| `tenant_id` | VARCHAR | ID del tenant |
| `name` | VARCHAR | Nombre completo |
| `correo` | VARCHAR | Email |
| `password` | VARCHAR | Contraseña (encriptada) |
| `rol_id` | VARCHAR | Rol del usuario |
| `activo` | BOOLEAN | Si está activo |
| `created_on` | TIMESTAMP | Fecha de creación |

**Uso para análisis:**
- Productividad por empleado
- Veterinarios más activos

---

## 🔗 **DIAGRAMA DE RELACIONES**

```
┌─────────────┐
│   CLIENT    │──────┬──────────────────────────────────────┐
└─────────────┘      │                                      │
       │             │                                      │
       │ 1:N         │ N:M (via pet_owner)                 │
       │             │                                      │
       ▼             ▼                                      │
┌─────────────┐  ┌─────────────┐                          │
│ APPOINTMENT │  │     PET     │                          │
└─────────────┘  └─────────────┘                          │
       │                │                                   │
       │                │ 1:N                              │
       │                ├────────────┬────────────┬────────┴────────┐
       │                │            │            │                 │
       │                ▼            ▼            ▼                 ▼
       │         ┌─────────────┐ ┌──────────┐ ┌───────────────┐ ┌──────────┐
       │         │ VACCINATION │ │ MEDICAL  │ │  APPOINTMENT  │ │ INVOICE  │
       │         │             │ │ HISTORY  │ │               │ │          │
       │         └─────────────┘ └──────────┘ └───────────────┘ └──────────┘
       │                │            │              │                 │
       │                │            │              │                 │ 1:N
       │                │            │              │                 ▼
       │                │            │              │           ┌──────────────┐
       │                └────────────┴──────────────┴──────────▶│INVOICE_DETAIL│
       │                                                         └──────────────┘
       │                                                               │
       │                                                               │
       ▼                                                               │
┌─────────────┐                                                       │
│   SERVICE   │◀──────────────────────────────────────────────────────┘
└─────────────┘
       ▲
       │
┌─────────────┐
│   PRODUCT   │
└─────────────┘
```

---

## 🔍 **CONSULTAS SQL PARA ANÁLISIS DE RED NEURONAL**

### **1. Servicios más utilizados**

```sql
-- Ranking de servicios por cantidad de citas
SELECT 
    s.nombre AS servicio,
    COUNT(a.appointment_id) AS total_citas,
    COUNT(CASE WHEN a.estado = 'COMPLETADA' THEN 1 END) AS citas_completadas,
    COUNT(CASE WHEN a.estado = 'CANCELADA' THEN 1 END) AS citas_canceladas,
    ROUND(AVG(s.precio::numeric), 2) AS precio_promedio
FROM appointment a
JOIN service s ON a.service_id = s.service_id
WHERE a.activo = true
GROUP BY s.service_id, s.nombre
ORDER BY total_citas DESC;
```

**Python (Pandas):**
```python
import pandas as pd

query = """
SELECT s.nombre AS servicio, COUNT(a.appointment_id) AS total_citas
FROM appointment a
JOIN service s ON a.service_id = s.service_id
WHERE a.activo = true
GROUP BY s.nombre
ORDER BY total_citas DESC;
"""

df_servicios = pd.read_sql(query, conn)
print(df_servicios)
```

---

### **2. Tipo de mascota que más va a servicios**

```sql
-- Análisis por tipo de mascota (perro, gato, ave, etc.)
SELECT 
    p.tipo AS tipo_mascota,
    COUNT(DISTINCT p.pet_id) AS total_mascotas,
    COUNT(a.appointment_id) AS total_citas,
    ROUND(COUNT(a.appointment_id)::numeric / COUNT(DISTINCT p.pet_id), 2) AS citas_por_mascota,
    s.nombre AS servicio_mas_comun
FROM pet p
JOIN appointment a ON p.pet_id = a.pet_id
JOIN service s ON a.service_id = s.service_id
WHERE p.activo = true AND a.activo = true
GROUP BY p.tipo, s.nombre
ORDER BY total_citas DESC
LIMIT 10;
```

**Para Red Neuronal - Crear dataset:**
```python
query = """
SELECT 
    p.tipo AS tipo_mascota,
    p.raza,
    p.edad,
    p.sexo,
    s.nombre AS servicio,
    a.fecha_hora,
    EXTRACT(HOUR FROM a.fecha_hora) AS hora,
    EXTRACT(DOW FROM a.fecha_hora) AS dia_semana,
    EXTRACT(MONTH FROM a.fecha_hora) AS mes,
    a.estado
FROM appointment a
JOIN pet p ON a.pet_id = p.pet_id
JOIN service s ON a.service_id = s.service_id
WHERE a.activo = true AND p.activo = true;
"""

df_citas = pd.read_sql(query, conn)
df_citas.to_csv('dataset_citas_mascotas.csv', index=False)
```

---

### **3. Días con más atención**

```sql
-- Análisis por día de la semana
SELECT 
    CASE EXTRACT(DOW FROM a.fecha_hora)
        WHEN 0 THEN 'Domingo'
        WHEN 1 THEN 'Lunes'
        WHEN 2 THEN 'Martes'
        WHEN 3 THEN 'Miércoles'
        WHEN 4 THEN 'Jueves'
        WHEN 5 THEN 'Viernes'
        WHEN 6 THEN 'Sábado'
    END AS dia_semana,
    EXTRACT(DOW FROM a.fecha_hora) AS numero_dia,
    COUNT(a.appointment_id) AS total_citas,
    COUNT(CASE WHEN a.estado = 'COMPLETADA' THEN 1 END) AS citas_completadas,
    ROUND(AVG(EXTRACT(HOUR FROM a.fecha_hora)), 2) AS hora_promedio
FROM appointment a
WHERE a.activo = true
GROUP BY EXTRACT(DOW FROM a.fecha_hora)
ORDER BY numero_dia;
```

```sql
-- Análisis por hora del día
SELECT 
    EXTRACT(HOUR FROM a.fecha_hora) AS hora,
    COUNT(a.appointment_id) AS total_citas,
    COUNT(DISTINCT a.pet_id) AS mascotas_unicas,
    COUNT(DISTINCT a.client_id) AS clientes_unicos
FROM appointment a
WHERE a.activo = true
GROUP BY hora
ORDER BY hora;
```

```sql
-- Análisis por mes (temporadas altas)
SELECT 
    EXTRACT(YEAR FROM a.fecha_hora) AS año,
    EXTRACT(MONTH FROM a.fecha_hora) AS mes,
    COUNT(a.appointment_id) AS total_citas,
    COUNT(DISTINCT a.pet_id) AS mascotas_atendidas,
    SUM(i.total) AS ingresos_mes
FROM appointment a
LEFT JOIN invoice i ON a.appointment_id = i.appointment_id
WHERE a.activo = true
GROUP BY año, mes
ORDER BY año, mes;
```

---

### **4. Dataset completo para Machine Learning**

```sql
-- Crear dataset para predicción de demanda
SELECT 
    -- Fecha y tiempo
    a.fecha_hora AS fecha_cita,
    EXTRACT(YEAR FROM a.fecha_hora) AS año,
    EXTRACT(MONTH FROM a.fecha_hora) AS mes,
    EXTRACT(DAY FROM a.fecha_hora) AS dia,
    EXTRACT(DOW FROM a.fecha_hora) AS dia_semana,
    EXTRACT(HOUR FROM a.fecha_hora) AS hora,
    
    -- Información del servicio
    s.service_id,
    s.nombre AS servicio,
    s.precio AS precio_servicio,
    s.duracion_minutos,
    
    -- Información de la mascota
    p.tipo AS tipo_mascota,
    p.raza,
    p.edad AS edad_mascota,
    p.sexo AS sexo_mascota,
    
    -- Información del cliente
    c.client_id,
    
    -- Estado de la cita
    a.estado,
    CASE WHEN a.estado = 'COMPLETADA' THEN 1 ELSE 0 END AS asistio,
    
    -- Facturación
    i.total AS monto_facturado,
    id.tipo AS tipo_facturacion
    
FROM appointment a
JOIN service s ON a.service_id = s.service_id
JOIN pet p ON a.pet_id = p.pet_id
JOIN client c ON a.client_id = c.client_id
LEFT JOIN invoice i ON a.appointment_id = i.appointment_id
LEFT JOIN invoice_detail id ON i.invoice_id = id.invoice_id
WHERE a.activo = true
ORDER BY a.fecha_hora;
```

---

### **5. Análisis de ingresos por servicio**

```sql
-- Rentabilidad por servicio
SELECT 
    s.nombre AS servicio,
    COUNT(id.detail_id) AS veces_facturado,
    SUM(id.subtotal) AS ingresos_totales,
    AVG(id.subtotal) AS ingreso_promedio,
    MIN(id.precio_unitario) AS precio_minimo,
    MAX(id.precio_unitario) AS precio_maximo
FROM invoice_detail id
JOIN service s ON id.service_id = s.service_id
WHERE id.tipo = 'SERVICIO'
GROUP BY s.service_id, s.nombre
ORDER BY ingresos_totales DESC;
```

---

## 🤖 **CASOS DE USO PARA CHATBOT**

### **1. Consultar historial de una mascota**

```python
def obtener_historial_mascota(pet_id):
    query = f"""
    SELECT 
        p.nombre AS mascota,
        pmh.fecha_atencion,
        s.nombre AS servicio,
        pmh.tipo_procedimiento,
        pmh.diagnostico,
        pmh.tratamiento,
        u.name AS veterinario
    FROM pet_medical_history pmh
    JOIN pet p ON pmh.pet_id = p.pet_id
    JOIN service s ON pmh.service_id = s.service_id
    JOIN "user" u ON pmh.veterinarian_id = u.user_id
    WHERE p.pet_id = {pet_id} AND pmh.activo = true
    ORDER BY pmh.fecha_atencion DESC;
    """
    return pd.read_sql(query, conn)
```

### **2. Próximas vacunas pendientes**

```python
def vacunas_pendientes(pet_id):
    query = f"""
    SELECT 
        p.nombre AS mascota,
        v.vaccine_name,
        v.next_dose_date AS proxima_dosis,
        CASE 
            WHEN v.next_dose_date < CURRENT_DATE THEN 'VENCIDA'
            WHEN v.next_dose_date <= CURRENT_DATE + INTERVAL '7 days' THEN 'URGENTE'
            ELSE 'PROGRAMADA'
        END AS urgencia
    FROM vaccination v
    JOIN pet p ON v.pet_id = p.pet_id
    WHERE p.pet_id = {pet_id} 
      AND v.requires_booster = true 
      AND v.next_dose_date IS NOT NULL
      AND v.activo = true
    ORDER BY v.next_dose_date;
    """
    return pd.read_sql(query, conn)
```

### **3. Recomendaciones personalizadas**

```python
def servicios_recomendados(pet_id):
    """
    Basado en el historial y tipo de mascota,
    recomienda servicios que aún no ha tomado
    """
    query = f"""
    SELECT s.nombre, s.descripcion, s.precio
    FROM service s
    WHERE s.activo = true
      AND s.service_id NOT IN (
          SELECT DISTINCT a.service_id
          FROM appointment a
          WHERE a.pet_id = {pet_id}
      )
    ORDER BY s.precio;
    """
    return pd.read_sql(query, conn)
```

---

## 🧠 **MODELOS DE RED NEURONAL SUGERIDOS**

### **1. Predicción de demanda de servicios**

**Objetivo:** Predecir cuántas citas habrá por día/hora

**Features (X):**
- Día de la semana (0-6)
- Mes del año (1-12)
- Hora del día (0-23)
- Tipo de servicio (one-hot encoding)

**Target (y):**
- Cantidad de citas

```python
# Ejemplo con TensorFlow/Keras
from tensorflow import keras
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import StandardScaler

# Preparar datos
df = pd.read_sql(query, conn)
X = df[['dia_semana', 'mes', 'hora', 'service_id']]
y = df['total_citas']

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2)

# Modelo simple
model = keras.Sequential([
    keras.layers.Dense(64, activation='relu', input_shape=(X_train.shape[1],)),
    keras.layers.Dense(32, activation='relu'),
    keras.layers.Dense(1)
])

model.compile(optimizer='adam', loss='mse', metrics=['mae'])
model.fit(X_train, y_train, epochs=50, validation_split=0.2)
```

---

### **2. Clasificación de tipo de mascota por servicio**

**Objetivo:** Predecir qué tipo de mascota es más probable para un servicio

**Features:**
- Servicio solicitado
- Hora del día
- Día de la semana

**Target:**
- Tipo de mascota (perro, gato, ave, etc.)

```python
from sklearn.ensemble import RandomForestClassifier

# Preparar datos
df = pd.read_sql(query_clasificacion, conn)
X = df[['service_id', 'hora', 'dia_semana']]
y = df['tipo_mascota']

# Modelo
clf = RandomForestClassifier(n_estimators=100)
clf.fit(X_train, y_train)

# Importancia de features
importancia = pd.DataFrame({
    'feature': X.columns,
    'importance': clf.feature_importances_
}).sort_values('importance', ascending=False)
```

---

### **3. Análisis de sentimiento (Chatbot)**

Si implementas un campo para comentarios/feedback:

```python
from transformers import pipeline

sentiment_analyzer = pipeline("sentiment-analysis", 
                             model="nlptown/bert-base-multilingual-uncased-sentiment")

def analizar_comentarios():
    query = "SELECT observaciones FROM appointment WHERE observaciones IS NOT NULL"
    df = pd.read_sql(query, conn)
    
    df['sentimiento'] = df['observaciones'].apply(
        lambda x: sentiment_analyzer(x)[0]['label']
    )
    return df
```

---

## 📊 **EJEMPLOS DE VISUALIZACIONES PARA DASHBOARD**

```python
import matplotlib.pyplot as plt
import seaborn as sns

# 1. Gráfico de servicios más usados
df_servicios = pd.read_sql(query_servicios, conn)
plt.figure(figsize=(10, 6))
sns.barplot(data=df_servicios, x='total_citas', y='servicio')
plt.title('Servicios Más Utilizados')
plt.xlabel('Cantidad de Citas')
plt.savefig('servicios_mas_usados.png')

# 2. Heatmap de días con más atención
df_dias = pd.read_sql(query_dias, conn)
pivot = df_dias.pivot_table(index='hora', columns='dia_semana', values='total_citas')
sns.heatmap(pivot, cmap='YlOrRd', annot=True, fmt='g')
plt.title('Mapa de Calor: Citas por Día y Hora')
plt.savefig('heatmap_citas.png')

# 3. Distribución por tipo de mascota
df_mascotas = pd.read_sql(query_mascotas, conn)
plt.figure(figsize=(8, 8))
plt.pie(df_mascotas['total_mascotas'], labels=df_mascotas['tipo_mascota'], autopct='%1.1f%%')
plt.title('Distribución de Mascotas por Tipo')
plt.savefig('distribucion_mascotas.png')
```

---

## 🎯 **ARQUITECTURA SUGERIDA PARA TU PROYECTO PYTHON**

```
proyecto-python/
│
├── src/
│   ├── database/
│   │   ├── __init__.py
│   │   ├── connection.py       # Conexión a PostgreSQL
│   │   └── queries.py          # Consultas SQL reutilizables
│   │
│   ├── models/
│   │   ├── __init__.py
│   │   ├── prediccion.py       # Modelo de predicción de demanda
│   │   ├── clasificacion.py    # Clasificación tipo mascota
│   │   └── recomendacion.py    # Sistema de recomendación
│   │
│   ├── chatbot/
│   │   ├── __init__.py
│   │   ├── bot.py              # Lógica del chatbot
│   │   ├── intents.py          # Intenciones del usuario
│   │   └── responses.py        # Generación de respuestas
│   │
│   ├── dashboard/
│   │   ├── __init__.py
│   │   ├── app.py              # Dashboard (Streamlit/Dash)
│   │   └── visualizations.py   # Gráficos y visualizaciones
│   │
│   └── utils/
│       ├── __init__.py
│       ├── preprocessing.py    # Limpieza de datos
│       └── feature_engineering.py
│
├── data/
│   ├── raw/                    # Datos crudos exportados
│   ├── processed/              # Datos procesados para ML
│   └── models/                 # Modelos entrenados (.pkl, .h5)
│
├── notebooks/
│   ├── exploracion.ipynb       # Análisis exploratorio
│   ├── entrenamiento.ipynb     # Entrenamiento de modelos
│   └── evaluacion.ipynb        # Evaluación de resultados
│
├── requirements.txt            # Dependencias
├── config.py                   # Configuración (DB, API keys)
└── main.py                     # Punto de entrada
```

---

## 📦 **DEPENDENCIAS RECOMENDADAS (requirements.txt)**

```txt
# Database
psycopg2-binary==2.9.6
sqlalchemy==2.0.15

# Data Processing
pandas==2.0.2
numpy==1.24.3

# Machine Learning
scikit-learn==1.2.2
tensorflow==2.12.0
torch==2.0.1

# NLP (Chatbot)
transformers==4.29.2
nltk==3.8.1
spacy==3.5.3

# Visualization
matplotlib==3.7.1
seaborn==0.12.2
plotly==5.14.1

# Dashboard
streamlit==1.23.1
dash==2.10.2

# Utilities
python-dotenv==1.0.0
jupyter==1.0.0
```

---

## 🔐 **SEGURIDAD: Variables de Entorno**

Crea un archivo `.env`:

```env
DB_HOST=gondola.proxy.rlwy.net
DB_PORT=22967
DB_NAME=railway
DB_USER=postgres
DB_PASSWORD=LpEGFItXIhiOLcvpeWczptlFPxYnxhhI
```

Usar en Python:

```python
import os
from dotenv import load_dotenv

load_dotenv()

DB_CONFIG = {
    'host': os.getenv('DB_HOST'),
    'port': int(os.getenv('DB_PORT')),
    'database': os.getenv('DB_NAME'),
    'user': os.getenv('DB_USER'),
    'password': os.getenv('DB_PASSWORD')
}
```

---

## 📌 **RESUMEN DE CAMPOS CLAVE PARA TU PROYECTO**

### ✅ Para Dashboard de Análisis:

1. **Servicios más utilizados:**
   - Tablas: `appointment` + `service`
   - Campos: `service.nombre`, `COUNT(appointment_id)`

2. **Tipo de mascota que más va:**
   - Tablas: `pet` + `appointment`
   - Campos: `pet.tipo`, `COUNT(appointment_id)`

3. **Días con más atención:**
   - Tabla: `appointment`
   - Campos: `fecha_hora` (extraer día de semana con `EXTRACT(DOW)`)

### ✅ Para Chatbot:

1. **Historial de mascota:**
   - Tabla: `pet_medical_history`
   - Campos: `diagnostico`, `tratamiento`, `fecha_atencion`

2. **Próximas vacunas:**
   - Tabla: `vaccination`
   - Campos: `vaccine_name`, `next_dose_date`

3. **Información de cliente:**
   - Tabla: `client`
   - Campos: `name`, `correo`, `telefono`

---

## 🚀 **PRÓXIMOS PASOS**

1. ✅ Conectar a la base de datos desde Python
2. ✅ Exportar datos históricos a CSV para análisis
3. ✅ Crear notebook de exploración (EDA)
4. ✅ Entrenar modelos de predicción
5. ✅ Implementar chatbot con intenciones básicas
6. ✅ Crear dashboard interactivo con Streamlit

---

## 📞 **SOPORTE**

Si necesitas ayuda específica con alguna consulta SQL o implementación de modelo, 
pregúntame y te ayudo con el código específico.

---

**Autor:** Documentación generada para proyecto de análisis con IA  
**Fecha:** 2025  
**Base de Datos:** PostgreSQL (Railway)  
**Backend:** Spring Boot (Java) - Documentado para integración con Python

