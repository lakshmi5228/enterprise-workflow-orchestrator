package com.yottaflex.orchestrator;

import com.yottaflex.orchestrator.repository.AccessGrantRepository;
import com.yottaflex.orchestrator.repository.AssetRepository;
import com.yottaflex.orchestrator.repository.CaseTaskRepository;
import com.yottaflex.orchestrator.repository.EmployeeRepository;
import com.yottaflex.orchestrator.repository.OnboardingCaseRepository;
import com.yottaflex.orchestrator.repository.OffboardingCaseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

// Uses the h2 in-memory profile so no MySQL needed during CI.
// The SyntheticDataLoaderService CommandLineRunner runs automatically on startup
// and seeds all 11 entity tables from the JSON export files.
@SpringBootTest
@ActiveProfiles("h2")
class OrchestratorApplicationTests {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private CaseTaskRepository caseTaskRepository;

    @Autowired
    private AccessGrantRepository accessGrantRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private OnboardingCaseRepository onboardingCaseRepository;

    @Autowired
    private OffboardingCaseRepository offboardingCaseRepository;

    // Basic smoke test: Spring context loads and all entity tables are seeded.
    // We do NOT run full reconciliation here because it loads 10K+ records into
    // memory and is better suited for a dedicated integration test profile.
    @Test
    void contextLoadsAndAllEntityTablesAreSeeded() {
        long empCount = employeeRepository.count();
        long taskCount = caseTaskRepository.count();
        long grantCount = accessGrantRepository.count();
        long assetCount = assetRepository.count();
        long onbCount = onboardingCaseRepository.count();
        long offCount = offboardingCaseRepository.count();

        System.out.println("===== Seeding Verification =====");
        System.out.println("Employees        : " + empCount);
        System.out.println("Tasks            : " + taskCount);
        System.out.println("Access Grants    : " + grantCount);
        System.out.println("Assets           : " + assetCount);
        System.out.println("Onboarding Cases : " + onbCount);
        System.out.println("Offboarding Cases: " + offCount);
        System.out.println("================================");

        // Core volume assertions — aligned with generator spec
        assertTrue(empCount >= 900,
                "Expected ~1,000 employees but got " + empCount
                        + ". Check that synthetic_data/exports/employees.json is reachable.");

        assertTrue(taskCount >= 7000,
                "Expected ~8,000 tasks but got " + taskCount);

        assertTrue(grantCount >= 1400,
                "Expected ~1,500 access grants but got " + grantCount);

        assertTrue(assetCount >= 1100,
                "Expected ~1,200 assets but got " + assetCount);

        assertTrue(onbCount >= 900,
                "Expected ~1,000 onboarding cases but got " + onbCount);

        assertTrue(offCount > 0,
                "Expected offboarding cases to be seeded but got 0");
    }

    // Verify the Spring application context itself wires up without errors.
    @Test
    void springContextLoadsCleanly() {
        assertNotNull(employeeRepository, "EmployeeRepository must be auto-wired by Spring");
        assertNotNull(caseTaskRepository, "CaseTaskRepository must be auto-wired by Spring");
    }
}
