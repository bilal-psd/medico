package com.medico.pharmacy.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.UUID;

public record CreateInventoryRequest(
    @NotNull(message = "Medication ID is required")
    UUID medicationId,

    @NotBlank(message = "Batch number is required")
    @Size(max = 100, message = "Batch number must be less than 100 characters")
    String batchNumber,

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    Integer quantity,

    @NotNull(message = "Expiry date is required")
    @Future(message = "Expiry date must be in the future")
    LocalDate expiryDate,

    LocalDate manufactureDate,

    @Size(max = 100, message = "Location must be less than 100 characters")
    String location,

    UUID supplierId,

    String notes
) {}

