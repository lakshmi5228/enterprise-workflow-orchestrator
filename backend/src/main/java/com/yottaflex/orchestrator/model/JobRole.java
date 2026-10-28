package com.yottaflex.orchestrator.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "job_roles")
public class JobRole {

    @Id
    @Column(name = "id", length = 32, nullable = false)
    private String id;

    @Column(name = "title", length = 120, nullable = false)
    private String title;

    @Column(name = "department_id", length = 32, nullable = false)
    private String departmentId;

    @Column(name = "level", length = 16, nullable = false)
    private String level;

    @Column(name = "equipment_tier", length = 32, nullable = false)
    private String equipmentTier;

    @Column(name = "base_access_profile", length = 64, nullable = false)
    private String baseAccessProfile;

    public JobRole() {
    }

    public JobRole(String id, String title, String departmentId, String level, String equipmentTier, String baseAccessProfile) {
        this.id = id;
        this.title = title;
        this.departmentId = departmentId;
        this.level = level;
        this.equipmentTier = equipmentTier;
        this.baseAccessProfile = baseAccessProfile;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getEquipmentTier() {
        return equipmentTier;
    }

    public void setEquipmentTier(String equipmentTier) {
        this.equipmentTier = equipmentTier;
    }

    public String getBaseAccessProfile() {
        return baseAccessProfile;
    }

    public void setBaseAccessProfile(String baseAccessProfile) {
        this.baseAccessProfile = baseAccessProfile;
    }
}

