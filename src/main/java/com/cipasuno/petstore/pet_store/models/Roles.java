package com.cipasuno.petstore.pet_store.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Roles {
    @Id
    @Column(name = "rol_id", length = 20)
    private String rolId;
    
    @Column(name = "name", nullable = false, length = 100)
    private String nombre;
}
