package com.medico.pharmacy.mapper;

import com.medico.pharmacy.domain.*;
import com.medico.pharmacy.dto.*;
import org.mapstruct.*;

import java.time.LocalDate;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PharmacyMapper {

    MedicationDto toDto(Medication medication);

    List<MedicationDto> toMedicationDtoList(List<Medication> medications);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    Medication toEntity(CreateMedicationRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    void updateMedication(CreateMedicationRequest request, @MappingTarget Medication medication);

    @Mapping(target = "medicationId", source = "medication.id")
    @Mapping(target = "medicationName", source = "medication.name")
    @Mapping(target = "medicationCode", source = "medication.code")
    @Mapping(target = "supplierId", source = "supplier.id")
    @Mapping(target = "supplierName", source = "supplier.name")
    @Mapping(target = "availableQuantity", expression = "java(inventory.getAvailableQuantity())")
    @Mapping(target = "expired", expression = "java(inventory.isExpired())")
    @Mapping(target = "expiringSoon", expression = "java(inventory.isExpiringSoon(30))")
    InventoryDto toDto(Inventory inventory);

    List<InventoryDto> toInventoryDtoList(List<Inventory> inventoryList);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "medication", ignore = true)
    @Mapping(target = "supplier", ignore = true)
    @Mapping(target = "reservedQuantity", constant = "0")
    @Mapping(target = "status", constant = "AVAILABLE")
    Inventory toEntity(CreateInventoryRequest request);

    SupplierDto toDto(Supplier supplier);

    List<SupplierDto> toSupplierDtoList(List<Supplier> suppliers);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    Supplier toEntity(CreateSupplierRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "code", ignore = true)
    void updateSupplier(CreateSupplierRequest request, @MappingTarget Supplier supplier);

    @Mapping(target = "prescriptionId", source = "prescription.id")
    @Mapping(target = "prescriptionNumber", source = "prescription.prescriptionNumber")
    @Mapping(target = "prescriptionItemId", source = "prescriptionItem.id")
    @Mapping(target = "medicationName", source = "prescriptionItem.medicationName")
    @Mapping(target = "inventoryId", source = "inventory.id")
    @Mapping(target = "batchNumber", source = "inventory.batchNumber")
    DispensingDto toDto(PrescriptionDispensing dispensing);

    List<DispensingDto> toDispensingDtoList(List<PrescriptionDispensing> dispensings);

    default InventoryAlertDto toAlertDto(Inventory inventory, InventoryAlertDto.AlertType alertType) {
        String message = switch (alertType) {
            case LOW_STOCK -> "Stock level is below reorder level";
            case OUT_OF_STOCK -> "Item is out of stock";
            case EXPIRING_SOON -> "Item is expiring within 30 days";
            case EXPIRED -> "Item has expired";
        };

        return new InventoryAlertDto(
            inventory.getId(),
            inventory.getMedication().getId(),
            inventory.getMedication().getName(),
            inventory.getMedication().getCode(),
            inventory.getBatchNumber(),
            alertType,
            message,
            inventory.getAvailableQuantity(),
            inventory.getMedication().getReorderLevel(),
            inventory.getExpiryDate()
        );
    }
}

