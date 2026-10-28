package com.yottaflex.orchestrator.controller;

import com.yottaflex.orchestrator.model.AuditEvent;
import com.yottaflex.orchestrator.repository.AuditEventRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/audit-events")
@CrossOrigin(origins = "*")
public class AuditController {

    private final AuditEventRepository auditEventRepository;

    public AuditController(AuditEventRepository auditEventRepository) {
        this.auditEventRepository = auditEventRepository;
    }

    @GetMapping
    public ResponseEntity<Page<AuditEvent>> listAuditEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(required = false) String search) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("timestamp").descending());
        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(auditEventRepository
                    .findByEntityNameContainingIgnoreCaseOrActionTypeContainingIgnoreCase(search, search, pageRequest));
        }
        return ResponseEntity.ok(auditEventRepository.findAll(pageRequest));
    }

    @GetMapping("/entity/{entityId}")
    public ResponseEntity<List<AuditEvent>> getByEntityId(@PathVariable String entityId) {
        return ResponseEntity.ok(auditEventRepository.findByEntityIdOrderByTimestampDesc(entityId));
    }
}

