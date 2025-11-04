# 🚀 GUÍA RÁPIDA - PROYECTO PYTHON PARA ANÁLISIS PET STORE

## 📋 Contenido del Proyecto

Este paquete incluye:

1. **`DATABASE_DOCUMENTATION_FOR_PYTHON.md`** - Documentación completa de la base de datos
2. **`python_integration_example.py`** - Script de ejemplo con análisis y funciones
3. **`requirements.txt`** - Dependencias necesarias
4. **`config_template.env`** - Plantilla de configuración
5. **Esta guía** - Instrucciones paso a paso

---

## ⚡ INICIO RÁPIDO (5 minutos)

### **Paso 1: Instalar Python**

Asegúrate de tener Python 3.9 o superior:

```bash
python --version
```

### **Paso 2: Crear entorno virtual**

```bash
# Windows
python -m venv venv
venv\Scripts\activate

# Linux/Mac
python3 -m venv venv
source venv/bin/activate
```

### **Paso 3: Instalar dependencias**

```bash
pip install -r requirements.txt
```

**Nota:** La instalación puede tardar 5-10 minutos (especialmente TensorFlow).

### **Paso 4: Configurar variables de entorno**

Copia `config_template.env` como `.env`:

```bash
# Windows
copy config_template.env .env

# Linux/Mac
cp config_template.env .env
```

### **Paso 5: Ejecutar el análisis**

```bash
python python_integration_example.py
```

**¡Listo!** El script generará:
- 5 gráficos en formato PNG
- 1 archivo CSV con dataset para Machine Learning
- Análisis impresos en consola

---

## 📊 LO QUE HACE EL SCRIPT DE EJEMPLO

### **1. Análisis de Servicios Más Utilizados**
```python
df_servicios = obtener_servicios_mas_usados(conn)
```
**Resultado:**
- Tabla con ranking de servicios
- Gráfico de barras: `servicios_mas_usados.png`

### **2. Tipos de Mascota Más Atendidas**
```python
df_mascotas = obtener_mascotas_por_servicio(conn)
```
**Resultado:**
- Distribución por tipo (perro, gato, ave, etc.)
- Gráficos: `mascotas_por_servicio.png`

### **3. Días y Horas con Más Atención**
```python
df_dias = obtener_dias_con_mas_atencion(conn)
df_horas = obtener_horas_con_mas_atencion(conn)
```
**Resultado:**
- Patrón de demanda por día de la semana
- Patrón de demanda por hora
- Gráficos: `dias_con_mas_atencion.png`, `horas_con_mas_atencion.png`

### **4. Análisis de Ingresos**
```python
df_ingresos = obtener_ingresos_por_servicio(conn)
```
**Resultado:**
- Rentabilidad por servicio
- Gráfico: `ingresos_por_servicio.png`

### **5. Dataset para Machine Learning**
```python
df_ml = exportar_dataset_ml(conn)
```
**Resultado:**
- Archivo CSV: `dataset_citas_ml.csv`
- Incluye features temporales, características de mascotas, servicios, etc.

---

## 🤖 FUNCIONES PARA CHATBOT

El script incluye funciones listas para integrar en un chatbot:

### **Consultar historial de una mascota**
```python
historial = obtener_historial_mascota(conn, pet_id=1)
```

### **Ver vacunas pendientes**
```python
vacunas = obtener_vacunas_pendientes(conn, pet_id=1)
```

### **Próximas citas de un cliente**
```python
citas = obtener_proximas_citas(conn, client_id=1)
```

---

## 🧠 MODELO DE RED NEURONAL - EJEMPLO BÁSICO

### **Paso 1: Cargar el dataset**

```python
import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import LabelEncoder, StandardScaler
from tensorflow import keras

# Cargar dataset
df = pd.read_csv('dataset_citas_ml.csv')

# Seleccionar features
features = ['dia_semana', 'hora', 'mes', 'service_id', 'edad_mascota']
X = df[features].fillna(0)

# Target: Predecir si la cita será completada
y = df['asistio']

# Dividir datos
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
```

### **Paso 2: Crear modelo de red neuronal**

