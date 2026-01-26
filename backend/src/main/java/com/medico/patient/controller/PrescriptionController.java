package com.medico.patient.controller;

import com.medico.common.dto.PageResponse;
import com.medico.patient.domain.Prescription.PrescriptionStatus;
import com.medico.patient.dto.*;
import com.medico.patient.service.PrescriptionService;
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
@RequestMapping("/api/v1/prescriptions")
@RequiredArgsConstructor
@Tag(name = "Prescription Management", description = "APIs for managing prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get patient prescriptions", description = "Get all prescriptions for a patient")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PHARMACIST')")
    public ResponseEntity<PageResponse<PrescriptionDto>> getPatientPrescriptions(
        @PathVariable UUID patientId,
        @PageableDefault(size = 20, sort = "prescriptionDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(prescriptionService.getPatientPrescriptions(patientId, pageable));
    }

    @GetMapping("/patient/{patientId}/active")
    @Operation(summary = "Get active prescriptions", description = "Get all active prescriptions for a patient")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PHARMACIST')")
    public ResponseEntity<List<PrescriptionDto>> getActivePrescriptions(@PathVariable UUID patientId) {
        return ResponseEntity.ok(prescriptionService.getActivePrescriptionsForPatient(patientId));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get prescriptions by status", description = "Get all prescriptions with a specific status")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public ResponseEntity<PageResponse<PrescriptionDto>> getPrescriptionsByStatus(
        @PathVariable PrescriptionStatus status,
        @PageableDefault(size = 20, sort = "prescriptionDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionsByStatus(status, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get prescription by ID", description = "Retrieve a specific prescription")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PHARMACIST')")
    public ResponseEntity<PrescriptionDto> getPrescriptionById(@PathVariable UUID id) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionById(id));
    }

    @GetMapping("/number/{prescriptionNumber}")
    @Operation(summary = "Get prescription by number", description = "Retrieve a prescription by its number")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PHARMACIST')")
    public ResponseEntity<PrescriptionDto> getPrescriptionByNumber(@PathVariable String prescriptionNumber) {
        return ResponseEntity.ok(prescriptionService.getPrescriptionByNumber(prescriptionNumber));
    }

    @PostMapping
    @Operation(summary = "Create prescription", description = "Create a new prescription")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<PrescriptionDto> createPrescription(@Valid @RequestBody CreatePrescriptionRequest request) {
        PrescriptionDto prescription = prescriptionService.createPrescription(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(prescription);
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update prescription status", description = "Update the status of a prescription")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PHARMACIST')")
    public ResponseEntity<PrescriptionDto> updatePrescriptionStatus(
        @PathVariable UUID id,
        @RequestParam PrescriptionStatus status
    ) {
        return ResponseEntity.ok(prescriptionService.updatePrescriptionStatus(id, status));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel prescription", description = "Cancel a prescription")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<Void> cancelPrescription(@PathVariable UUID id) {
        prescriptionService.cancelPrescription(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/active/count")
    @Operation(summary = "Get active prescription count", description = "Get the number of active prescriptions")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public ResponseEntity<Long> getActivePrescriptionCount() {
        return ResponseEntity.ok(prescriptionService.getActivePrescriptionCount());
    }
}

