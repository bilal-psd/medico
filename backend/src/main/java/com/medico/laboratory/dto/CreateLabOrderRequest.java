package com.medico.laboratory.dto;

import com.medico.laboratory.domain.LabOrder.OrderPriority;
import jakarta.validation.constraints.*;

import java.util.List;
import java.util.UUID;

public record CreateLabOrderRequest(
    @NotNull(message = "Patient ID is required")
    UUID patientId,

    @NotNull(message = "Ordering doctor ID is required")
    UUID orderingDoctorId,

    @NotBlank(message = "Ordering doctor name is required")
    String orderingDoctorName,

    @NotNull(message = "Priority is required")
    OrderPriority priority,

    String clinicalNotes,
    String diagnosis,

    @NotEmpty(message = "At least one test is required")
    List<CreateLabOrderItemRequest> items
) {}

