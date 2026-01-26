package com.medico.patient.dto;

import com.medico.patient.domain.Patient.BloodType;
import com.medico.patient.domain.Patient.Gender;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record PatientDto(
    UUID id,
    String medicalRecordNumber,
    String firstName,
    String lastName,
    LocalDate dateOfBirth,
    Gender gender,
    String phoneNumber,
    String email,
    String address,
    String city,
    String state,
    String postalCode,
    String country,
    String emergencyContactName,
    String emergencyContactPhone,
    BloodType bloodType,
    String allergies,
    String medicalNotes,
    boolean active,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public String fullName() {
        return firstName + " " + lastName;
    }
}

