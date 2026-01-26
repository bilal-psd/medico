package com.medico.pharmacy.service;

import com.medico.common.dto.PageResponse;
import com.medico.common.exception.BusinessException;
import com.medico.common.exception.ResourceNotFoundException;
import com.medico.patient.domain.Prescription;
import com.medico.patient.domain.Prescription.PrescriptionStatus;
import com.medico.patient.domain.PrescriptionItem;
import com.medico.patient.repository.PrescriptionRepository;
import com.medico.pharmacy.domain.Inventory;
import com.medico.pharmacy.domain.PrescriptionDispensing;
import com.medico.pharmacy.domain.PrescriptionDispensing.DispensingStatus;
import com.medico.pharmacy.dto.*;
import com.medico.pharmacy.mapper.PharmacyMapper;
import com.medico.pharmacy.repository.InventoryRepository;
import com.medico.pharmacy.repository.PrescriptionDispensingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DispensingService {

    private final PrescriptionDispensingRepository dispensingRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryService inventoryService;
    private final PharmacyMapper pharmacyMapper;

    public List<DispensingDto> getDispensingsByPrescription(UUID prescriptionId) {
        List<PrescriptionDispensing> dispensings = dispensingRepository.findByPrescriptionId(prescriptionId);
        return pharmacyMapper.toDispensingDtoList(dispensings);
    }

    public PageResponse<DispensingDto> getDispensingsByPharmacist(UUID pharmacistId, Pageable pageable) {
        Page<PrescriptionDispensing> dispensings = dispensingRepository.findByPharmacist(pharmacistId, pageable);
        return PageResponse.from(dispensings, dispensings.getContent().stream()
            .map(pharmacyMapper::toDto)
            .toList());
    }

    public DispensingDto getDispensingById(UUID id) {
        PrescriptionDispensing dispensing = findDispensingById(id);
        return pharmacyMapper.toDto(dispensing);
    }

    @Transactional
    public DispensingDto dispensePrescriptionItem(DispenseRequest request) {
        Prescription prescription = prescriptionRepository.findById(request.prescriptionId())
            .orElseThrow(() -> new ResourceNotFoundException("Prescription", "id", request.prescriptionId()));

        if (prescription.getStatus() == PrescriptionStatus.CANCELLED) {
            throw new BusinessException("Cannot dispense a cancelled prescription");
        }

        if (prescription.getStatus() == PrescriptionStatus.EXPIRED ||
            (prescription.getValidUntil() != null && prescription.getValidUntil().isBefore(LocalDate.now()))) {
            throw new BusinessException("Prescription has expired");
        }

        PrescriptionItem prescriptionItem = prescription.getItems().stream()
            .filter(item -> item.getId().equals(request.prescriptionItemId()))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("PrescriptionItem", "id", request.prescriptionItemId()));

        Inventory inventory = inventoryRepository.findById(request.inventoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Inventory", "id", request.inventoryId()));

        if (inventory.getAvailableQuantity() < request.quantity()) {
            throw new BusinessException("Insufficient inventory quantity");
        }

        int alreadyDispensed = prescriptionItem.getDispensedQuantity() != null ? prescriptionItem.getDispensedQuantity() : 0;
        int remainingToDispense = prescriptionItem.getQuantity() - alreadyDispensed;

        if (request.quantity() > remainingToDispense) {
            throw new BusinessException("Quantity exceeds remaining prescription quantity");
        }

        // Create dispensing record
        PrescriptionDispensing dispensing = PrescriptionDispensing.builder()
            .prescription(prescription)
            .prescriptionItem(prescriptionItem)
            .inventory(inventory)
            .dispensedQuantity(request.quantity())
            .dispensedAt(LocalDateTime.now())
            .dispensedBy(request.pharmacistId())
            .pharmacistName(request.pharmacistName())
            .status(DispensingStatus.DISPENSED)
            .notes(request.notes())
            .build();

        // Update inventory
        inventoryService.updateInventoryQuantity(inventory.getId(), -request.quantity());

        // Update prescription item
        prescriptionItem.setDispensedQuantity(alreadyDispensed + request.quantity());

        // Update prescription status
        updatePrescriptionStatus(prescription);

        PrescriptionDispensing savedDispensing = dispensingRepository.save(dispensing);
        prescriptionRepository.save(prescription);

        log.info("Dispensed {} units of {} for prescription {}",
            request.quantity(), prescriptionItem.getMedicationName(), prescription.getPrescriptionNumber());

        return pharmacyMapper.toDto(savedDispensing);
    }

    public long getTodayDispensingCount() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        return dispensingRepository.countDispensingsInRange(startOfDay, endOfDay);
    }

    private PrescriptionDispensing findDispensingById(UUID id) {
        return dispensingRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("PrescriptionDispensing", "id", id));
    }

    private void updatePrescriptionStatus(Prescription prescription) {
        boolean allDispensed = prescription.getItems().stream()
            .allMatch(item -> {
                int dispensed = item.getDispensedQuantity() != null ? item.getDispensedQuantity() : 0;
                return dispensed >= item.getQuantity();
            });

        boolean anyDispensed = prescription.getItems().stream()
            .anyMatch(item -> {
                int dispensed = item.getDispensedQuantity() != null ? item.getDispensedQuantity() : 0;
                return dispensed > 0;
            });

        if (allDispensed) {
            prescription.setStatus(PrescriptionStatus.DISPENSED);
        } else if (anyDispensed) {
            prescription.setStatus(PrescriptionStatus.PARTIALLY_DISPENSED);
        }
    }
}

