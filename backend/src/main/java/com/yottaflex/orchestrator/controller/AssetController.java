package com.yottaflex.orchestrator.controller;

import com.yottaflex.orchestrator.dto.AssetAssignRequest;
import com.yottaflex.orchestrator.model.Asset;
import com.yottaflex.orchestrator.repository.AssetRepository;
import com.yottaflex.orchestrator.service.AssetManagementService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/assets")
@CrossOrigin(origins = "*")
public class AssetController {

    private final AssetRepository assetRepository;
    private final AssetManagementService assetManagementService;

    public AssetController(AssetRepository assetRepository,
                           AssetManagementService assetManagementService) {
        this.assetRepository = assetRepository;
        this.assetManagementService = assetManagementService;
    }

    @GetMapping
    public ResponseEntity<Page<Asset>> listAssets(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(required = false) String search) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id").ascending());
        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(assetRepository
                    .findByStatusContainingIgnoreCaseOrAssetTypeContainingIgnoreCase(search, search, pageRequest));
        }
        return ResponseEntity.ok(assetRepository.findAll(pageRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Asset> getById(@PathVariable String id) {
        return ResponseEntity.ok(assetManagementService.getAssetById(id));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<Asset>> getByEmployee(@PathVariable String employeeId) {
        return ResponseEntity.ok(assetManagementService.getAssetsForEmployee(employeeId));
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<Asset> assignAsset(@PathVariable String id,
                                            @RequestBody AssetAssignRequest request) {
        return ResponseEntity.ok(assetManagementService.assignAsset(id, request));
    }

    @PostMapping("/{id}/acknowledge")
    public ResponseEntity<Asset> acknowledgeAsset(@PathVariable String id,
                                                 @RequestBody Map<String, String> body) {
        String empId = body.get("employeeId");
        return ResponseEntity.ok(assetManagementService.acknowledgeReceipt(id, empId));
    }

    @PostMapping("/{id}/recover")
    public ResponseEntity<Asset> recoverAsset(@PathVariable String id,
                                             @RequestBody(required = false) Map<String, String> body) {
        String inspectorId = body != null ? body.get("inspectorId") : "facilities.lead@company.internal";
        String condition = body != null ? body.get("conditionStatus") : "GOOD";
        return ResponseEntity.ok(assetManagementService.recoverAsset(id, inspectorId, condition));
    }
}

