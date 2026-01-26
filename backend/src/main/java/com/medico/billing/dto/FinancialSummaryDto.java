package com.medico.billing.dto;

import java.math.BigDecimal;

public record FinancialSummaryDto(
    BigDecimal totalRevenue,
    BigDecimal totalCollected,
    BigDecimal totalOutstanding,
    long pendingInvoices,
    long overdueInvoices,
    long todayInvoices,
    long todayPayments
) {}

