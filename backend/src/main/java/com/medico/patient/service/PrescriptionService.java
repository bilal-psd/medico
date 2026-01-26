package com.medico.patient.service;

import com.medico.common.dto.PageResponse;
import com.medico.common.exception.BusinessException;
import com.medico.common.exception.ResourceNotFoundException;
import com.medico.patient.domain.*;
import com.medico.patient.domain.Prescription.PrescriptionStatus;
import com.medico.patient.dto.*;
import com.medico.patient.mapper.PatientMapper;
import com.medico.patient.repository.AppointmentRepository;
import com.medico.patient.repository.PatientRepository;
import com.medico.patient.repository.PrescriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PrescriptionService {

    private final PrescriptionRepository prescriptionRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientMapper patientMapper;
    private static final AtomicLong rxCounter = new AtomicLong(System.currentTimeMillis() % 100000);

    public PageResponse<PrescriptionDto> getPatientPrescriptions(UUID patientId, Pageable pageable) {
        Page<Prescription> prescriptions = prescriptionRepository.findByPatientIdOrderByPrescriptionDateDesc(patientId, pageable);
        return PageResponse.from(prescriptions, prescriptions.getContent().stream()
            .map(patientMapper::toDto)
            .toList());
    }

    public PageResponse<PrescriptionDto> getPrescriptionsByStatus(PrescriptionStatus status, Pageable pageable) {
        Page<Prescription> prescriptions = prescriptionRepository.findByStatusOrderByPrescriptionDateDesc(status, pageable);
        return PageResponse.from(prescriptions, prescriptions.getContent().stream()
            .map(patientMapper::toDto)
            .toList());
    }

    public PrescriptionDto getPrescriptionById(UUID id) {
        Prescription prescription = findPrescriptionById(id);
        return patientMapper.toDto(prescription);
    }

    public PrescriptionDto getPrescriptionByNumber(String prescriptionNumber) {
        Prescription prescription = prescriptionRepository.findByPrescriptionNumber(prescriptionNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Prescription", "number", prescriptionNumber));
        return patientMapper.toDto(prescription);
    }

    @Transactional
    public PrescriptionDto createPrescription(CreatePrescriptionRequest request) {
        Patient patient = patientRepository.findById(request.patientId())
            .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", request.patientId()));

        Prescription prescription = Prescription.builder()
            .prescriptionNumber(generatePrescriptionNumber())
            .patient(patient)
            .doctorId(request.doctorId())
            .doctorName(request.doctorName())
            .prescriptionDate(LocalDateTime.now())
            .validUntil(request.validUntil() != null ? request.validUntil() : LocalDate.now().plusMonths(1))
            .status(PrescriptionStatus.ACTIVE)
            .diagnosis(request.diagnosis())
            .notes(request.notes())
            .build();

        if (request.appointmentId() != null) {
            Appointment appointment = appointmentRepository.findById(request.appointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", request.appointmentId()));
            prescription.setAppointment(appointment);
        }

        for (CreatePrescriptionItemRequest itemRequest : request.items()) {
            PrescriptionItem item = patientMapper.toEntity(itemRequest);
            prescription.addItem(item);
        }

        Prescription savedPrescription = prescriptionRepository.save(prescription);
        log.info("Created prescription {} for patient {}", savedPrescription.getPrescriptionNumber(), patient.getMedicalRecordNumber());

        return patientMapper.toDto(savedPrescription);
    }

    @Transactional
    public PrescriptionDto updatePrescriptionStatus(UUID id, PrescriptionStatus status) {
        Prescription prescription = findPrescriptionById(id);

        if (prescription.getStatus() == PrescriptionStatus.CANCELLED) {
            throw new BusinessException("Cannot update a cancelled prescription");
        }

        prescription.setStatus(status);
        Prescription updatedPrescription = prescriptionRepository.save(prescription);
        log.info("Updated prescription {} status to {}", prescription.getPrescriptionNumber(), status);

        return patientMapper.toDto(updatedPrescription);
    }

    @Transactional
    public void cancelPrescription(UUID id) {
        Prescription prescription = findPrescriptionById(id);

        if (prescription.getStatus() == PrescriptionStatus.DISPENSED) {
            throw new BusinessException("Cannot cancel a fully dispensed prescription");
        }

        prescription.setStatus(PrescriptionStatus.CANCELLED);
        prescriptionRepository.save(prescription);
        log.info("Cancelled prescription {}", prescription.getPrescriptionNumber());
    }

    public List<PrescriptionDto> getActivePrescriptionsForPatient(UUID patientId) {
        List<Prescription> prescriptions = prescriptionRepository.findByPatientIdAndStatus(patientId, PrescriptionStatus.ACTIVE);
        return patientMapper.toPrescriptionDtoList(prescriptions);
    }

    public long getActivePrescriptionCount() {
        return prescriptionRepository.countByStatus(PrescriptionStatus.ACTIVE);
    }

    private Prescription findPrescriptionById(UUID id) {
        return prescriptionRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Prescription", "id", id));
    }

    private String generatePrescriptionNumber() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String sequencePart = String.format("%05d", rxCounter.incrementAndGet() % 100000);
        String rxNumber = "RX-" + datePart + "-" + sequencePart;

        while (prescriptionRepository.existsByPrescriptionNumber(rxNumber)) {
            sequencePart = String.format("%05d", rxCounter.incrementAndGet() % 100000);
            rxNumber = "RX-" + datePart + "-" + sequencePart;
        }

        return rxNumber;
    }
}

