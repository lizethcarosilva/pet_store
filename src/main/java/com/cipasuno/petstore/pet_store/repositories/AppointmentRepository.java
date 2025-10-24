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
    
    List<Appointment> findByActivoTrue();
    
    List<Appointment> findByEstado(String estado);
    
    @Query("SELECT a FROM Appointment a WHERE a.pet.petId = :petId AND a.activo = true ORDER BY a.fechaHora DESC")
    List<Appointment> findByPetId(Integer petId);
    
    @Query("SELECT a FROM Appointment a WHERE a.user.userId = :userId AND a.activo = true ORDER BY a.fechaHora DESC")
    List<Appointment> findByUserId(Integer userId);
    
    @Query("SELECT a FROM Appointment a WHERE a.fechaHora BETWEEN :inicio AND :fin AND a.activo = true ORDER BY a.fechaHora")
    List<Appointment> findByFechaHoraBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
    
    @Query("SELECT COUNT(a) FROM Appointment a WHERE CAST(a.fechaHora AS date) = CURRENT_DATE AND a.activo = true")
    long countAppointmentsToday();
    
    @Query("SELECT a FROM Appointment a WHERE CAST(a.fechaHora AS date) = CURRENT_DATE AND a.activo = true ORDER BY a.fechaHora")
    List<Appointment> findAppointmentsToday();
}

