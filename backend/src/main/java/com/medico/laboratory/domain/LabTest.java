package com.medico.laboratory.domain;

import com.medico.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "lab_tests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class LabTest extends BaseEntity {

    @Column(name = "code", unique = true, nullable = false)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private LabTestCategory category;

    @Column(name = "sample_type")
    private String sampleType;

    @Column(name = "sample_volume")
    private String sampleVolume;

    @Column(name = "container_type")
    private String containerType;

    @Column(name = "preparation_instructions", columnDefinition = "TEXT")
    private String preparationInstructions;

    @Column(name = "turnaround_time")
    private String turnaroundTime;

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "normal_range", columnDefinition = "TEXT")
    private String normalRange;

    @Column(name = "unit")
    private String unit;

    @Column(name = "active")
    private boolean active = true;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public enum LabTestCategory {
        HEMATOLOGY,
        CHEMISTRY,
        MICROBIOLOGY,
        IMMUNOLOGY,
        URINALYSIS,
        PATHOLOGY,
        RADIOLOGY,
        CARDIOLOGY,
        ENDOCRINOLOGY,
        TOXICOLOGY,
        GENETICS,
        OTHER
    }
}

