package com.medico.laboratory.dto;

import com.medico.laboratory.domain.LabTest.LabTestCategory;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateLabTestRequest(
    @NotBlank(message = "Test code is required")
    @Size(max = 50, message = "Code must be less than 50 characters")
    String code,

    @NotBlank(message = "Test name is required")
    @Size(max = 200, message = "Name must be less than 200 characters")
    String name,

    String description,

    @NotNull(message = "Category is required")
    LabTestCategory category,

    @Size(max = 100, message = "Sample type must be less than 100 characters")
    String sampleType,

    @Size(max = 50, message = "Sample volume must be less than 50 characters")
    String sampleVolume,

    @Size(max = 100, message = "Container type must be less than 100 characters")
    String containerType,

    String preparationInstructions,

    @Size(max = 100, message = "Turnaround time must be less than 100 characters")
    String turnaroundTime,

    @DecimalMin(value = "0.0", message = "Price cannot be negative")
    BigDecimal price,

    String normalRange,

    @Size(max = 50, message = "Unit must be less than 50 characters")
    String unit,

    String notes
) {}

