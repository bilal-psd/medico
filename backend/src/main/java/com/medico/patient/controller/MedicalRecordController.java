package com.medico.patient.controller;

import com.medico.common.dto.PageResponse;
import com.medico.patient.dto.*;
import com.medico.patient.service.MedicalRecordService;
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
@RequestMapping("/api/v1/medical-records")
@RequiredArgsConstructor
@Tag(name = "Medical Records", description = "APIs for managing medical records (EHR)")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get patient medical records", description = "Get all medical records for a patient")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<PageResponse<MedicalRecordDto>> getPatientRecords(
        @PathVariable UUID patientId,
        @PageableDefault(size = 20, sort = "visitDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(medicalRecordService.getPatientRecords(patientId, pageable));
    }

    @GetMapping("/patient/{patientId}/search")
    @Operation(summary = "Search patient records", description = "Search medical records by diagnosis, complaint, or notes")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<PageResponse<MedicalRecordDto>> searchPatientRecords(
        @PathVariable UUID patientId,
        @RequestParam String query,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(medicalRecordService.searchPatientRecords(patientId, query, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get medical record by ID", description = "Retrieve a specific medical record")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<MedicalRecordDto> getRecordById(@PathVariable UUID id) {
        return ResponseEntity.ok(medicalRecordService.getRecordById(id));
    }

    @PostMapping
    @Operation(summary = "Create medical record", description = "Create a new medical record entry")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<MedicalRecordDto> createRecord(@Valid @RequestBody CreateMedicalRecordRequest request) {
        MedicalRecordDto record = medicalRecordService.createRecord(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(record);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update medical record", description = "Update an existing medical record")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<MedicalRecordDto> updateRecord(
        @PathVariable UUID id,
        @Valid @RequestBody CreateMedicalRecordRequest request
    ) {
        return ResponseEntity.ok(medicalRecordService.updateRecord(id, request));
    }
}

