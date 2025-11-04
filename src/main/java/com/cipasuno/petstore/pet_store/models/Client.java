package com.cipasuno.petstore.pet_store.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad Cliente - Tabla separada de usuarios
 * Los clientes tienen su propia tabla para mantener datos específicos
 */
@Entity
@Table(name = "client")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id")
    private Integer clientId;
    
    @Column(name = "tenant_id", nullable = false)
    private String tenantId;
    
    @Column(nullable = false, length = 200)
    private String name;
    
    @Column(name = "tipo_id", length = 10)
    private String tipoId; // CC, TI, CE, NIT, etc.
    
    @Column(unique = true, nullable = false, length = 50)
    private String ident;
    
    @Column(unique = true, nullable = false, length = 100)
    private String correo;
    
    @Column(length = 20)
    private String telefono;
    
    @Column(length = 300)
    private String direccion;
    
    @Column(length = 500)
    private String observaciones; // Notas adicionales sobre el cliente
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    @Column(name = "created_on", nullable = false, updatable = false)
    private LocalDateTime createdOn;
    
    // Relación con mascotas
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({"client", "pet", "hibernateLazyInitializer", "handler"})
    private Set<PetOwner> pets = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        if (createdOn == null) {
            createdOn = LocalDateTime.now();
        }
        if (activo == null) {
            activo = true;
        }
    }
}

