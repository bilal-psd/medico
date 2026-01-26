package com.medico.laboratory.service;

import com.medico.common.dto.PageResponse;
import com.medico.common.exception.BusinessException;
import com.medico.common.exception.ResourceNotFoundException;
import com.medico.laboratory.domain.*;
import com.medico.laboratory.domain.LabOrder.OrderStatus;
import com.medico.laboratory.domain.LabOrderItem.ItemStatus;
import com.medico.laboratory.dto.*;
import com.medico.laboratory.mapper.LaboratoryMapper;
import com.medico.laboratory.repository.LabOrderRepository;
import com.medico.laboratory.repository.LabResultRepository;
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
public class LabResultService {

    private final LabResultRepository labResultRepository;
    private final LabOrderRepository labOrderRepository;
    private final LaboratoryMapper laboratoryMapper;

    public PageResponse<LabResultDto> getPatientResults(UUID patientId, Pageable pageable) {
        Page<LabResult> results = labResultRepository.findByPatientId(patientId, pageable);
        return PageResponse.from(results, results.getContent().stream()
            .map(laboratoryMapper::toDto)
            .toList());
    }

    public List<LabResultDto> getResultsByOrder(UUID orderId) {
        List<LabResult> results = labResultRepository.findByOrderId(orderId);
        return laboratoryMapper.toLabResultDtoList(results);
    }

    public PageResponse<LabResultDto> getAbnormalResults(UUID patientId, Pageable pageable) {
        Page<LabResult> results = labResultRepository.findAbnormalResultsByPatientId(patientId, pageable);
        return PageResponse.from(results, results.getContent().stream()
            .map(laboratoryMapper::toDto)
            .toList());
    }

    public LabResultDto getResultById(UUID id) {
        LabResult result = findResultById(id);
        return laboratoryMapper.toDto(result);
    }

    @Transactional
    public LabResultDto createResult(CreateLabResultRequest request) {
        LabOrder labOrder = labOrderRepository.findAll().stream()
            .filter(order -> order.getItems().stream()
                .anyMatch(item -> item.getId().equals(request.labOrderItemId())))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("LabOrder with item", "id", request.labOrderItemId()));

        LabOrderItem labOrderItem = labOrder.getItems().stream()
            .filter(item -> item.getId().equals(request.labOrderItemId()))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("LabOrderItem", "id", request.labOrderItemId()));

        if (labOrderItem.getResult() != null) {
            throw new BusinessException("Result already exists for this order item");
        }

        if (labOrderItem.getStatus() == ItemStatus.CANCELLED) {
            throw new BusinessException("Cannot add result to a cancelled order item");
        }

        LabResult result = LabResult.builder()
            .labOrderItem(labOrderItem)
            .resultValue(request.resultValue())
            .unit(request.unit())
            .referenceRange(request.referenceRange())
            .flag(request.flag())
            .interpretation(request.interpretation())
            .performedAt(LocalDateTime.now())
            .performedBy(request.performedBy())
            .technicianName(request.technicianName())
            .notes(request.notes())
            .abnormal(request.abnormal())
            .critical(request.critical())
            .build();

        labOrderItem.setResult(result);
        labOrderItem.setStatus(ItemStatus.COMPLETED);

        // Update order status if all items are completed
        updateOrderStatus(labOrder);

        LabResult savedResult = labResultRepository.save(result);
        log.info("Created result for order {} item {}", labOrder.getOrderNumber(), labOrderItem.getId());

        return laboratoryMapper.toDto(savedResult);
    }

    @Transactional
    public LabResultDto verifyResult(UUID id, UUID verifiedBy, String verifierName) {
        LabResult result = findResultById(id);

        if (result.getVerifiedAt() != null) {
            throw new BusinessException("Result is already verified");
        }

        result.setVerifiedAt(LocalDateTime.now());
        result.setVerifiedBy(verifiedBy);
        result.setVerifierName(verifierName);

        LabResult verifiedResult = labResultRepository.save(result);
        log.info("Verified result {}", id);

        return laboratoryMapper.toDto(verifiedResult);
    }

    public List<LabResultDto> getUnverifiedCriticalResults() {
        List<LabResult> results = labResultRepository.findUnverifiedCriticalResults();
        return laboratoryMapper.toLabResultDtoList(results);
    }

    public long getTodayResultCount() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        return labResultRepository.countResultsInRange(startOfDay, endOfDay);
    }

    public long getPendingVerificationCount() {
        return labResultRepository.countPendingVerification();
    }

    private LabResult findResultById(UUID id) {
        return labResultRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("LabResult", "id", id));
    }

    private void updateOrderStatus(LabOrder labOrder) {
        boolean allCompleted = labOrder.getItems().stream()
            .allMatch(item -> item.getStatus() == ItemStatus.COMPLETED || item.getStatus() == ItemStatus.CANCELLED);

        boolean anyInProgress = labOrder.getItems().stream()
            .anyMatch(item -> item.getStatus() == ItemStatus.IN_PROGRESS);

        boolean anyCompleted = labOrder.getItems().stream()
            .anyMatch(item -> item.getStatus() == ItemStatus.COMPLETED);

        if (allCompleted) {
            labOrder.setStatus(OrderStatus.COMPLETED);
        } else if (anyInProgress || anyCompleted) {
            labOrder.setStatus(OrderStatus.IN_PROGRESS);
        }

        labOrderRepository.save(labOrder);
    }
}

