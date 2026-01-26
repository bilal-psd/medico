package com.medico.laboratory.controller;

import com.medico.common.dto.PageResponse;
import com.medico.laboratory.domain.LabOrder.OrderStatus;
import com.medico.laboratory.dto.*;
import com.medico.laboratory.service.LabOrderService;
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
@RequestMapping("/api/v1/lab-orders")
@RequiredArgsConstructor
@Tag(name = "Lab Orders", description = "APIs for managing laboratory orders")
public class LabOrderController {

    private final LabOrderService labOrderService;

    @GetMapping
    @Operation(summary = "Get all lab orders", description = "Retrieve a paginated list of all lab orders")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'LAB_TECHNICIAN')")
    public ResponseEntity<PageResponse<LabOrderDto>> getAllLabOrders(
        @PageableDefault(size = 20, sort = "orderDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(labOrderService.getAllLabOrders(pageable));
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get patient lab orders", description = "Get all lab orders for a patient")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'LAB_TECHNICIAN')")
    public ResponseEntity<PageResponse<LabOrderDto>> getPatientLabOrders(
        @PathVariable UUID patientId,
        @PageableDefault(size = 20, sort = "orderDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(labOrderService.getLabOrdersByPatient(patientId, pageable));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get lab orders by status", description = "Get lab orders filtered by status")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'LAB_TECHNICIAN')")
    public ResponseEntity<PageResponse<LabOrderDto>> getLabOrdersByStatus(
        @PathVariable OrderStatus status,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(labOrderService.getLabOrdersByStatus(status, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get lab order by ID", description = "Retrieve a specific lab order")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'LAB_TECHNICIAN')")
    public ResponseEntity<LabOrderDto> getLabOrderById(@PathVariable UUID id) {
        return ResponseEntity.ok(labOrderService.getLabOrderById(id));
    }

    @GetMapping("/number/{orderNumber}")
    @Operation(summary = "Get lab order by number", description = "Retrieve a lab order by its order number")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'LAB_TECHNICIAN')")
    public ResponseEntity<LabOrderDto> getLabOrderByNumber(@PathVariable String orderNumber) {
        return ResponseEntity.ok(labOrderService.getLabOrderByNumber(orderNumber));
    }

    @PostMapping
    @Operation(summary = "Create lab order", description = "Create a new lab order")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<LabOrderDto> createLabOrder(@Valid @RequestBody CreateLabOrderRequest request) {
        LabOrderDto labOrder = labOrderService.createLabOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(labOrder);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update lab order status", description = "Update the status of a lab order")
    @PreAuthorize("hasAnyRole('ADMIN', 'LAB_TECHNICIAN')")
    public ResponseEntity<LabOrderDto> updateLabOrderStatus(
        @PathVariable UUID id,
        @RequestParam OrderStatus status
    ) {
        return ResponseEntity.ok(labOrderService.updateLabOrderStatus(id, status));
    }

    @PostMapping("/{id}/collect-sample")
    @Operation(summary = "Collect sample", description = "Mark sample as collected for a lab order")
    @PreAuthorize("hasAnyRole('ADMIN', 'LAB_TECHNICIAN')")
    public ResponseEntity<LabOrderDto> collectSample(
        @PathVariable UUID id,
        @RequestParam UUID collectedBy
    ) {
        return ResponseEntity.ok(labOrderService.collectSample(id, collectedBy));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel lab order", description = "Cancel a lab order")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<Void> cancelLabOrder(@PathVariable UUID id) {
        labOrderService.cancelLabOrder(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/pending/count")
    @Operation(summary = "Get pending order count", description = "Get the number of pending lab orders")
    @PreAuthorize("hasAnyRole('ADMIN', 'LAB_TECHNICIAN')")
    public ResponseEntity<Long> getPendingOrderCount() {
        return ResponseEntity.ok(labOrderService.getPendingOrderCount());
    }

    @GetMapping("/today/count")
    @Operation(summary = "Get today's order count", description = "Get the number of lab orders today")
    @PreAuthorize("hasAnyRole('ADMIN', 'LAB_TECHNICIAN')")
    public ResponseEntity<Long> getTodayOrderCount() {
        return ResponseEntity.ok(labOrderService.getTodayOrderCount());
    }
}

