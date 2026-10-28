package com.yottaflex.orchestrator.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "assets")
public class Asset {

    @Id
    @Column(name = "id", length = 32, nullable = false)
    private String id; // Format: AST-000001

    @Column(name = "asset_tag", length = 32, nullable = false)
    private String assetTag;

    @Column(name = "asset_type", length = 32, nullable = false)
    private String assetType; // LAPTOP, MONITOR, SECURITY_KEY, MOBILE_DEVICE, ACCESS_BADGE

    @Column(name = "model", length = 100, nullable = false)
    private String model;

    @Column(name = "serial_number", length = 64, nullable = false)
    private String serialNumber;

    @Column(name = "status", length = 24, nullable = false)
    private String status; // IN_STOCK, ASSIGNED, IN_TRANSIT, DELIVERED, ACKNOWLEDGED, RETURN_REQUESTED, RECOVERED, RETIRED

    @Column(name = "assigned_to_employee_id", length = 32)
    private String assignedToEmployeeId;

    @Column(name = "assigned_date")
    private LocalDate assignedDate;

    @Column(name = "acknowledged_at")
    private LocalDateTime acknowledgedAt;

    @Column(name = "recovered_at")
    private LocalDateTime recoveredAt;

    @Column(name = "condition_status", length = 24, nullable = false)
    private String conditionStatus = "GOOD"; // NEW, GOOD, DAMAGED, SCRAPPED

    public Asset() {
    }

    public Asset(String id, String assetTag, String assetType, String model, String serialNumber,
                 String status, String assignedToEmployeeId, LocalDate assignedDate,
                 LocalDateTime acknowledgedAt, LocalDateTime recoveredAt, String conditionStatus) {
        this.id = id;
        this.assetTag = assetTag;
        this.assetType = assetType;
        this.model = model;
        this.serialNumber = serialNumber;
        this.status = status;
        this.assignedToEmployeeId = assignedToEmployeeId;
        this.assignedDate = assignedDate;
        this.acknowledgedAt = acknowledgedAt;
        this.recoveredAt = recoveredAt;
        this.conditionStatus = conditionStatus != null ? conditionStatus : "GOOD";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAssetTag() {
        return assetTag;
    }

    public void setAssetTag(String assetTag) {
        this.assetTag = assetTag;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAssignedToEmployeeId() {
        return assignedToEmployeeId;
    }

    public void setAssignedToEmployeeId(String assignedToEmployeeId) {
        this.assignedToEmployeeId = assignedToEmployeeId;
    }

    public LocalDate getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(LocalDate assignedDate) {
        this.assignedDate = assignedDate;
    }

    public LocalDateTime getAcknowledgedAt() {
        return acknowledgedAt;
    }

    public void setAcknowledgedAt(LocalDateTime acknowledgedAt) {
        this.acknowledgedAt = acknowledgedAt;
    }

    public LocalDateTime getRecoveredAt() {
        return recoveredAt;
    }

    public void setRecoveredAt(LocalDateTime recoveredAt) {
        this.recoveredAt = recoveredAt;
    }

    public String getConditionStatus() {
        return conditionStatus;
    }

    public void setConditionStatus(String conditionStatus) {
        this.conditionStatus = conditionStatus;
    }
}

