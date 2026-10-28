package com.yottaflex.orchestrator.controller;

import com.yottaflex.orchestrator.repository.EmployeeRepository;
import com.yottaflex.orchestrator.service.SyntheticDataLoaderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/synthetic-data")
@CrossOrigin(origins = "*")
public class SyntheticDataController {

    private final SyntheticDataLoaderService dataLoaderService;
    private final EmployeeRepository employeeRepository;

    public SyntheticDataController(SyntheticDataLoaderService dataLoaderService,
                                   EmployeeRepository employeeRepository) {
        this.dataLoaderService = dataLoaderService;
        this.employeeRepository = employeeRepository;
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("totalEmployeesLoaded", employeeRepository.count());
        status.put("isSeeded", employeeRepository.count() > 0);
        return ResponseEntity.ok(status);
    }

    @PostMapping("/load")
    public ResponseEntity<Map<String, Object>> reloadDataset() {
        boolean success = dataLoaderService.loadDataset();
        Map<String, Object> resp = new HashMap<>();
        resp.put("success", success);
        resp.put("totalEmployees", employeeRepository.count());
        return ResponseEntity.ok(resp);
    }
}

