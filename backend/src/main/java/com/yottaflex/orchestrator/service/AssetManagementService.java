package com.yottaflex.orchestrator.service;

import com.yottaflex.orchestrator.dto.AssetAssignRequest;
import com.yottaflex.orchestrator.exception.ResourceNotFoundException;
import com.yottaflex.orchestrator.exception.WorkflowValidationException;
import com.yottaflex.orchestrator.model.Asset;
import com.yottaflex.orchestrator.model.Employee;
import com.yottaflex.orchestrator.repository.AssetRepository;
import com.yottaflex.orchestrator.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AssetManagementService {

    private final AssetRepository assetRepository;
    private final EmployeeRepository employeeRepository;
    private final AuditService auditService;

    public AssetManagementService(AssetRepository assetRepository,
                                  EmployeeRepository employeeRepository,
                                  AuditService auditService) {
        this.assetRepository = assetRepository;
        this.employeeRepository = employeeRepository;
        this.auditService = auditService;
    }

    public List<Asset> getAssetsForEmployee(String employeeId) {
        return assetRepository.findByAssignedToEmployeeId(employeeId);
    }

    public Asset getAssetById(String assetId) {
        return assetRepository.findById(assetId)
                .orElseThrow(() -> new ResourceNotFoundException("Asset not found with ID: " + assetId));
    }

    @Transactional
    public Asset assignAsset(String assetId, AssetAssignRequest request) {
        Asset asset = getAssetById(assetId);
        Employee employee = employeeRepository.findById(request.getEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + request.getEmployeeId()));

        if ("ASSIGNED".equalsIgnoreCase(asset.getStatus()) || "ACKNOWLEDGED".equalsIgnoreCase(asset.getStatus())) {
            if (asset.getAssignedToEmployeeId() != null && !asset.getAssignedToEmployeeId().equals(employee.getId())) {
                throw new WorkflowValidationException("Asset " + assetId + " is already assigned to employee " + asset.getAssignedToEmployeeId());
            }
        }

        String oldState = asset.getStatus() + ":" + asset.getAssignedToEmployeeId();
        asset.setStatus("ASSIGNED");
        asset.setAssignedToEmployeeId(employee.getId());
        asset.setAssignedDate(LocalDate.now());

        Asset updated = assetRepository.save(asset);
        auditService.recordEvent(
                "Asset",
                updated.getId(),
                "ASSIGN",
                request.getActorId(),
                "FACILITIES",
                oldState,
                updated.getStatus() + ":" + updated.getAssignedToEmployeeId(),
                "Assigned device " + asset.getModel() + " (" + asset.getAssetTag() + ") to employee " + employee.getFullName()
        );
        return updated;
    }

    @Transactional
    public Asset acknowledgeReceipt(String assetId, String employeeId) {
        Asset asset = getAssetById(assetId);
        if (!employeeId.equalsIgnoreCase(asset.getAssignedToEmployeeId())) {
            throw new WorkflowValidationException("Receipt acknowledgment can only be submitted by assigned employee " + asset.getAssignedToEmployeeId());
        }

        String oldState = asset.getStatus();
        asset.setStatus("ACKNOWLEDGED");
        asset.setAcknowledgedAt(LocalDateTime.now());

        Asset updated = assetRepository.save(asset);
        auditService.recordEvent(
                "Asset",
                updated.getId(),
                "ACKNOWLEDGE",
                employeeId,
                "EMPLOYEE",
                oldState,
                updated.getStatus(),
                "Employee confirmed delivery and physical receipt of device."
        );
        return updated;
    }

    @Transactional
    public Asset recoverAsset(String assetId, String inspectorId, String conditionStatus) {
        Asset asset = getAssetById(assetId);
        String oldState = asset.getStatus() + ":" + asset.getAssignedToEmployeeId();

        asset.setStatus("IN_STOCK");
        asset.setRecoveredAt(LocalDateTime.now());
        asset.setAssignedToEmployeeId(null); // Clear employee assignment!
        if (conditionStatus != null) {
            asset.setConditionStatus(conditionStatus);
        }

        Asset updated = assetRepository.save(asset);
        auditService.recordEvent(
                "Asset",
                updated.getId(),
                "RECOVER",
                inspectorId,
                "FACILITIES",
                oldState,
                updated.getStatus() + ":NULL",
                "Asset recovered from offboarding employee, inspected (" + asset.getConditionStatus() + "), and returned to stock."
        );
        return updated;
    }
}

