package com.medico.patient.dto;

import com.medico.patient.domain.MedicalRecord.RecordType;

import java.time.LocalDateTime;
import java.util.UUID;

public record MedicalRecordDto(
    UUID id,
    UUID patientId,
    String patientName,
    UUID doctorId,
    String doctorName,
    LocalDateTime visitDate,
    RecordType recordType,
    String chiefComplaint,
    String symptoms,
    String diagnosis,
    String treatmentPlan,
    String vitalSigns,
    String physicalExamination,
    String notes,
    LocalDateTime followUpDate,
    UUID appointmentId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}

