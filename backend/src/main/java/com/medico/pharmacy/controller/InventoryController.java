package com.medico.pharmacy.controller;

import com.medico.common.dto.PageResponse;
import com.medico.pharmacy.domain.Inventory.InventoryStatus;
import com.medico.pharmacy.dto.*;
import com.medico.pharmacy.service.InventoryService;
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
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory Management", description = "APIs for managing pharmacy inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    @Operation(summary = "Get all inventory", description = "Retrieve a paginated list of all inventory items")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public ResponseEntity<PageResponse<InventoryDto>> getAllInventory(
        @PageableDefault(size = 20, sort = "expiryDate", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(inventoryService.getAllInventory(pageable));
    }

    @GetMapping("/medication/{medicationId}")
    @Operation(summary = "Get inventory by medication", description = "Get inventory for a specific medication")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public ResponseEntity<PageResponse<InventoryDto>> getInventoryByMedication(
        @PathVariable UUID medicationId,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(inventoryService.getInventoryByMedication(medicationId, pageable));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get inventory by status", description = "Get inventory filtered by status")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public ResponseEntity<PageResponse<InventoryDto>> getInventoryByStatus(
        @PathVariable InventoryStatus status,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(inventoryService.getInventoryByStatus(status, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get inventory by ID", description = "Retrieve a specific inventory item")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public ResponseEntity<InventoryDto> getInventoryById(@PathVariable UUID id) {
        return ResponseEntity.ok(inventoryService.getInventoryById(id));
    }

    @GetMapping("/medication/{medicationId}/available")
    @Operation(summary = "Get available quantity", description = "Get total available quantity for a medication")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public ResponseEntity<Integer> getAvailableQuantity(@PathVariable UUID medicationId) {
        return ResponseEntity.ok(inventoryService.getAvailableQuantity(medicationId));
    }

    @PostMapping
    @Operation(summary = "Add inventory", description = "Add a new inventory item")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public ResponseEntity<InventoryDto> addInventory(@Valid @RequestBody CreateInventoryRequest request) {
        InventoryDto inventory = inventoryService.addInventory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(inventory);
    }

    @PatchMapping("/{id}/quantity")
    @Operation(summary = "Update inventory quantity", description = "Adjust inventory quantity (positive or negative)")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public ResponseEntity<InventoryDto> updateInventoryQuantity(
        @PathVariable UUID id,
        @RequestParam Integer change
    ) {
        return ResponseEntity.ok(inventoryService.updateInventoryQuantity(id, change));
    }

    @GetMapping("/alerts")
    @Operation(summary = "Get inventory alerts", description = "Get all inventory alerts (low stock, expiring, etc.)")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public ResponseEntity<List<InventoryAlertDto>> getInventoryAlerts() {
        return ResponseEntity.ok(inventoryService.getInventoryAlerts());
    }

    @GetMapping("/low-stock/count")
    @Operation(summary = "Get low stock count", description = "Get the number of medications with low stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public ResponseEntity<Long> getLowStockCount() {
        return ResponseEntity.ok(inventoryService.getLowStockCount());
    }

    @GetMapping("/expiring/count")
    @Operation(summary = "Get expiring items count", description = "Get the number of items expiring soon")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public ResponseEntity<Long> getExpiringItemsCount() {
        return ResponseEntity.ok(inventoryService.getExpiringItemsCount());
    }
}

