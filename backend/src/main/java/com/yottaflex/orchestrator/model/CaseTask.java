package com.yottaflex.orchestrator.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "case_tasks")
public class CaseTask {

    @Id
    @Column(name = "id", length = 64, nullable = false)
    private String id; // Format: TASK-ONB-2026-000001-001

    @Column(name = "case_id", length = 32, nullable = false)
    private String caseId;

    @Column(name = "task_name", length = 128, nullable = false)
    private String taskName;

    @Column(name = "category", length = 32, nullable = false)
    private String category; // HR_PREBOARDING, IT_ACCESS, EQUIPMENT_DELIVERY, ORIENTATION, IT_REVOCATION, ASSET_RECOVERY

    @Column(name = "assigned_role", length = 32, nullable = false)
    private String assignedRole; // HR_PARTNER, IT_OPERATIONS, FACILITIES, MANAGER, EMPLOYEE

    @Column(name = "assigned_to_user", length = 64)
    private String assignedToUser;

    @Column(name = "status", length = 24, nullable = false)
    private String status; // PENDING, READY, IN_PROGRESS, WAITING_APPROVAL, COMPLETED, FAILED, OVERRIDDEN

    @Column(name = "priority", length = 16, nullable = false)
    private String priority; // LOW, MEDIUM, HIGH, URGENT

    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "is_exception", nullable = false)
    private Boolean isException = false;

    public CaseTask() {
    }

    public CaseTask(String id, String caseId, String taskName, String category, String assignedRole,
                    String assignedToUser, String status, String priority, LocalDate dueDate,
                    LocalDateTime completedAt, String notes, Boolean isException) {
        this.id = id;
        this.caseId = caseId;
        this.taskName = taskName;
        this.category = category;
        this.assignedRole = assignedRole;
        this.assignedToUser = assignedToUser;
        this.status = status;
        this.priority = priority;
        this.dueDate = dueDate;
        this.completedAt = completedAt;
        this.notes = notes;
        this.isException = isException != null ? isException : false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCaseId() {
        return caseId;
    }

    public void setCaseId(String caseId) {
        this.caseId = caseId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAssignedRole() {
        return assignedRole;
    }

    public void setAssignedRole(String assignedRole) {
        this.assignedRole = assignedRole;
    }

    public String getAssignedToUser() {
        return assignedToUser;
    }

    public void setAssignedToUser(String assignedToUser) {
        this.assignedToUser = assignedToUser;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Boolean getIsException() {
        return isException;
    }

    public void setIsException(Boolean exception) {
        isException = exception;
    }
}

