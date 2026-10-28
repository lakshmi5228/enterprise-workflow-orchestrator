package com.yottaflex.orchestrator.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "onboarding_cases")
public class OnboardingCase {

    @Id
    @Column(name = "id", length = 32, nullable = false)
    private String id; // Format: ONB-2026-000001

    @Column(name = "employee_id", length = 32, nullable = false, unique = true)
    private String employeeId;

    @Column(name = "candidate_name", length = 128, nullable = false)
    private String candidateName;

    @Column(name = "stage", length = 32, nullable = false)
    private String stage; // PREBOARDING, IT_PROVISIONING, ASSET_DELIVERY, ORIENTATION, COMPLETED, CANCELLED

    @Column(name = "status", length = 24, nullable = false)
    private String status; // PENDING, IN_PROGRESS, COMPLETED, BLOCKED, EXCEPTION

    @Column(name = "hr_partner_id", length = 32, nullable = false)
    private String hrPartnerId;

    @Column(name = "manager_id", length = 32, nullable = false)
    private String managerId;

    @Column(name = "scheduled_start_date", nullable = false)
    private LocalDate scheduledStartDate;

    @Column(name = "actual_start_date")
    private LocalDate actualStartDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public OnboardingCase() {
    }

    public OnboardingCase(String id, String employeeId, String candidateName, String stage, String status,
                          String hrPartnerId, String managerId, LocalDate scheduledStartDate,
                          LocalDate actualStartDate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.employeeId = employeeId;
        this.candidateName = candidateName;
        this.stage = stage;
        this.status = status;
        this.hrPartnerId = hrPartnerId;
        this.managerId = managerId;
        this.scheduledStartDate = scheduledStartDate;
        this.actualStartDate = actualStartDate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHrPartnerId() {
        return hrPartnerId;
    }

    public void setHrPartnerId(String hrPartnerId) {
        this.hrPartnerId = hrPartnerId;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    public LocalDate getScheduledStartDate() {
        return scheduledStartDate;
    }

    public void setScheduledStartDate(LocalDate scheduledStartDate) {
        this.scheduledStartDate = scheduledStartDate;
    }

    public LocalDate getActualStartDate() {
        return actualStartDate;
    }

    public void setActualStartDate(LocalDate actualStartDate) {
        this.actualStartDate = actualStartDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

