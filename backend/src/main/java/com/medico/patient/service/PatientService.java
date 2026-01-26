package com.medico.patient.service;

import com.medico.common.dto.PageResponse;
import com.medico.common.exception.BusinessException;
import com.medico.common.exception.ResourceNotFoundException;
import com.medico.patient.domain.Patient;
import com.medico.patient.dto.*;
import com.medico.patient.mapper.PatientMapper;
import com.medico.patient.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;
    private static final AtomicLong mrnCounter = new AtomicLong(System.currentTimeMillis() % 100000);

    public PageResponse<PatientDto> getAllPatients(Pageable pageable) {
        Page<Patient> patients = patientRepository.findAllActive(pageable);
        return PageResponse.from(patients, patients.getContent().stream()
            .map(patientMapper::toDto)
            .toList());
    }

    public PageResponse<PatientDto> searchPatients(String search, Pageable pageable) {
        Page<Patient> patients = patientRepository.searchPatients(search, pageable);
        return PageResponse.from(patients, patients.getContent().stream()
            .map(patientMapper::toDto)
            .toList());
    }

    public PatientDto getPatientById(UUID id) {
        Patient patient = findPatientById(id);
        return patientMapper.toDto(patient);
    }

    public PatientDto getPatientByMrn(String mrn) {
        Patient patient = patientRepository.findByMedicalRecordNumber(mrn)
            .orElseThrow(() -> new ResourceNotFoundException("Patient", "MRN", mrn));
        return patientMapper.toDto(patient);
    }

    @Transactional
    public PatientDto createPatient(CreatePatientRequest request) {
        if (request.email() != null && patientRepository.existsByEmail(request.email())) {
            throw new BusinessException("A patient with this email already exists");
        }

        Patient patient = patientMapper.toEntity(request);
        patient.setMedicalRecordNumber(generateMRN());

        Patient savedPatient = patientRepository.save(patient);
        log.info("Created new patient with MRN: {}", savedPatient.getMedicalRecordNumber());

        return patientMapper.toDto(savedPatient);
    }

    @Transactional
    public PatientDto updatePatient(UUID id, UpdatePatientRequest request) {
        Patient patient = findPatientById(id);

        if (request.email() != null && !request.email().equals(patient.getEmail())
            && patientRepository.existsByEmail(request.email())) {
            throw new BusinessException("A patient with this email already exists");
        }

        patientMapper.updatePatient(request, patient);
        Patient updatedPatient = patientRepository.save(patient);
        log.info("Updated patient with ID: {}", id);

        return patientMapper.toDto(updatedPatient);
    }

    @Transactional
    public void deactivatePatient(UUID id) {
        Patient patient = findPatientById(id);
        patient.setActive(false);
        patientRepository.save(patient);
        log.info("Deactivated patient with ID: {}", id);
    }

    @Transactional
    public void activatePatient(UUID id) {
        Patient patient = patientRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", id));
        patient.setActive(true);
        patientRepository.save(patient);
        log.info("Activated patient with ID: {}", id);
    }

    public long getActivePatientCount() {
        return patientRepository.countActivePatients();
    }

    private Patient findPatientById(UUID id) {
        return patientRepository.findById(id)
            .filter(Patient::isActive)
            .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", id));
    }

    private String generateMRN() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String sequencePart = String.format("%05d", mrnCounter.incrementAndGet() % 100000);
        String mrn = "MRN-" + datePart + "-" + sequencePart;

        while (patientRepository.existsByMedicalRecordNumber(mrn)) {
            sequencePart = String.format("%05d", mrnCounter.incrementAndGet() % 100000);
            mrn = "MRN-" + datePart + "-" + sequencePart;
        }

        return mrn;
    }
}

