package com.medico.patient.domain;

import com.medico.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Entity
@Table(name = "prescription_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PrescriptionItem extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id", nullable = false)
    private Prescription prescription;

    @Column(name = "medication_id")
    private UUID medicationId;

    @Column(name = "medication_name", nullable = false)
    private String medicationName;

    @Column(name = "dosage", nullable = false)
    private String dosage;

    @Column(name = "frequency", nullable = false)
    private String frequency;

    @Column(name = "duration")
    private String duration;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "instructions", columnDefinition = "TEXT")
    private String instructions;

    @Column(name = "dispensed_quantity")
    private Integer dispensedQuantity;

    @Column(name = "refills_allowed")
    private Integer refillsAllowed;

    @Column(name = "refills_remaining")
    private Integer refillsRemaining;
}

