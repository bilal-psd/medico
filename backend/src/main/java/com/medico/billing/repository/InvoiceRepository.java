package com.medico.billing.repository;

import com.medico.billing.domain.Invoice;
import com.medico.billing.domain.Invoice.InvoiceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    boolean existsByInvoiceNumber(String invoiceNumber);

    Page<Invoice> findByPatientIdOrderByInvoiceDateDesc(UUID patientId, Pageable pageable);

    Page<Invoice> findByStatusOrderByInvoiceDateDesc(InvoiceStatus status, Pageable pageable);

    @Query("SELECT i FROM Invoice i WHERE i.status = 'PENDING' AND i.dueDate < :today")
    List<Invoice> findOverdueInvoices(@Param("today") LocalDate today);

    @Query("SELECT i FROM Invoice i WHERE i.invoiceDate BETWEEN :start AND :end")
    List<Invoice> findByDateRange(
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    @Query("SELECT SUM(i.totalAmount) FROM Invoice i WHERE i.invoiceDate BETWEEN :start AND :end AND i.status != 'CANCELLED'")
    BigDecimal getTotalRevenue(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT SUM(i.paidAmount) FROM Invoice i WHERE i.invoiceDate BETWEEN :start AND :end")
    BigDecimal getTotalCollected(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT SUM(i.balanceDue) FROM Invoice i WHERE i.status IN ('PENDING', 'PARTIALLY_PAID', 'OVERDUE')")
    BigDecimal getTotalOutstanding();

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.status = :status")
    long countByStatus(@Param("status") InvoiceStatus status);

    @Query("SELECT COUNT(i) FROM Invoice i WHERE i.invoiceDate BETWEEN :start AND :end")
    long countInvoicesInRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}

