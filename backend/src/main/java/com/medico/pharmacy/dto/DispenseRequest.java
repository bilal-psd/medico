package com.medico.pharmacy.dto;

import jakarta.validation.constraints.*;

import java.util.UUID;

public record DispenseRequest(
    @NotNull(message = "Prescription ID is required")
    UUID prescriptionId,

    @NotNull(message = "Prescription item ID is required")
    UUID prescriptionItemId,

    @NotNull(message = "Inventory ID is required")
    UUID inventoryId,

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    Integer quantity,

    @NotNull(message = "Pharmacist ID is required")
    UUID pharmacistId,

    @NotBlank(message = "Pharmacist name is required")
    String pharmacistName,

    String notes
) {}

