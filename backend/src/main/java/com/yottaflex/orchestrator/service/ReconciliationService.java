package com.yottaflex.orchestrator.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yottaflex.orchestrator.dto.AnomalyItemDto;
import com.yottaflex.orchestrator.dto.ReconciliationReportDto;
import com.yottaflex.orchestrator.model.*;
import com.yottaflex.orchestrator.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ReconciliationService {

    private final EmployeeRepository employeeRepository;
    private final AccessGrantRepository accessGrantRepository;
    private final AssetRepository assetRepository;
    private final CaseTaskRepository taskRepository;
    private final TaskDependencyRepository dependencyRepository;
    private final WorkflowEngineService workflowEngineService;
    private final ObjectMapper objectMapper;

    @Value("${synthetic.data.dir:../synthetic_data/exports}")
    private String syntheticDataDir;

    public ReconciliationService(EmployeeRepository employeeRepository,
                                 AccessGrantRepository accessGrantRepository,
                                 AssetRepository assetRepository,
                                 CaseTaskRepository taskRepository,
                                 TaskDependencyRepository dependencyRepository,
                                 WorkflowEngineService workflowEngineService,
                                 ObjectMapper objectMapper) {
        this.employeeRepository = employeeRepository;
        this.accessGrantRepository = accessGrantRepository;
        this.assetRepository = assetRepository;
        this.taskRepository = taskRepository;
        this.dependencyRepository = dependencyRepository;
        this.workflowEngineService = workflowEngineService;
        this.objectMapper = objectMapper;
    }

    public ReconciliationReportDto runFullReconciliation() {
        List<AnomalyItemDto> detectedAnomalies = new ArrayList<>();
        int anomalySeq = 1;

        Map<String, Employee> employeeMap = employeeRepository.findAll().stream()
                .collect(Collectors.toMap(Employee::getId, Function.identity(), (a, b) -> a));

        List<AccessGrant> allGrants = accessGrantRepository.findAll();
        List<Asset> allAssets = assetRepository.findAll();
        List<TaskDependency> allDependencies = dependencyRepository.findAll();

        // -------------------------------------------------------------
        // Rule 1: TERMINATED_EMPLOYEE_RETAINS_GRANT
        // Active access grant held by an employee marked TERMINATED
        // -------------------------------------------------------------
        for (AccessGrant grant : allGrants) {
            Employee emp = employeeMap.get(grant.getEmployeeId());
            if (emp != null && "TERMINATED".equalsIgnoreCase(emp.getEmploymentStatus())) {
                if ("PROVISIONED".equalsIgnoreCase(grant.getStatus()) && grant.getRevokedAt() == null) {
                    detectedAnomalies.add(new AnomalyItemDto(
                            "DET-" + String.format("%04d", anomalySeq++),
                            "TERMINATED_EMPLOYEE_RETAINS_GRANT",
                            "AccessGrant",
                            grant.getId(),
                            emp.getId(),
                            "Employee " + emp.getId() + " is TERMINATED since " + emp.getEndDate()
                                    + " but holds active grant for role " + grant.getAccessRoleId() + ".",
                            "CRITICAL",
                            "IF employee.employment_status == 'TERMINATED' AND grant.status == 'PROVISIONED' THEN FLAG",
                            "Immediately invoke IT SCIM connector to revoke grant " + grant.getId(),
                            false
                    ));
                }
            }
        }

        // -------------------------------------------------------------
        // Rule 2: ACCESS_GRANTED_BEFORE_START_APPROVAL
        // Access grant provisioned before employee start date or without approved status
        // -------------------------------------------------------------
        for (AccessGrant grant : allGrants) {
            Employee emp = employeeMap.get(grant.getEmployeeId());
            if ("PROVISIONED".equalsIgnoreCase(grant.getStatus())) {
                boolean pendingApproval = !"APPROVED".equalsIgnoreCase(grant.getApprovalStatus())
                        && !"AUTO_APPROVED".equalsIgnoreCase(grant.getApprovalStatus());

                boolean prematureDate = false;
                if (emp != null && grant.getProvisionedAt() != null) {
                    prematureDate = grant.getProvisionedAt().toLocalDate().isBefore(emp.getStartDate());
                }

                if (pendingApproval || prematureDate) {
                    detectedAnomalies.add(new AnomalyItemDto(
                            "DET-" + String.format("%04d", anomalySeq++),
                            "ACCESS_GRANTED_BEFORE_START_APPROVAL",
                            "AccessGrant",
                            grant.getId(),
                            grant.getEmployeeId(),
                            "Grant " + grant.getId() + " was provisioned without required manager/security approval or prior to candidate start date.",
                            "CRITICAL",
                            "IF grant.status == 'PROVISIONED' AND (approval_status != 'APPROVED' OR provisioned_at < start_date) THEN FLAG",
                            "Suspend provisioning until employee start date and record formal approval.",
                            false
                    ));
                }
            }
        }

        // -------------------------------------------------------------
        // Rule 3: DUPLICATE_ASSET_ASSIGNMENT
        // Same physical serial number concurrently assigned to multiple employees
        // -------------------------------------------------------------
        Map<String, List<Asset>> assetsBySerial = allAssets.stream()
                .filter(a -> a.getSerialNumber() != null && !a.getSerialNumber().isBlank())
                .collect(Collectors.groupingBy(Asset::getSerialNumber));

        for (Map.Entry<String, List<Asset>> entry : assetsBySerial.entrySet()) {
            List<Asset> matches = entry.getValue();
            Set<String> assignedEmployees = matches.stream()
                    .map(Asset::getAssignedToEmployeeId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            if (assignedEmployees.size() > 1 || matches.size() > 1) {
                Asset primary = matches.get(0);
                String secondId = matches.size() > 1 ? matches.get(1).getId() : primary.getId();
                detectedAnomalies.add(new AnomalyItemDto(
                        "DET-" + String.format("%04d", anomalySeq++),
                        "DUPLICATE_ASSET_ASSIGNMENT",
                        "Asset",
                        primary.getId(),
                        secondId,
                        "Serial number " + entry.getKey() + " is concurrently referenced across multiple records: "
                                + matches.stream().map(Asset::getId).collect(Collectors.joining(", ")),
                        "HIGH",
                        "COUNT(DISTINCT assigned_to_employee_id) GROUP BY serial_number HAVING COUNT > 1",
                        "Re-tag secondary asset with a distinct physical barcode and audit warehouse serial entries.",
                        false
                ));
            }
        }

        // -------------------------------------------------------------
        // Rule 4: TASK_DEPENDENCY_CYCLE
        // Circular dependency loops in task DAG
        // -------------------------------------------------------------
        List<String> cyclePairs = workflowEngineService.detectDependencyCycles(allDependencies);
        for (String cycle : cyclePairs) {
            detectedAnomalies.add(new AnomalyItemDto(
                    "DET-" + String.format("%04d", anomalySeq++),
                    "TASK_DEPENDENCY_CYCLE",
                    "TaskDependency",
                    cycle,
                    "DAG_CIRCULAR_LOOP",
                    "Deadlock detected in workflow task execution graph: " + cycle,
                    "HIGH",
                    "Topological sort / DFS cycle detection on task DAG",
                    "Sever prerequisite circular edge to restore workflow automation.",
                    false
            ));
        }

        // -------------------------------------------------------------
        // Rule 5: RECOVERED_ASSET_REMAINS_ASSIGNED
        // Hardware marked RECOVERED but assigned_to_employee_id not cleared
        // -------------------------------------------------------------
        for (Asset ast : allAssets) {
            if ("RECOVERED".equalsIgnoreCase(ast.getStatus()) && ast.getAssignedToEmployeeId() != null) {
                detectedAnomalies.add(new AnomalyItemDto(
                        "DET-" + String.format("%04d", anomalySeq++),
                        "RECOVERED_ASSET_REMAINS_ASSIGNED",
                        "Asset",
                        ast.getId(),
                        ast.getAssignedToEmployeeId(),
                        "Asset " + ast.getId() + " is marked RECOVERED on " + ast.getRecoveredAt()
                                + " but still holds active reference to employee " + ast.getAssignedToEmployeeId() + ".",
                        "MEDIUM",
                        "IF asset.status == 'RECOVERED' AND asset.assigned_to_employee_id IS NOT NULL THEN FLAG",
                        "Clear assigned_to_employee_id to NULL and return asset to IN_STOCK inventory.",
                        false
                ));
            }
        }

        // -------------------------------------------------------------
        // Load Ground Truth and Compute Precision, Recall, F1
        // -------------------------------------------------------------
        List<Map<String, Object>> groundTruthList = loadGroundTruth();
        int knownCount = groundTruthList.size();

        // Mark detected anomalies that match ground truth
        Set<String> gtEntityIds = groundTruthList.stream()
                .map(m -> String.valueOf(m.get("entity_id")))
                .collect(Collectors.toSet());

        long truePositives = 0;
        for (AnomalyItemDto item : detectedAnomalies) {
            if (gtEntityIds.contains(item.getEntityId()) || gtEntityIds.contains(item.getSecondaryEntityId())) {
                item.setKnownGroundTruth(true);
                truePositives++;
            }
        }

        long totalDetected = detectedAnomalies.size();
        double precision = totalDetected > 0 ? (double) truePositives / totalDetected : 1.0;
        double recall = knownCount > 0 ? (double) truePositives / knownCount : 1.0;
        double f1 = (precision + recall) > 0 ? 2 * (precision * recall) / (precision + recall) : 1.0;

        // Health Score (100% - penalty proportional to anomalies)
        long totalRecords = employeeMap.size() + allGrants.size() + allAssets.size() + taskRepository.count();
        double errorRate = totalRecords > 0 ? (double) totalDetected / totalRecords : 0;
        double healthScore = Math.max(0.0, Math.min(100.0, 100.0 * (1.0 - errorRate * 5.0)));

        // Aggregations
        Map<String, Long> byType = detectedAnomalies.stream()
                .collect(Collectors.groupingBy(AnomalyItemDto::getIssueType, Collectors.counting()));

        Map<String, Long> bySeverity = detectedAnomalies.stream()
                .collect(Collectors.groupingBy(AnomalyItemDto::getSeverity, Collectors.counting()));

        ReconciliationReportDto report = new ReconciliationReportDto();
        report.setScanTimestamp(LocalDateTime.now());
        report.setTotalRecordsScanned(totalRecords);
        report.setTotalAnomaliesDetected(totalDetected);
        report.setKnownGroundTruthCount(knownCount);
        report.setPrecision(Math.round(precision * 1000.0) / 1000.0);
        report.setRecall(Math.round(recall * 1000.0) / 1000.0);
        report.setF1Score(Math.round(f1 * 1000.0) / 1000.0);
        report.setReconciliationHealthScore(Math.round(healthScore * 10.0) / 10.0);
        report.setAnomaliesByType(byType);
        report.setAnomaliesBySeverity(bySeverity);
        report.setAnomalyDetails(detectedAnomalies);

        return report;
    }

    public List<Map<String, Object>> loadGroundTruth() {
        // Try multiple path candidates so it works from any working directory
        String userDir = System.getProperty("user.dir");
        List<String> candidates = Arrays.asList(
                syntheticDataDir + "/ground_truth.json",
                "synthetic_data/exports/ground_truth.json",
                "../synthetic_data/exports/ground_truth.json",
                userDir + "/synthetic_data/exports/ground_truth.json",
                userDir + "/../synthetic_data/exports/ground_truth.json"
        );

        for (String path : candidates) {
            try {
                File gtFile = new File(path).getCanonicalFile();
                if (gtFile.exists()) {
                    return objectMapper.readValue(gtFile, new TypeReference<List<Map<String, Object>>>() {});
                }
            } catch (Exception e) {
                // try next candidate
            }
        }
        System.err.println("[!] ground_truth.json not found in any expected location.");
        return Collections.emptyList();
    }
}

