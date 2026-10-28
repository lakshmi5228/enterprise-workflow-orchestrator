package com.yottaflex.orchestrator.service;

import com.yottaflex.orchestrator.dto.TaskActionRequest;
import com.yottaflex.orchestrator.exception.ResourceNotFoundException;
import com.yottaflex.orchestrator.exception.WorkflowValidationException;
import com.yottaflex.orchestrator.model.CaseTask;
import com.yottaflex.orchestrator.model.OnboardingCase;
import com.yottaflex.orchestrator.model.TaskDependency;
import com.yottaflex.orchestrator.repository.CaseTaskRepository;
import com.yottaflex.orchestrator.repository.OnboardingCaseRepository;
import com.yottaflex.orchestrator.repository.TaskDependencyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class WorkflowEngineService {

    private final CaseTaskRepository taskRepository;
    private final TaskDependencyRepository dependencyRepository;
    private final OnboardingCaseRepository onboardingCaseRepository;
    private final AuditService auditService;

    public WorkflowEngineService(CaseTaskRepository taskRepository,
                                 TaskDependencyRepository dependencyRepository,
                                 OnboardingCaseRepository onboardingCaseRepository,
                                 AuditService auditService) {
        this.taskRepository = taskRepository;
        this.dependencyRepository = dependencyRepository;
        this.onboardingCaseRepository = onboardingCaseRepository;
        this.auditService = auditService;
    }

    public List<CaseTask> getTasksForCase(String caseId) {
        return taskRepository.findByCaseId(caseId);
    }

    public CaseTask getTaskById(String taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));
    }

    @Transactional
    public CaseTask executeTaskAction(String taskId, TaskActionRequest request) {
        CaseTask task = getTaskById(taskId);
        String oldState = task.getStatus();

        if ("COMPLETE".equalsIgnoreCase(request.getAction())) {
            // Validate prerequisite dependencies unless explicitly overridden
            List<TaskDependency> prerequisites = dependencyRepository.findByTaskId(taskId);
            for (TaskDependency dep : prerequisites) {
                if (Boolean.TRUE.equals(dep.getIsMandatory())) {
                    CaseTask prereqTask = taskRepository.findById(dep.getPrerequisiteTaskId()).orElse(null);
                    if (prereqTask != null && !"COMPLETED".equalsIgnoreCase(prereqTask.getStatus())) {
                        throw new WorkflowValidationException(
                                "Cannot complete task " + taskId + " because mandatory prerequisite "
                                        + prereqTask.getId() + " (" + prereqTask.getTaskName() + ") is in state: "
                                        + prereqTask.getStatus()
                        );
                    }
                }
            }

            task.setStatus("COMPLETED");
            task.setCompletedAt(LocalDateTime.now());
            if (request.getNotes() != null) {
                task.setNotes(request.getNotes());
            }

        } else if ("OVERRIDE".equalsIgnoreCase(request.getAction())) {
            // Administrative override of blocker
            task.setStatus("OVERRIDDEN");
            task.setCompletedAt(LocalDateTime.now());
            task.setIsException(true);
            task.setNotes("Manager/Admin Override: " + (request.getNotes() != null ? request.getNotes() : "Authorized exception"));

        } else if ("REASSIGN".equalsIgnoreCase(request.getAction())) {
            if (request.getAssignedToUser() != null) {
                task.setAssignedToUser(request.getAssignedToUser());
            }

        } else if ("BLOCK".equalsIgnoreCase(request.getAction())) {
            task.setStatus("WAITING_APPROVAL");
            task.setIsException(true);
            if (request.getNotes() != null) {
                task.setNotes(request.getNotes());
            }
        }

        CaseTask updated = taskRepository.save(task);

        // Audit Trail
        auditService.recordEvent(
                "Task",
                task.getId(),
                request.getAction().toUpperCase(),
                request.getActorId(),
                request.getActorRole(),
                oldState,
                updated.getStatus(),
                "Task action executed: " + request.getAction() + ". Notes: " + request.getNotes()
        );

        // Evaluate and advance case stage
        advanceCaseLifecycle(task.getCaseId());

        return updated;
    }

    @Transactional
    public void advanceCaseLifecycle(String caseId) {
        OnboardingCase onboardingCase = onboardingCaseRepository.findById(caseId).orElse(null);
        if (onboardingCase == null) {
            return;
        }

        List<CaseTask> tasks = taskRepository.findByCaseId(caseId);
        long completedCount = tasks.stream().filter(t -> "COMPLETED".equalsIgnoreCase(t.getStatus()) || "OVERRIDDEN".equalsIgnoreCase(t.getStatus())).count();

        String oldStage = onboardingCase.getStage();
        String oldStatus = onboardingCase.getStatus();

        if (completedCount == tasks.size()) {
            onboardingCase.setStage("COMPLETED");
            onboardingCase.setStatus("COMPLETED");
        } else if (completedCount >= 6) {
            onboardingCase.setStage("ORIENTATION");
            onboardingCase.setStatus("IN_PROGRESS");
        } else if (completedCount >= 4) {
            onboardingCase.setStage("ASSET_DELIVERY");
            onboardingCase.setStatus("IN_PROGRESS");
        } else if (completedCount >= 2) {
            onboardingCase.setStage("IT_PROVISIONING");
            onboardingCase.setStatus("IN_PROGRESS");
        }

        onboardingCase.setUpdatedAt(LocalDateTime.now());
        onboardingCaseRepository.save(onboardingCase);

        if (!oldStage.equals(onboardingCase.getStage()) || !oldStatus.equals(onboardingCase.getStatus())) {
            auditService.recordEvent(
                    "OnboardingCase",
                    caseId,
                    "UPDATE",
                    "SYSTEM_DAEMON",
                    "WORKFLOW_ENGINE",
                    oldStage + ":" + oldStatus,
                    onboardingCase.getStage() + ":" + onboardingCase.getStatus(),
                    "Onboarding stage automatically advanced based on task completion."
            );
        }
    }

    /**
     * Detects cycle in the task dependency DAG using Depth First Search (DFS).
     */
    public List<String> detectDependencyCycles(List<TaskDependency> allDependencies) {
        Map<String, List<String>> adj = new HashMap<>();
        for (TaskDependency dep : allDependencies) {
            adj.computeIfAbsent(dep.getTaskId(), k -> new ArrayList<>()).add(dep.getPrerequisiteTaskId());
        }

        Set<String> visited = new HashSet<>();
        Set<String> recStack = new HashSet<>();
        List<String> cyclePairs = new ArrayList<>();

        for (String node : adj.keySet()) {
            if (!visited.contains(node)) {
                checkCycleDfs(node, adj, visited, recStack, cyclePairs);
            }
        }
        return cyclePairs;
    }

    private void checkCycleDfs(String node, Map<String, List<String>> adj,
                               Set<String> visited, Set<String> recStack, List<String> cyclePairs) {
        visited.add(node);
        recStack.add(node);

        List<String> neighbors = adj.getOrDefault(node, Collections.emptyList());
        for (String neighbor : neighbors) {
            if (recStack.contains(neighbor)) {
                String pair = node + " <-> " + neighbor;
                if (!cyclePairs.contains(pair)) {
                    cyclePairs.add(pair);
                }
            } else if (!visited.contains(neighbor)) {
                checkCycleDfs(neighbor, adj, visited, recStack, cyclePairs);
            }
        }

        recStack.remove(node);
    }
}
