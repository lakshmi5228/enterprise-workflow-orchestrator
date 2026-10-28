package com.yottaflex.orchestrator.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "access_grants")
public class AccessGrant {

    @Id
    @Column(name = "id", length = 64, nullable = false)
    private String id; // Format: GRANT-EMP-000001-001

    @Column(name = "employee_id", length = 32, nullable = false)
    private String employeeId;

    @Column(name = "access_role_id", length = 32, nullable = false)
    private String accessRoleId;

    @Column(name = "status", length = 24, nullable = false)
    private String status; // REQUESTED, APPROVED, PROVISIONED, REVOKED, EXPIRED, SUSPENDED

    @Column(name = "approval_status", length = 24, nullable = false)
    private String approvalStatus; // PENDING_APPROVAL, APPROVED, REJECTED, AUTO_APPROVED

    @Column(name = "requested_by", length = 64, nullable = false)
    private String requestedBy;

    @Column(name = "approved_by", length = 64)
    private String approvedBy;

    @Column(name = "provisioned_at")
    private LocalDateTime provisionedAt;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Column(name = "issue_flag", length = 64)
    private String issueFlag;

    public AccessGrant() {
    }

    public AccessGrant(String id, String employeeId, String accessRoleId, String status,
                       String approvalStatus, String requestedBy, String approvedBy,
                       LocalDateTime provisionedAt, LocalDateTime revokedAt, String issueFlag) {
        this.id = id;
        this.employeeId = employeeId;
        this.accessRoleId = accessRoleId;
        this.status = status;
        this.approvalStatus = approvalStatus;
        this.requestedBy = requestedBy;
        this.approvedBy = approvedBy;
        this.provisionedAt = provisionedAt;
        this.revokedAt = revokedAt;
        this.issueFlag = issueFlag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getAccessRoleId() {
        return accessRoleId;
    }

    public void setAccessRoleId(String accessRoleId) {
        this.accessRoleId = accessRoleId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public LocalDateTime getProvisionedAt() {
        return provisionedAt;
    }

    public void setProvisionedAt(LocalDateTime provisionedAt) {
        this.provisionedAt = provisionedAt;
    }

    public LocalDateTime getRevokedAt() {
        return revokedAt;
    }

    public void setRevokedAt(LocalDateTime revokedAt) {
        this.revokedAt = revokedAt;
    }

    public String getIssueFlag() {
        return issueFlag;
    }

    public void setIssueFlag(String issueFlag) {
        this.issueFlag = issueFlag;
    }
}

