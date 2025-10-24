package com.cipasuno.petstore.pet_store.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

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
    private Pet pet;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    private User user;
    
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
        
        @Column(name = "user_id")
        private Integer userId;
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof PetOwnerId)) return false;
            PetOwnerId that = (PetOwnerId) o;
            return petId.equals(that.petId) && userId.equals(that.userId);
        }
        
        @Override
        public int hashCode() {
            return 31 * petId.hashCode() + userId.hashCode();
        }
    }
}

