package com.medico.laboratory.dto;

import com.medico.laboratory.domain.LabResult.ResultFlag;

import java.time.LocalDateTime;
import java.util.UUID;

public record LabResultDto(
    UUID id,
    UUID labOrderItemId,
    String testName,
    String testCode,
    String resultValue,
    String unit,
    String referenceRange,
    ResultFlag flag,
    String interpretation,
    LocalDateTime performedAt,
    UUID performedBy,
    String technicianName,
    LocalDateTime verifiedAt,
    UUID verifiedBy,
    String verifierName,
    String notes,
    boolean abnormal,
    boolean critical,
    LocalDateTime createdAt
) {}

