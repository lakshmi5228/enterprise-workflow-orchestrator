package com.yottaflex.orchestrator.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ReconciliationReportDto {

    private LocalDateTime scanTimestamp;
    private long totalRecordsScanned;
    private long totalAnomaliesDetected;
    private long knownGroundTruthCount;
    private double precision;
    private double recall;
    private double f1Score;
    private double reconciliationHealthScore; // 100% - anomaly penalty

    private Map<String, Long> anomaliesByType;
    private Map<String, Long> anomaliesBySeverity;
    private List<AnomalyItemDto> anomalyDetails;

    public ReconciliationReportDto() {
    }

    public LocalDateTime getScanTimestamp() {
        return scanTimestamp;
    }

    public void setScanTimestamp(LocalDateTime scanTimestamp) {
        this.scanTimestamp = scanTimestamp;
    }

    public long getTotalRecordsScanned() {
        return totalRecordsScanned;
    }

    public void setTotalRecordsScanned(long totalRecordsScanned) {
        this.totalRecordsScanned = totalRecordsScanned;
    }

    public long getTotalAnomaliesDetected() {
        return totalAnomaliesDetected;
    }

    public void setTotalAnomaliesDetected(long totalAnomaliesDetected) {
        this.totalAnomaliesDetected = totalAnomaliesDetected;
    }

    public long getKnownGroundTruthCount() {
        return knownGroundTruthCount;
    }

    public void setKnownGroundTruthCount(long knownGroundTruthCount) {
        this.knownGroundTruthCount = knownGroundTruthCount;
    }

    public double getPrecision() {
        return precision;
    }

    public void setPrecision(double precision) {
        this.precision = precision;
    }

    public double getRecall() {
        return recall;
    }

    public void setRecall(double recall) {
        this.recall = recall;
    }

    public double getF1Score() {
        return f1Score;
    }

    public void setF1Score(double f1Score) {
        this.f1Score = f1Score;
    }

    public double getReconciliationHealthScore() {
        return reconciliationHealthScore;
    }

    public void setReconciliationHealthScore(double reconciliationHealthScore) {
        this.reconciliationHealthScore = reconciliationHealthScore;
    }

    public Map<String, Long> getAnomaliesByType() {
        return anomaliesByType;
    }

    public void setAnomaliesByType(Map<String, Long> anomaliesByType) {
        this.anomaliesByType = anomaliesByType;
    }

    public Map<String, Long> getAnomaliesBySeverity() {
        return anomaliesBySeverity;
    }

    public void setAnomaliesBySeverity(Map<String, Long> anomaliesBySeverity) {
        this.anomaliesBySeverity = anomaliesBySeverity;
    }

    public List<AnomalyItemDto> getAnomalyDetails() {
        return anomalyDetails;
    }

    public void setAnomalyDetails(List<AnomalyItemDto> anomalyDetails) {
        this.anomalyDetails = anomalyDetails;
    }
}

