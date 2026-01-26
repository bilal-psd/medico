package com.medico.laboratory.domain;

import com.medico.common.domain.BaseEntity;
import com.medico.patient.domain.Patient;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "lab_orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class LabOrder extends BaseEntity {

    @Column(name = "order_number", unique = true, nullable = false)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "ordering_doctor_id", nullable = false)
    private UUID orderingDoctorId;

    @Column(name = "ordering_doctor_name", nullable = false)
    private String orderingDoctorName;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private OrderPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "clinical_notes", columnDefinition = "TEXT")
    private String clinicalNotes;

    @Column(name = "diagnosis")
    private String diagnosis;

    @Column(name = "sample_collected_at")
    private LocalDateTime sampleCollectedAt;

    @Column(name = "sample_collected_by")
    private UUID sampleCollectedBy;

    @OneToMany(mappedBy = "labOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<LabOrderItem> items = new ArrayList<>();

    public void addItem(LabOrderItem item) {
        items.add(item);
        item.setLabOrder(this);
    }

    public void removeItem(LabOrderItem item) {
        items.remove(item);
        item.setLabOrder(null);
    }

    public enum OrderPriority {
        ROUTINE,
        URGENT,
        STAT
    }

    public enum OrderStatus {
        PENDING,
        SAMPLE_COLLECTED,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }
}

