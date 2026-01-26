package com.medico.billing.dto;

import com.medico.billing.domain.Payment.PaymentMethod;
import com.medico.billing.domain.Payment.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PaymentDto(
    UUID id,
    String paymentNumber,
    UUID invoiceId,
    String invoiceNumber,
    BigDecimal amount,
    LocalDateTime paymentDate,
    PaymentMethod paymentMethod,
    PaymentStatus status,
    String transactionReference,
    UUID receivedById,
    String receivedByName,
    String notes,
    LocalDateTime createdAt
) {}

