# Dockerfile para Railway
FROM eclipse-temurin:17-jdk-alpine

# Crear directorio de trabajo
WORKDIR /app

# Copiar archivos de configuración de Maven
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn

# Copiar código fuente
COPY src src

# Construir la aplicación
RUN ./mvnw clean package -DskipTests

# Exponer el puerto
EXPOSE 8090

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "target/pet_store-0.0.1-SNAPSHOT.jar"]
