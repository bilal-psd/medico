package com.medico.pharmacy.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record SupplierDto(
    UUID id,
    String code,
    String name,
    String contactPerson,
    String email,
    String phone,
    String address,
    String city,
    String state,
    String postalCode,
    String country,
    String taxId,
    String paymentTerms,
    Integer leadTimeDays,
    boolean active,
    String notes,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}

