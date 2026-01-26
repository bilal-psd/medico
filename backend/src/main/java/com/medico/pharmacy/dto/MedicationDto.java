package com.medico.pharmacy.dto;

import com.medico.pharmacy.domain.Medication.MedicationCategory;
import com.medico.pharmacy.domain.Medication.MedicationForm;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record MedicationDto(
    UUID id,
    String code,
    String name,
    String genericName,
    String brandName,
    String description,
    MedicationCategory category,
    MedicationForm form,
    String strength,
    String unit,
    String manufacturer,
    BigDecimal unitPrice,
    Integer reorderLevel,
    boolean requiresPrescription,
    boolean controlledSubstance,
    boolean active,
    String notes,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}

