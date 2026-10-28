package com.yottaflex.orchestrator.repository;

import com.yottaflex.orchestrator.model.CaseTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CaseTaskRepository extends JpaRepository<CaseTask, String> {

    List<CaseTask> findByCaseId(String caseId);

    List<CaseTask> findByStatus(String status);

    List<CaseTask> findByAssignedRole(String assignedRole);

    List<CaseTask> findByCategory(String category);

    Page<CaseTask> findByStatusOrCategory(String status, String category, Pageable pageable);

    @Query("SELECT COUNT(t) FROM CaseTask t WHERE t.status = 'COMPLETED'")
    long countCompletedTasks();

    @Query("SELECT COUNT(t) FROM CaseTask t WHERE t.status IN ('PENDING', 'READY', 'IN_PROGRESS', 'WAITING_APPROVAL')")
    long countPendingTasks();

    @Query("SELECT COUNT(t) FROM CaseTask t WHERE t.isException = true OR t.status = 'FAILED'")
    long countExceptionTasks();
}

