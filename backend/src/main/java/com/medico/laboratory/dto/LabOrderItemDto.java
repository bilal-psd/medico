package com.medico.laboratory.dto;

import com.medico.laboratory.domain.LabOrderItem.ItemStatus;

import java.util.UUID;

public record LabOrderItemDto(
    UUID id,
    UUID labTestId,
    String labTestCode,
    String labTestName,
    ItemStatus status,
    String notes,
    LabResultDto result
) {}

