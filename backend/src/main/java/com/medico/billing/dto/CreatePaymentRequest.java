package com.medico.billing.dto;

import com.medico.billing.domain.Payment.PaymentMethod;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePaymentRequest(
    @NotNull(message = "Invoice ID is required")
    UUID invoiceId,

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than zero")
    BigDecimal amount,

    @NotNull(message = "Payment method is required")
    PaymentMethod paymentMethod,

    @Size(max = 200, message = "Transaction reference must be less than 200 characters")
    String transactionReference,

    @NotNull(message = "Receiver ID is required")
    UUID receivedById,

    @NotBlank(message = "Receiver name is required")
    String receivedByName,

    String notes
) {}

