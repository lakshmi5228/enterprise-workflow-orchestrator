package com.yottaflex.orchestrator.dto;

import java.util.Map;

public class DashboardMetricsDto {

    private long totalEmployees;
    private long activeEmployees;
    private long preboardingEmployees;
    private long terminatedEmployees;

    private long activeOnboardingCases;
    private long blockedOnboardingCases;

    private long pendingTasks;
    private long completedTasks;
    private long exceptionTasks;

    private long provisionedGrants;
    private long pendingApprovalGrants;

    private long deployedAssets;
    private long inStockAssets;
    private long returnRequestedAssets;

    private long activeOffboardingCases;
    private long urgentTerminations;

    private double reconciliationHealthScore; // 0 - 100%
    private long detectedAnomaliesCount;

    private Map<String, Long> tasksByRole;
    private Map<String, Long> assetsByType;

    public DashboardMetricsDto() {
    }

    public long getTotalEmployees() {
        return totalEmployees;
    }

    public void setTotalEmployees(long totalEmployees) {
        this.totalEmployees = totalEmployees;
    }

    public long getActiveEmployees() {
        return activeEmployees;
    }

    public void setActiveEmployees(long activeEmployees) {
        this.activeEmployees = activeEmployees;
    }

    public long getPreboardingEmployees() {
        return preboardingEmployees;
    }

    public void setPreboardingEmployees(long preboardingEmployees) {
        this.preboardingEmployees = preboardingEmployees;
    }

    public long getTerminatedEmployees() {
        return terminatedEmployees;
    }

    public void setTerminatedEmployees(long terminatedEmployees) {
        this.terminatedEmployees = terminatedEmployees;
    }

    public long getActiveOnboardingCases() {
        return activeOnboardingCases;
    }

    public void setActiveOnboardingCases(long activeOnboardingCases) {
        this.activeOnboardingCases = activeOnboardingCases;
    }

    public long getBlockedOnboardingCases() {
        return blockedOnboardingCases;
    }

    public void setBlockedOnboardingCases(long blockedOnboardingCases) {
        this.blockedOnboardingCases = blockedOnboardingCases;
    }

    public long getPendingTasks() {
        return pendingTasks;
    }

    public void setPendingTasks(long pendingTasks) {
        this.pendingTasks = pendingTasks;
    }

    public long getCompletedTasks() {
        return completedTasks;
    }

    public void setCompletedTasks(long completedTasks) {
        this.completedTasks = completedTasks;
    }

    public long getExceptionTasks() {
        return exceptionTasks;
    }

    public void setExceptionTasks(long exceptionTasks) {
        this.exceptionTasks = exceptionTasks;
    }

    public long getProvisionedGrants() {
        return provisionedGrants;
    }

    public void setProvisionedGrants(long provisionedGrants) {
        this.provisionedGrants = provisionedGrants;
    }

    public long getPendingApprovalGrants() {
        return pendingApprovalGrants;
    }

    public void setPendingApprovalGrants(long pendingApprovalGrants) {
        this.pendingApprovalGrants = pendingApprovalGrants;
    }

    public long getDeployedAssets() {
        return deployedAssets;
    }

    public void setDeployedAssets(long deployedAssets) {
        this.deployedAssets = deployedAssets;
    }

    public long getInStockAssets() {
        return inStockAssets;
    }

    public void setInStockAssets(long inStockAssets) {
        this.inStockAssets = inStockAssets;
    }

    public long getReturnRequestedAssets() {
        return returnRequestedAssets;
    }

    public void setReturnRequestedAssets(long returnRequestedAssets) {
        this.returnRequestedAssets = returnRequestedAssets;
    }

    public long getActiveOffboardingCases() {
        return activeOffboardingCases;
    }

    public void setActiveOffboardingCases(long activeOffboardingCases) {
        this.activeOffboardingCases = activeOffboardingCases;
    }

    public long getUrgentTerminations() {
        return urgentTerminations;
    }

    public void setUrgentTerminations(long urgentTerminations) {
        this.urgentTerminations = urgentTerminations;
    }

    public double getReconciliationHealthScore() {
        return reconciliationHealthScore;
    }

    public void setReconciliationHealthScore(double reconciliationHealthScore) {
        this.reconciliationHealthScore = reconciliationHealthScore;
    }

    public long getDetectedAnomaliesCount() {
        return detectedAnomaliesCount;
    }

    public void setDetectedAnomaliesCount(long detectedAnomaliesCount) {
        this.detectedAnomaliesCount = detectedAnomaliesCount;
    }

    public Map<String, Long> getTasksByRole() {
        return tasksByRole;
    }

    public void setTasksByRole(Map<String, Long> tasksByRole) {
        this.tasksByRole = tasksByRole;
    }

    public Map<String, Long> getAssetsByType() {
        return assetsByType;
    }

    public void setAssetsByType(Map<String, Long> assetsByType) {
        this.assetsByType = assetsByType;
    }
}

