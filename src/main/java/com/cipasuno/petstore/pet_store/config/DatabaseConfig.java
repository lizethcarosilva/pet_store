package com.cipasuno.petstore.pet_store.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

/**
 * Configuración de base de datos para Railway
 * Procesa DATABASE_URL y agrega jdbc: si es necesario
 */
@Configuration
@Profile("railway")
public class DatabaseConfig {

    @Bean
    @ConfigurationProperties("spring.datasource.hikari")
    public DataSource dataSource() {
        String databaseUrl = System.getenv("DATABASE_URL");
        
        if (databaseUrl != null && !databaseUrl.startsWith("jdbc:")) {
            // Railway proporciona postgresql:// pero necesitamos jdbc:postgresql://
            databaseUrl = "jdbc:" + databaseUrl;
        }
        
        if (databaseUrl == null) {
            // Fallback: construir desde variables individuales
            String host = System.getenv().getOrDefault("DB_HOST", "postgres.railway.internal");
            String port = System.getenv().getOrDefault("DB_PORT", "5432");
            String database = System.getenv().getOrDefault("DB_NAME", "railway");
            databaseUrl = String.format("jdbc:postgresql://%s:%s/%s", host, port, database);
        }
        
        String username = System.getenv().getOrDefault("POSTGRES_USER", "postgres");
        String password = System.getenv().getOrDefault("POSTGRES_PASSWORD", "");
        
        return DataSourceBuilder
            .create()
            .url(databaseUrl)
            .username(username)
            .password(password)
            .driverClassName("org.postgresql.Driver")
            .build();
    }
}

