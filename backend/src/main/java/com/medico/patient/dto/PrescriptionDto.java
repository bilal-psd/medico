package com.medico.patient.dto;

import com.medico.patient.domain.Prescription.PrescriptionStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PrescriptionDto(
    UUID id,
    String prescriptionNumber,
    UUID patientId,
    String patientName,
    UUID doctorId,
    String doctorName,
    LocalDateTime prescriptionDate,
    LocalDate validUntil,
    PrescriptionStatus status,
    String diagnosis,
    String notes,
    List<PrescriptionItemDto> items,
    UUID appointmentId,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}

