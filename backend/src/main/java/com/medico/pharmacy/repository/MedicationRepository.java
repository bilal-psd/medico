package com.medico.pharmacy.repository;

import com.medico.pharmacy.domain.Medication;
import com.medico.pharmacy.domain.Medication.MedicationCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MedicationRepository extends JpaRepository<Medication, UUID> {

    Optional<Medication> findByCode(String code);

    boolean existsByCode(String code);

    Page<Medication> findByActiveTrue(Pageable pageable);

    Page<Medication> findByCategoryAndActiveTrue(MedicationCategory category, Pageable pageable);

    @Query("SELECT m FROM Medication m WHERE m.active = true AND " +
           "(LOWER(m.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(m.genericName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(m.code) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Medication> searchMedications(@Param("search") String search, Pageable pageable);

    @Query("SELECT m FROM Medication m WHERE m.active = true AND m.requiresPrescription = false")
    List<Medication> findOverTheCounterMedications();

    @Query("SELECT COUNT(m) FROM Medication m WHERE m.active = true")
    long countActiveMedications();
}

