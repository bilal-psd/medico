package com.medico.patient.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CreatePrescriptionRequest(
    @NotNull(message = "Patient ID is required")
    UUID patientId,

    @NotNull(message = "Doctor ID is required")
    UUID doctorId,

    @NotBlank(message = "Doctor name is required")
    String doctorName,

    @Future(message = "Valid until date must be in the future")
    LocalDate validUntil,

    String diagnosis,
    String notes,
    UUID appointmentId,

    @NotEmpty(message = "At least one prescription item is required")
    @Valid
    List<CreatePrescriptionItemRequest> items
) {}

