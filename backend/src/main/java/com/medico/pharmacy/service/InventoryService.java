package com.medico.pharmacy.service;

import com.medico.common.dto.PageResponse;
import com.medico.common.exception.BusinessException;
import com.medico.common.exception.ResourceNotFoundException;
import com.medico.pharmacy.domain.Inventory;
import com.medico.pharmacy.domain.Inventory.InventoryStatus;
import com.medico.pharmacy.domain.Medication;
import com.medico.pharmacy.domain.Supplier;
import com.medico.pharmacy.dto.*;
import com.medico.pharmacy.mapper.PharmacyMapper;
import com.medico.pharmacy.repository.InventoryRepository;
import com.medico.pharmacy.repository.MedicationRepository;
import com.medico.pharmacy.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final MedicationRepository medicationRepository;
    private final SupplierRepository supplierRepository;
    private final PharmacyMapper pharmacyMapper;

    private static final int EXPIRY_WARNING_DAYS = 30;

    public PageResponse<InventoryDto> getAllInventory(Pageable pageable) {
        Page<Inventory> inventory = inventoryRepository.findAll(pageable);
        return PageResponse.from(inventory, inventory.getContent().stream()
            .map(pharmacyMapper::toDto)
            .toList());
    }

    public PageResponse<InventoryDto> getInventoryByMedication(UUID medicationId, Pageable pageable) {
        Page<Inventory> inventory = inventoryRepository.findByMedicationId(medicationId, pageable);
        return PageResponse.from(inventory, inventory.getContent().stream()
            .map(pharmacyMapper::toDto)
            .toList());
    }

    public PageResponse<InventoryDto> getInventoryByStatus(InventoryStatus status, Pageable pageable) {
        Page<Inventory> inventory = inventoryRepository.findByStatus(status, pageable);
        return PageResponse.from(inventory, inventory.getContent().stream()
            .map(pharmacyMapper::toDto)
            .toList());
    }

    public InventoryDto getInventoryById(UUID id) {
        Inventory inventory = findInventoryById(id);
        return pharmacyMapper.toDto(inventory);
    }

    public Integer getAvailableQuantity(UUID medicationId) {
        Integer quantity = inventoryRepository.getTotalAvailableQuantity(medicationId, LocalDate.now());
        return quantity != null ? quantity : 0;
    }

    @Transactional
    public InventoryDto addInventory(CreateInventoryRequest request) {
        Medication medication = medicationRepository.findById(request.medicationId())
            .orElseThrow(() -> new ResourceNotFoundException("Medication", "id", request.medicationId()));

        Inventory inventory = pharmacyMapper.toEntity(request);
        inventory.setMedication(medication);

        if (request.supplierId() != null) {
            Supplier supplier = supplierRepository.findById(request.supplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", request.supplierId()));
            inventory.setSupplier(supplier);
        }

        updateInventoryStatus(inventory);

        Inventory savedInventory = inventoryRepository.save(inventory);
        log.info("Added inventory for medication: {}, batch: {}", medication.getCode(), request.batchNumber());

        return pharmacyMapper.toDto(savedInventory);
    }

    @Transactional
    public InventoryDto updateInventoryQuantity(UUID id, Integer quantityChange) {
        Inventory inventory = findInventoryById(id);

        int newQuantity = inventory.getQuantity() + quantityChange;
        if (newQuantity < 0) {
            throw new BusinessException("Cannot reduce quantity below zero");
        }

        inventory.setQuantity(newQuantity);
        updateInventoryStatus(inventory);

        Inventory updatedInventory = inventoryRepository.save(inventory);
        log.info("Updated inventory {} quantity by {}", id, quantityChange);

        return pharmacyMapper.toDto(updatedInventory);
    }

    @Transactional
    public void reserveInventory(UUID id, Integer quantity) {
        Inventory inventory = findInventoryById(id);

        if (inventory.getAvailableQuantity() < quantity) {
            throw new BusinessException("Insufficient available quantity");
        }

        int currentReserved = inventory.getReservedQuantity() != null ? inventory.getReservedQuantity() : 0;
        inventory.setReservedQuantity(currentReserved + quantity);
        updateInventoryStatus(inventory);

        inventoryRepository.save(inventory);
        log.info("Reserved {} units of inventory {}", quantity, id);
    }

    @Transactional
    public void releaseReservation(UUID id, Integer quantity) {
        Inventory inventory = findInventoryById(id);

        int currentReserved = inventory.getReservedQuantity() != null ? inventory.getReservedQuantity() : 0;
        int newReserved = Math.max(0, currentReserved - quantity);
        inventory.setReservedQuantity(newReserved);
        updateInventoryStatus(inventory);

        inventoryRepository.save(inventory);
        log.info("Released {} units reservation from inventory {}", quantity, id);
    }

    public List<InventoryAlertDto> getInventoryAlerts() {
        List<InventoryAlertDto> alerts = new ArrayList<>();

        // Low stock alerts
        List<Inventory> lowStock = inventoryRepository.findLowStockItems();
        for (Inventory inv : lowStock) {
            if (inv.getAvailableQuantity() == 0) {
                alerts.add(pharmacyMapper.toAlertDto(inv, InventoryAlertDto.AlertType.OUT_OF_STOCK));
            } else {
                alerts.add(pharmacyMapper.toAlertDto(inv, InventoryAlertDto.AlertType.LOW_STOCK));
            }
        }

        // Expiring soon alerts
        LocalDate expiryThreshold = LocalDate.now().plusDays(EXPIRY_WARNING_DAYS);
        List<Inventory> expiring = inventoryRepository.findExpiredOrExpiringSoon(expiryThreshold);
        for (Inventory inv : expiring) {
            if (inv.isExpired()) {
                alerts.add(pharmacyMapper.toAlertDto(inv, InventoryAlertDto.AlertType.EXPIRED));
            } else {
                alerts.add(pharmacyMapper.toAlertDto(inv, InventoryAlertDto.AlertType.EXPIRING_SOON));
            }
        }

        return alerts;
    }

    public long getLowStockCount() {
        return inventoryRepository.countLowStockMedications();
    }

    public long getExpiringItemsCount() {
        return inventoryRepository.countExpiringItems(LocalDate.now().plusDays(EXPIRY_WARNING_DAYS));
    }

    private Inventory findInventoryById(UUID id) {
        return inventoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Inventory", "id", id));
    }

    private void updateInventoryStatus(Inventory inventory) {
        if (inventory.isExpired()) {
            inventory.setStatus(InventoryStatus.EXPIRED);
        } else if (inventory.getAvailableQuantity() == 0) {
            inventory.setStatus(InventoryStatus.OUT_OF_STOCK);
        } else if (inventory.getMedication().getReorderLevel() != null &&
                   inventory.getAvailableQuantity() <= inventory.getMedication().getReorderLevel()) {
            inventory.setStatus(InventoryStatus.LOW_STOCK);
        } else {
            inventory.setStatus(InventoryStatus.AVAILABLE);
        }
    }
}

