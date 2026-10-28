package com.yottaflex.orchestrator.repository;

import com.yottaflex.orchestrator.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {

    Optional<Employee> findByEmail(String email);

    List<Employee> findByDepartmentId(String departmentId);

    List<Employee> findByEmploymentStatus(String employmentStatus);

    List<Employee> findByEmploymentType(String employmentType);

    List<Employee> findByManagerId(String managerId);

    Page<Employee> findByEmploymentStatusContainingIgnoreCaseOrFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String status, String firstName, String lastName, Pageable pageable);

    @Query("SELECT COUNT(e) FROM Employee e WHERE e.employmentStatus = 'ACTIVE'")
    long countActiveEmployees();

    @Query("SELECT COUNT(e) FROM Employee e WHERE e.employmentStatus = 'TERMINATED'")
    long countTerminatedEmployees();

    @Query("SELECT COUNT(e) FROM Employee e WHERE e.employmentStatus IN ('OFFER_ACCEPTED', 'PREBOARDING')")
    long countPreboardingEmployees();
}

