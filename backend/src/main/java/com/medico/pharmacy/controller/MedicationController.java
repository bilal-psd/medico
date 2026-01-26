package com.medico.pharmacy.controller;

import com.medico.common.dto.PageResponse;
import com.medico.pharmacy.domain.Medication.MedicationCategory;
import com.medico.pharmacy.dto.*;
import com.medico.pharmacy.service.MedicationService;
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
@RequestMapping("/api/v1/medications")
@RequiredArgsConstructor
@Tag(name = "Medication Catalog", description = "APIs for managing medication catalog")
public class MedicationController {

    private final MedicationService medicationService;

    @GetMapping
    @Operation(summary = "Get all medications", description = "Retrieve a paginated list of all active medications")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PHARMACIST')")
    public ResponseEntity<PageResponse<MedicationDto>> getAllMedications(
        @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(medicationService.getAllMedications(pageable));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get medications by category", description = "Get medications filtered by category")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PHARMACIST')")
    public ResponseEntity<PageResponse<MedicationDto>> getMedicationsByCategory(
        @PathVariable MedicationCategory category,
        @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(medicationService.getMedicationsByCategory(category, pageable));
    }

    @GetMapping("/search")
    @Operation(summary = "Search medications", description = "Search medications by name or code")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PHARMACIST')")
    public ResponseEntity<PageResponse<MedicationDto>> searchMedications(
        @RequestParam String query,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(medicationService.searchMedications(query, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get medication by ID", description = "Retrieve a specific medication")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PHARMACIST')")
    public ResponseEntity<MedicationDto> getMedicationById(@PathVariable UUID id) {
        return ResponseEntity.ok(medicationService.getMedicationById(id));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get medication by code", description = "Retrieve a medication by its code")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PHARMACIST')")
    public ResponseEntity<MedicationDto> getMedicationByCode(@PathVariable String code) {
        return ResponseEntity.ok(medicationService.getMedicationByCode(code));
    }

    @PostMapping
    @Operation(summary = "Create medication", description = "Add a new medication to the catalog")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public ResponseEntity<MedicationDto> createMedication(@Valid @RequestBody CreateMedicationRequest request) {
        MedicationDto medication = medicationService.createMedication(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(medication);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update medication", description = "Update an existing medication")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public ResponseEntity<MedicationDto> updateMedication(
        @PathVariable UUID id,
        @Valid @RequestBody CreateMedicationRequest request
    ) {
        return ResponseEntity.ok(medicationService.updateMedication(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate medication", description = "Deactivate a medication")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateMedication(@PathVariable UUID id) {
        medicationService.deactivateMedication(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    @Operation(summary = "Get medication count", description = "Get the total number of active medications")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public ResponseEntity<Long> getMedicationCount() {
        return ResponseEntity.ok(medicationService.getActiveMedicationCount());
    }
}

