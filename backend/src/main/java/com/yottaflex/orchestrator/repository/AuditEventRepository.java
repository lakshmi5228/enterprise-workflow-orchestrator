package com.yottaflex.orchestrator.repository;

import com.yottaflex.orchestrator.model.AuditEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AuditEventRepository extends JpaRepository<AuditEvent, String> {

    List<AuditEvent> findByEntityIdOrderByTimestampDesc(String entityId);

    List<AuditEvent> findByEntityNameOrderByTimestampDesc(String entityName);

    Page<AuditEvent> findByEntityNameContainingIgnoreCaseOrActionTypeContainingIgnoreCase(
            String entityName, String actionType, Pageable pageable);
}

