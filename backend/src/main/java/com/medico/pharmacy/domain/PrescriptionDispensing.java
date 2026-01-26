package com.medico.pharmacy.domain;

import com.medico.common.domain.BaseEntity;
import com.medico.patient.domain.Prescription;
import com.medico.patient.domain.PrescriptionItem;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "prescription_dispensing")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PrescriptionDispensing extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_item_id", nullable = false)
    private PrescriptionItem prescriptionItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_id", nullable = false)
    private Inventory inventory;

    @Column(name = "dispensed_quantity", nullable = false)
    private Integer dispensedQuantity;

    @Column(name = "dispensed_at", nullable = false)
    private LocalDateTime dispensedAt;

    @Column(name = "dispensed_by", nullable = false)
    private UUID dispensedBy;

    @Column(name = "pharmacist_name", nullable = false)
    private String pharmacistName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DispensingStatus status;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public enum DispensingStatus {
        PENDING,
        DISPENSED,
        PARTIALLY_DISPENSED,
        RETURNED,
        CANCELLED
    }
}

