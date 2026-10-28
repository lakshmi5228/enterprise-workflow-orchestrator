package com.yottaflex.orchestrator.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_events")
public class AuditEvent {

    @Id
    @Column(name = "id", length = 64, nullable = false)
    private String id; // Format: AUD-00000001

    @Column(name = "entity_name", length = 32, nullable = false)
    private String entityName;

    @Column(name = "entity_id", length = 64, nullable = false)
    private String entityId;

    @Column(name = "action_type", length = 32, nullable = false)
    private String actionType; // CREATE, UPDATE, APPROVE, PROVISION, REASSIGN, REVOKE, OVERRIDE, DETECT_ANOMALY

    @Column(name = "actor_id", length = 64, nullable = false)
    private String actorId;

    @Column(name = "actor_role", length = 32, nullable = false)
    private String actorRole;

    @Column(name = "old_state", columnDefinition = "TEXT")
    private String oldState;

    @Column(name = "new_state", columnDefinition = "TEXT")
    private String newState;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "details", columnDefinition = "TEXT")
    private String details;

    public AuditEvent() {
    }

    public AuditEvent(String id, String entityName, String entityId, String actionType,
                      String actorId, String actorRole, String oldState, String newState,
                      LocalDateTime timestamp, String details) {
        this.id = id;
        this.entityName = entityName;
        this.entityId = entityId;
        this.actionType = actionType;
        this.actorId = actorId;
        this.actorRole = actorRole;
        this.oldState = oldState;
        this.newState = newState;
        this.timestamp = timestamp;
        this.details = details;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getActorId() {
        return actorId;
    }

    public void setActorId(String actorId) {
        this.actorId = actorId;
    }

    public String getActorRole() {
        return actorRole;
    }

    public void setActorRole(String actorRole) {
        this.actorRole = actorRole;
    }

    public String getOldState() {
        return oldState;
    }

    public void setOldState(String oldState) {
        this.oldState = oldState;
    }

    public String getNewState() {
        return newState;
    }

    public void setNewState(String newState) {
        this.newState = newState;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}

