package com.yottaflex.orchestrator.dto;

import java.time.LocalDate;

public class InitiateOffboardingRequest {

    private String employeeId;
    private String terminationType; // VOLUNTARY, INVOLUNTARY, CONTRACT_END, URGENT_TERMINATION
    private LocalDate lastWorkingDay;
    private String hrOwnerId;
    private String itOwnerId;
    private String reason;

    public InitiateOffboardingRequest() {
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getTerminationType() {
        return terminationType;
    }

    public void setTerminationType(String terminationType) {
        this.terminationType = terminationType;
    }

    public LocalDate getLastWorkingDay() {
        return lastWorkingDay;
    }

    public void setLastWorkingDay(LocalDate lastWorkingDay) {
        this.lastWorkingDay = lastWorkingDay;
    }

    public String getHrOwnerId() {
        return hrOwnerId;
    }

    public void setHrOwnerId(String hrOwnerId) {
        this.hrOwnerId = hrOwnerId;
    }

    public String getItOwnerId() {
        return itOwnerId;
    }

    public void setItOwnerId(String itOwnerId) {
        this.itOwnerId = itOwnerId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}

