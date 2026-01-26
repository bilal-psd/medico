package com.medico.pharmacy.domain;

import com.medico.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Entity
@Table(name = "medications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Medication extends BaseEntity {

    @Column(name = "code", unique = true, nullable = false)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "generic_name")
    private String genericName;

    @Column(name = "brand_name")
    private String brandName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private MedicationCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "form", nullable = false)
    private MedicationForm form;

    @Column(name = "strength")
    private String strength;

    @Column(name = "unit")
    private String unit;

    @Column(name = "manufacturer")
    private String manufacturer;

    @Column(name = "unit_price", precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "reorder_level")
    private Integer reorderLevel;

    @Column(name = "requires_prescription")
    private boolean requiresPrescription;

    @Column(name = "controlled_substance")
    private boolean controlledSubstance;

    @Column(name = "active")
    private boolean active = true;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    public enum MedicationCategory {
        ANTIBIOTIC,
        ANALGESIC,
        ANTIVIRAL,
        ANTIFUNGAL,
        CARDIOVASCULAR,
        RESPIRATORY,
        GASTROINTESTINAL,
        NEUROLOGICAL,
        PSYCHIATRIC,
        HORMONAL,
        VITAMINS_SUPPLEMENTS,
        TOPICAL,
        OPHTHALMIC,
        OTHER
    }

    public enum MedicationForm {
        TABLET,
        CAPSULE,
        SYRUP,
        INJECTION,
        CREAM,
        OINTMENT,
        GEL,
        DROPS,
        INHALER,
        PATCH,
        SUPPOSITORY,
        POWDER,
        SOLUTION,
        SUSPENSION,
        OTHER
    }
}

