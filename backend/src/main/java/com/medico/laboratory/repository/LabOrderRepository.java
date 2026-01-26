package com.medico.laboratory.repository;

import com.medico.laboratory.domain.LabOrder;
import com.medico.laboratory.domain.LabOrder.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LabOrderRepository extends JpaRepository<LabOrder, UUID> {

    Optional<LabOrder> findByOrderNumber(String orderNumber);

    boolean existsByOrderNumber(String orderNumber);

    Page<LabOrder> findByPatientIdOrderByOrderDateDesc(UUID patientId, Pageable pageable);

    Page<LabOrder> findByOrderingDoctorIdOrderByOrderDateDesc(UUID doctorId, Pageable pageable);

    Page<LabOrder> findByStatusOrderByOrderDateDesc(OrderStatus status, Pageable pageable);

    @Query("SELECT lo FROM LabOrder lo WHERE lo.status = :status ORDER BY " +
           "CASE lo.priority WHEN 'STAT' THEN 1 WHEN 'URGENT' THEN 2 ELSE 3 END, lo.orderDate ASC")
    Page<LabOrder> findByStatusOrderByPriority(@Param("status") OrderStatus status, Pageable pageable);

    @Query("SELECT lo FROM LabOrder lo WHERE lo.orderDate BETWEEN :start AND :end")
    List<LabOrder> findByDateRange(
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    @Query("SELECT COUNT(lo) FROM LabOrder lo WHERE lo.status = :status")
    long countByStatus(@Param("status") OrderStatus status);

    @Query("SELECT COUNT(lo) FROM LabOrder lo WHERE lo.orderDate BETWEEN :start AND :end")
    long countOrdersInRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}

