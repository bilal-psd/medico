package com.medico.patient.repository;

import com.medico.patient.domain.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {

    Optional<Patient> findByMedicalRecordNumber(String medicalRecordNumber);

    boolean existsByMedicalRecordNumber(String medicalRecordNumber);

    boolean existsByEmail(String email);

    @Query("SELECT p FROM Patient p WHERE p.active = true AND " +
           "(LOWER(p.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.medicalRecordNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.phoneNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.email) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Patient> searchPatients(@Param("search") String search, Pageable pageable);

    Page<Patient> findByActiveTrue(Pageable pageable);

    @Query("SELECT p FROM Patient p WHERE p.active = true")
    Page<Patient> findAllActive(Pageable pageable);

    @Query("SELECT COUNT(p) FROM Patient p WHERE p.active = true")
    long countActivePatients();
}

