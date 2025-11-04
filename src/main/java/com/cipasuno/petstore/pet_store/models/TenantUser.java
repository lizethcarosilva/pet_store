package com.cipasuno.petstore.pet_store.models;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tenantuser")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(TenantUser.TenantUserId.class) // Referencia correcta
public class TenantUser {

    @Id
    @Column(name = "user_id")
    private String userId;

    @Id
    @Column(name = "tenant_id")
    private String tenantId;

    // Clase interna ESTÁTICA
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TenantUserId implements Serializable {
        private String userId;
        private String tenantId;
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TenantUserId that = (TenantUserId) o;
            return userId.equals(that.userId) && tenantId.equals(that.tenantId);
        }
        
        @Override
        public int hashCode() {
            return userId.hashCode() + tenantId.hashCode();
        }
    }
}