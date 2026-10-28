package com.yottaflex.orchestrator.controller;

import com.yottaflex.orchestrator.dto.AccessGrantRequest;
import com.yottaflex.orchestrator.model.AccessGrant;
import com.yottaflex.orchestrator.repository.AccessGrantRepository;
import com.yottaflex.orchestrator.service.AccessProvisioningService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/access-grants")
@CrossOrigin(origins = "*")
public class AccessGrantController {

    private final AccessGrantRepository accessGrantRepository;
    private final AccessProvisioningService accessProvisioningService;

    public AccessGrantController(AccessGrantRepository accessGrantRepository,
                                 AccessProvisioningService accessProvisioningService) {
        this.accessGrantRepository = accessGrantRepository;
        this.accessProvisioningService = accessProvisioningService;
    }

    @GetMapping
    public ResponseEntity<Page<AccessGrant>> listGrants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(required = false) String search) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("id").ascending());
        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(accessGrantRepository
                    .findByStatusContainingIgnoreCaseOrEmployeeIdContainingIgnoreCase(search, search, pageRequest));
        }
        return ResponseEntity.ok(accessGrantRepository.findAll(pageRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccessGrant> getById(@PathVariable String id) {
        return ResponseEntity.ok(accessProvisioningService.getGrantById(id));
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<AccessGrant>> getByEmployee(@PathVariable String employeeId) {
        return ResponseEntity.ok(accessProvisioningService.getGrantsForEmployee(employeeId));
    }

    @PostMapping("/request")
    public ResponseEntity<AccessGrant> requestGrant(@RequestBody AccessGrantRequest request) {
        return ResponseEntity.ok(accessProvisioningService.requestAccess(request));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<AccessGrant> approveGrant(@PathVariable String id,
                                                   @RequestBody(required = false) Map<String, String> body) {
        String approverId = body != null ? body.get("approverId") : "security.approver@company.internal";
        return ResponseEntity.ok(accessProvisioningService.approveAccess(id, approverId));
    }

    @PostMapping("/{id}/provision")
    public ResponseEntity<AccessGrant> provisionGrant(@PathVariable String id,
                                                     @RequestBody(required = false) Map<String, String> body) {
        String operatorId = body != null ? body.get("operatorId") : "it.admin@company.internal";
        return ResponseEntity.ok(accessProvisioningService.provisionAccess(id, operatorId));
    }

    @PostMapping("/{id}/revoke")
    public ResponseEntity<AccessGrant> revokeGrant(@PathVariable String id,
                                                  @RequestBody(required = false) Map<String, String> body) {
        String operatorId = body != null ? body.get("operatorId") : "it.security@company.internal";
        String reason = body != null ? body.get("reason") : "Manual administrative revocation";
        return ResponseEntity.ok(accessProvisioningService.revokeAccess(id, operatorId, reason));
    }
}

