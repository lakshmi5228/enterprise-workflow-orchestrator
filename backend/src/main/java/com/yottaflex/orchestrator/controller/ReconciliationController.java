package com.yottaflex.orchestrator.controller;

import com.yottaflex.orchestrator.dto.ReconciliationReportDto;
import com.yottaflex.orchestrator.service.ReconciliationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/reconciliation")
@CrossOrigin(origins = "*")
public class ReconciliationController {

    private final ReconciliationService reconciliationService;

    public ReconciliationController(ReconciliationService reconciliationService) {
        this.reconciliationService = reconciliationService;
    }

    @GetMapping("/scan")
    public ResponseEntity<ReconciliationReportDto> executeScan() {
        return ResponseEntity.ok(reconciliationService.runFullReconciliation());
    }

    @GetMapping("/ground-truth")
    public ResponseEntity<List<Map<String, Object>>> getGroundTruth() {
        return ResponseEntity.ok(reconciliationService.loadGroundTruth());
    }
}

