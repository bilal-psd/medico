package com.medico.patient.dto;

import com.medico.patient.domain.Appointment.AppointmentType;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreateAppointmentRequest(
    @NotNull(message = "Patient ID is required")
    UUID patientId,

    @NotNull(message = "Doctor ID is required")
    UUID doctorId,

    @NotBlank(message = "Doctor name is required")
    String doctorName,

    String department,

    @NotNull(message = "Appointment date/time is required")
    @Future(message = "Appointment must be in the future")
    LocalDateTime appointmentDateTime,

    LocalDateTime endDateTime,

    @NotNull(message = "Appointment type is required")
    AppointmentType type,

    @Size(max = 1000, message = "Reason must be less than 1000 characters")
    String reason,

    @Size(max = 2000, message = "Notes must be less than 2000 characters")
    String notes,

    @Size(max = 50, message = "Room number must be less than 50 characters")
    String roomNumber
) {}

