package com.medico.laboratory.controller;

import com.medico.common.dto.PageResponse;
import com.medico.laboratory.dto.*;
import com.medico.laboratory.service.LabResultService;
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
@RequestMapping("/api/v1/lab-results")
@RequiredArgsConstructor
@Tag(name = "Lab Results", description = "APIs for managing laboratory results")
public class LabResultController {

    private final LabResultService labResultService;

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get patient lab results", description = "Get all lab results for a patient")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'LAB_TECHNICIAN')")
    public ResponseEntity<PageResponse<LabResultDto>> getPatientResults(
        @PathVariable UUID patientId,
        @PageableDefault(size = 20, sort = "performedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(labResultService.getPatientResults(patientId, pageable));
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get order results", description = "Get all results for a lab order")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'LAB_TECHNICIAN')")
    public ResponseEntity<List<LabResultDto>> getOrderResults(@PathVariable UUID orderId) {
        return ResponseEntity.ok(labResultService.getResultsByOrder(orderId));
    }

    @GetMapping("/patient/{patientId}/abnormal")
    @Operation(summary = "Get abnormal results", description = "Get abnormal lab results for a patient")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<PageResponse<LabResultDto>> getAbnormalResults(
        @PathVariable UUID patientId,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(labResultService.getAbnormalResults(patientId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get result by ID", description = "Retrieve a specific lab result")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'LAB_TECHNICIAN')")
    public ResponseEntity<LabResultDto> getResultById(@PathVariable UUID id) {
        return ResponseEntity.ok(labResultService.getResultById(id));
    }

    @PostMapping
    @Operation(summary = "Create lab result", description = "Add a result for a lab order item")
    @PreAuthorize("hasAnyRole('ADMIN', 'LAB_TECHNICIAN')")
    public ResponseEntity<LabResultDto> createResult(@Valid @RequestBody CreateLabResultRequest request) {
        LabResultDto result = labResultService.createResult(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PostMapping("/{id}/verify")
    @Operation(summary = "Verify lab result", description = "Verify/approve a lab result")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'LAB_TECHNICIAN')")
    public ResponseEntity<LabResultDto> verifyResult(
        @PathVariable UUID id,
        @RequestParam UUID verifiedBy,
        @RequestParam String verifierName
    ) {
        return ResponseEntity.ok(labResultService.verifyResult(id, verifiedBy, verifierName));
    }

    @GetMapping("/critical/unverified")
    @Operation(summary = "Get unverified critical results", description = "Get critical results pending verification")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'LAB_TECHNICIAN')")
    public ResponseEntity<List<LabResultDto>> getUnverifiedCriticalResults() {
        return ResponseEntity.ok(labResultService.getUnverifiedCriticalResults());
    }

    @GetMapping("/today/count")
    @Operation(summary = "Get today's result count", description = "Get the number of results entered today")
    @PreAuthorize("hasAnyRole('ADMIN', 'LAB_TECHNICIAN')")
    public ResponseEntity<Long> getTodayResultCount() {
        return ResponseEntity.ok(labResultService.getTodayResultCount());
    }

    @GetMapping("/pending-verification/count")
    @Operation(summary = "Get pending verification count", description = "Get the number of results pending verification")
    @PreAuthorize("hasAnyRole('ADMIN', 'LAB_TECHNICIAN')")
    public ResponseEntity<Long> getPendingVerificationCount() {
        return ResponseEntity.ok(labResultService.getPendingVerificationCount());
    }
}

