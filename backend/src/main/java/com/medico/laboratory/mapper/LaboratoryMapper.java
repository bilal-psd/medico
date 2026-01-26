package com.medico.laboratory.mapper;

import com.medico.laboratory.domain.*;
import com.medico.laboratory.dto.*;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LaboratoryMapper {

    LabTestDto toDto(LabTest labTest);

    List<LabTestDto> toLabTestDtoList(List<LabTest> labTests);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    LabTest toEntity(CreateLabTestRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    void updateLabTest(CreateLabTestRequest request, @MappingTarget LabTest labTest);

    @Mapping(target = "patientId", source = "patient.id")
    @Mapping(target = "patientName", expression = "java(labOrder.getPatient().getFullName())")
    @Mapping(target = "patientMrn", source = "patient.medicalRecordNumber")
    LabOrderDto toDto(LabOrder labOrder);

    List<LabOrderDto> toLabOrderDtoList(List<LabOrder> labOrders);

    @Mapping(target = "labTestId", source = "labTest.id")
    @Mapping(target = "labTestCode", source = "labTest.code")
    @Mapping(target = "labTestName", source = "labTest.name")
    LabOrderItemDto toDto(LabOrderItem labOrderItem);

    List<LabOrderItemDto> toLabOrderItemDtoList(List<LabOrderItem> items);

    @Mapping(target = "testName", source = "labOrderItem.labTest.name")
    @Mapping(target = "testCode", source = "labOrderItem.labTest.code")
    @Mapping(target = "labOrderItemId", source = "labOrderItem.id")
    LabResultDto toDto(LabResult labResult);

    List<LabResultDto> toLabResultDtoList(List<LabResult> labResults);
}