```python
# Definir arquitectura
model = keras.Sequential([
    keras.layers.Dense(64, activation='relu', input_shape=(X_train.shape[1],)),
    keras.layers.Dropout(0.3),
    keras.layers.Dense(32, activation='relu'),
    keras.layers.Dropout(0.2),
    keras.layers.Dense(1, activation='sigmoid')  # Clasificación binaria
])

# Compilar
model.compile(
    optimizer='adam',
    loss='binary_crossentropy',
    metrics=['accuracy', 'AUC']
)

# Entrenar
history = model.fit(
    X_train, y_train,
    epochs=50,
    batch_size=32,
    validation_split=0.2,
    verbose=1
)

# Evaluar
test_loss, test_acc, test_auc = model.evaluate(X_test, y_test)
print(f"Precisión en test: {test_acc:.2%}")
```

### **Paso 3: Hacer predicciones**

```python
# Predecir para una nueva cita
nueva_cita = [[3, 10, 11, 5, 2]]  # Miércoles, 10 AM, Noviembre, Servicio 5, Mascota 2 años
probabilidad = model.predict(nueva_cita)
print(f"Probabilidad de asistir: {probabilidad[0][0]:.2%}")
```

---

## 📈 CREAR DASHBOARD CON STREAMLIT

### **Paso 1: Crear archivo `dashboard.py`**

```python
import streamlit as st
import pandas as pd
import psycopg2
import plotly.express as px

# Configuración de la página
st.set_page_config(page_title="Pet Store Analytics", layout="wide")

# Título
st.title("🐾 Pet Store - Dashboard de Análisis")

# Conectar a la base de datos
@st.cache_resource
def conectar_db():
    return psycopg2.connect(
        host='gondola.proxy.rlwy.net',
        port=22967,
        database='railway',
        user='postgres',
        password='LpEGFItXIhiOLcvpeWczptlFPxYnxhhI'
    )

conn = conectar_db()

# Cargar datos
@st.cache_data
def cargar_servicios():
    query = """
    SELECT s.nombre, COUNT(a.appointment_id) as total_citas
    FROM appointment a
    JOIN service s ON a.service_id = s.service_id
    WHERE a.activo = true
    GROUP BY s.nombre
    ORDER BY total_citas DESC;
    """
    return pd.read_sql(query, conn)

df_servicios = cargar_servicios()

# Visualización
st.subheader("Servicios Más Utilizados")
fig = px.bar(df_servicios, x='total_citas', y='nombre', orientation='h')
st.plotly_chart(fig, use_container_width=True)

# Métricas
col1, col2, col3 = st.columns(3)
col1.metric("Total Servicios", len(df_servicios))
col2.metric("Citas Totales", df_servicios['total_citas'].sum())
col3.metric("Promedio por Servicio", f"{df_servicios['total_citas'].mean():.1f}")
```

### **Paso 2: Ejecutar el dashboard**

```bash
streamlit run dashboard.py
```

Se abrirá automáticamente en `http://localhost:8501`

---

## 🗂️ ESTRUCTURA DE PROYECTO SUGERIDA

```
mi-proyecto-petstore/
│
├── data/
│   ├── raw/                    # Datos crudos
│   ├── processed/              # Datos procesados
│   └── models/                 # Modelos entrenados
│
├── notebooks/
│   ├── 01_exploracion.ipynb    # Análisis exploratorio
│   ├── 02_modelo_prediccion.ipynb
│   └── 03_evaluacion.ipynb
│
├── src/
│   ├── database/
│   │   ├── __init__.py
│   │   └── connection.py       # Funciones de conexión
│   │
│   ├── models/
│   │   ├── __init__.py
│   │   ├── prediccion.py       # Modelo de predicción
│   │   └── clasificacion.py
│   │
│   ├── chatbot/
│   │   ├── __init__.py
│   │   └── bot.py
│   │
│   └── dashboard/
│       ├── __init__.py
│       └── app.py
│
├── python_integration_example.py  # Script de ejemplo
├── requirements.txt
├── config_template.env
├── DATABASE_DOCUMENTATION_FOR_PYTHON.md
└── GUIA_RAPIDA_PYTHON.md
```

