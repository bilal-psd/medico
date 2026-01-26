package com.medico.patient.dto;

import com.medico.patient.domain.MedicalRecord.RecordType;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateMedicalRecordRequest(
    @NotNull(message = "Patient ID is required")
    UUID patientId,

    @NotNull(message = "Doctor ID is required")
    UUID doctorId,

    @NotBlank(message = "Doctor name is required")
    String doctorName,

    @NotNull(message = "Visit date is required")
    LocalDateTime visitDate,

    @NotNull(message = "Record type is required")
    RecordType recordType,

    String chiefComplaint,
    String symptoms,
    String diagnosis,
    String treatmentPlan,
    String vitalSigns,
    String physicalExamination,
    String notes,
    LocalDateTime followUpDate,
    UUID appointmentId
) {}

