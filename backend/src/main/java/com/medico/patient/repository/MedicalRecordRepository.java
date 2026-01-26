package com.medico.patient.repository;

import com.medico.patient.domain.MedicalRecord;
import com.medico.patient.domain.MedicalRecord.RecordType;
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
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, UUID> {

    Page<MedicalRecord> findByPatientIdOrderByVisitDateDesc(UUID patientId, Pageable pageable);

    Page<MedicalRecord> findByPatientIdAndRecordTypeOrderByVisitDateDesc(
        UUID patientId,
        RecordType recordType,
        Pageable pageable
    );

    List<MedicalRecord> findByPatientIdAndVisitDateBetweenOrderByVisitDateDesc(
        UUID patientId,
        LocalDateTime start,
        LocalDateTime end
    );

    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.doctorId = :doctorId ORDER BY mr.visitDate DESC")
    Page<MedicalRecord> findByDoctorId(@Param("doctorId") UUID doctorId, Pageable pageable);

    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.patient.id = :patientId AND " +
           "(LOWER(mr.diagnosis) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(mr.chiefComplaint) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(mr.notes) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<MedicalRecord> searchPatientRecords(
        @Param("patientId") UUID patientId,
        @Param("search") String search,
        Pageable pageable
    );
}

