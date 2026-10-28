package com.yottaflex.orchestrator.service;

import com.yottaflex.orchestrator.dto.AccessGrantRequest;
import com.yottaflex.orchestrator.exception.ResourceNotFoundException;
import com.yottaflex.orchestrator.exception.WorkflowValidationException;
import com.yottaflex.orchestrator.model.AccessGrant;
import com.yottaflex.orchestrator.model.AccessRole;
import com.yottaflex.orchestrator.model.Employee;
import com.yottaflex.orchestrator.repository.AccessGrantRepository;
import com.yottaflex.orchestrator.repository.AccessRoleRepository;
import com.yottaflex.orchestrator.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AccessProvisioningService {

    private final AccessGrantRepository accessGrantRepository;
    private final AccessRoleRepository accessRoleRepository;
    private final EmployeeRepository employeeRepository;
    private final AuditService auditService;

    public AccessProvisioningService(AccessGrantRepository accessGrantRepository,
                                     AccessRoleRepository accessRoleRepository,
                                     EmployeeRepository employeeRepository,
                                     AuditService auditService) {
        this.accessGrantRepository = accessGrantRepository;
        this.accessRoleRepository = accessRoleRepository;
        this.employeeRepository = employeeRepository;
        this.auditService = auditService;
    }

    public List<AccessGrant> getGrantsForEmployee(String employeeId) {
        return accessGrantRepository.findByEmployeeId(employeeId);
    }

    public AccessGrant getGrantById(String grantId) {
        return accessGrantRepository.findById(grantId)
                .orElseThrow(() -> new ResourceNotFoundException("Access Grant not found with ID: " + grantId));
    }

    @Transactional
    public AccessGrant requestAccess(AccessGrantRequest request) {
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + request.getEmployeeId()));

        AccessRole role = accessRoleRepository.findById(request.getAccessRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Access Role not found with ID: " + request.getAccessRoleId()));

        String grantId = "GRANT-" + employee.getId() + "-" + UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        boolean autoApprove = !Boolean.TRUE.equals(role.getApprovalRequired());

        AccessGrant grant = new AccessGrant(
                grantId,
                employee.getId(),
                role.getId(),
                autoApprove ? "APPROVED" : "REQUESTED",
                autoApprove ? "AUTO_APPROVED" : "PENDING_APPROVAL",
                request.getRequestedBy() != null ? request.getRequestedBy() : "MANAGER",
                autoApprove ? "SYSTEM_DAEMON" : null,
                null,
                null,
                null
        );

        AccessGrant saved = accessGrantRepository.save(grant);
        auditService.recordEvent(
                "AccessGrant",
                saved.getId(),
                "CREATE",
                request.getRequestedBy(),
                request.getActorRole() != null ? request.getActorRole() : "MANAGER",
                null,
                saved.getStatus(),
                "Access requested for role: " + role.getRoleName() + " in system " + role.getSystemName()
        );
        return saved;
    }

    @Transactional
    public AccessGrant approveAccess(String grantId, String approverId) {
        AccessGrant grant = getGrantById(grantId);
        String oldState = grant.getStatus() + ":" + grant.getApprovalStatus();

        grant.setApprovalStatus("APPROVED");
        grant.setApprovedBy(approverId != null ? approverId : "security.approver@company.internal");
        grant.setStatus("APPROVED");

        AccessGrant updated = accessGrantRepository.save(grant);
        auditService.recordEvent(
                "AccessGrant",
                updated.getId(),
                "APPROVE",
                approverId,
                "SECURITY_ADMIN",
                oldState,
                updated.getStatus() + ":" + updated.getApprovalStatus(),
                "Access grant formally approved for activation."
        );
        return updated;
    }

    @Transactional
    public AccessGrant provisionAccess(String grantId, String operatorId) {
        AccessGrant grant = getGrantById(grantId);
        Employee employee = employeeRepository.findById(grant.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found: " + grant.getEmployeeId()));

        AccessRole role = accessRoleRepository.findById(grant.getAccessRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Access role not found: " + grant.getAccessRoleId()));

        // Business Rule 1: Approval required before provisioning
        if (!"APPROVED".equalsIgnoreCase(grant.getApprovalStatus()) && !"AUTO_APPROVED".equalsIgnoreCase(grant.getApprovalStatus())) {
            throw new WorkflowValidationException("Cannot provision grant " + grantId + " because approval status is: " + grant.getApprovalStatus());
        }

        // Business Rule 2: Active or start date reached
        LocalDate today = LocalDate.now();
        if (employee.getStartDate().isAfter(today) && Boolean.TRUE.equals(role.getRequiresActiveStatus())) {
            throw new WorkflowValidationException("Cannot provision access prior to employee start date (" + employee.getStartDate() + ")");
        }

        // Business Rule 3: Terminated employee cannot receive new provisioning
        if ("TERMINATED".equalsIgnoreCase(employee.getEmploymentStatus())) {
            throw new WorkflowValidationException("Cannot provision access to terminated employee " + employee.getId());
        }

        String oldState = grant.getStatus();
        grant.setStatus("PROVISIONED");
        grant.setProvisionedAt(LocalDateTime.now());

        AccessGrant updated = accessGrantRepository.save(grant);
        auditService.recordEvent(
                "AccessGrant",
                updated.getId(),
                "PROVISION",
                operatorId,
                "IT_ADMIN",
                oldState,
                updated.getStatus(),
                "Entitlement active in target system " + role.getSystemName()
        );
        return updated;
    }

    @Transactional
    public AccessGrant revokeAccess(String grantId, String operatorId, String reason) {
        AccessGrant grant = getGrantById(grantId);
        String oldState = grant.getStatus();

        grant.setStatus("REVOKED");
        grant.setRevokedAt(LocalDateTime.now());

        AccessGrant updated = accessGrantRepository.save(grant);
        auditService.recordEvent(
                "AccessGrant",
                updated.getId(),
                "REVOKE",
                operatorId,
                "SECURITY_ADMIN",
                oldState,
                updated.getStatus(),
                "Access revoked. Reason: " + reason
        );
        return updated;
    }

    @Transactional
    public void revokeAllForTerminatedEmployee(String employeeId, String operatorId) {
        List<AccessGrant> grants = accessGrantRepository.findByEmployeeId(employeeId);
        for (AccessGrant grant : grants) {
            if ("PROVISIONED".equalsIgnoreCase(grant.getStatus()) || "APPROVED".equalsIgnoreCase(grant.getStatus())) {
                revokeAccess(grant.getId(), operatorId, "Automated offboarding exit revocation");
            }
        }
    }
}

