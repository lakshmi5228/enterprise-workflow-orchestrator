package com.yottaflex.orchestrator.repository;

import com.yottaflex.orchestrator.model.AccessGrant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AccessGrantRepository extends JpaRepository<AccessGrant, String> {

    List<AccessGrant> findByEmployeeId(String employeeId);

    List<AccessGrant> findByStatus(String status);

    List<AccessGrant> findByAccessRoleId(String accessRoleId);

    Page<AccessGrant> findByStatusContainingIgnoreCaseOrEmployeeIdContainingIgnoreCase(
            String status, String employeeId, Pageable pageable);

    @Query("SELECT COUNT(g) FROM AccessGrant g WHERE g.status = 'PROVISIONED'")
    long countProvisionedGrants();

    @Query("SELECT COUNT(g) FROM AccessGrant g WHERE g.approvalStatus = 'PENDING_APPROVAL'")
    long countPendingApprovalGrants();

    @Query("SELECT COUNT(g) FROM AccessGrant g WHERE g.status = 'REVOKED'")
    long countRevokedGrants();
}

