package com.yottaflex.orchestrator.repository;

import com.yottaflex.orchestrator.model.TaskDependency;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TaskDependencyRepository extends JpaRepository<TaskDependency, Long> {

    List<TaskDependency> findByTaskId(String taskId);

    List<TaskDependency> findByPrerequisiteTaskId(String prerequisiteTaskId);
}

