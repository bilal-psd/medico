package com.medico.billing.controller;

import com.medico.billing.domain.Payment.PaymentStatus;
import com.medico.billing.dto.*;
import com.medico.billing.service.PaymentService;
import com.medico.common.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payment Management", description = "APIs for managing payments")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/invoice/{invoiceId}")
    @Operation(summary = "Get invoice payments", description = "Get all payments for an invoice")
    @PreAuthorize("hasAnyRole('ADMIN', 'BILLING_STAFF')")
    public ResponseEntity<List<PaymentDto>> getInvoicePayments(@PathVariable UUID invoiceId) {
        return ResponseEntity.ok(paymentService.getPaymentsByInvoice(invoiceId));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get payments by status", description = "Get payments filtered by status")
    @PreAuthorize("hasAnyRole('ADMIN', 'BILLING_STAFF')")
    public ResponseEntity<PageResponse<PaymentDto>> getPaymentsByStatus(
        @PathVariable PaymentStatus status,
        @PageableDefault(size = 20, sort = "paymentDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(paymentService.getPaymentsByStatus(status, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get payment by ID", description = "Retrieve a specific payment")
    @PreAuthorize("hasAnyRole('ADMIN', 'BILLING_STAFF')")
    public ResponseEntity<PaymentDto> getPaymentById(@PathVariable UUID id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @GetMapping("/number/{paymentNumber}")
    @Operation(summary = "Get payment by number", description = "Retrieve a payment by its number")
    @PreAuthorize("hasAnyRole('ADMIN', 'BILLING_STAFF')")
    public ResponseEntity<PaymentDto> getPaymentByNumber(@PathVariable String paymentNumber) {
        return ResponseEntity.ok(paymentService.getPaymentByNumber(paymentNumber));
    }

    @PostMapping
    @Operation(summary = "Create payment", description = "Record a new payment")
    @PreAuthorize("hasAnyRole('ADMIN', 'BILLING_STAFF')")
    public ResponseEntity<PaymentDto> createPayment(@Valid @RequestBody CreatePaymentRequest request) {
        PaymentDto payment = paymentService.createPayment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(payment);
    }

    @PostMapping("/{id}/refund")
    @Operation(summary = "Refund payment", description = "Refund a payment")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentDto> refundPayment(
        @PathVariable UUID id,
        @RequestParam(required = false) String notes
    ) {
        return ResponseEntity.ok(paymentService.refundPayment(id, notes));
    }

    @GetMapping("/today/count")
    @Operation(summary = "Get today's payment count", description = "Get the number of payments today")
    @PreAuthorize("hasAnyRole('ADMIN', 'BILLING_STAFF')")
    public ResponseEntity<Long> getTodayPaymentCount() {
        return ResponseEntity.ok(paymentService.getTodayPaymentCount());
    }

    @GetMapping("/today/total")
    @Operation(summary = "Get today's total payments", description = "Get the total amount of payments today")
    @PreAuthorize("hasAnyRole('ADMIN', 'BILLING_STAFF')")
    public ResponseEntity<BigDecimal> getTodayTotalPayments() {
        return ResponseEntity.ok(paymentService.getTodayTotalPayments());
    }
}

