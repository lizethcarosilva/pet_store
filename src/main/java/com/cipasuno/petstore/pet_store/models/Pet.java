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

@Entity
@Table(name = "pet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pet_id")
    private Integer petId;
    
    @Column(name = "tenant_id", nullable = false)
    private String tenantId;
    
    @Column(nullable = false)
    private String nombre;
    
    @Column(nullable = false)
    private String tipo; // perro, gato, ave, etc.
    
    private String raza;
    
    @Column(name = "cuidados_especiales", length = 500)
    private String cuidadosEspeciales;
    
    private Integer edad;
    
    @Column(length = 10)
    private String sexo; // Macho/Hembra
    
    private String color;
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    @Column(name = "created_on", nullable = false, updatable = false)
    private LocalDateTime createdOn;
    
    // Relación muchos a muchos con User a través de PetOwner
    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"pet", "client", "hibernateLazyInitializer", "handler"})
    private Set<PetOwner> owners = new HashSet<>();
    
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

