package com.yottaflex.orchestrator.controller;

import com.yottaflex.orchestrator.dto.TaskActionRequest;
import com.yottaflex.orchestrator.model.CaseTask;
import com.yottaflex.orchestrator.repository.CaseTaskRepository;
import com.yottaflex.orchestrator.service.WorkflowEngineService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@CrossOrigin(origins = "*")
public class TaskController {

    private final CaseTaskRepository taskRepository;
    private final WorkflowEngineService workflowEngineService;

    public TaskController(CaseTaskRepository taskRepository,
                          WorkflowEngineService workflowEngineService) {
        this.taskRepository = taskRepository;
        this.workflowEngineService = workflowEngineService;
    }

    @GetMapping
    public ResponseEntity<Page<CaseTask>> listTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "25") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category) {

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("dueDate").ascending());
        if (status != null || category != null) {
            return ResponseEntity.ok(taskRepository.findByStatusOrCategory(status, category, pageRequest));
        }
        return ResponseEntity.ok(taskRepository.findAll(pageRequest));
    }

    @GetMapping("/case/{caseId}")
    public ResponseEntity<List<CaseTask>> getTasksByCase(@PathVariable String caseId) {
        return ResponseEntity.ok(workflowEngineService.getTasksForCase(caseId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CaseTask> getTaskById(@PathVariable String id) {
        return ResponseEntity.ok(workflowEngineService.getTaskById(id));
    }

    @PostMapping("/{id}/action")
    public ResponseEntity<CaseTask> performAction(@PathVariable String id,
                                                  @RequestBody TaskActionRequest request) {
        return ResponseEntity.ok(workflowEngineService.executeTaskAction(id, request));
    }
}

