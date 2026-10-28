package com.yottaflex.orchestrator.controller;

import com.yottaflex.orchestrator.dto.DashboardMetricsDto;
import com.yottaflex.orchestrator.repository.*;
import com.yottaflex.orchestrator.service.ReconciliationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final EmployeeRepository employeeRepository;
    private final OnboardingCaseRepository onboardingCaseRepository;
    private final CaseTaskRepository taskRepository;
    private final AccessGrantRepository accessGrantRepository;
    private final AssetRepository assetRepository;
    private final OffboardingCaseRepository offboardingCaseRepository;
    private final ReconciliationService reconciliationService;

    public DashboardController(EmployeeRepository employeeRepository,
                               OnboardingCaseRepository onboardingCaseRepository,
                               CaseTaskRepository taskRepository,
                               AccessGrantRepository accessGrantRepository,
                               AssetRepository assetRepository,
                               OffboardingCaseRepository offboardingCaseRepository,
                               ReconciliationService reconciliationService) {
        this.employeeRepository = employeeRepository;
        this.onboardingCaseRepository = onboardingCaseRepository;
        this.taskRepository = taskRepository;
        this.accessGrantRepository = accessGrantRepository;
        this.assetRepository = assetRepository;
        this.offboardingCaseRepository = offboardingCaseRepository;
        this.reconciliationService = reconciliationService;
    }

    @GetMapping("/metrics")
    public ResponseEntity<DashboardMetricsDto> getMetrics() {
        DashboardMetricsDto dto = new DashboardMetricsDto();

        dto.setTotalEmployees(employeeRepository.count());
        dto.setActiveEmployees(employeeRepository.countActiveEmployees());
        dto.setPreboardingEmployees(employeeRepository.countPreboardingEmployees());
        dto.setTerminatedEmployees(employeeRepository.countTerminatedEmployees());

        dto.setActiveOnboardingCases(onboardingCaseRepository.countInProgressCases());
        dto.setBlockedOnboardingCases(onboardingCaseRepository.countBlockedCases());

        dto.setPendingTasks(taskRepository.countPendingTasks());
        dto.setCompletedTasks(taskRepository.countCompletedTasks());
        dto.setExceptionTasks(taskRepository.countExceptionTasks());

        dto.setProvisionedGrants(accessGrantRepository.countProvisionedGrants());
        dto.setPendingApprovalGrants(accessGrantRepository.countPendingApprovalGrants());

        dto.setDeployedAssets(assetRepository.countDeployedAssets());
        dto.setInStockAssets(assetRepository.countInStockAssets());
        dto.setReturnRequestedAssets(assetRepository.countReturnRequestedAssets());

        dto.setActiveOffboardingCases(offboardingCaseRepository.countActiveOffboardingCases());
        dto.setUrgentTerminations(offboardingCaseRepository.countUrgentTerminations());

        // Fast estimation of health score based on anomalies
        var reconReport = reconciliationService.runFullReconciliation();
        dto.setReconciliationHealthScore(reconReport.getReconciliationHealthScore());
        dto.setDetectedAnomaliesCount(reconReport.getTotalAnomaliesDetected());

        // Tasks grouped by role
        Map<String, Long> tasksByRole = new HashMap<>();
        taskRepository.findAll().forEach(t -> {
            tasksByRole.merge(t.getAssignedRole(), 1L, Long::sum);
        });
        dto.setTasksByRole(tasksByRole);

        // Assets grouped by type
        Map<String, Long> assetsByType = new HashMap<>();
        assetRepository.findAll().forEach(a -> {
            assetsByType.merge(a.getAssetType(), 1L, Long::sum);
        });
        dto.setAssetsByType(assetsByType);

        return ResponseEntity.ok(dto);
    }
}

