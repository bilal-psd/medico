package com.medico.pharmacy.repository;

import com.medico.pharmacy.domain.PrescriptionDispensing;
import com.medico.pharmacy.domain.PrescriptionDispensing.DispensingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PrescriptionDispensingRepository extends JpaRepository<PrescriptionDispensing, UUID> {

    List<PrescriptionDispensing> findByPrescriptionId(UUID prescriptionId);

    List<PrescriptionDispensing> findByPrescriptionItemId(UUID prescriptionItemId);

    Page<PrescriptionDispensing> findByStatus(DispensingStatus status, Pageable pageable);

    @Query("SELECT pd FROM PrescriptionDispensing pd WHERE pd.dispensedBy = :pharmacistId ORDER BY pd.dispensedAt DESC")
    Page<PrescriptionDispensing> findByPharmacist(@Param("pharmacistId") UUID pharmacistId, Pageable pageable);

    @Query("SELECT pd FROM PrescriptionDispensing pd WHERE pd.dispensedAt BETWEEN :start AND :end")
    List<PrescriptionDispensing> findByDateRange(
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    @Query("SELECT SUM(pd.dispensedQuantity) FROM PrescriptionDispensing pd WHERE pd.inventory.medication.id = :medicationId AND pd.dispensedAt BETWEEN :start AND :end")
    Integer getTotalDispensedQuantity(
        @Param("medicationId") UUID medicationId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    @Query("SELECT COUNT(pd) FROM PrescriptionDispensing pd WHERE pd.dispensedAt BETWEEN :start AND :end")
    long countDispensingsInRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}

