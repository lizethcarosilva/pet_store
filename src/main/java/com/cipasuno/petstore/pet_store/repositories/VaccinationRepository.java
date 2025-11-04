package com.cipasuno.petstore.pet_store.repositories;

import com.cipasuno.petstore.pet_store.models.Vaccination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VaccinationRepository extends JpaRepository<Vaccination, Integer> {
    
    @Query("SELECT v FROM Vaccination v WHERE v.pet.petId = :petId AND v.activo = true ORDER BY v.applicationDate DESC")
    List<Vaccination> findByPetId(@Param("petId") Integer petId);
    
    @Query("SELECT v FROM Vaccination v WHERE v.medicalHistory.historyId = :medicalHistoryId AND v.activo = true")
    List<Vaccination> findByMedicalHistoryId(@Param("medicalHistoryId") Integer medicalHistoryId);
    
    @Query("SELECT v FROM Vaccination v WHERE v.veterinarian.userId = :veterinarianId AND v.activo = true ORDER BY v.applicationDate DESC")
    List<Vaccination> findByVeterinarianId(@Param("veterinarianId") Integer veterinarianId);
    
    @Query("SELECT v FROM Vaccination v WHERE v.pet.petId = :petId AND v.vaccineName = :vaccineName AND v.activo = true ORDER BY v.applicationDate DESC")
    List<Vaccination> findByPetIdAndVaccineName(@Param("petId") Integer petId, @Param("vaccineName") String vaccineName);
    
    @Query("SELECT v FROM Vaccination v WHERE v.nextDoseDate BETWEEN :startDate AND :endDate AND v.activo = true AND v.isCompleted = false ORDER BY v.nextDoseDate")
    List<Vaccination> findUpcomingVaccinations(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
    @Query("SELECT v FROM Vaccination v WHERE v.pet.petId = :petId AND v.nextDoseDate < :currentDate AND v.activo = true AND v.isCompleted = false ORDER BY v.nextDoseDate")
    List<Vaccination> findOverdueVaccinationsByPetId(@Param("petId") Integer petId, @Param("currentDate") LocalDate currentDate);
    
    @Query("SELECT v FROM Vaccination v WHERE v.pet.petId = :petId AND v.isCompleted = false AND v.activo = true ORDER BY v.applicationDate DESC")
    List<Vaccination> findPendingVaccinationsByPetId(@Param("petId") Integer petId);
    
    @Query("SELECT v FROM Vaccination v LEFT JOIN FETCH v.pet LEFT JOIN FETCH v.veterinarian WHERE v.tenantId = :tenantId AND v.activo = true ORDER BY v.applicationDate DESC")
    List<Vaccination> findByTenantId(@Param("tenantId") String tenantId);
    
    @Query("SELECT v FROM Vaccination v WHERE v.pet.petId = :petId AND v.tenantId = :tenantId AND v.activo = true ORDER BY v.applicationDate DESC")
    List<Vaccination> findByPetIdAndTenantId(@Param("petId") Integer petId, @Param("tenantId") String tenantId);
    
    @Query("SELECT COUNT(v) FROM Vaccination v WHERE v.pet.petId = :petId AND v.activo = true")
    long countVaccinationsByPetId(@Param("petId") Integer petId);
    
    @Query("SELECT v FROM Vaccination v WHERE v.pet.petId = :petId AND v.estado = :estado AND v.tenantId = :tenantId AND v.activo = :activo ORDER BY v.applicationDate DESC")
    List<Vaccination> findByPetIdAndEstadoAndTenantIdAndActivo(
        @Param("petId") Integer petId, 
        @Param("estado") String estado, 
        @Param("tenantId") String tenantId, 
        @Param("activo") Boolean activo
    );
    
    @Query("SELECT v FROM Vaccination v WHERE v.pet.petId = :petId AND v.tenantId = :tenantId AND v.activo = :activo ORDER BY v.applicationDate DESC")
    List<Vaccination> findByPetIdAndTenantIdAndActivo(
        @Param("petId") Integer petId, 
        @Param("tenantId") String tenantId, 
        @Param("activo") Boolean activo
    );
}

