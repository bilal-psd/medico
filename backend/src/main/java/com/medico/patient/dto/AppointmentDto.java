package com.medico.patient.dto;

import com.medico.patient.domain.Appointment.AppointmentStatus;
import com.medico.patient.domain.Appointment.AppointmentType;

import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentDto(
    UUID id,
    UUID patientId,
    String patientName,
    String patientMrn,
    UUID doctorId,
    String doctorName,
    String department,
    LocalDateTime appointmentDateTime,
    LocalDateTime endDateTime,
    AppointmentStatus status,
    AppointmentType type,
    String reason,
    String notes,
    String roomNumber,
    String cancelledReason,
    LocalDateTime cancelledAt,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}

