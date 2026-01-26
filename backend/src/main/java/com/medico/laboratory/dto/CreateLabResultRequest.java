package com.medico.laboratory.dto;

import com.medico.laboratory.domain.LabResult.ResultFlag;
import jakarta.validation.constraints.*;

import java.util.UUID;

public record CreateLabResultRequest(
    @NotNull(message = "Lab order item ID is required")
    UUID labOrderItemId,

    @NotBlank(message = "Result value is required")
    String resultValue,

    String unit,
    String referenceRange,

    @NotNull(message = "Result flag is required")
    ResultFlag flag,

    String interpretation,

    @NotNull(message = "Technician ID is required")
    UUID performedBy,

    @NotBlank(message = "Technician name is required")
    String technicianName,

    String notes,

    boolean abnormal,
    boolean critical
) {}

