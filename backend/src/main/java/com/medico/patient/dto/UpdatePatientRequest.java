package com.medico.patient.dto;

import com.medico.patient.domain.Patient.BloodType;
import com.medico.patient.domain.Patient.Gender;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UpdatePatientRequest(
    @Size(max = 100, message = "First name must be less than 100 characters")
    String firstName,

    @Size(max = 100, message = "Last name must be less than 100 characters")
    String lastName,

    @Past(message = "Date of birth must be in the past")
    LocalDate dateOfBirth,

    Gender gender,

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    String phoneNumber,

    @Email(message = "Invalid email format")
    String email,

    @Size(max = 500, message = "Address must be less than 500 characters")
    String address,

    @Size(max = 100, message = "City must be less than 100 characters")
    String city,

    @Size(max = 100, message = "State must be less than 100 characters")
    String state,

    @Size(max = 20, message = "Postal code must be less than 20 characters")
    String postalCode,

    @Size(max = 100, message = "Country must be less than 100 characters")
    String country,

    @Size(max = 200, message = "Emergency contact name must be less than 200 characters")
    String emergencyContactName,

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid emergency contact phone format")
    String emergencyContactPhone,

    BloodType bloodType,

    String allergies,

    String medicalNotes,

    Boolean active
) {}

