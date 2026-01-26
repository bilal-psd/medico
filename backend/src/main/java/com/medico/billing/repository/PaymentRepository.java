package com.medico.billing.repository;

import com.medico.billing.domain.Payment;
import com.medico.billing.domain.Payment.PaymentMethod;
import com.medico.billing.domain.Payment.PaymentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByPaymentNumber(String paymentNumber);

    boolean existsByPaymentNumber(String paymentNumber);

    List<Payment> findByInvoiceIdOrderByPaymentDateDesc(UUID invoiceId);

    Page<Payment> findByStatusOrderByPaymentDateDesc(PaymentStatus status, Pageable pageable);

    @Query("SELECT p FROM Payment p WHERE p.paymentDate BETWEEN :start AND :end ORDER BY p.paymentDate DESC")
    List<Payment> findByDateRange(
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.paymentDate BETWEEN :start AND :end AND p.status = 'COMPLETED'")
    BigDecimal getTotalPayments(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT SUM(p.amount) FROM Payment p WHERE p.paymentMethod = :method AND p.paymentDate BETWEEN :start AND :end AND p.status = 'COMPLETED'")
    BigDecimal getTotalByPaymentMethod(
        @Param("method") PaymentMethod method,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    @Query("SELECT COUNT(p) FROM Payment p WHERE p.paymentDate BETWEEN :start AND :end AND p.status = 'COMPLETED'")
    long countPaymentsInRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}

