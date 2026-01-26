package com.medico.laboratory.dto;

import com.medico.laboratory.domain.LabTest.LabTestCategory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record LabTestDto(
    UUID id,
    String code,
    String name,
    String description,
    LabTestCategory category,
    String sampleType,
    String sampleVolume,
    String containerType,
    String preparationInstructions,
    String turnaroundTime,
    BigDecimal price,
    String normalRange,
    String unit,
    boolean active,
    String notes,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}

