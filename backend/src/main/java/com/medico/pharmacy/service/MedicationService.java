package com.medico.pharmacy.service;

import com.medico.common.dto.PageResponse;
import com.medico.common.exception.BusinessException;
import com.medico.common.exception.ResourceNotFoundException;
import com.medico.pharmacy.domain.Medication;
import com.medico.pharmacy.domain.Medication.MedicationCategory;
import com.medico.pharmacy.dto.*;
import com.medico.pharmacy.mapper.PharmacyMapper;
import com.medico.pharmacy.repository.MedicationRepository;
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
public class MedicationService {

    private final MedicationRepository medicationRepository;
    private final PharmacyMapper pharmacyMapper;

    public PageResponse<MedicationDto> getAllMedications(Pageable pageable) {
        Page<Medication> medications = medicationRepository.findByActiveTrue(pageable);
        return PageResponse.from(medications, medications.getContent().stream()
            .map(pharmacyMapper::toDto)
            .toList());
    }

    public PageResponse<MedicationDto> getMedicationsByCategory(MedicationCategory category, Pageable pageable) {
        Page<Medication> medications = medicationRepository.findByCategoryAndActiveTrue(category, pageable);
        return PageResponse.from(medications, medications.getContent().stream()
            .map(pharmacyMapper::toDto)
            .toList());
    }

    public PageResponse<MedicationDto> searchMedications(String search, Pageable pageable) {
        Page<Medication> medications = medicationRepository.searchMedications(search, pageable);
        return PageResponse.from(medications, medications.getContent().stream()
            .map(pharmacyMapper::toDto)
            .toList());
    }

    public MedicationDto getMedicationById(UUID id) {
        Medication medication = findMedicationById(id);
        return pharmacyMapper.toDto(medication);
    }

    public MedicationDto getMedicationByCode(String code) {
        Medication medication = medicationRepository.findByCode(code)
            .orElseThrow(() -> new ResourceNotFoundException("Medication", "code", code));
        return pharmacyMapper.toDto(medication);
    }

    @Transactional
    public MedicationDto createMedication(CreateMedicationRequest request) {
        if (medicationRepository.existsByCode(request.code())) {
            throw new BusinessException("A medication with this code already exists");
        }

        Medication medication = pharmacyMapper.toEntity(request);
        Medication savedMedication = medicationRepository.save(medication);
        log.info("Created medication: {}", savedMedication.getCode());

        return pharmacyMapper.toDto(savedMedication);
    }

    @Transactional
    public MedicationDto updateMedication(UUID id, CreateMedicationRequest request) {
        Medication medication = findMedicationById(id);
        pharmacyMapper.updateMedication(request, medication);
        Medication updatedMedication = medicationRepository.save(medication);
        log.info("Updated medication: {}", updatedMedication.getCode());

        return pharmacyMapper.toDto(updatedMedication);
    }

    @Transactional
    public void deactivateMedication(UUID id) {
        Medication medication = findMedicationById(id);
        medication.setActive(false);
        medicationRepository.save(medication);
        log.info("Deactivated medication: {}", medication.getCode());
    }

    public long getActiveMedicationCount() {
        return medicationRepository.countActiveMedications();
    }

    private Medication findMedicationById(UUID id) {
        return medicationRepository.findById(id)
            .filter(Medication::isActive)
            .orElseThrow(() -> new ResourceNotFoundException("Medication", "id", id));
    }
}

