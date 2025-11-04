package com.cipasuno.petstore.pet_store.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Relación entre mascotas y sus propietarios (Clientes)
 * Ahora solo asocia mascotas con clientes de la tabla client
 */
@Entity
@Table(name = "pet_owner")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PetOwner {
    
    @EmbeddedId
    private PetOwnerId id = new PetOwnerId();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("petId")
    @JoinColumn(name = "pet_id")
    @JsonIgnoreProperties({"owners", "hibernateLazyInitializer", "handler"})
    private Pet pet;
    
    // Relación principal con Client (clientes de la tabla client)
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("clientId")
    @JoinColumn(name = "client_id")
    @JsonIgnoreProperties({"pets", "hibernateLazyInitializer", "handler"})
    private Client client;
    
    // user_id es opcional (legacy), ya no se usa
    @Column(name = "user_id")
    private Integer userId;
    
    @Column(name = "created_on", nullable = false, updatable = false)
    private LocalDateTime createdOn;
    
    @PrePersist
    protected void onCreate() {
        if (createdOn == null) {
            createdOn = LocalDateTime.now();
        }
    }
    
    @Embeddable
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PetOwnerId implements Serializable {
        @Column(name = "pet_id")
        private Integer petId;
        
        @Column(name = "client_id")
        private Integer clientId;
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PetOwnerId)) return false;
            PetOwnerId that = (PetOwnerId) o;
            return petId.equals(that.petId) && clientId.equals(that.clientId);
        }
        
        @Override
        public int hashCode() {
            return 31 * petId.hashCode() + clientId.hashCode();
        }
    }
}

