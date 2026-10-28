package com.yottaflex.orchestrator.dto;

public class AccessGrantRequest {

    private String employeeId;
    private String accessRoleId;
    private String requestedBy;
    private String actorRole;
    private String rationale;

    public AccessGrantRequest() {
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

    public String getRequestedBy() {
        return requestedBy;
    }

    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }

    public String getActorRole() {
        return actorRole;
    }

    public void setActorRole(String actorRole) {
        this.actorRole = actorRole;
    }

    public String getRationale() {
        return rationale;
    }

    public void setRationale(String rationale) {
        this.rationale = rationale;
    }
}

