package com.medico.billing.dto;

import com.medico.billing.domain.Invoice.InvoiceStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record InvoiceDto(
    UUID id,
    String invoiceNumber,
    UUID patientId,
    String patientName,
    String patientMrn,
    LocalDateTime invoiceDate,
    LocalDate dueDate,
    InvoiceStatus status,
    BigDecimal subtotal,
    BigDecimal taxAmount,
    BigDecimal discountAmount,
    BigDecimal totalAmount,
    BigDecimal paidAmount,
    BigDecimal balanceDue,
    String notes,
    UUID createdById,
    String createdByName,
    List<BillingItemDto> items,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}

