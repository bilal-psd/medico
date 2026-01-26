package com.medico.laboratory.service;

import com.medico.common.dto.PageResponse;
import com.medico.common.exception.BusinessException;
import com.medico.common.exception.ResourceNotFoundException;
import com.medico.laboratory.domain.LabTest;
import com.medico.laboratory.domain.LabTest.LabTestCategory;
import com.medico.laboratory.dto.*;
import com.medico.laboratory.mapper.LaboratoryMapper;
import com.medico.laboratory.repository.LabTestRepository;
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
public class LabTestService {

    private final LabTestRepository labTestRepository;
    private final LaboratoryMapper laboratoryMapper;

    public PageResponse<LabTestDto> getAllLabTests(Pageable pageable) {
        Page<LabTest> labTests = labTestRepository.findByActiveTrue(pageable);
        return PageResponse.from(labTests, labTests.getContent().stream()
            .map(laboratoryMapper::toDto)
            .toList());
    }

    public PageResponse<LabTestDto> getLabTestsByCategory(LabTestCategory category, Pageable pageable) {
        Page<LabTest> labTests = labTestRepository.findByCategoryAndActiveTrue(category, pageable);
        return PageResponse.from(labTests, labTests.getContent().stream()
            .map(laboratoryMapper::toDto)
            .toList());
    }

    public PageResponse<LabTestDto> searchLabTests(String search, Pageable pageable) {
        Page<LabTest> labTests = labTestRepository.searchLabTests(search, pageable);
        return PageResponse.from(labTests, labTests.getContent().stream()
            .map(laboratoryMapper::toDto)
            .toList());
    }

    public LabTestDto getLabTestById(UUID id) {
        LabTest labTest = findLabTestById(id);
        return laboratoryMapper.toDto(labTest);
    }

    public LabTestDto getLabTestByCode(String code) {
        LabTest labTest = labTestRepository.findByCode(code)
            .orElseThrow(() -> new ResourceNotFoundException("LabTest", "code", code));
        return laboratoryMapper.toDto(labTest);
    }

    @Transactional
    public LabTestDto createLabTest(CreateLabTestRequest request) {
        if (labTestRepository.existsByCode(request.code())) {
            throw new BusinessException("A lab test with this code already exists");
        }

        LabTest labTest = laboratoryMapper.toEntity(request);
        LabTest savedLabTest = labTestRepository.save(labTest);
        log.info("Created lab test: {}", savedLabTest.getCode());

        return laboratoryMapper.toDto(savedLabTest);
    }

    @Transactional
    public LabTestDto updateLabTest(UUID id, CreateLabTestRequest request) {
        LabTest labTest = findLabTestById(id);
        laboratoryMapper.updateLabTest(request, labTest);
        LabTest updatedLabTest = labTestRepository.save(labTest);
        log.info("Updated lab test: {}", updatedLabTest.getCode());

        return laboratoryMapper.toDto(updatedLabTest);
    }

    @Transactional
    public void deactivateLabTest(UUID id) {
        LabTest labTest = findLabTestById(id);
        labTest.setActive(false);
        labTestRepository.save(labTest);
        log.info("Deactivated lab test: {}", labTest.getCode());
    }

    public long getActiveLabTestCount() {
        return labTestRepository.countActiveLabTests();
    }

    private LabTest findLabTestById(UUID id) {
        return labTestRepository.findById(id)
            .filter(LabTest::isActive)
            .orElseThrow(() -> new ResourceNotFoundException("LabTest", "id", id));
    }
}

