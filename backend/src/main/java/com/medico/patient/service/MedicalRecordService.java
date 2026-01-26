package com.medico.patient.service;

import com.medico.common.dto.PageResponse;
import com.medico.common.exception.ResourceNotFoundException;
import com.medico.patient.domain.Appointment;
import com.medico.patient.domain.MedicalRecord;
import com.medico.patient.domain.Patient;
import com.medico.patient.dto.*;
import com.medico.patient.mapper.PatientMapper;
import com.medico.patient.repository.AppointmentRepository;
import com.medico.patient.repository.MedicalRecordRepository;
import com.medico.patient.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final PatientMapper patientMapper;

    public PageResponse<MedicalRecordDto> getPatientRecords(UUID patientId, Pageable pageable) {
        Page<MedicalRecord> records = medicalRecordRepository.findByPatientIdOrderByVisitDateDesc(patientId, pageable);
        return PageResponse.from(records, records.getContent().stream()
            .map(patientMapper::toDto)
            .toList());
    }

    public PageResponse<MedicalRecordDto> searchPatientRecords(UUID patientId, String search, Pageable pageable) {
        Page<MedicalRecord> records = medicalRecordRepository.searchPatientRecords(patientId, search, pageable);
        return PageResponse.from(records, records.getContent().stream()
            .map(patientMapper::toDto)
            .toList());
    }

    public MedicalRecordDto getRecordById(UUID id) {
        MedicalRecord record = findRecordById(id);
        return patientMapper.toDto(record);
    }

    @Transactional
    public MedicalRecordDto createRecord(CreateMedicalRecordRequest request) {
        Patient patient = patientRepository.findById(request.patientId())
            .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", request.patientId()));

        MedicalRecord record = patientMapper.toEntity(request);
        record.setPatient(patient);

        if (request.appointmentId() != null) {
            Appointment appointment = appointmentRepository.findById(request.appointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", "id", request.appointmentId()));
            record.setAppointment(appointment);
        }

        MedicalRecord savedRecord = medicalRecordRepository.save(record);
        log.info("Created medical record {} for patient {}", savedRecord.getId(), patient.getMedicalRecordNumber());

        return patientMapper.toDto(savedRecord);
    }

    @Transactional
    public MedicalRecordDto updateRecord(UUID id, CreateMedicalRecordRequest request) {
        MedicalRecord record = findRecordById(id);

        record.setDoctorId(request.doctorId());
        record.setDoctorName(request.doctorName());
        record.setVisitDate(request.visitDate());
        record.setRecordType(request.recordType());
        record.setChiefComplaint(request.chiefComplaint());
        record.setSymptoms(request.symptoms());
        record.setDiagnosis(request.diagnosis());
        record.setTreatmentPlan(request.treatmentPlan());
        record.setVitalSigns(request.vitalSigns());
        record.setPhysicalExamination(request.physicalExamination());
        record.setNotes(request.notes());
        record.setFollowUpDate(request.followUpDate());

        MedicalRecord updatedRecord = medicalRecordRepository.save(record);
        log.info("Updated medical record {}", id);

        return patientMapper.toDto(updatedRecord);
    }

    private MedicalRecord findRecordById(UUID id) {
        return medicalRecordRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("MedicalRecord", "id", id));
    }
}

