package com.medico.billing.service;

import com.medico.billing.domain.Invoice.InvoiceStatus;
import com.medico.billing.dto.FinancialSummaryDto;
import com.medico.billing.repository.InvoiceRepository;
import com.medico.billing.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FinancialReportService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;

    public FinancialSummaryDto getFinancialSummary() {
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = LocalDate.now().atTime(LocalTime.MAX);
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        BigDecimal totalRevenue = invoiceRepository.getTotalRevenue(startOfMonth, endOfMonth);
        BigDecimal totalCollected = invoiceRepository.getTotalCollected(startOfMonth, endOfMonth);
        BigDecimal totalOutstanding = invoiceRepository.getTotalOutstanding();

        long pendingInvoices = invoiceRepository.countByStatus(InvoiceStatus.PENDING);
        long overdueInvoices = invoiceRepository.countByStatus(InvoiceStatus.OVERDUE);
        long todayInvoices = invoiceRepository.countInvoicesInRange(startOfDay, endOfDay);
        long todayPayments = paymentRepository.countPaymentsInRange(startOfDay, endOfDay);

        return new FinancialSummaryDto(
            totalRevenue != null ? totalRevenue : BigDecimal.ZERO,
            totalCollected != null ? totalCollected : BigDecimal.ZERO,
            totalOutstanding != null ? totalOutstanding : BigDecimal.ZERO,
            pendingInvoices,
            overdueInvoices,
            todayInvoices,
            todayPayments
        );
    }

    public BigDecimal getRevenueForPeriod(LocalDateTime start, LocalDateTime end) {
        BigDecimal revenue = invoiceRepository.getTotalRevenue(start, end);
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    public BigDecimal getCollectionsForPeriod(LocalDateTime start, LocalDateTime end) {
        BigDecimal collected = paymentRepository.getTotalPayments(start, end);
        return collected != null ? collected : BigDecimal.ZERO;
    }
}

