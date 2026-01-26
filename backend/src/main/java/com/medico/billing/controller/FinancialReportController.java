package com.medico.billing.controller;

import com.medico.billing.dto.FinancialSummaryDto;
import com.medico.billing.service.FinancialReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/reports/financial")
@RequiredArgsConstructor
@Tag(name = "Financial Reports", description = "APIs for financial reporting and analytics")
public class FinancialReportController {

    private final FinancialReportService financialReportService;

    @GetMapping("/summary")
    @Operation(summary = "Get financial summary", description = "Get a summary of financial metrics")
    @PreAuthorize("hasAnyRole('ADMIN', 'BILLING_STAFF')")
    public ResponseEntity<FinancialSummaryDto> getFinancialSummary() {
        return ResponseEntity.ok(financialReportService.getFinancialSummary());
    }

    @GetMapping("/revenue")
    @Operation(summary = "Get revenue for period", description = "Get total revenue for a date range")
    @PreAuthorize("hasAnyRole('ADMIN', 'BILLING_STAFF')")
    public ResponseEntity<BigDecimal> getRevenue(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        return ResponseEntity.ok(financialReportService.getRevenueForPeriod(start, end));
    }

    @GetMapping("/collections")
    @Operation(summary = "Get collections for period", description = "Get total collections for a date range")
    @PreAuthorize("hasAnyRole('ADMIN', 'BILLING_STAFF')")
    public ResponseEntity<BigDecimal> getCollections(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        return ResponseEntity.ok(financialReportService.getCollectionsForPeriod(start, end));
    }
}

