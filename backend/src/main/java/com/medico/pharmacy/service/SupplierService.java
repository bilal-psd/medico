package com.medico.pharmacy.service;

import com.medico.common.dto.PageResponse;
import com.medico.common.exception.BusinessException;
import com.medico.common.exception.ResourceNotFoundException;
import com.medico.pharmacy.domain.Supplier;
import com.medico.pharmacy.dto.*;
import com.medico.pharmacy.mapper.PharmacyMapper;
import com.medico.pharmacy.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SupplierService {

    private final SupplierRepository supplierRepository;
    private final PharmacyMapper pharmacyMapper;

    public PageResponse<SupplierDto> getAllSuppliers(Pageable pageable) {
        Page<Supplier> suppliers = supplierRepository.findByActiveTrue(pageable);
        return PageResponse.from(suppliers, suppliers.getContent().stream()
            .map(pharmacyMapper::toDto)
            .toList());
    }

    public PageResponse<SupplierDto> searchSuppliers(String search, Pageable pageable) {
        Page<Supplier> suppliers = supplierRepository.searchSuppliers(search, pageable);
        return PageResponse.from(suppliers, suppliers.getContent().stream()
            .map(pharmacyMapper::toDto)
            .toList());
    }

    public SupplierDto getSupplierById(UUID id) {
        Supplier supplier = findSupplierById(id);
        return pharmacyMapper.toDto(supplier);
    }

    public SupplierDto getSupplierByCode(String code) {
        Supplier supplier = supplierRepository.findByCode(code)
            .orElseThrow(() -> new ResourceNotFoundException("Supplier", "code", code));
        return pharmacyMapper.toDto(supplier);
    }

    @Transactional
    public SupplierDto createSupplier(CreateSupplierRequest request) {
        if (supplierRepository.existsByCode(request.code())) {
            throw new BusinessException("A supplier with this code already exists");
        }

        Supplier supplier = pharmacyMapper.toEntity(request);
        Supplier savedSupplier = supplierRepository.save(supplier);
        log.info("Created supplier: {}", savedSupplier.getCode());

        return pharmacyMapper.toDto(savedSupplier);
    }

    @Transactional
    public SupplierDto updateSupplier(UUID id, CreateSupplierRequest request) {
        Supplier supplier = findSupplierById(id);
        pharmacyMapper.updateSupplier(request, supplier);
        Supplier updatedSupplier = supplierRepository.save(supplier);
        log.info("Updated supplier: {}", updatedSupplier.getCode());

        return pharmacyMapper.toDto(updatedSupplier);
    }

    @Transactional
    public void deactivateSupplier(UUID id) {
        Supplier supplier = findSupplierById(id);
        supplier.setActive(false);
        supplierRepository.save(supplier);
        log.info("Deactivated supplier: {}", supplier.getCode());
    }

    public long getActiveSupplierCount() {
        return supplierRepository.countActiveSuppliers();
    }

    private Supplier findSupplierById(UUID id) {
        return supplierRepository.findById(id)
            .filter(Supplier::isActive)
            .orElseThrow(() -> new ResourceNotFoundException("Supplier", "id", id));
    }
}

