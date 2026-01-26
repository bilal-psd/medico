package com.medico.pharmacy.domain;

import com.medico.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Inventory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medication_id", nullable = false)
    private Medication medication;

    @Column(name = "batch_number", nullable = false)
    private String batchNumber;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "reserved_quantity")
    private Integer reservedQuantity = 0;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "manufacture_date")
    private LocalDate manufactureDate;

    @Column(name = "location")
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InventoryStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public int getAvailableQuantity() {
        return quantity - (reservedQuantity != null ? reservedQuantity : 0);
    }

    public boolean isExpired() {
        return expiryDate.isBefore(LocalDate.now());
    }

    public boolean isExpiringSoon(int daysThreshold) {
        return expiryDate.isBefore(LocalDate.now().plusDays(daysThreshold));
    }

    public boolean isLowStock(int threshold) {
        return getAvailableQuantity() <= threshold;
    }

    public enum InventoryStatus {
        AVAILABLE,
        LOW_STOCK,
        OUT_OF_STOCK,
        EXPIRED,
        RESERVED,
        QUARANTINE
    }
}

