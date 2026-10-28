package com.yottaflex.orchestrator.repository;

import com.yottaflex.orchestrator.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, String> {
}

