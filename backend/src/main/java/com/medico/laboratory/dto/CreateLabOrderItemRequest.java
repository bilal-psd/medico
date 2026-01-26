package com.medico.laboratory.dto;

import jakarta.validation.constraints.*;

import java.util.UUID;

public record CreateLabOrderItemRequest(
    @NotNull(message = "Lab test ID is required")
    UUID labTestId,

    String notes
) {}

