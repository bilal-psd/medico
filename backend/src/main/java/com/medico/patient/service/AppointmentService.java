package com.medico.patient.service;

import com.medico.common.dto.PageResponse;
import com.medico.common.exception.BusinessException;
import com.medico.common.exception.ResourceNotFoundException;
import com.medico.patient.domain.Appointment;
import com.medico.patient.domain.Appointment.AppointmentStatus;
import com.medico.patient.domain.Patient;
import com.medico.patient.dto.*;
import com.medico.patient.mapper.PatientMapper;
import com.medico.patient.repository.AppointmentRepository;
import com.medico.patient.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    public PageResponse<AppointmentDto> getAllAppointments(Pageable pageable) {
        Page<Appointment> appointments = appointmentRepository.findAll(pageable);
        return PageResponse.from(appointments, appointments.getContent().stream()
            .map(patientMapper::toDto)
            .toList());
    }

    public PageResponse<AppointmentDto> getAppointmentsByPatient(UUID patientId, Pageable pageable) {
        Page<Appointment> appointments = appointmentRepository.findByPatientId(patientId, pageable);
        return PageResponse.from(appointments, appointments.getContent().stream()
            .map(patientMapper::toDto)
            .toList());
    }

    public PageResponse<AppointmentDto> getAppointmentsByDoctor(UUID doctorId, Pageable pageable) {
        Page<Appointment> appointments = appointmentRepository.findByDoctorId(doctorId, pageable);
        return PageResponse.from(appointments, appointments.getContent().stream()
            .map(patientMapper::toDto)
            .toList());
    }

    public List<AppointmentDto> getAppointmentsByDateRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        List<Appointment> appointments = appointmentRepository.findActiveAppointmentsByDateRange(start, end);
        return patientMapper.toAppointmentDtoList(appointments);
    }

    public List<AppointmentDto> getDoctorAppointmentsByDateRange(UUID doctorId, LocalDate startDate, LocalDate endDate) {
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atTime(LocalTime.MAX);
        List<Appointment> appointments = appointmentRepository.findByDoctorIdAndDateRange(doctorId, start, end);
        return patientMapper.toAppointmentDtoList(appointments);
    }

    public AppointmentDto getAppointmentById(UUID id) {
        Appointment appointment = findAppointmentById(id);
        return patientMapper.toDto(appointment);
    }

    @Transactional
    public AppointmentDto createAppointment(CreateAppointmentRequest request) {
        Patient patient = patientRepository.findById(request.patientId())
            .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", request.patientId()));

        // Check for conflicting appointments
        LocalDateTime endTime = request.endDateTime() != null
            ? request.endDateTime()
            : request.appointmentDateTime().plusMinutes(30);

        if (hasConflictingAppointment(request.doctorId(), request.appointmentDateTime(), endTime)) {
            throw new BusinessException("Doctor has a conflicting appointment at this time");
        }

        Appointment appointment = patientMapper.toEntity(request);
        appointment.setPatient(patient);
        appointment.setEndDateTime(endTime);

        Appointment savedAppointment = appointmentRepository.save(appointment);
        log.info("Created appointment {} for patient {}", savedAppointment.getId(), patient.getMedicalRecordNumber());

        return patientMapper.toDto(savedAppointment);
    }

    @Transactional
    public AppointmentDto updateAppointment(UUID id, UpdateAppointmentRequest request) {
        Appointment appointment = findAppointmentById(id);

        if (request.appointmentDateTime() != null || request.doctorId() != null) {
            UUID doctorId = request.doctorId() != null ? request.doctorId() : appointment.getDoctorId();
            LocalDateTime startTime = request.appointmentDateTime() != null
                ? request.appointmentDateTime()
                : appointment.getAppointmentDateTime();
            LocalDateTime endTime = request.endDateTime() != null
                ? request.endDateTime()
                : appointment.getEndDateTime();

            if (hasConflictingAppointmentExcluding(doctorId, startTime, endTime, id)) {
                throw new BusinessException("Doctor has a conflicting appointment at this time");
            }
        }

        patientMapper.updateAppointment(request, appointment);

        if (request.status() == AppointmentStatus.CANCELLED && request.cancelledReason() != null) {
            appointment.setCancelledAt(LocalDateTime.now());
        }

        Appointment updatedAppointment = appointmentRepository.save(appointment);
        log.info("Updated appointment {}", id);

        return patientMapper.toDto(updatedAppointment);
    }

    @Transactional
    public AppointmentDto updateAppointmentStatus(UUID id, AppointmentStatus status) {
        Appointment appointment = findAppointmentById(id);
        appointment.setStatus(status);

        if (status == AppointmentStatus.CANCELLED) {
            appointment.setCancelledAt(LocalDateTime.now());
        }

        Appointment updatedAppointment = appointmentRepository.save(appointment);
        log.info("Updated appointment {} status to {}", id, status);

        return patientMapper.toDto(updatedAppointment);
    }

    @Transactional
    public void cancelAppointment(UUID id, String reason) {
        Appointment appointment = findAppointmentById(id);
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setCancelledReason(reason);
        appointment.setCancelledAt(LocalDateTime.now());

        appointmentRepository.save(appointment);
        log.info("Cancelled appointment {}", id);
    }

    public long getTodayAppointmentCount() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        return appointmentRepository.countAppointmentsInRange(startOfDay, endOfDay);
    }

    private Appointment findAppointmentById(UUID id) {
        return appointmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", id));
    }

    private boolean hasConflictingAppointment(UUID doctorId, LocalDateTime start, LocalDateTime end) {
        return appointmentRepository.existsByDoctorIdAndAppointmentDateTimeBetweenAndStatusNot(
            doctorId, start.minusMinutes(1), end.plusMinutes(1), AppointmentStatus.CANCELLED);
    }

    private boolean hasConflictingAppointmentExcluding(UUID doctorId, LocalDateTime start, LocalDateTime end, UUID excludeId) {
        List<Appointment> conflicts = appointmentRepository.findByDoctorIdAndDateRange(doctorId, start.minusMinutes(1), end.plusMinutes(1));
        return conflicts.stream()
            .anyMatch(a -> !a.getId().equals(excludeId) && a.getStatus() != AppointmentStatus.CANCELLED);
    }
}

