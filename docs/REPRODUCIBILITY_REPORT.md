# Reproducibility & Integrity Report
**Seed:** `20261022`
**Architecture:** Deterministic Pseudo-Random Generation (`random.Random(20261022)`)
**Verification Status:** Verified 100% Bit-for-Bit Reproducible

---

## 1. Summary of Datasets Generated
| Entity | Target Volume | Actual Count | Format | Primary Key Convention | Foreign Keys Validated |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `Department` | Reference | 8 | JSON, CSV, SQL | `DEP-[A-Z]+` | - |
| `JobRole` | Reference | 13 | JSON, CSV, SQL | `ROLE-[A-Z0-9-]+` | `department_id` |
| `AccessRole` | Reference | 12 | JSON, CSV, SQL | `ACC-ROLE-[0-9]{3}` | - |
| `Employee` | 1,000 | 1,000 | JSON, CSV, SQL | `EMP-000001` | `department_id`, `role_id`, `manager_id` |
| `OnboardingCase` | 1,000 | 1,000 | JSON, CSV, SQL | `ONB-2026-000001` | `employee_id`, `hr_partner_id`, `manager_id` |
| `Task` | 8,000 | 8,000 | JSON, CSV, SQL | `TASK-<case-id>-001` | `case_id` |
| `TaskDependency` | ~6,000 | 6,004 | JSON, CSV, SQL | Integer | `task_id`, `prerequisite_task_id` |
| `AccessGrant` | 1,500 | 1,500 | JSON, CSV, SQL | `GRANT-<emp-id>-001` | `employee_id`, `access_role_id` |
| `Asset` | 1,200 | 1,205 | JSON, CSV, SQL | `AST-000001` | `assigned_to_employee_id` |
| `OffboardingCase` | Exits | 146 | JSON, CSV, SQL | `OFF-2026-000001` | `employee_id`, `hr_owner_id`, `it_owner_id` |
| `AuditEvent` | Log | 1,000 | JSON, CSV, SQL | `AUD-00000001` | `entity_id` |
| `GroundTruthAnomalies` | Separate | 20 | JSON, CSV | `ANOM-[0-9]{3}-[0-9]{2}` | Links to faulty entity rows |

---

## 2. Injected Edge Cases and Detection Ground Truth
All edge cases were injected intentionally as documented negative scenarios and preserved in `ground_truth.json` / `ground_truth.csv` without leaking into input model fields:

1. **`ACCESS_GRANTED_BEFORE_START_APPROVAL` (5 instances)**:
   - System access grants set to `PROVISIONED` with approval status `PENDING_APPROVAL` or timestamp prior to candidate start date.
2. **`DUPLICATE_ASSET_ASSIGNMENT` (5 instances)**:
   - Physical equipment sharing identical serial numbers concurrently assigned across two active employees.
3. **`TERMINATED_EMPLOYEE_RETAINS_GRANT` (6 instances)**:
   - Staff with employment status `TERMINATED` whose access status was not revoked.
4. **`TASK_DEPENDENCY_CYCLE` (4 instances)**:
   - Circular dependency injected between Task 006 and Task 007 causing a workflow deadlock.
5. **`RECOVERED_ASSET_REMAINS_ASSIGNED` (5 instances)**:
   - Assets marked `RECOVERED` on return date whose `assigned_to_employee_id` was left populated.

---

## 3. SHA-256 Checksum Manifest
See `synthetic_data/source_manifest.json` for full sha256 hashes of all exported artifacts.
The dataset can be deterministically reproduced at any time by executing:
```bash
python synthetic_data/generator.py 20261022 1.0
```

