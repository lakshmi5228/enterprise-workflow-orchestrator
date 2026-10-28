package com.yottaflex.orchestrator.service;

import com.yottaflex.orchestrator.model.AuditEvent;
import com.yottaflex.orchestrator.repository.AuditEventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuditService {

    private final AuditEventRepository auditEventRepository;

    public AuditService(AuditEventRepository auditEventRepository) {
        this.auditEventRepository = auditEventRepository;
    }

    @Transactional
    public void recordEvent(String entityName, String entityId, String actionType,
                            String actorId, String actorRole, String oldState,
                            String newState, String details) {
        String auditId = "AUD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        AuditEvent event = new AuditEvent(
                auditId,
                entityName,
                entityId,
                actionType,
                actorId != null ? actorId : "SYSTEM",
                actorRole != null ? actorRole : "SYSTEM_DAEMON",
                oldState,
                newState,
                LocalDateTime.now(),
                details
        );
        auditEventRepository.save(event);
    }
}

