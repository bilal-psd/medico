package com.medico.billing.dto;

import com.medico.billing.domain.BillingItem.BillingItemType;

import java.math.BigDecimal;
import java.util.UUID;

public record BillingItemDto(
    UUID id,
    BillingItemType itemType,
    UUID referenceId,
    String description,
    Integer quantity,
    BigDecimal unitPrice,
    BigDecimal discountPercent,
    BigDecimal totalPrice,
    String notes
) {}

