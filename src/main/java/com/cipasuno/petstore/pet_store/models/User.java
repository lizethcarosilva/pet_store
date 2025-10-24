package com.cipasuno.petstore.pet_store.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "\"user\"") 
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "user_id")
    private Integer userId;
    
    @Column(name = "tenant_id")
    private String tenantId;
    
    private String name;
    
    @Column(name = "tipo_id")
    private String tipoId;
    
    private String ident;
    private String correo;
    private String telefono;
    private String direccion;
    private String password;
    
    @Column(name = "rol_id")
    private String rolId;
    
    @Column(nullable = false)
    private Boolean activo = true;
    
    // Se genera automáticamente al crear
    @Column(name = "created_on", nullable = false, updatable = false)
    private LocalDateTime createdOn;
    
    // Relación muchos a muchos con Pet a través de PetOwner
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PetOwner> pets = new HashSet<>();
    
    // Este método se ejecuta ANTES de guardar en BD por primera vez
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
