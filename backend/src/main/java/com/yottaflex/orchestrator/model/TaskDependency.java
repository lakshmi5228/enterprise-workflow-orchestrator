package com.yottaflex.orchestrator.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "task_dependencies")
public class TaskDependency {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "task_id", length = 64, nullable = false)
    private String taskId;

    @Column(name = "prerequisite_task_id", length = 64, nullable = false)
    private String prerequisiteTaskId;

    @Column(name = "dependency_type", length = 32, nullable = false)
    private String dependencyType; // FINISH_TO_START, APPROVAL_REQUIRED

    @Column(name = "is_mandatory", nullable = false)
    private Boolean isMandatory = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public TaskDependency() {
    }

    public TaskDependency(Long id, String taskId, String prerequisiteTaskId, String dependencyType,
                          Boolean isMandatory, LocalDateTime createdAt) {
        this.id = id;
        this.taskId = taskId;
        this.prerequisiteTaskId = prerequisiteTaskId;
        this.dependencyType = dependencyType;
        this.isMandatory = isMandatory != null ? isMandatory : true;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getPrerequisiteTaskId() {
        return prerequisiteTaskId;
    }

    public void setPrerequisiteTaskId(String prerequisiteTaskId) {
        this.prerequisiteTaskId = prerequisiteTaskId;
    }

    public String getDependencyType() {
        return dependencyType;
    }

    public void setDependencyType(String dependencyType) {
        this.dependencyType = dependencyType;
    }

    public Boolean getIsMandatory() {
        return isMandatory;
    }

    public void setIsMandatory(Boolean mandatory) {
        isMandatory = mandatory;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

