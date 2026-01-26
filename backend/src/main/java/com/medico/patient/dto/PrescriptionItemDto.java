package com.medico.patient.dto;

import java.util.UUID;

public record PrescriptionItemDto(
    UUID id,
    UUID medicationId,
    String medicationName,
    String dosage,
    String frequency,
    String duration,
    Integer quantity,
    String instructions,
    Integer dispensedQuantity,
    Integer refillsAllowed,
    Integer refillsRemaining
) {}

