package com.medico.billing.controller;

import com.medico.billing.domain.Invoice.InvoiceStatus;
import com.medico.billing.dto.*;
import com.medico.billing.service.InvoiceService;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/invoices")
@RequiredArgsConstructor
@Tag(name = "Invoice Management", description = "APIs for managing invoices")
public class InvoiceController {

    private final InvoiceService invoiceService;

    @GetMapping
    @Operation(summary = "Get all invoices", description = "Retrieve a paginated list of all invoices")
    @PreAuthorize("hasAnyRole('ADMIN', 'BILLING_STAFF')")
    public ResponseEntity<PageResponse<InvoiceDto>> getAllInvoices(
        @PageableDefault(size = 20, sort = "invoiceDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(invoiceService.getAllInvoices(pageable));
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get patient invoices", description = "Get all invoices for a patient")
    @PreAuthorize("hasAnyRole('ADMIN', 'BILLING_STAFF', 'RECEPTIONIST')")
    public ResponseEntity<PageResponse<InvoiceDto>> getPatientInvoices(
        @PathVariable UUID patientId,
        @PageableDefault(size = 20, sort = "invoiceDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(invoiceService.getInvoicesByPatient(patientId, pageable));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get invoices by status", description = "Get invoices filtered by status")
    @PreAuthorize("hasAnyRole('ADMIN', 'BILLING_STAFF')")
    public ResponseEntity<PageResponse<InvoiceDto>> getInvoicesByStatus(
        @PathVariable InvoiceStatus status,
        @PageableDefault(size = 20, sort = "invoiceDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(invoiceService.getInvoicesByStatus(status, pageable));
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue invoices", description = "Get all overdue invoices")
    @PreAuthorize("hasAnyRole('ADMIN', 'BILLING_STAFF')")
    public ResponseEntity<List<InvoiceDto>> getOverdueInvoices() {
        return ResponseEntity.ok(invoiceService.getOverdueInvoices());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get invoice by ID", description = "Retrieve a specific invoice")
    @PreAuthorize("hasAnyRole('ADMIN', 'BILLING_STAFF', 'RECEPTIONIST')")
    public ResponseEntity<InvoiceDto> getInvoiceById(@PathVariable UUID id) {
        return ResponseEntity.ok(invoiceService.getInvoiceById(id));
    }

    @GetMapping("/number/{invoiceNumber}")
    @Operation(summary = "Get invoice by number", description = "Retrieve an invoice by its number")
    @PreAuthorize("hasAnyRole('ADMIN', 'BILLING_STAFF', 'RECEPTIONIST')")
    public ResponseEntity<InvoiceDto> getInvoiceByNumber(@PathVariable String invoiceNumber) {
        return ResponseEntity.ok(invoiceService.getInvoiceByNumber(invoiceNumber));
    }

    @PostMapping
    @Operation(summary = "Create invoice", description = "Create a new invoice")
    @PreAuthorize("hasAnyRole('ADMIN', 'BILLING_STAFF')")
    public ResponseEntity<InvoiceDto> createInvoice(@Valid @RequestBody CreateInvoiceRequest request) {
        InvoiceDto invoice = invoiceService.createInvoice(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(invoice);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update invoice status", description = "Update the status of an invoice")
    @PreAuthorize("hasAnyRole('ADMIN', 'BILLING_STAFF')")
    public ResponseEntity<InvoiceDto> updateInvoiceStatus(
        @PathVariable UUID id,
        @RequestParam InvoiceStatus status
    ) {
        return ResponseEntity.ok(invoiceService.updateInvoiceStatus(id, status));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel invoice", description = "Cancel an invoice")
    @PreAuthorize("hasAnyRole('ADMIN', 'BILLING_STAFF')")
    public ResponseEntity<Void> cancelInvoice(@PathVariable UUID id) {
        invoiceService.cancelInvoice(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pending/count")
    @Operation(summary = "Get pending invoice count", description = "Get the number of pending invoices")
    @PreAuthorize("hasAnyRole('ADMIN', 'BILLING_STAFF')")
    public ResponseEntity<Long> getPendingInvoiceCount() {
        return ResponseEntity.ok(invoiceService.getPendingInvoiceCount());
    }

    @GetMapping("/overdue/count")
    @Operation(summary = "Get overdue invoice count", description = "Get the number of overdue invoices")
    @PreAuthorize("hasAnyRole('ADMIN', 'BILLING_STAFF')")
    public ResponseEntity<Long> getOverdueInvoiceCount() {
        return ResponseEntity.ok(invoiceService.getOverdueInvoiceCount());
    }

    @GetMapping("/today/count")
    @Operation(summary = "Get today's invoice count", description = "Get the number of invoices created today")
    @PreAuthorize("hasAnyRole('ADMIN', 'BILLING_STAFF')")
    public ResponseEntity<Long> getTodayInvoiceCount() {
        return ResponseEntity.ok(invoiceService.getTodayInvoiceCount());
    }
}

