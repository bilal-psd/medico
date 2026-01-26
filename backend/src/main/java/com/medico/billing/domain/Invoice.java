package com.medico.billing.domain;

import com.medico.common.domain.BaseEntity;
import com.medico.patient.domain.Patient;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "invoices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Invoice extends BaseEntity {

    @Column(name = "invoice_number", unique = true, nullable = false)
    private String invoiceNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "invoice_date", nullable = false)
    private LocalDateTime invoiceDate;

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InvoiceStatus status;

    @Column(name = "subtotal", precision = 12, scale = 2, nullable = false)
    private BigDecimal subtotal;

    @Column(name = "tax_amount", precision = 12, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "discount_amount", precision = 12, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "total_amount", precision = 12, scale = 2, nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "paid_amount", precision = 12, scale = 2)
    private BigDecimal paidAmount;

    @Column(name = "balance_due", precision = 12, scale = 2)
    private BigDecimal balanceDue;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_by_id")
    private UUID createdById;

    @Column(name = "created_by_name")
    private String createdByName;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<BillingItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Payment> payments = new ArrayList<>();

    public void addItem(BillingItem item) {
        items.add(item);
        item.setInvoice(this);
    }

    public void removeItem(BillingItem item) {
        items.remove(item);
        item.setInvoice(null);
    }

    public void calculateTotals() {
        this.subtotal = items.stream()
            .map(BillingItem::getTotalPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tax = taxAmount != null ? taxAmount : BigDecimal.ZERO;
        BigDecimal discount = discountAmount != null ? discountAmount : BigDecimal.ZERO;
        BigDecimal paid = paidAmount != null ? paidAmount : BigDecimal.ZERO;

        this.totalAmount = subtotal.add(tax).subtract(discount);
        this.balanceDue = totalAmount.subtract(paid);
    }

    public enum InvoiceStatus {
        DRAFT,
        PENDING,
        PARTIALLY_PAID,
        PAID,
        OVERDUE,
        CANCELLED,
        REFUNDED
    }
}

