# Dockerfile para Railway
FROM eclipse-temurin:17-jdk-alpine

# Instalar Maven
RUN apk add --no-cache maven

# Crear directorio de trabajo
WORKDIR /app

# Copiar archivos de configuración de Maven
COPY pom.xml .

# Copiar código fuente
COPY src src

# Construir la aplicación usando Maven directamente
RUN mvn clean package -DskipTests

# Exponer el puerto (Railway inyectará el puerto correcto)
EXPOSE $PORT

# Comando para ejecutar la aplicación
CMD ["java", "-jar", "target/pet_store-0.0.1-SNAPSHOT.jar"]
