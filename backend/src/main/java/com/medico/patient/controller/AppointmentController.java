package com.medico.patient.controller;

import com.medico.common.dto.PageResponse;
import com.medico.patient.domain.Appointment.AppointmentStatus;
import com.medico.patient.dto.*;
import com.medico.patient.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointment Management", description = "APIs for managing appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @GetMapping
    @Operation(summary = "Get all appointments", description = "Retrieve a paginated list of all appointments")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    public ResponseEntity<PageResponse<AppointmentDto>> getAllAppointments(
        @PageableDefault(size = 20, sort = "appointmentDateTime", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(appointmentService.getAllAppointments(pageable));
    }

    @GetMapping("/patient/{patientId}")
    @Operation(summary = "Get patient appointments", description = "Get all appointments for a specific patient")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    public ResponseEntity<PageResponse<AppointmentDto>> getPatientAppointments(
        @PathVariable UUID patientId,
        @PageableDefault(size = 20, sort = "appointmentDateTime", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByPatient(patientId, pageable));
    }

    @GetMapping("/doctor/{doctorId}")
    @Operation(summary = "Get doctor appointments", description = "Get all appointments for a specific doctor")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    public ResponseEntity<PageResponse<AppointmentDto>> getDoctorAppointments(
        @PathVariable UUID doctorId,
        @PageableDefault(size = 20, sort = "appointmentDateTime", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByDoctor(doctorId, pageable));
    }

    @GetMapping("/date-range")
    @Operation(summary = "Get appointments by date range", description = "Get appointments within a date range")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    public ResponseEntity<List<AppointmentDto>> getAppointmentsByDateRange(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(appointmentService.getAppointmentsByDateRange(startDate, endDate));
    }

    @GetMapping("/doctor/{doctorId}/date-range")
    @Operation(summary = "Get doctor appointments by date range", description = "Get a doctor's appointments within a date range")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    public ResponseEntity<List<AppointmentDto>> getDoctorAppointmentsByDateRange(
        @PathVariable UUID doctorId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(appointmentService.getDoctorAppointmentsByDateRange(doctorId, startDate, endDate));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get appointment by ID", description = "Retrieve a specific appointment by ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    public ResponseEntity<AppointmentDto> getAppointmentById(@PathVariable UUID id) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    @PostMapping
    @Operation(summary = "Create appointment", description = "Schedule a new appointment")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    public ResponseEntity<AppointmentDto> createAppointment(@Valid @RequestBody CreateAppointmentRequest request) {
        AppointmentDto appointment = appointmentService.createAppointment(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(appointment);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update appointment", description = "Update an existing appointment")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    public ResponseEntity<AppointmentDto> updateAppointment(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateAppointmentRequest request
    ) {
        return ResponseEntity.ok(appointmentService.updateAppointment(id, request));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update appointment status", description = "Update the status of an appointment")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    public ResponseEntity<AppointmentDto> updateAppointmentStatus(
        @PathVariable UUID id,
        @RequestParam AppointmentStatus status
    ) {
        return ResponseEntity.ok(appointmentService.updateAppointmentStatus(id, status));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel appointment", description = "Cancel an appointment with a reason")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'RECEPTIONIST')")
    public ResponseEntity<Void> cancelAppointment(
        @PathVariable UUID id,
        @RequestParam(required = false) String reason
    ) {
        appointmentService.cancelAppointment(id, reason);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/today/count")
    @Operation(summary = "Get today's appointment count", description = "Get the number of appointments scheduled for today")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECEPTIONIST')")
    public ResponseEntity<Long> getTodayAppointmentCount() {
        return ResponseEntity.ok(appointmentService.getTodayAppointmentCount());
    }
}

