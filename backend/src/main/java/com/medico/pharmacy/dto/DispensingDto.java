package com.medico.pharmacy.dto;

import com.medico.pharmacy.domain.PrescriptionDispensing.DispensingStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record DispensingDto(
    UUID id,
    UUID prescriptionId,
    String prescriptionNumber,
    UUID prescriptionItemId,
    String medicationName,
    UUID inventoryId,
    String batchNumber,
    Integer dispensedQuantity,
    LocalDateTime dispensedAt,
    UUID dispensedBy,
    String pharmacistName,
    DispensingStatus status,
    String notes,
    LocalDateTime createdAt
) {}

