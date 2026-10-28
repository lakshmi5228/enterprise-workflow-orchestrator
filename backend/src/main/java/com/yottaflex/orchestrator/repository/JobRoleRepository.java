package com.yottaflex.orchestrator.repository;

import com.yottaflex.orchestrator.model.JobRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JobRoleRepository extends JpaRepository<JobRole, String> {
    List<JobRole> findByDepartmentId(String departmentId);
}

