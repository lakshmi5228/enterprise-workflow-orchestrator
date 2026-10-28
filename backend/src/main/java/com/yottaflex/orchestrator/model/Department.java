package com.yottaflex.orchestrator.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "departments")
public class Department {

    @Id
    @Column(name = "id", length = 32, nullable = false)
    private String id;

    @Column(name = "name", length = 100, nullable = false, unique = true)
    private String name;

    @Column(name = "cost_center", length = 32, nullable = false, unique = true)
    private String costCenter;

    @Column(name = "hr_partner_id", length = 32)
    private String hrPartnerId;

    @Column(name = "it_partner_id", length = 32)
    private String itPartnerId;

    public Department() {
    }

    public Department(String id, String name, String costCenter, String hrPartnerId, String itPartnerId) {
        this.id = id;
        this.name = name;
        this.costCenter = costCenter;
        this.hrPartnerId = hrPartnerId;
        this.itPartnerId = itPartnerId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCostCenter() {
        return costCenter;
    }

    public void setCostCenter(String costCenter) {
        this.costCenter = costCenter;
    }

    public String getHrPartnerId() {
        return hrPartnerId;
    }

    public void setHrPartnerId(String hrPartnerId) {
        this.hrPartnerId = hrPartnerId;
    }

    public String getItPartnerId() {
        return itPartnerId;
    }

    public void setItPartnerId(String itPartnerId) {
        this.itPartnerId = itPartnerId;
    }
}

