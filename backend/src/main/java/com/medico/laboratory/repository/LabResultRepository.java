package com.medico.laboratory.repository;

import com.medico.laboratory.domain.LabResult;
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
public interface LabResultRepository extends JpaRepository<LabResult, UUID> {

    @Query("SELECT lr FROM LabResult lr WHERE lr.labOrderItem.labOrder.patient.id = :patientId ORDER BY lr.performedAt DESC")
    Page<LabResult> findByPatientId(@Param("patientId") UUID patientId, Pageable pageable);

    @Query("SELECT lr FROM LabResult lr WHERE lr.labOrderItem.labOrder.id = :orderId")
    List<LabResult> findByOrderId(@Param("orderId") UUID orderId);

    @Query("SELECT lr FROM LabResult lr WHERE lr.abnormal = true AND lr.labOrderItem.labOrder.patient.id = :patientId ORDER BY lr.performedAt DESC")
    Page<LabResult> findAbnormalResultsByPatientId(@Param("patientId") UUID patientId, Pageable pageable);

    @Query("SELECT lr FROM LabResult lr WHERE lr.critical = true AND lr.verifiedAt IS NULL")
    List<LabResult> findUnverifiedCriticalResults();

    @Query("SELECT lr FROM LabResult lr WHERE lr.performedAt BETWEEN :start AND :end")
    List<LabResult> findByDateRange(
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    @Query("SELECT COUNT(lr) FROM LabResult lr WHERE lr.performedAt BETWEEN :start AND :end")
    long countResultsInRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT COUNT(lr) FROM LabResult lr WHERE lr.verifiedAt IS NULL")
    long countPendingVerification();
}

