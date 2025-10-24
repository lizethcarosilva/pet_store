# ==================================
# DOCKERFILE CORREGIDO PARA RAILWAY
# ==================================

# ============================================
# ETAPA 1: BUILD (Compilación con Maven)
# ============================================
FROM maven:3.9.9-eclipse-temurin-17-alpine AS build

# Establecer directorio de trabajo
WORKDIR /app

# PASO 1: Copiar archivos de Maven primero (para caché de dependencias)
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# PASO 2: Descargar dependencias (se cachea si pom.xml no cambia)
RUN mvn dependency:go-offline -B || true

# PASO 3: Ahora copiar el código fuente
COPY src ./src

# PASO 4: Compilar la aplicación (skip tests para build más rápido)
RUN mvn clean package -DskipTests -B

# Verificar que el JAR se creó
RUN ls -la /app/target/

# ============================================
# ETAPA 2: RUNTIME (Ejecución ligera)
# ============================================
FROM eclipse-temurin:17-jre-alpine

# Metadata
LABEL maintainer="cipasuno"
LABEL description="Pet Store Application for Railway"

# Instalar curl para health checks
RUN apk add --no-cache curl

# Crear usuario no-root para seguridad
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Establecer directorio de trabajo
WORKDIR /app

# Copiar el JAR compilado desde la etapa de build
COPY --from=build /app/target/pet_store-0.0.1-SNAPSHOT.jar app.jar

# Verificar que el JAR existe
RUN ls -la /app/ && test -f /app/app.jar

# Cambiar permisos
RUN chown -R appuser:appgroup /app

# Cambiar al usuario no-root
USER appuser

# Exponer el puerto (Railway lo asignará dinámicamente)
EXPOSE ${PORT:-8090}

# Healthcheck para Railway
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:${PORT:-8090}/actuator/health || exit 1

# Variables de entorno por defecto
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0" \
    SPRING_PROFILES_ACTIVE=railway

# Comando de inicio con logging para debug
ENTRYPOINT ["sh", "-c", "echo 'Starting Pet Store Application...' && echo 'Java opts: $JAVA_OPTS' && java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]

