package com.medico.laboratory.controller;

import com.medico.common.dto.PageResponse;
import com.medico.laboratory.domain.LabTest.LabTestCategory;
import com.medico.laboratory.dto.*;
import com.medico.laboratory.service.LabTestService;
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
@RequestMapping("/api/v1/lab-tests")
@RequiredArgsConstructor
@Tag(name = "Lab Test Catalog", description = "APIs for managing laboratory test catalog")
public class LabTestController {

    private final LabTestService labTestService;

    @GetMapping
    @Operation(summary = "Get all lab tests", description = "Retrieve a paginated list of all active lab tests")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'LAB_TECHNICIAN')")
    public ResponseEntity<PageResponse<LabTestDto>> getAllLabTests(
        @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(labTestService.getAllLabTests(pageable));
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Get lab tests by category", description = "Get lab tests filtered by category")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'LAB_TECHNICIAN')")
    public ResponseEntity<PageResponse<LabTestDto>> getLabTestsByCategory(
        @PathVariable LabTestCategory category,
        @PageableDefault(size = 20, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(labTestService.getLabTestsByCategory(category, pageable));
    }

    @GetMapping("/search")
    @Operation(summary = "Search lab tests", description = "Search lab tests by name or code")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'LAB_TECHNICIAN')")
    public ResponseEntity<PageResponse<LabTestDto>> searchLabTests(
        @RequestParam String query,
        @PageableDefault(size = 20) Pageable pageable
    ) {
        return ResponseEntity.ok(labTestService.searchLabTests(query, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get lab test by ID", description = "Retrieve a specific lab test")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'LAB_TECHNICIAN')")
    public ResponseEntity<LabTestDto> getLabTestById(@PathVariable UUID id) {
        return ResponseEntity.ok(labTestService.getLabTestById(id));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get lab test by code", description = "Retrieve a lab test by its code")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'LAB_TECHNICIAN')")
    public ResponseEntity<LabTestDto> getLabTestByCode(@PathVariable String code) {
        return ResponseEntity.ok(labTestService.getLabTestByCode(code));
    }

    @PostMapping
    @Operation(summary = "Create lab test", description = "Add a new lab test to the catalog")
    @PreAuthorize("hasAnyRole('ADMIN', 'LAB_TECHNICIAN')")
    public ResponseEntity<LabTestDto> createLabTest(@Valid @RequestBody CreateLabTestRequest request) {
        LabTestDto labTest = labTestService.createLabTest(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(labTest);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update lab test", description = "Update an existing lab test")
    @PreAuthorize("hasAnyRole('ADMIN', 'LAB_TECHNICIAN')")
    public ResponseEntity<LabTestDto> updateLabTest(
        @PathVariable UUID id,
        @Valid @RequestBody CreateLabTestRequest request
    ) {
        return ResponseEntity.ok(labTestService.updateLabTest(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate lab test", description = "Deactivate a lab test")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deactivateLabTest(@PathVariable UUID id) {
        labTestService.deactivateLabTest(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    @Operation(summary = "Get lab test count", description = "Get the total number of active lab tests")
    @PreAuthorize("hasAnyRole('ADMIN', 'LAB_TECHNICIAN')")
    public ResponseEntity<Long> getLabTestCount() {
        return ResponseEntity.ok(labTestService.getActiveLabTestCount());
    }
}

