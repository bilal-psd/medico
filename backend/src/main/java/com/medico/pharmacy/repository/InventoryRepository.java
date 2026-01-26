package com.medico.pharmacy.repository;

import com.medico.pharmacy.domain.Inventory;
import com.medico.pharmacy.domain.Inventory.InventoryStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

    Page<Inventory> findByMedicationId(UUID medicationId, Pageable pageable);

    List<Inventory> findByMedicationIdAndStatusNot(UUID medicationId, InventoryStatus excludeStatus);

    @Query("SELECT i FROM Inventory i WHERE i.medication.id = :medicationId AND i.status = 'AVAILABLE' AND i.expiryDate > :today ORDER BY i.expiryDate ASC")
    List<Inventory> findAvailableByMedicationId(@Param("medicationId") UUID medicationId, @Param("today") LocalDate today);

    @Query("SELECT i FROM Inventory i WHERE i.expiryDate <= :expiryDate AND i.status != 'EXPIRED'")
    List<Inventory> findExpiredOrExpiringSoon(@Param("expiryDate") LocalDate expiryDate);

    @Query("SELECT i FROM Inventory i WHERE i.quantity - COALESCE(i.reservedQuantity, 0) <= i.medication.reorderLevel AND i.status NOT IN ('EXPIRED', 'OUT_OF_STOCK')")
    List<Inventory> findLowStockItems();

    Page<Inventory> findByStatus(InventoryStatus status, Pageable pageable);

    @Query("SELECT SUM(i.quantity - COALESCE(i.reservedQuantity, 0)) FROM Inventory i WHERE i.medication.id = :medicationId AND i.status = 'AVAILABLE' AND i.expiryDate > :today")
    Integer getTotalAvailableQuantity(@Param("medicationId") UUID medicationId, @Param("today") LocalDate today);

    @Query("SELECT COUNT(DISTINCT i.medication.id) FROM Inventory i WHERE i.quantity - COALESCE(i.reservedQuantity, 0) <= i.medication.reorderLevel AND i.status = 'AVAILABLE'")
    long countLowStockMedications();

    @Query("SELECT COUNT(i) FROM Inventory i WHERE i.expiryDate <= :expiryDate AND i.status != 'EXPIRED'")
    long countExpiringItems(@Param("expiryDate") LocalDate expiryDate);
}

