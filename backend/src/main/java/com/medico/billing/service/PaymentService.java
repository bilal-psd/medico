package com.medico.billing.service;

import com.medico.billing.domain.*;
import com.medico.billing.domain.Invoice.InvoiceStatus;
import com.medico.billing.domain.Payment.PaymentStatus;
import com.medico.billing.dto.*;
import com.medico.billing.mapper.BillingMapper;
import com.medico.billing.repository.InvoiceRepository;
import com.medico.billing.repository.PaymentRepository;
import com.medico.common.dto.PageResponse;
import com.medico.common.exception.BusinessException;
import com.medico.common.exception.ResourceNotFoundException;
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
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceService invoiceService;
    private final BillingMapper billingMapper;
    private static final AtomicLong paymentCounter = new AtomicLong(System.currentTimeMillis() % 100000);

    public List<PaymentDto> getPaymentsByInvoice(UUID invoiceId) {
        List<Payment> payments = paymentRepository.findByInvoiceIdOrderByPaymentDateDesc(invoiceId);
        return billingMapper.toPaymentDtoList(payments);
    }

    public PageResponse<PaymentDto> getPaymentsByStatus(PaymentStatus status, Pageable pageable) {
        Page<Payment> payments = paymentRepository.findByStatusOrderByPaymentDateDesc(status, pageable);
        return PageResponse.from(payments, payments.getContent().stream()
            .map(billingMapper::toDto)
            .toList());
    }

    public PaymentDto getPaymentById(UUID id) {
        Payment payment = findPaymentById(id);
        return billingMapper.toDto(payment);
    }

    public PaymentDto getPaymentByNumber(String paymentNumber) {
        Payment payment = paymentRepository.findByPaymentNumber(paymentNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Payment", "number", paymentNumber));
        return billingMapper.toDto(payment);
    }

    @Transactional
    public PaymentDto createPayment(CreatePaymentRequest request) {
        Invoice invoice = invoiceService.findInvoiceById(request.invoiceId());

        if (invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw new BusinessException("Cannot add payment to a cancelled invoice");
        }

        if (invoice.getStatus() == InvoiceStatus.PAID) {
            throw new BusinessException("Invoice is already fully paid");
        }

        if (request.amount().compareTo(invoice.getBalanceDue()) > 0) {
            throw new BusinessException("Payment amount exceeds balance due");
        }

        Payment payment = Payment.builder()
            .paymentNumber(generatePaymentNumber())
            .invoice(invoice)
            .amount(request.amount())
            .paymentDate(LocalDateTime.now())
            .paymentMethod(request.paymentMethod())
            .status(PaymentStatus.COMPLETED)
            .transactionReference(request.transactionReference())
            .receivedById(request.receivedById())
            .receivedByName(request.receivedByName())
            .notes(request.notes())
            .build();

        Payment savedPayment = paymentRepository.save(payment);

        // Update invoice amounts
        BigDecimal currentPaid = invoice.getPaidAmount() != null ? invoice.getPaidAmount() : BigDecimal.ZERO;
        invoice.setPaidAmount(currentPaid.add(request.amount()));
        invoice.setBalanceDue(invoice.getTotalAmount().subtract(invoice.getPaidAmount()));

        // Update invoice status
        if (invoice.getBalanceDue().compareTo(BigDecimal.ZERO) <= 0) {
            invoice.setStatus(InvoiceStatus.PAID);
        } else {
            invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
        }

        invoiceRepository.save(invoice);

        log.info("Created payment {} for invoice {}", savedPayment.getPaymentNumber(), invoice.getInvoiceNumber());

        return billingMapper.toDto(savedPayment);
    }

    @Transactional
    public PaymentDto refundPayment(UUID id, String notes) {
        Payment payment = findPaymentById(id);

        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            throw new BusinessException("Payment is already refunded");
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        payment.setNotes(payment.getNotes() != null ? payment.getNotes() + " | Refund: " + notes : "Refund: " + notes);

        // Update invoice
        Invoice invoice = payment.getInvoice();
        BigDecimal newPaidAmount = invoice.getPaidAmount().subtract(payment.getAmount());
        invoice.setPaidAmount(newPaidAmount.max(BigDecimal.ZERO));
        invoice.calculateTotals();

        if (invoice.getPaidAmount().compareTo(BigDecimal.ZERO) == 0) {
            invoice.setStatus(InvoiceStatus.PENDING);
        } else {
            invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
        }

        invoiceRepository.save(invoice);
        Payment refundedPayment = paymentRepository.save(payment);

        log.info("Refunded payment {}", payment.getPaymentNumber());

        return billingMapper.toDto(refundedPayment);
    }

    public long getTodayPaymentCount() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        return paymentRepository.countPaymentsInRange(startOfDay, endOfDay);
    }

    public BigDecimal getTodayTotalPayments() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        BigDecimal total = paymentRepository.getTotalPayments(startOfDay, endOfDay);
        return total != null ? total : BigDecimal.ZERO;
    }

    private Payment findPaymentById(UUID id) {
        return paymentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Payment", "id", id));
    }

    private String generatePaymentNumber() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String sequencePart = String.format("%05d", paymentCounter.incrementAndGet() % 100000);
        String paymentNumber = "PAY-" + datePart + "-" + sequencePart;

        while (paymentRepository.existsByPaymentNumber(paymentNumber)) {
            sequencePart = String.format("%05d", paymentCounter.incrementAndGet() % 100000);
            paymentNumber = "PAY-" + datePart + "-" + sequencePart;
        }

        return paymentNumber;
    }
}

