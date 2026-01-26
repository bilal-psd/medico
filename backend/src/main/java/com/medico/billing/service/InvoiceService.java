package com.medico.billing.service;

import com.medico.billing.domain.*;
import com.medico.billing.domain.Invoice.InvoiceStatus;
import com.medico.billing.dto.*;
import com.medico.billing.mapper.BillingMapper;
import com.medico.billing.repository.InvoiceRepository;
import com.medico.common.dto.PageResponse;
import com.medico.common.exception.BusinessException;
import com.medico.common.exception.ResourceNotFoundException;
import com.medico.patient.domain.Patient;
import com.medico.patient.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final PatientRepository patientRepository;
    private final BillingMapper billingMapper;
    private static final AtomicLong invoiceCounter = new AtomicLong(System.currentTimeMillis() % 100000);

    public PageResponse<InvoiceDto> getAllInvoices(Pageable pageable) {
        Page<Invoice> invoices = invoiceRepository.findAll(pageable);
        return PageResponse.from(invoices, invoices.getContent().stream()
            .map(billingMapper::toDto)
            .toList());
    }

    public PageResponse<InvoiceDto> getInvoicesByPatient(UUID patientId, Pageable pageable) {
        Page<Invoice> invoices = invoiceRepository.findByPatientIdOrderByInvoiceDateDesc(patientId, pageable);
        return PageResponse.from(invoices, invoices.getContent().stream()
            .map(billingMapper::toDto)
            .toList());
    }

    public PageResponse<InvoiceDto> getInvoicesByStatus(InvoiceStatus status, Pageable pageable) {
        Page<Invoice> invoices = invoiceRepository.findByStatusOrderByInvoiceDateDesc(status, pageable);
        return PageResponse.from(invoices, invoices.getContent().stream()
            .map(billingMapper::toDto)
            .toList());
    }

    public InvoiceDto getInvoiceById(UUID id) {
        Invoice invoice = findInvoiceById(id);
        return billingMapper.toDto(invoice);
    }

    public InvoiceDto getInvoiceByNumber(String invoiceNumber) {
        Invoice invoice = invoiceRepository.findByInvoiceNumber(invoiceNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Invoice", "number", invoiceNumber));
        return billingMapper.toDto(invoice);
    }

    @Transactional
    public InvoiceDto createInvoice(CreateInvoiceRequest request) {
        Patient patient = patientRepository.findById(request.patientId())
            .orElseThrow(() -> new ResourceNotFoundException("Patient", "id", request.patientId()));

        Invoice invoice = Invoice.builder()
            .invoiceNumber(generateInvoiceNumber())
            .patient(patient)
            .invoiceDate(LocalDateTime.now())
            .dueDate(request.dueDate())
            .status(InvoiceStatus.PENDING)
            .taxAmount(request.taxAmount())
            .discountAmount(request.discountAmount())
            .paidAmount(BigDecimal.ZERO)
            .notes(request.notes())
            .createdById(request.createdById())
            .createdByName(request.createdByName())
            .build();

        for (CreateBillingItemRequest itemRequest : request.items()) {
            BillingItem item = billingMapper.toEntity(itemRequest);
            item.calculateTotal();
            invoice.addItem(item);
        }

        invoice.calculateTotals();

        Invoice savedInvoice = invoiceRepository.save(invoice);
        log.info("Created invoice {} for patient {}", savedInvoice.getInvoiceNumber(), patient.getMedicalRecordNumber());

        return billingMapper.toDto(savedInvoice);
    }

    @Transactional
    public InvoiceDto updateInvoiceStatus(UUID id, InvoiceStatus status) {
        Invoice invoice = findInvoiceById(id);

        if (invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw new BusinessException("Cannot update a cancelled invoice");
        }

        invoice.setStatus(status);
        Invoice updatedInvoice = invoiceRepository.save(invoice);
        log.info("Updated invoice {} status to {}", invoice.getInvoiceNumber(), status);

        return billingMapper.toDto(updatedInvoice);
    }

    @Transactional
    public void cancelInvoice(UUID id) {
        Invoice invoice = findInvoiceById(id);

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new BusinessException("Cannot cancel a paid invoice");
        }

        if (invoice.getPaidAmount() != null && invoice.getPaidAmount().compareTo(BigDecimal.ZERO) > 0) {
            throw new BusinessException("Cannot cancel an invoice with payments. Refund first.");
        }

        invoice.setStatus(InvoiceStatus.CANCELLED);
        invoiceRepository.save(invoice);
        log.info("Cancelled invoice {}", invoice.getInvoiceNumber());
    }

    @Transactional
    public void updateOverdueInvoices() {
        List<Invoice> overdueInvoices = invoiceRepository.findOverdueInvoices(LocalDate.now());
        for (Invoice invoice : overdueInvoices) {
            invoice.setStatus(InvoiceStatus.OVERDUE);
        }
        invoiceRepository.saveAll(overdueInvoices);
        log.info("Marked {} invoices as overdue", overdueInvoices.size());
    }

    public List<InvoiceDto> getOverdueInvoices() {
        List<Invoice> overdueInvoices = invoiceRepository.findOverdueInvoices(LocalDate.now());
        return billingMapper.toInvoiceDtoList(overdueInvoices);
    }

    public long getPendingInvoiceCount() {
        return invoiceRepository.countByStatus(InvoiceStatus.PENDING);
    }

    public long getOverdueInvoiceCount() {
        return invoiceRepository.countByStatus(InvoiceStatus.OVERDUE);
    }

    public long getTodayInvoiceCount() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        return invoiceRepository.countInvoicesInRange(startOfDay, endOfDay);
    }

    Invoice findInvoiceById(UUID id) {
        return invoiceRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Invoice", "id", id));
    }

    private String generateInvoiceNumber() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String sequencePart = String.format("%05d", invoiceCounter.incrementAndGet() % 100000);
        String invoiceNumber = "INV-" + datePart + "-" + sequencePart;

        while (invoiceRepository.existsByInvoiceNumber(invoiceNumber)) {
            sequencePart = String.format("%05d", invoiceCounter.incrementAndGet() % 100000);
            invoiceNumber = "INV-" + datePart + "-" + sequencePart;
        }

        return invoiceNumber;
    }
}

