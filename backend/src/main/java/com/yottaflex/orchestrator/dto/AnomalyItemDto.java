package com.yottaflex.orchestrator.dto;

public class AnomalyItemDto {

    private String anomalyId;
    private String issueType;
    private String entityName;
    private String entityId;
    private String secondaryEntityId;
    private String description;
    private String severity; // CRITICAL, HIGH, MEDIUM, LOW
    private String detectionRule;
    private String resolutionRecommendation;
    private boolean isKnownGroundTruth;

    public AnomalyItemDto() {
    }

    public AnomalyItemDto(String anomalyId, String issueType, String entityName, String entityId,
                          String secondaryEntityId, String description, String severity,
                          String detectionRule, String resolutionRecommendation, boolean isKnownGroundTruth) {
        this.anomalyId = anomalyId;
        this.issueType = issueType;
        this.entityName = entityName;
        this.entityId = entityId;
        this.secondaryEntityId = secondaryEntityId;
        this.description = description;
        this.severity = severity;
        this.detectionRule = detectionRule;
        this.resolutionRecommendation = resolutionRecommendation;
        this.isKnownGroundTruth = isKnownGroundTruth;
    }

    public String getAnomalyId() {
        return anomalyId;
    }

    public void setAnomalyId(String anomalyId) {
        this.anomalyId = anomalyId;
    }

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
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

    public String getSecondaryEntityId() {
        return secondaryEntityId;
    }

    public void setSecondaryEntityId(String secondaryEntityId) {
        this.secondaryEntityId = secondaryEntityId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getDetectionRule() {
        return detectionRule;
    }

    public void setDetectionRule(String detectionRule) {
        this.detectionRule = detectionRule;
    }

    public String getResolutionRecommendation() {
        return resolutionRecommendation;
    }

    public void setResolutionRecommendation(String resolutionRecommendation) {
        this.resolutionRecommendation = resolutionRecommendation;
    }

    public boolean isKnownGroundTruth() {
        return isKnownGroundTruth;
    }

    public void setKnownGroundTruth(boolean knownGroundTruth) {
        isKnownGroundTruth = knownGroundTruth;
    }
}

