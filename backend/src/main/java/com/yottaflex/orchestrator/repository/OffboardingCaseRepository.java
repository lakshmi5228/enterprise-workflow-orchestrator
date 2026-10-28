package com.yottaflex.orchestrator.repository;

import com.yottaflex.orchestrator.model.OffboardingCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OffboardingCaseRepository extends JpaRepository<OffboardingCase, String> {

    Optional<OffboardingCase> findByEmployeeId(String employeeId);

    List<OffboardingCase> findByStatus(String status);

    List<OffboardingCase> findByTerminationType(String terminationType);

    Page<OffboardingCase> findByStatusContainingIgnoreCaseOrTerminationTypeContainingIgnoreCase(
            String status, String terminationType, Pageable pageable);

    @Query("SELECT COUNT(o) FROM OffboardingCase o WHERE o.status = 'IN_PROGRESS' OR o.status = 'INITIATED'")
    long countActiveOffboardingCases();

    @Query("SELECT COUNT(o) FROM OffboardingCase o WHERE o.terminationType = 'URGENT_TERMINATION'")
    long countUrgentTerminations();
}

