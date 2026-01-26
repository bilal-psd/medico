package com.medico.patient.dto;

import jakarta.validation.constraints.*;

import java.util.UUID;

public record CreatePrescriptionItemRequest(
    UUID medicationId,

    @NotBlank(message = "Medication name is required")
    String medicationName,

    @NotBlank(message = "Dosage is required")
    String dosage,

    @NotBlank(message = "Frequency is required")
    String frequency,

    String duration,

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    Integer quantity,

    String instructions,

    @Min(value = 0, message = "Refills allowed cannot be negative")
    Integer refillsAllowed
) {}

