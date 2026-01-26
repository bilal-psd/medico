package com.medico.laboratory.dto;

import com.medico.laboratory.domain.LabOrder.OrderPriority;
import com.medico.laboratory.domain.LabOrder.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record LabOrderDto(
    UUID id,
    String orderNumber,
    UUID patientId,
    String patientName,
    String patientMrn,
    UUID orderingDoctorId,
    String orderingDoctorName,
    LocalDateTime orderDate,
    OrderPriority priority,
    OrderStatus status,
    String clinicalNotes,
    String diagnosis,
    LocalDateTime sampleCollectedAt,
    UUID sampleCollectedBy,
    List<LabOrderItemDto> items,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}

