package com.medico.laboratory.domain;

import com.medico.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "lab_results")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class LabResult extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lab_order_item_id", nullable = false, unique = true)
    private LabOrderItem labOrderItem;

    @Column(name = "result_value", columnDefinition = "TEXT")
    private String resultValue;

    @Column(name = "unit")
    private String unit;

    @Column(name = "reference_range")
    private String referenceRange;

    @Enumerated(EnumType.STRING)
    @Column(name = "flag")
    private ResultFlag flag;

    @Column(name = "interpretation", columnDefinition = "TEXT")
    private String interpretation;

    @Column(name = "performed_at")
    private LocalDateTime performedAt;

    @Column(name = "performed_by")
    private UUID performedBy;

    @Column(name = "technician_name")
    private String technicianName;

    @Column(name = "verified_at")
    private LocalDateTime verifiedAt;

    @Column(name = "verified_by")
    private UUID verifiedBy;

    @Column(name = "verifier_name")
    private String verifierName;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "is_abnormal")
    private boolean abnormal;

    @Column(name = "is_critical")
    private boolean critical;

    public enum ResultFlag {
        NORMAL,
        LOW,
        HIGH,
        CRITICAL_LOW,
        CRITICAL_HIGH,
        ABNORMAL
    }
}

