package com.medico.patient.dto;

import com.medico.patient.domain.Appointment.AppointmentStatus;
import com.medico.patient.domain.Appointment.AppointmentType;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.UUID;

public record UpdateAppointmentRequest(
    UUID doctorId,
    String doctorName,
    String department,
    LocalDateTime appointmentDateTime,
    LocalDateTime endDateTime,
    AppointmentStatus status,
    AppointmentType type,

    @Size(max = 1000, message = "Reason must be less than 1000 characters")
    String reason,

    @Size(max = 2000, message = "Notes must be less than 2000 characters")
    String notes,

    @Size(max = 50, message = "Room number must be less than 50 characters")
    String roomNumber,

    @Size(max = 500, message = "Cancellation reason must be less than 500 characters")
    String cancelledReason
) {}

