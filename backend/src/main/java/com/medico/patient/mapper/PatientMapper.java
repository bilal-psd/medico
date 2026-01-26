package com.medico.patient.mapper;

import com.medico.patient.domain.*;
import com.medico.patient.dto.*;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PatientMapper {

    PatientDto toDto(Patient patient);

    List<PatientDto> toDtoList(List<Patient> patients);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "medicalRecordNumber", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "appointments", ignore = true)
    @Mapping(target = "medicalRecords", ignore = true)
    @Mapping(target = "prescriptions", ignore = true)
    Patient toEntity(CreatePatientRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "medicalRecordNumber", ignore = true)
    @Mapping(target = "appointments", ignore = true)
    @Mapping(target = "medicalRecords", ignore = true)
    @Mapping(target = "prescriptions", ignore = true)
    void updatePatient(UpdatePatientRequest request, @MappingTarget Patient patient);

    @Mapping(target = "patientName", expression = "java(appointment.getPatient().getFullName())")
    @Mapping(target = "patientMrn", source = "patient.medicalRecordNumber")
    @Mapping(target = "patientId", source = "patient.id")
    AppointmentDto toDto(Appointment appointment);

    List<AppointmentDto> toAppointmentDtoList(List<Appointment> appointments);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "status", constant = "SCHEDULED")
    Appointment toEntity(CreateAppointmentRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "patient", ignore = true)
    void updateAppointment(UpdateAppointmentRequest request, @MappingTarget Appointment appointment);

    @Mapping(target = "patientName", expression = "java(record.getPatient().getFullName())")
    @Mapping(target = "patientId", source = "patient.id")
    @Mapping(target = "appointmentId", source = "appointment.id")
    MedicalRecordDto toDto(MedicalRecord record);

    List<MedicalRecordDto> toMedicalRecordDtoList(List<MedicalRecord> records);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "patient", ignore = true)
    @Mapping(target = "appointment", ignore = true)
    MedicalRecord toEntity(CreateMedicalRecordRequest request);

    @Mapping(target = "patientName", expression = "java(prescription.getPatient().getFullName())")
    @Mapping(target = "patientId", source = "patient.id")
    @Mapping(target = "appointmentId", source = "appointment.id")
    PrescriptionDto toDto(Prescription prescription);

    List<PrescriptionDto> toPrescriptionDtoList(List<Prescription> prescriptions);

    PrescriptionItemDto toDto(PrescriptionItem item);

    List<PrescriptionItemDto> toPrescriptionItemDtoList(List<PrescriptionItem> items);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "prescription", ignore = true)
    @Mapping(target = "dispensedQuantity", constant = "0")
    @Mapping(target = "refillsRemaining", source = "refillsAllowed")
    PrescriptionItem toEntity(CreatePrescriptionItemRequest request);
}

