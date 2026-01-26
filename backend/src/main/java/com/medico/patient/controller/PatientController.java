package com.medico.patient.controller;

import com.medico.common.dto.PageResponse;
import com.medico.patient.dto.*;
import com.medico.patient.service.PatientService;
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
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
@Tag(name = "Patient Management", description = "APIs for managing patients")
public class PatientController {

    private final PatientService patientService;

    @GetMapping
    @Operation(summary = "Get all patients", description = "Retrieve a paginated list of all active patients")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    public ResponseEntity<PageResponse<PatientDto>> getAllPatients(
        @PageableDefault(size = 20, sort = "lastName", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(patientService.getAllPatients(pageable));
    }

    @GetMapping("/search")
    @Operation(summary = "Search patients", description = "Search patients by name, MRN, phone, or email")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    public ResponseEntity<PageResponse<PatientDto>> searchPatients(
        @RequestParam String query,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(patientService.searchPatients(query, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get patient by ID", description = "Retrieve a specific patient by their ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST', 'PHARMACIST', 'LAB_TECHNICIAN')")
    public ResponseEntity<PatientDto> getPatientById(@PathVariable UUID id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }

    @GetMapping("/mrn/{mrn}")
    @Operation(summary = "Get patient by MRN", description = "Retrieve a specific patient by their Medical Record Number")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST', 'PHARMACIST', 'LAB_TECHNICIAN')")
    public ResponseEntity<PatientDto> getPatientByMrn(@PathVariable String mrn) {
        return ResponseEntity.ok(patientService.getPatientByMrn(mrn));
    }

    @PostMapping
    @Operation(summary = "Create patient", description = "Register a new patient")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<PatientDto> createPatient(@Valid @RequestBody CreatePatientRequest request) {
        PatientDto patient = patientService.createPatient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(patient);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update patient", description = "Update an existing patient's information")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<PatientDto> updatePatient(
        @PathVariable UUID id,
        @Valid @RequestBody UpdatePatientRequest request
    ) {
        return ResponseEntity.ok(patientService.updatePatient(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate patient", description = "Soft delete a patient by deactivating their record")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivatePatient(@PathVariable UUID id) {
        patientService.deactivatePatient(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/activate")
    @Operation(summary = "Activate patient", description = "Reactivate a deactivated patient")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> activatePatient(@PathVariable UUID id) {
        patientService.activatePatient(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    @Operation(summary = "Get patient count", description = "Get the total number of active patients")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<Long> getPatientCount() {
        return ResponseEntity.ok(patientService.getActivePatientCount());
    }
}

