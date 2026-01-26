package com.medico.pharmacy.dto;

import com.medico.pharmacy.domain.Medication.MedicationCategory;
import com.medico.pharmacy.domain.Medication.MedicationForm;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateMedicationRequest(
    @NotBlank(message = "Medication code is required")
    @Size(max = 50, message = "Code must be less than 50 characters")
    String code,

    @NotBlank(message = "Medication name is required")
    @Size(max = 200, message = "Name must be less than 200 characters")
    String name,

    @Size(max = 200, message = "Generic name must be less than 200 characters")
    String genericName,

    @Size(max = 200, message = "Brand name must be less than 200 characters")
    String brandName,

    String description,

    @NotNull(message = "Category is required")
    MedicationCategory category,

    @NotNull(message = "Form is required")
    MedicationForm form,

    @Size(max = 100, message = "Strength must be less than 100 characters")
    String strength,

    @Size(max = 50, message = "Unit must be less than 50 characters")
    String unit,

    @Size(max = 200, message = "Manufacturer must be less than 200 characters")
    String manufacturer,

    @DecimalMin(value = "0.0", message = "Unit price cannot be negative")
    BigDecimal unitPrice,

    @Min(value = 0, message = "Reorder level cannot be negative")
    Integer reorderLevel,

    boolean requiresPrescription,
    boolean controlledSubstance,
    String notes
) {}

