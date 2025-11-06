package com.cipasuno.petstore.pet_store;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PetStoreApplication {

	public static void main(String[] args) {
		// Cargar variables del archivo .env ANTES de iniciar Spring Boot
		System.out.println("========================================");
		System.out.println("CARGANDO VARIABLES DE ENTORNO DESDE .env");
		System.out.println("========================================");
		
		try {
			Dotenv dotenv = Dotenv.configure()
				.directory(".")  // Buscar en el directorio actual
				.ignoreIfMissing()  // No fallar si no existe
				.load();
			
			System.out.println("Archivo .env encontrado y cargado exitosamente!");
			System.out.println("\nVariables cargadas:");
			
			// Establecer las variables como propiedades del sistema y mostrarlas
			dotenv.entries().forEach(entry -> {
				String key = entry.getKey();
				String value = entry.getValue();
				
				// Ocultar parcialmente valores sensibles (passwords, secrets)
				String displayValue = value;
				if (key.toLowerCase().contains("password") || key.toLowerCase().contains("secret")) {
					displayValue = "***" + value.substring(Math.max(0, value.length() - 4));
				}
				
				System.out.println("  " + key + " = " + displayValue);
				System.setProperty(key, value);
			});
			
			// Mostrar también las variables que Spring Boot usará
			System.out.println("\n========================================");
			System.out.println("VARIABLES PARA SPRING BOOT:");
			System.out.println("========================================");
			System.out.println("  DB_HOST = " + System.getProperty("DB_HOST", "NO CONFIGURADO"));
			System.out.println("  DB_PORT = " + System.getProperty("DB_PORT", "NO CONFIGURADO"));
			System.out.println("  DB_NAME = " + System.getProperty("DB_NAME", "NO CONFIGURADO"));
			System.out.println("  DB_USERNAME = " + System.getProperty("DB_USERNAME", "NO CONFIGURADO"));
			String dbPassword = System.getProperty("DB_PASSWORD", "NO CONFIGURADO");
			System.out.println("  DB_PASSWORD = " + (dbPassword.equals("NO CONFIGURADO") ? "NO CONFIGURADO" : "***" + dbPassword.substring(Math.max(0, dbPassword.length() - 4))));
			System.out.println("========================================\n");
			
		} catch (Exception e) {
			System.out.println("❌ Error al cargar el archivo .env: " + e.getMessage());
			System.out.println("Usando valores por defecto de application.properties");
			System.out.println("========================================\n");
		}
		
		SpringApplication.run(PetStoreApplication.class, args);
	}

}
