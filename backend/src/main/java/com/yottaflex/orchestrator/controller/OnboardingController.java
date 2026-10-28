package com.yottaflex.orchestrator.controller;

import com.yottaflex.orchestrator.exception.ResourceNotFoundException;
import com.yottaflex.orchestrator.model.OnboardingCase;
import com.yottaflex.orchestrator.repository.OnboardingCaseRepository;
import com.yottaflex.orchestrator.service.WorkflowEngineService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/onboarding")
@CrossOrigin(origins = "*")
public class OnboardingController {

    private final OnboardingCaseRepository onboardingCaseRepository;
    private final WorkflowEngineService workflowEngineService;

    public OnboardingController(OnboardingCaseRepository onboardingCaseRepository,
                                WorkflowEngineService workflowEngineService) {
        this.onboardingCaseRepository = onboardingCaseRepository;
        this.workflowEngineService = workflowEngineService;
    }

    @GetMapping
    public ResponseEntity<Page<OnboardingCase>> listCases(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(required = false) String search) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("scheduledStartDate").descending());
        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(onboardingCaseRepository
                    .findByCandidateNameContainingIgnoreCaseOrStatusContainingIgnoreCase(search, search, pageRequest));
        }
        return ResponseEntity.ok(onboardingCaseRepository.findAll(pageRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OnboardingCase> getCaseById(@PathVariable String id) {
        return onboardingCaseRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Onboarding case not found with ID: " + id));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<OnboardingCase> getByEmployeeId(@PathVariable String employeeId) {
        return onboardingCaseRepository.findByEmployeeId(employeeId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Onboarding case not found for employee: " + employeeId));
    }

    @PostMapping("/{id}/advance")
    public ResponseEntity<OnboardingCase> advanceStage(@PathVariable String id) {
        workflowEngineService.advanceCaseLifecycle(id);
        return ResponseEntity.ok(onboardingCaseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found: " + id)));
    }
}

