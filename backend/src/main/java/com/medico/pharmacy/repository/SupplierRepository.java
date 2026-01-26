package com.medico.pharmacy.repository;

import com.medico.pharmacy.domain.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, UUID> {

    Optional<Supplier> findByCode(String code);

    boolean existsByCode(String code);

    Page<Supplier> findByActiveTrue(Pageable pageable);

    @Query("SELECT s FROM Supplier s WHERE s.active = true AND " +
           "(LOWER(s.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(s.code) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Supplier> searchSuppliers(@Param("search") String search, Pageable pageable);

    @Query("SELECT COUNT(s) FROM Supplier s WHERE s.active = true")
    long countActiveSuppliers();
}

