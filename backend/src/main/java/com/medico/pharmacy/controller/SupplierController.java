package com.medico.pharmacy.controller;

import com.medico.common.dto.PageResponse;
import com.medico.pharmacy.dto.*;
import com.medico.pharmacy.service.SupplierService;
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

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/suppliers")
@RequiredArgsConstructor
@Tag(name = "Supplier Management", description = "APIs for managing suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping
    @Operation(summary = "Get all suppliers", description = "Retrieve a paginated list of all active suppliers")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public ResponseEntity<PageResponse<SupplierDto>> getAllSuppliers(
        @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(supplierService.getAllSuppliers(pageable));
    }

    @GetMapping("/search")
    @Operation(summary = "Search suppliers", description = "Search suppliers by name or code")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public ResponseEntity<PageResponse<SupplierDto>> searchSuppliers(
        @RequestParam String query,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(supplierService.searchSuppliers(query, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get supplier by ID", description = "Retrieve a specific supplier")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public ResponseEntity<SupplierDto> getSupplierById(@PathVariable UUID id) {
        return ResponseEntity.ok(supplierService.getSupplierById(id));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get supplier by code", description = "Retrieve a supplier by its code")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public ResponseEntity<SupplierDto> getSupplierByCode(@PathVariable String code) {
        return ResponseEntity.ok(supplierService.getSupplierByCode(code));
    }

    @PostMapping
    @Operation(summary = "Create supplier", description = "Add a new supplier")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public ResponseEntity<SupplierDto> createSupplier(@Valid @RequestBody CreateSupplierRequest request) {
        SupplierDto supplier = supplierService.createSupplier(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(supplier);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update supplier", description = "Update an existing supplier")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public ResponseEntity<SupplierDto> updateSupplier(
        @PathVariable UUID id,
        @Valid @RequestBody CreateSupplierRequest request
    ) {
        return ResponseEntity.ok(supplierService.updateSupplier(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate supplier", description = "Deactivate a supplier")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateSupplier(@PathVariable UUID id) {
        supplierService.deactivateSupplier(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    @Operation(summary = "Get supplier count", description = "Get the total number of active suppliers")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public ResponseEntity<Long> getSupplierCount() {
        return ResponseEntity.ok(supplierService.getActiveSupplierCount());
    }
}

