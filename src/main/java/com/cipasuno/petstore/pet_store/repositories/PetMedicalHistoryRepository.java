package com.cipasuno.petstore.pet_store.repositories;

import com.cipasuno.petstore.pet_store.models.PetMedicalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PetMedicalHistoryRepository extends JpaRepository<PetMedicalHistory, Integer> {
    
    @Query("SELECT h FROM PetMedicalHistory h WHERE h.pet.petId = :petId AND h.activo = true ORDER BY h.fechaAtencion DESC")
    List<PetMedicalHistory> findByPetId(@Param("petId") Integer petId);
    
    @Query("SELECT h FROM PetMedicalHistory h WHERE h.appointment.appointmentId = :appointmentId AND h.activo = true")
    List<PetMedicalHistory> findByAppointmentId(@Param("appointmentId") Integer appointmentId);
    
    @Query("SELECT h FROM PetMedicalHistory h WHERE h.veterinarian.userId = :veterinarianId AND h.activo = true ORDER BY h.fechaAtencion DESC")
    List<PetMedicalHistory> findByVeterinarianId(@Param("veterinarianId") Integer veterinarianId);
    
    @Query("SELECT h FROM PetMedicalHistory h WHERE h.service.serviceId = :serviceId AND h.activo = true ORDER BY h.fechaAtencion DESC")
    List<PetMedicalHistory> findByServiceId(@Param("serviceId") Integer serviceId);
    
    @Query("SELECT h FROM PetMedicalHistory h WHERE h.pet.petId = :petId AND h.tipoProcedimiento = :tipoProcedimiento AND h.activo = true ORDER BY h.fechaAtencion DESC")
    List<PetMedicalHistory> findByPetIdAndTipoProcedimiento(@Param("petId") Integer petId, @Param("tipoProcedimiento") String tipoProcedimiento);
    
    @Query("SELECT h FROM PetMedicalHistory h WHERE h.pet.petId = :petId AND h.fechaAtencion >= :fechaInicio AND h.fechaAtencion <= :fechaFin AND h.activo = true ORDER BY h.fechaAtencion DESC")
    List<PetMedicalHistory> findByPetIdAndDateRange(@Param("petId") Integer petId, 
                                                     @Param("fechaInicio") LocalDateTime fechaInicio,
                                                     @Param("fechaFin") LocalDateTime fechaFin);
    
    @Query("SELECT h FROM PetMedicalHistory h LEFT JOIN FETCH h.pet LEFT JOIN FETCH h.service LEFT JOIN FETCH h.veterinarian WHERE h.tenantId = :tenantId AND h.activo = true ORDER BY h.fechaAtencion DESC")
    List<PetMedicalHistory> findByTenantId(@Param("tenantId") String tenantId);
    
    @Query("SELECT h FROM PetMedicalHistory h WHERE h.pet.petId = :petId AND h.tenantId = :tenantId AND h.activo = true ORDER BY h.fechaAtencion DESC")
    List<PetMedicalHistory> findByPetIdAndTenantId(@Param("petId") Integer petId, @Param("tenantId") String tenantId);
}

