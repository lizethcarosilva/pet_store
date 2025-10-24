# Multi-stage build para compilar y ejecutar
FROM openjdk:17-jdk-slim as builder

# Instalar Maven
RUN apt-get update && apt-get install -y maven && rm -rf /var/lib/apt/lists/*

# Establecer directorio de trabajo
WORKDIR /app

# Copiar archivos de configuración de Maven
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Copiar código fuente
COPY src src

# Compilar el proyecto
RUN ./mvnw clean package -DskipTests

# Imagen final para ejecución
FROM openjdk:17-jdk-slim

# Establecer directorio de trabajo
WORKDIR /app

# Copiar el JAR compilado desde la etapa anterior
COPY --from=builder /app/target/pet_store-0.0.1-SNAPSHOT.jar app.jar

# Exponer el puerto (Railway usa la variable PORT)
EXPOSE 8090

# Variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=prod
ENV SERVER_PORT=8090

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]
