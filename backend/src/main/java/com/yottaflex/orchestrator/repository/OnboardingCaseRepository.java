package com.yottaflex.orchestrator.repository;

import com.yottaflex.orchestrator.model.OnboardingCase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface OnboardingCaseRepository extends JpaRepository<OnboardingCase, String> {

    Optional<OnboardingCase> findByEmployeeId(String employeeId);

    List<OnboardingCase> findByStatus(String status);

    List<OnboardingCase> findByStage(String stage);

    Page<OnboardingCase> findByCandidateNameContainingIgnoreCaseOrStatusContainingIgnoreCase(
            String candidateName, String status, Pageable pageable);

    @Query("SELECT COUNT(o) FROM OnboardingCase o WHERE o.status = 'IN_PROGRESS'")
    long countInProgressCases();

    @Query("SELECT COUNT(o) FROM OnboardingCase o WHERE o.status = 'BLOCKED' OR o.status = 'EXCEPTION'")
    long countBlockedCases();
}

