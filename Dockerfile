# ==================================
# DOCKERFILE OPTIMIZADO PARA RAILWAY
# ==================================

# Etapa 1: Construcción de la aplicación
FROM maven:3.9.9-eclipse-temurin-17-alpine AS build

# Establecer directorio de trabajo
WORKDIR /app

# Copiar archivos de configuración de Maven
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Descargar dependencias (se cachea si pom.xml no cambia)
RUN mvn dependency:go-offline -B

# Copiar el código fuente
COPY src ./src

# Construir la aplicación (skip tests para deploy más rápido)
RUN mvn clean package -DskipTests -B

# Etapa 2: Imagen de ejecución ligera
FROM eclipse-temurin:17-jre-alpine

# Metadata
LABEL maintainer="cipasuno"
LABEL description="Pet Store Application for Railway"

# Instalar herramientas necesarias
RUN apk add --no-cache curl

# Crear usuario no-root para seguridad
RUN addgroup -g 1001 -S appgroup && \
    adduser -u 1001 -S appuser -G appgroup

# Establecer directorio de trabajo
WORKDIR /app

# Copiar el JAR desde la etapa de build
COPY --from=build /app/target/pet_store-0.0.1-SNAPSHOT.jar app.jar

# Cambiar permisos
RUN chown -R appuser:appgroup /app

# Cambiar al usuario no-root
USER appuser

# Exponer el puerto (Railway lo asignará dinámicamente)
EXPOSE ${PORT:-8090}

# Healthcheck para Railway
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD curl -f http://localhost:${PORT:-8090}/actuator/health || exit 1

# Variables de entorno por defecto
ENV JAVA_OPTS="-Xmx512m -Xms256m" \
    SPRING_PROFILES_ACTIVE=railway

# Comando de inicio
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar app.jar"]

