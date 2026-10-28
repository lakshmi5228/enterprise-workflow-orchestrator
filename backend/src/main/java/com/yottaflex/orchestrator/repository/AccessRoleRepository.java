package com.yottaflex.orchestrator.repository;

import com.yottaflex.orchestrator.model.AccessRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AccessRoleRepository extends JpaRepository<AccessRole, String> {
    List<AccessRole> findBySystemName(String systemName);
}

