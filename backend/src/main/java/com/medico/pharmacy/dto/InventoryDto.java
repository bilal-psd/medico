package com.medico.pharmacy.dto;

import com.medico.pharmacy.domain.Inventory.InventoryStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record InventoryDto(
    UUID id,
    UUID medicationId,
    String medicationName,
    String medicationCode,
    String batchNumber,
    Integer quantity,
    Integer reservedQuantity,
    Integer availableQuantity,
    LocalDate expiryDate,
    LocalDate manufactureDate,
    String location,
    InventoryStatus status,
    UUID supplierId,
    String supplierName,
    String notes,
    boolean expired,
    boolean expiringSoon,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}

