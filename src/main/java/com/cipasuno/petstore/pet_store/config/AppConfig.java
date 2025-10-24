package com.cipasuno.petstore.pet_store.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    
    private Database database = new Database();
    private Jpa jpa = new Jpa();
    
    // Getters and Setters (IMPORTANTE: deben ser públicos)
    public Database getDatabase() { 
        return database; 
    }
    
    public void setDatabase(Database database) { 
        this.database = database; 
    }
    
    public Jpa getJpa() { 
        return jpa; 
    }
    
    public void setJpa(Jpa jpa) { 
        this.jpa = jpa; 
    }
    
    public static class Database {
        private String host = "localhost";
        private String port = "5432";
        private String name = "petstore";
        private String username = "postgres";
        private String password = "password";
        
        // Getters and Setters
        public String getHost() { return host; }
        public void setHost(String host) { this.host = host; }
        
        public String getPort() { return port; }
        public void setPort(String port) { this.port = port; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public String getUrl() {
            return "jdbc:postgresql://" + host + ":" + port + "/" + name;
        }
    }
    
    public static class Jpa {
        private String ddlAuto = "update";
        private boolean showSql = true;
        
        // Getters and Setters
        public String getDdlAuto() { return ddlAuto; }
        public void setDdlAuto(String ddlAuto) { this.ddlAuto = ddlAuto; }
        
        public boolean isShowSql() { return showSql; }
        public void setShowSql(boolean showSql) { this.showSql = showSql; }
    }
}