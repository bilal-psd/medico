package com.medico.billing.domain;

import com.medico.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "billing_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BillingItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false)
    private BillingItemType itemType;

    @Column(name = "reference_id")
    private UUID referenceId;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "discount_percent", precision = 5, scale = 2)
    private BigDecimal discountPercent;

    @Column(name = "total_price", precision = 12, scale = 2, nullable = false)
    private BigDecimal totalPrice;

    @Column(name = "notes")
    private String notes;

    @PrePersist
    @PreUpdate
    public void calculateTotal() {
        BigDecimal gross = unitPrice.multiply(BigDecimal.valueOf(quantity));
        if (discountPercent != null && discountPercent.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal discountAmount = gross.multiply(discountPercent).divide(BigDecimal.valueOf(100));
            this.totalPrice = gross.subtract(discountAmount);
        } else {
            this.totalPrice = gross;
        }
    }

    public enum BillingItemType {
        CONSULTATION,
        PROCEDURE,
        MEDICATION,
        LAB_TEST,
        IMAGING,
        ROOM_CHARGE,
        EQUIPMENT,
        SUPPLIES,
        OTHER
    }
}

