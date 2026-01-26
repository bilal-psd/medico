package com.medico.billing.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record CreateInvoiceRequest(
    @NotNull(message = "Patient ID is required")
    UUID patientId,

    @NotNull(message = "Due date is required")
    @Future(message = "Due date must be in the future")
    LocalDate dueDate,

    @DecimalMin(value = "0.0", message = "Tax amount cannot be negative")
    BigDecimal taxAmount,

    @DecimalMin(value = "0.0", message = "Discount amount cannot be negative")
    BigDecimal discountAmount,

    String notes,

    @NotNull(message = "Creator ID is required")
    UUID createdById,

    @NotBlank(message = "Creator name is required")
    String createdByName,

    @NotEmpty(message = "At least one billing item is required")
    @Valid
    List<CreateBillingItemRequest> items
) {}

