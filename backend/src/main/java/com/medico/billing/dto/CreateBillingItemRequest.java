package com.medico.billing.dto;

import com.medico.billing.domain.BillingItem.BillingItemType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateBillingItemRequest(
    @NotNull(message = "Item type is required")
    BillingItemType itemType,

    UUID referenceId,

    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description must be less than 500 characters")
    String description,

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    Integer quantity,

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", message = "Unit price cannot be negative")
    BigDecimal unitPrice,

    @DecimalMin(value = "0.0", message = "Discount percent cannot be negative")
    @DecimalMax(value = "100.0", message = "Discount percent cannot exceed 100")
    BigDecimal discountPercent,

    String notes
) {}

