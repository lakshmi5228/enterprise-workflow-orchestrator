package com.yottaflex.orchestrator.controller;

import com.yottaflex.orchestrator.exception.ResourceNotFoundException;
import com.yottaflex.orchestrator.model.Employee;
import com.yottaflex.orchestrator.repository.EmployeeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@CrossOrigin(origins = "*")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    public EmployeeController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @GetMapping
    public ResponseEntity<Page<Employee>> listEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(required = false) String search) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(sortBy).ascending());
        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(employeeRepository
                    .findByEmploymentStatusContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                            search, search, search, pageRequest));
        }
        return ResponseEntity.ok(employeeRepository.findAll(pageRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable String id) {
        return employeeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Employee>> getByStatus(@PathVariable String status) {
        return ResponseEntity.ok(employeeRepository.findByEmploymentStatus(status.toUpperCase()));
    }
}

