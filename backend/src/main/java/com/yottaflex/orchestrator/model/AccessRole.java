package com.yottaflex.orchestrator.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "access_roles")
public class AccessRole {

    @Id
    @Column(name = "id", length = 32, nullable = false)
    private String id; // Format: ACC-ROLE-001

    @Column(name = "system_name", length = 64, nullable = false)
    private String systemName;

    @Column(name = "role_name", length = 64, nullable = false)
    private String roleName;

    @Column(name = "access_level", length = 24, nullable = false)
    private String accessLevel; // READ_ONLY, STANDARD_USER, POWER_USER, ADMIN, BILLING_ADMIN

    @Column(name = "approval_required", nullable = false)
    private Boolean approvalRequired = false;

    @Column(name = "requires_active_status", nullable = false)
    private Boolean requiresActiveStatus = true;

    public AccessRole() {
    }

    public AccessRole(String id, String systemName, String roleName, String accessLevel,
                      Boolean approvalRequired, Boolean requiresActiveStatus) {
        this.id = id;
        this.systemName = systemName;
        this.roleName = roleName;
        this.accessLevel = accessLevel;
        this.approvalRequired = approvalRequired != null ? approvalRequired : false;
        this.requiresActiveStatus = requiresActiveStatus != null ? requiresActiveStatus : true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(String accessLevel) {
        this.accessLevel = accessLevel;
    }

    public Boolean getApprovalRequired() {
        return approvalRequired;
    }

    public void setApprovalRequired(Boolean approvalRequired) {
        this.approvalRequired = approvalRequired;
    }

    public Boolean getRequiresActiveStatus() {
        return requiresActiveStatus;
    }

    public void setRequiresActiveStatus(Boolean requiresActiveStatus) {
        this.requiresActiveStatus = requiresActiveStatus;
    }
}