---

## 🎯 CASOS DE USO ESPECÍFICOS

### **1. ¿Qué servicio es más utilizado?**

**Consulta SQL:**
```sql
SELECT s.nombre, COUNT(*) as total
FROM appointment a
JOIN service s ON a.service_id = s.service_id
GROUP BY s.nombre
ORDER BY total DESC;
```

**Python:**
```python
df = pd.read_sql(query, conn)
print(df.head(10))
```

---

### **2. ¿Qué tipo de mascota va más a servicios?**

**Consulta SQL:**
```sql
SELECT p.tipo, COUNT(a.appointment_id) as total_citas
FROM appointment a
JOIN pet p ON a.pet_id = p.pet_id
GROUP BY p.tipo
ORDER BY total_citas DESC;
```

---

### **3. ¿Qué día hay más atención?**

**Consulta SQL:**
```sql
SELECT 
    CASE EXTRACT(DOW FROM fecha_hora)
        WHEN 0 THEN 'Domingo'
        WHEN 1 THEN 'Lunes'
        -- ...
    END as dia,
    COUNT(*) as total
FROM appointment
GROUP BY EXTRACT(DOW FROM fecha_hora)
ORDER BY total DESC;
```

---

## 🔍 TIPS Y MEJORES PRÁCTICAS

### **1. Optimizar consultas largas**

```python
import time

inicio = time.time()
df = pd.read_sql(query, conn)
fin = time.time()
print(f"⏱️ Consulta ejecutada en {fin-inicio:.2f} segundos")
```

### **2. Caché de datos en Streamlit**

```python
@st.cache_data(ttl=600)  # Cache por 10 minutos
def cargar_datos():
    return pd.read_sql(query, conn)
```

### **3. Manejo de errores**

```python
try:
    df = pd.read_sql(query, conn)
except Exception as e:
    print(f"❌ Error: {e}")
    df = pd.DataFrame()  # DataFrame vacío
```

### **4. Exportar resultados**

```python
# CSV
df.to_csv('resultados.csv', index=False)

# Excel
df.to_excel('resultados.xlsx', index=False)

# JSON
df.to_json('resultados.json', orient='records')
```

---

## 📚 RECURSOS ADICIONALES

### **Tutoriales recomendados:**

1. **Pandas:** https://pandas.pydata.org/docs/getting_started/intro_tutorials/
2. **TensorFlow:** https://www.tensorflow.org/tutorials
3. **Streamlit:** https://docs.streamlit.io/
4. **Machine Learning:** https://scikit-learn.org/stable/tutorial/

### **Datasets de ejemplo:**

- Kaggle: https://www.kaggle.com/datasets
- UCI ML Repository: https://archive.ics.uci.edu/ml/

---

## ❓ SOLUCIÓN DE PROBLEMAS

### **Error: "No module named 'psycopg2'"**
```bash
pip install psycopg2-binary
```

### **Error: "Could not connect to database"**
- Verifica que las credenciales en `.env` sean correctas
- Revisa que tienes conexión a internet
- Prueba con: `ping gondola.proxy.rlwy.net`

### **Error: "TensorFlow not found"**
```bash
# Instalar versión CPU (más ligera)
pip install tensorflow-cpu==2.15.0
```

### **Dataset vacío**
- Verifica que hay datos en la base de datos
- Revisa la cláusula WHERE en tus queries
- Confirma que el `tenant_id` sea correcto

---

## 📞 SIGUIENTE PASO

1. ✅ Ejecuta `python python_integration_example.py`
2. ✅ Revisa los gráficos generados
3. ✅ Abre el archivo `dataset_citas_ml.csv` en Excel o Jupyter
4. ✅ Experimenta modificando las consultas SQL
5. ✅ Crea tu primer modelo de red neuronal
6. ✅ Implementa tu dashboard con Streamlit

---

**¡Éxito con tu proyecto! 🚀**

Si tienes dudas o necesitas ayuda específica, consulta la documentación completa en `DATABASE_DOCUMENTATION_FOR_PYTHON.md`

