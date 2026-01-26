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
import com.medico.laboratory.repository.LabTestRepository;
import com.medico.patient.domain.Patient;
import com.medico.patient.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LabOrderService {

    private final LabOrderRepository labOrderRepository;
    private final LabTestRepository labTestRepository;
    private final PatientRepository patientRepository;
    private final LaboratoryMapper laboratoryMapper;
    private static final AtomicLong orderCounter = new AtomicLong(System.currentTimeMillis() % 100000);

    public PageResponse<LabOrderDto> getAllLabOrders(Pageable pageable) {
        Page<LabOrder> labOrders = labOrderRepository.findAll(pageable);
        return PageResponse.from(labOrders, labOrders.getContent().stream()
            .map(laboratoryMapper::toDto)
            .toList());
    }

    public PageResponse<LabOrderDto> getLabOrdersByPatient(UUID patientId, Pageable pageable) {
        Page<LabOrder> labOrders = labOrderRepository.findByPatientIdOrderByOrderDateDesc(patientId, pageable);
        return PageResponse.from(labOrders, labOrders.getContent().stream()
            .map(laboratoryMapper::toDto)
            .toList());
    }

    public PageResponse<LabOrderDto> getLabOrdersByStatus(OrderStatus status, Pageable pageable) {
        Page<LabOrder> labOrders = labOrderRepository.findByStatusOrderByPriority(status, pageable);
        return PageResponse.from(labOrders, labOrders.getContent().stream()
            .map(laboratoryMapper::toDto)
            .toList());
    }

    public LabOrderDto getLabOrderById(UUID id) {
        LabOrder labOrder = findLabOrderById(id);
        return laboratoryMapper.toDto(labOrder);
    }

    public LabOrderDto getLabOrderByNumber(String orderNumber) {
        LabOrder labOrder = labOrderRepository.findByOrderNumber(orderNumber)
            .orElseThrow(() -> new ResourceNotFoundException("LabOrder", "orderNumber", orderNumber));
        return laboratoryMapper.toDto(labOrder);
    }

    @Transactional
    public LabOrderDto createLabOrder(CreateLabOrderRequest request) {
        Patient patient = patientRepository.findById(request.patientId())
            .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", request.patientId()));

        LabOrder labOrder = LabOrder.builder()
            .orderNumber(generateOrderNumber())
            .patient(patient)
            .orderingDoctorId(request.orderingDoctorId())
            .orderingDoctorName(request.orderingDoctorName())
            .orderDate(LocalDateTime.now())
            .priority(request.priority())
            .status(OrderStatus.PENDING)
            .clinicalNotes(request.clinicalNotes())
            .diagnosis(request.diagnosis())
            .build();

        for (CreateLabOrderItemRequest itemRequest : request.items()) {
            LabTest labTest = labTestRepository.findById(itemRequest.labTestId())
                .orElseThrow(() -> new ResourceNotFoundException("LabTest", "id", itemRequest.labTestId()));

            LabOrderItem item = LabOrderItem.builder()
                .labTest(labTest)
                .status(ItemStatus.PENDING)
                .notes(itemRequest.notes())
                .build();

            labOrder.addItem(item);
        }

        LabOrder savedLabOrder = labOrderRepository.save(labOrder);
        log.info("Created lab order {} for patient {}", savedLabOrder.getOrderNumber(), patient.getMedicalRecordNumber());

        return laboratoryMapper.toDto(savedLabOrder);
    }

    @Transactional
    public LabOrderDto updateLabOrderStatus(UUID id, OrderStatus status) {
        LabOrder labOrder = findLabOrderById(id);

        if (labOrder.getStatus() == OrderStatus.CANCELLED) {
            throw new BusinessException("Cannot update a cancelled order");
        }

        labOrder.setStatus(status);
        LabOrder updatedLabOrder = labOrderRepository.save(labOrder);
        log.info("Updated lab order {} status to {}", labOrder.getOrderNumber(), status);

        return laboratoryMapper.toDto(updatedLabOrder);
    }

    @Transactional
    public LabOrderDto collectSample(UUID id, UUID collectedBy) {
        LabOrder labOrder = findLabOrderById(id);

        if (labOrder.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException("Sample can only be collected for pending orders");
        }

        labOrder.setStatus(OrderStatus.SAMPLE_COLLECTED);
        labOrder.setSampleCollectedAt(LocalDateTime.now());
        labOrder.setSampleCollectedBy(collectedBy);

        LabOrder updatedLabOrder = labOrderRepository.save(labOrder);
        log.info("Sample collected for lab order {}", labOrder.getOrderNumber());

        return laboratoryMapper.toDto(updatedLabOrder);
    }

    @Transactional
    public void cancelLabOrder(UUID id) {
        LabOrder labOrder = findLabOrderById(id);

        if (labOrder.getStatus() == OrderStatus.COMPLETED) {
            throw new BusinessException("Cannot cancel a completed order");
        }

        labOrder.setStatus(OrderStatus.CANCELLED);
        labOrder.getItems().forEach(item -> item.setStatus(ItemStatus.CANCELLED));

        labOrderRepository.save(labOrder);
        log.info("Cancelled lab order {}", labOrder.getOrderNumber());
    }

    public long getPendingOrderCount() {
        return labOrderRepository.countByStatus(OrderStatus.PENDING);
    }

    public long getTodayOrderCount() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        return labOrderRepository.countOrdersInRange(startOfDay, endOfDay);
    }

    private LabOrder findLabOrderById(UUID id) {
        return labOrderRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("LabOrder", "id", id));
    }

    private String generateOrderNumber() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String sequencePart = String.format("%05d", orderCounter.incrementAndGet() % 100000);
        String orderNumber = "LAB-" + datePart + "-" + sequencePart;

        while (labOrderRepository.existsByOrderNumber(orderNumber)) {
            sequencePart = String.format("%05d", orderCounter.incrementAndGet() % 100000);
            orderNumber = "LAB-" + datePart + "-" + sequencePart;
        }

        return orderNumber;
    }
}

