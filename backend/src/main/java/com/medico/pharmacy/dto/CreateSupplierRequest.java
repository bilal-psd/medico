package com.medico.pharmacy.dto;

import jakarta.validation.constraints.*;

public record CreateSupplierRequest(
    @NotBlank(message = "Supplier code is required")
    @Size(max = 50, message = "Code must be less than 50 characters")
    String code,

    @NotBlank(message = "Supplier name is required")
    @Size(max = 200, message = "Name must be less than 200 characters")
    String name,

    @Size(max = 200, message = "Contact person must be less than 200 characters")
    String contactPerson,

    @Email(message = "Invalid email format")
    String email,

    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "Invalid phone number format")
    String phone,

    String address,

    @Size(max = 100, message = "City must be less than 100 characters")
    String city,

    @Size(max = 100, message = "State must be less than 100 characters")
    String state,

    @Size(max = 20, message = "Postal code must be less than 20 characters")
    String postalCode,

    @Size(max = 100, message = "Country must be less than 100 characters")
    String country,

    @Size(max = 50, message = "Tax ID must be less than 50 characters")
    String taxId,

    @Size(max = 200, message = "Payment terms must be less than 200 characters")
    String paymentTerms,

    @Min(value = 0, message = "Lead time cannot be negative")
    Integer leadTimeDays,

    String notes
) {}

