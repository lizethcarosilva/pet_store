package com.cipasuno.petstore.pet_store.repositories;

import com.cipasuno.petstore.pet_store.models.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    
    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.pet LEFT JOIN FETCH a.service LEFT JOIN FETCH a.client LEFT JOIN FETCH a.veterinarian WHERE a.activo = true")
    List<Appointment> findByActivoTrue();
    
    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.pet LEFT JOIN FETCH a.service LEFT JOIN FETCH a.client LEFT JOIN FETCH a.veterinarian WHERE a.estado = :estado")
    List<Appointment> findByEstado(@Param("estado") String estado);
    
    @Query("SELECT a FROM Appointment a WHERE a.pet.petId = :petId AND a.activo = true ORDER BY a.fechaHora DESC")
    List<Appointment> findByPetId(Integer petId);
    
    @Query("SELECT a FROM Appointment a WHERE a.client.clientId = :clientId AND a.activo = true ORDER BY a.fechaHora DESC")
    List<Appointment> findByClientId(Integer clientId);
    
    @Query("SELECT a FROM Appointment a WHERE a.fechaHora BETWEEN :inicio AND :fin AND a.activo = true ORDER BY a.fechaHora")
    List<Appointment> findByFechaHoraBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
    
    @Query("SELECT COUNT(a) FROM Appointment a WHERE CAST(a.fechaHora AS date) = CURRENT_DATE AND a.activo = true")
    long countAppointmentsToday();
    
    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.pet LEFT JOIN FETCH a.service LEFT JOIN FETCH a.client LEFT JOIN FETCH a.veterinarian WHERE CAST(a.fechaHora AS date) = CURRENT_DATE AND a.activo = true ORDER BY a.fechaHora")
    List<Appointment> findAppointmentsToday();
    
    @Query("SELECT a FROM Appointment a WHERE a.veterinarian.userId = :veterinarianId " +
           "AND a.fechaHora = :fechaHora AND a.activo = true AND a.estado != 'CANCELADA'")
    List<Appointment> findByVeterinarianIdAndFechaHora(@Param("veterinarianId") Integer veterinarianId, 
                                                       @Param("fechaHora") LocalDateTime fechaHora);
    
    @Query("SELECT a FROM Appointment a WHERE a.veterinarian.userId = :veterinarianId " +
           "AND CAST(a.fechaHora AS date) = CAST(:fecha AS date) " +
           "AND a.activo = true AND a.estado != 'CANCELADA' " +
           "ORDER BY a.fechaHora")
    List<Appointment> findByVeterinarianIdAndDate(@Param("veterinarianId") Integer veterinarianId, 
                                                   @Param("fecha") LocalDateTime fecha);
    
    // Query optimizada con JOIN FETCH para evitar LazyInitializationException
    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.pet LEFT JOIN FETCH a.service LEFT JOIN FETCH a.client LEFT JOIN FETCH a.veterinarian")
    List<Appointment> findAllWithRelations();
    
}

