package com.yottaflex.orchestrator.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "offboarding_cases")
public class OffboardingCase {

    @Id
    @Column(name = "id", length = 32, nullable = false)
    private String id; // Format: OFF-2026-000001

    @Column(name = "employee_id", length = 32, nullable = false, unique = true)
    private String employeeId;

    @Column(name = "termination_type", length = 32, nullable = false)
    private String terminationType; // VOLUNTARY, INVOLUNTARY, CONTRACT_END, URGENT_TERMINATION

    @Column(name = "status", length = 24, nullable = false)
    private String status; // INITIATED, IN_PROGRESS, ASSETS_RETURNED, ACCESS_REVOKED, COMPLETED, FLAGGED_EXCEPTION

    @Column(name = "last_working_day", nullable = false)
    private LocalDate lastWorkingDay;

    @Column(name = "hr_owner_id", length = 32, nullable = false)
    private String hrOwnerId;

    @Column(name = "it_owner_id", length = 32, nullable = false)
    private String itOwnerId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    public OffboardingCase() {
    }

    public OffboardingCase(String id, String employeeId, String terminationType, String status,
                           LocalDate lastWorkingDay, String hrOwnerId, String itOwnerId,
                           LocalDateTime createdAt, LocalDateTime completedAt) {
        this.id = id;
        this.employeeId = employeeId;
        this.terminationType = terminationType;
        this.status = status;
        this.lastWorkingDay = lastWorkingDay;
        this.hrOwnerId = hrOwnerId;
        this.itOwnerId = itOwnerId;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
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

    public String getTerminationType() {
        return terminationType;
    }

    public void setTerminationType(String terminationType) {
        this.terminationType = terminationType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}

