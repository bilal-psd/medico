package com.medico.pharmacy.controller;

import com.medico.common.dto.PageResponse;
import com.medico.pharmacy.dto.*;
import com.medico.pharmacy.service.DispensingService;
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
@RequestMapping("/api/v1/dispensing")
@RequiredArgsConstructor
@Tag(name = "Prescription Dispensing", description = "APIs for dispensing prescriptions")
public class DispensingController {

    private final DispensingService dispensingService;

    @GetMapping("/prescription/{prescriptionId}")
    @Operation(summary = "Get dispensings by prescription", description = "Get all dispensing records for a prescription")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public ResponseEntity<List<DispensingDto>> getDispensingsByPrescription(@PathVariable UUID prescriptionId) {
        return ResponseEntity.ok(dispensingService.getDispensingsByPrescription(prescriptionId));
    }

    @GetMapping("/pharmacist/{pharmacistId}")
    @Operation(summary = "Get dispensings by pharmacist", description = "Get all dispensing records by a pharmacist")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public ResponseEntity<PageResponse<DispensingDto>> getDispensingsByPharmacist(
        @PathVariable UUID pharmacistId,
        @PageableDefault(size = 20, sort = "dispensedAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(dispensingService.getDispensingsByPharmacist(pharmacistId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get dispensing by ID", description = "Retrieve a specific dispensing record")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public ResponseEntity<DispensingDto> getDispensingById(@PathVariable UUID id) {
        return ResponseEntity.ok(dispensingService.getDispensingById(id));
    }

    @PostMapping
    @Operation(summary = "Dispense prescription item", description = "Dispense a prescription item from inventory")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public ResponseEntity<DispensingDto> dispensePrescriptionItem(@Valid @RequestBody DispenseRequest request) {
        DispensingDto dispensing = dispensingService.dispensePrescriptionItem(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dispensing);
    }

    @GetMapping("/today/count")
    @Operation(summary = "Get today's dispensing count", description = "Get the number of dispensings today")
    @PreAuthorize("hasAnyRole('ADMIN', 'PHARMACIST')")
    public ResponseEntity<Long> getTodayDispensingCount() {
        return ResponseEntity.ok(dispensingService.getTodayDispensingCount());
    }
}

