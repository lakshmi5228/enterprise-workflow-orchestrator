package com.yottaflex.orchestrator.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yottaflex.orchestrator.model.*;
import com.yottaflex.orchestrator.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class SyntheticDataLoaderService implements CommandLineRunner {

    private final DepartmentRepository departmentRepository;
    private final JobRoleRepository jobRoleRepository;
    private final AccessRoleRepository accessRoleRepository;
    private final EmployeeRepository employeeRepository;
    private final OnboardingCaseRepository onboardingCaseRepository;
    private final CaseTaskRepository taskRepository;
    private final TaskDependencyRepository dependencyRepository;
    private final AccessGrantRepository accessGrantRepository;
    private final AssetRepository assetRepository;
    private final OffboardingCaseRepository offboardingCaseRepository;
    private final AuditEventRepository auditEventRepository;
    private final ObjectMapper objectMapper;

    @Value("${synthetic.data.dir:../synthetic_data/exports}")
    private String syntheticDataDir;

    public SyntheticDataLoaderService(DepartmentRepository departmentRepository,
                                      JobRoleRepository jobRoleRepository,
                                      AccessRoleRepository accessRoleRepository,
                                      EmployeeRepository employeeRepository,
                                      OnboardingCaseRepository onboardingCaseRepository,
                                      CaseTaskRepository taskRepository,
                                      TaskDependencyRepository dependencyRepository,
                                      AccessGrantRepository accessGrantRepository,
                                      AssetRepository assetRepository,
                                      OffboardingCaseRepository offboardingCaseRepository,
                                      AuditEventRepository auditEventRepository,
                                      ObjectMapper objectMapper) {
        this.departmentRepository = departmentRepository;
        this.jobRoleRepository = jobRoleRepository;
        this.accessRoleRepository = accessRoleRepository;
        this.employeeRepository = employeeRepository;
        this.onboardingCaseRepository = onboardingCaseRepository;
        this.taskRepository = taskRepository;
        this.dependencyRepository = dependencyRepository;
        this.accessGrantRepository = accessGrantRepository;
        this.assetRepository = assetRepository;
        this.offboardingCaseRepository = offboardingCaseRepository;
        this.auditEventRepository = auditEventRepository;
        this.objectMapper = objectMapper;
        this.objectMapper.setPropertyNamingStrategy(com.fasterxml.jackson.databind.PropertyNamingStrategies.SNAKE_CASE);
        this.objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    }

    @Override
    public void run(String... args) {
        if (employeeRepository.count() == 0) {
            System.out.println("[*] Database is empty. Loading baseline synthetic dataset...");
            loadDataset();
        } else {
            System.out.println("[+] Database already contains " + employeeRepository.count() + " employee records.");
        }
    }

    // Try multiple candidate paths for the synthetic exports directory.
    // Works both when running from IDE (project root) and from Maven surefire (backend/ subdir).
    private File resolveDataDirectory() {
        String userDir = System.getProperty("user.dir");

        List<String> candidates = new ArrayList<>();
        candidates.add(syntheticDataDir);                                      // configured value
        candidates.add("synthetic_data/exports");                              // run from project root
        candidates.add("../synthetic_data/exports");                           // run from backend/
        candidates.add(userDir + "/synthetic_data/exports");                   // absolute from CWD
        candidates.add(userDir + "/../synthetic_data/exports");                // CWD parent
        // Walk up until we find the yottaflex-project root
        File cur = new File(userDir);
        for (int i = 0; i < 4; i++) {
            File candidate = new File(cur, "synthetic_data/exports");
            candidates.add(candidate.getAbsolutePath());
            if (cur.getParentFile() != null) cur = cur.getParentFile();
        }

        for (String path : candidates) {
            File dir = new File(path);
            try {
                File normalized = dir.getCanonicalFile();
                if (normalized.exists() && normalized.isDirectory()) {
                    System.out.println("[+] Resolved synthetic data dir: " + normalized.getAbsolutePath());
                    return normalized;
                }
            } catch (Exception ignored) {}
        }
        return null;
    }

    @Transactional
    public synchronized boolean loadDataset() {
        File dir = resolveDataDirectory();
        if (dir == null) {
            System.err.println("[-] Synthetic data directory not found. Tried: " + syntheticDataDir
                    + ", synthetic_data/exports, ../synthetic_data/exports, and absolute sibling paths.");
            return false;
        }

        try {
            System.out.println("[*] Reading synthetic export files from: " + dir.getAbsolutePath());

            // 1. Departments
            File deptFile = new File(dir, "departments.json");
            if (deptFile.exists()) {
                List<Department> list = objectMapper.readValue(deptFile, new TypeReference<List<Department>>() {});
                departmentRepository.saveAll(list);
            }

            // 2. Job Roles
            File rolesFile = new File(dir, "job_roles.json");
            if (rolesFile.exists()) {
                List<JobRole> list = objectMapper.readValue(rolesFile, new TypeReference<List<JobRole>>() {});
                jobRoleRepository.saveAll(list);
            }

            // 3. Access Roles
            File accRolesFile = new File(dir, "access_roles.json");
            if (accRolesFile.exists()) {
                List<AccessRole> list = objectMapper.readValue(accRolesFile, new TypeReference<List<AccessRole>>() {});
                accessRoleRepository.saveAll(list);
            }

            // 4. Employees
            File empFile = new File(dir, "employees.json");
            if (empFile.exists()) {
                List<Employee> list = objectMapper.readValue(empFile, new TypeReference<List<Employee>>() {});
                employeeRepository.saveAll(list);
            }

            // 5. Onboarding Cases
            File onbFile = new File(dir, "onboarding_cases.json");
            if (onbFile.exists()) {
                List<OnboardingCase> list = objectMapper.readValue(onbFile, new TypeReference<List<OnboardingCase>>() {});
                onboardingCaseRepository.saveAll(list);
            }

            // 6. Tasks
            File taskFile = new File(dir, "tasks.json");
            if (taskFile.exists()) {
                List<CaseTask> list = objectMapper.readValue(taskFile, new TypeReference<List<CaseTask>>() {});
                taskRepository.saveAll(list);
            }

            // 7. Dependencies
            File depFile = new File(dir, "task_dependencies.json");
            if (depFile.exists()) {
                List<TaskDependency> list = objectMapper.readValue(depFile, new TypeReference<List<TaskDependency>>() {});
                dependencyRepository.saveAll(list);
            }

            // 8. Access Grants
            File grantFile = new File(dir, "access_grants.json");
            if (grantFile.exists()) {
                List<AccessGrant> list = objectMapper.readValue(grantFile, new TypeReference<List<AccessGrant>>() {});
                accessGrantRepository.saveAll(list);
            }

            // 9. Assets
            File assetFile = new File(dir, "assets.json");
            if (assetFile.exists()) {
                List<Asset> list = objectMapper.readValue(assetFile, new TypeReference<List<Asset>>() {});
                assetRepository.saveAll(list);
            }

            // 10. Offboarding Cases
            File offFile = new File(dir, "offboarding_cases.json");
            if (offFile.exists()) {
                List<OffboardingCase> list = objectMapper.readValue(offFile, new TypeReference<List<OffboardingCase>>() {});
                offboardingCaseRepository.saveAll(list);
            }

            // 11. Audit Events
            File auditFile = new File(dir, "audit_events.json");
            if (auditFile.exists()) {
                List<AuditEvent> list = objectMapper.readValue(auditFile, new TypeReference<List<AuditEvent>>() {});
                auditEventRepository.saveAll(list);
            }

            System.out.println("[+] Successfully seeded database with all 11 entities!");
            return true;
        } catch (Exception e) {
            System.err.println("[-] Error loading synthetic data: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
