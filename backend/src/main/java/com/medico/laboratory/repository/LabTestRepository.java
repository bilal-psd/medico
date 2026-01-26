package com.medico.laboratory.repository;

import com.medico.laboratory.domain.LabTest;
import com.medico.laboratory.domain.LabTest.LabTestCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface LabTestRepository extends JpaRepository<LabTest, UUID> {

    Optional<LabTest> findByCode(String code);

    boolean existsByCode(String code);

    Page<LabTest> findByActiveTrue(Pageable pageable);

    Page<LabTest> findByCategoryAndActiveTrue(LabTestCategory category, Pageable pageable);

    @Query("SELECT lt FROM LabTest lt WHERE lt.active = true AND " +
           "(LOWER(lt.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(lt.code) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<LabTest> searchLabTests(@Param("search") String search, Pageable pageable);

    @Query("SELECT COUNT(lt) FROM LabTest lt WHERE lt.active = true")
    long countActiveLabTests();
}

