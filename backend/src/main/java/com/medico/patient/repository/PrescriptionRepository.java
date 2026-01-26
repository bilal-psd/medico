package com.medico.patient.repository;

import com.medico.patient.domain.Prescription;
import com.medico.patient.domain.Prescription.PrescriptionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PrescriptionRepository extends JpaRepository<Prescription, UUID> {

    Optional<Prescription> findByPrescriptionNumber(String prescriptionNumber);

    Page<Prescription> findByPatientIdOrderByPrescriptionDateDesc(UUID patientId, Pageable pageable);

    Page<Prescription> findByDoctorIdOrderByPrescriptionDateDesc(UUID doctorId, Pageable pageable);

    Page<Prescription> findByStatusOrderByPrescriptionDateDesc(PrescriptionStatus status, Pageable pageable);

    @Query("SELECT p FROM Prescription p WHERE p.status = 'ACTIVE' AND p.validUntil < :today")
    List<Prescription> findExpiredPrescriptions(@Param("today") LocalDate today);

    @Query("SELECT p FROM Prescription p WHERE p.patient.id = :patientId AND p.status = :status")
    List<Prescription> findByPatientIdAndStatus(
        @Param("patientId") UUID patientId,
        @Param("status") PrescriptionStatus status
    );

    @Query("SELECT COUNT(p) FROM Prescription p WHERE p.status = :status")
    long countByStatus(@Param("status") PrescriptionStatus status);

    @Query("SELECT p FROM Prescription p WHERE p.prescriptionDate BETWEEN :start AND :end")
    List<Prescription> findByDateRange(
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    boolean existsByPrescriptionNumber(String prescriptionNumber);
}

