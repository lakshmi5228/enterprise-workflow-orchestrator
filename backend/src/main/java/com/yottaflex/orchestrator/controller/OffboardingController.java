package com.yottaflex.orchestrator.controller;

import com.yottaflex.orchestrator.dto.InitiateOffboardingRequest;
import com.yottaflex.orchestrator.exception.ResourceNotFoundException;
import com.yottaflex.orchestrator.model.Employee;
import com.yottaflex.orchestrator.model.OffboardingCase;
import com.yottaflex.orchestrator.repository.EmployeeRepository;
import com.yottaflex.orchestrator.repository.OffboardingCaseRepository;
import com.yottaflex.orchestrator.service.AccessProvisioningService;
import com.yottaflex.orchestrator.service.AuditService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/offboarding")
@CrossOrigin(origins = "*")
public class OffboardingController {

    private final OffboardingCaseRepository offboardingCaseRepository;
    private final EmployeeRepository employeeRepository;
    private final AccessProvisioningService accessProvisioningService;
    private final AuditService auditService;

    public OffboardingController(OffboardingCaseRepository offboardingCaseRepository,
                                 EmployeeRepository employeeRepository,
                                 AccessProvisioningService accessProvisioningService,
                                 AuditService auditService) {
        this.offboardingCaseRepository = offboardingCaseRepository;
        this.employeeRepository = employeeRepository;
        this.accessProvisioningService = accessProvisioningService;
        this.auditService = auditService;
    }

    @GetMapping
    public ResponseEntity<Page<OffboardingCase>> listCases(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(required = false) String search) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("lastWorkingDay").descending());
        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(offboardingCaseRepository
                    .findByStatusContainingIgnoreCaseOrTerminationTypeContainingIgnoreCase(search, search, pageRequest));
        }
        return ResponseEntity.ok(offboardingCaseRepository.findAll(pageRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OffboardingCase> getById(@PathVariable String id) {
        return offboardingCaseRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Offboarding case not found: " + id));
    }

    @PostMapping("/initiate")
    @Transactional
    public ResponseEntity<OffboardingCase> initiateOffboarding(@RequestBody InitiateOffboardingRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + request.getEmployeeId()));

        String offId = "OFF-2026-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        LocalDate lwd = request.getLastWorkingDay() != null ? request.getLastWorkingDay() : LocalDate.now();

        // Update employee status
        employee.setEmploymentStatus("TERMINATED".equalsIgnoreCase(request.getTerminationType()) || "URGENT_TERMINATION".equalsIgnoreCase(request.getTerminationType()) ? "TERMINATED" : "NOTICE_PERIOD");
        employee.setEndDate(lwd);
        employeeRepository.save(employee);

        OffboardingCase offCase = new OffboardingCase(
                offId,
                employee.getId(),
                request.getTerminationType(),
                "URGENT_TERMINATION".equalsIgnoreCase(request.getTerminationType()) ? "ACCESS_REVOKED" : "INITIATED",
                lwd,
                request.getHrOwnerId() != null ? request.getHrOwnerId() : "EMP-000006",
                request.getItOwnerId() != null ? request.getItOwnerId() : "EMP-000008",
                LocalDateTime.now(),
                null
        );

        OffboardingCase saved = offboardingCaseRepository.save(offCase);

        // Immediate revocation for urgent termination
        if ("URGENT_TERMINATION".equalsIgnoreCase(request.getTerminationType())) {
            accessProvisioningService.revokeAllForTerminatedEmployee(employee.getId(), "IT_SECURITY_DAEMON");
        }

        auditService.recordEvent(
                "OffboardingCase",
                saved.getId(),
                "CREATE",
                request.getHrOwnerId(),
                "HR_PARTNER",
                null,
                saved.getStatus(),
                "Initiated " + request.getTerminationType() + " for employee " + employee.getFullName() + ". Reason: " + request.getReason()
        );

        return ResponseEntity.ok(saved);
    }

    @PostMapping("/{id}/complete")
    @Transactional
    public ResponseEntity<OffboardingCase> completeOffboarding(@PathVariable String id) {
        OffboardingCase offCase = offboardingCaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found: " + id));

        String oldState = offCase.getStatus();
        offCase.setStatus("COMPLETED");
        offCase.setCompletedAt(LocalDateTime.now());

        // Revoke any lingering grants
        accessProvisioningService.revokeAllForTerminatedEmployee(offCase.getEmployeeId(), "SYSTEM_DAEMON");

        OffboardingCase updated = offboardingCaseRepository.save(offCase);
        auditService.recordEvent(
                "OffboardingCase",
                updated.getId(),
                "COMPLETE",
                "SYSTEM_DAEMON",
                "HR_PARTNER",
                oldState,
                updated.getStatus(),
                "Offboarding checklist completed. All physical assets recovered and digital access revoked."
        );
        return ResponseEntity.ok(updated);
    }
}

