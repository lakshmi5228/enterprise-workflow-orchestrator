package com.yottaflex.orchestrator.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @Column(name = "id", length = 32, nullable = false)
    private String id; // Format: EMP-000001

    @Column(name = "first_name", length = 64, nullable = false)
    private String firstName;

    @Column(name = "last_name", length = 64, nullable = false)
    private String lastName;

    @Column(name = "email", length = 120, nullable = false, unique = true)
    private String email;

    @Column(name = "department_id", length = 32, nullable = false)
    private String departmentId;

    @Column(name = "role_id", length = 32, nullable = false)
    private String roleId;

    @Column(name = "manager_id", length = 32)
    private String managerId;

    @Column(name = "employment_type", length = 24, nullable = false)
    private String employmentType; // FULL_TIME, CONTRACTOR, INTERN

    @Column(name = "employment_status", length = 24, nullable = false)
    private String employmentStatus; // OFFER_ACCEPTED, PREBOARDING, ACTIVE, NOTICE_PERIOD, TERMINATED

    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "location", length = 64, nullable = false)
    private String location;

    public Employee() {
    }

    public Employee(String id, String firstName, String lastName, String email, String departmentId,
                    String roleId, String managerId, String employmentType, String employmentStatus,
                    LocalDate hireDate, LocalDate startDate, LocalDate endDate, String location) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.departmentId = departmentId;
        this.roleId = roleId;
        this.managerId = managerId;
        this.employmentType = employmentType;
        this.employmentStatus = employmentStatus;
        this.hireDate = hireDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.location = location;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getManagerId() {
        return managerId;
    }

    public void setManagerId(String managerId) {
        this.managerId = managerId;
    }

    public String getEmploymentType() {
        return employmentType;
    }

    public void setEmploymentType(String employmentType) {
        this.employmentType = employmentType;
    }

    public String getEmploymentStatus() {
        return employmentStatus;
    }

    public void setEmploymentStatus(String employmentStatus) {
        this.employmentStatus = employmentStatus;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}

