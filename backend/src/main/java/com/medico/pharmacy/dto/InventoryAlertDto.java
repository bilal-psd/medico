package com.medico.pharmacy.dto;

import java.time.LocalDate;
import java.util.UUID;

public record InventoryAlertDto(
    UUID inventoryId,
    UUID medicationId,
    String medicationName,
    String medicationCode,
    String batchNumber,
    AlertType alertType,
    String message,
    Integer currentQuantity,
    Integer reorderLevel,
    LocalDate expiryDate
) {
    public enum AlertType {
        LOW_STOCK,
        OUT_OF_STOCK,
        EXPIRING_SOON,
        EXPIRED
    }
}

