# Reproducibility & Integrity Report
**Seed:** `20261022`
**Architecture:** Deterministic Pseudo-Random Generation (`random.Random(20261022)`)
**Verification Status:** ✅ Verified 100% Bit-for-Bit Reproducible

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
| `TaskDependency` | ~6,000 | 6,004+ | JSON, CSV, SQL | Integer | `task_id`, `prerequisite_task_id` |
| `AccessGrant` | 1,500 | 1,500 | JSON, CSV, SQL | `GRANT-<emp-id>-001` | `employee_id`, `access_role_id` |
| `Asset` | 1,200 | 1,205 | JSON, CSV, SQL | `AST-000001` | `assigned_to_employee_id` |
| `OffboardingCase` | Exits | **139** | JSON, CSV, SQL | `OFF-2026-000001` | `employee_id`, `hr_owner_id`, `it_owner_id` |
| `AuditEvent` | Log | 1,000 | JSON, CSV, SQL | `AUD-00000001` | `entity_id` |
| `GroundTruthAnomalies` | Separate | **25** | JSON, CSV | `ANOM-[0-9]{3}-[0-9]{2}` | Links to faulty entity rows |

---

## 2. Injected Edge Cases and Detection Ground Truth

All edge cases were injected intentionally as documented negative scenarios and preserved in `ground_truth.json` / `ground_truth.csv` without leaking into input model fields:

1. **`ACCESS_GRANTED_BEFORE_START_APPROVAL` (5 instances)**:
   - System access grants set to `PROVISIONED` with approval status `PENDING_APPROVAL` or timestamp prior to candidate start date.
   - Entities: `GRANT-EMP-000079-335`, `GRANT-EMP-000101-425`, `GRANT-EMP-000313-1274`, `GRANT-EMP-000326-1330`, `GRANT-EMP-000335-1365`

2. **`DUPLICATE_ASSET_ASSIGNMENT` (5 instances)**:
   - Physical equipment sharing identical serial numbers concurrently assigned across two active employees.
   - Primary entities: `AST-000001` through `AST-000005` (duplicate records: `AST-990000` through `AST-990004`)

3. **`TERMINATED_EMPLOYEE_RETAINS_GRANT` (6 instances)**:
   - Staff with employment status `TERMINATED` whose access status was not revoked.
   - Entities: `GRANT-EMP-000006-021`, `GRANT-EMP-000008-033`, `GRANT-EMP-000020-087`, `GRANT-EMP-000038-164`, `GRANT-EMP-000045-194`, `GRANT-EMP-000059-251`

4. **`TASK_DEPENDENCY_CYCLE` (4 instances)**:
   - Circular dependency injected between Task 006 and Task 007 causing a workflow deadlock in onboarding cases `ONB-2026-000001` through `ONB-2026-000004`.

5. **`RECOVERED_ASSET_REMAINS_ASSIGNED` (5 instances)**:
   - Assets marked `RECOVERED` on return date whose `assigned_to_employee_id` was left populated.
   - Entities: `AST-000011` through `AST-000015` (disjoint from Rule 2 assets)

> **Asset Disjointness:** Rule 2 uses `AST-000001..AST-000005`, Rule 5 uses `AST-000011..AST-000015`. These ranges are fully disjoint ensuring 1:1 entity-to-anomaly mapping.

**Total: 5 + 5 + 6 + 4 + 5 = 25 intentional anomalies**

---

## 3. Validation Results

`validate_dataset()` in `generator.py` runs comprehensive validation across all 11 entities:

- ✅ **Primary Key Uniqueness**: 100% passed (11 entities)
- ✅ **Foreign Key Integrity**: 100% passed (all 11+ FK relationships)
- ✅ **Enum Domain Constraints**: 100% passed (all status/type/category fields)
- ✅ **Date Ordering**: 100% passed (hire_date ≤ start_date ≤ end_date)
- ✅ **Cycle Detection (Rule 4)**: 4 cycles detected, 0 unexpected cases
- ✅ **Reconciliation Rules 1–5**: All 25 anomalies matched, 0 unexpected errors
- ✅ **Intentional anomalies**: Exactly 25 (5 + 5 + 6 + 4 + 5)

---

## 4. SHA-256 Checksum Manifest

**Reproducibility verified:** Running `python synthetic_data/generator.py 20261022 1.0` twice produces identical SHA-256 checksums for all 25 exported files.

| File | SHA-256 |
|------|---------|
| `access_grants.csv` | `083b1e3f7371aac1cccbeb9d06734b72aff4a77ef5bd3fbb0326c2abeafb53a3` |
| `access_grants.json` | `a54da24586741a79bc2b185f3b621c33200764fd5157e1b89905af4f47479680` |
| `access_roles.csv` | `1215f81e1a1ca29b1560642190fe016a33daff07d61dae4a7714de0cda0c3c35` |
| `access_roles.json` | `8ca3c32ff8bccf5f166955c0c6d24bd6d5c0bbc8ce9dd310b988a86a3c4b344b` |
| `assets.csv` | `17c94444e2d2a6d088902d3c5fe6694ed64f08a4775a344d53ded2fc4e0a0648` |
| `assets.json` | `1f53b4b83e57e32659f83b817e7a91bedbcc2a9e8a0299959723284486c5fcca` |
| `audit_events.csv` | `b95a59b4df074b0752120cafd79740105e0634a5c383192ef5ce43ccf3b8322f` |
| `audit_events.json` | `5a5b5de0bd9cc8b37e029cd52a860119737e0e3cdf8274ad0c4a96f162728ee8` |
| `departments.csv` | `418e54823cbe176e1285c75335217292210ef95ce25f8e9928cdc61c379959be` |
| `departments.json` | `f0ee81fa46cff0f681fc76f5c9ed68a2dfd481ee035769af3e65422bcdc519d6` |
| `employees.csv` | `3c027075444544eaa45479c70a573679aa29322834ba84e02e5edd45fc08ffed` |
| `employees.json` | `95a422cdedac37709feb059ae035a24dc3cc3f3aaa59a0e6a630ba5cc268a89e` |
| `ground_truth.csv` | `4c110d8e1a36085aeebea64eafd8b8178fae42d7630b1a586a2cbf5d72715cba` |
| `ground_truth.json` | `c4f7005d3f8129246b9526deebd65ac315228c409ef7d994194cd83a13d3241c` |
| `job_roles.csv` | `2791ea311bedb242ea1c9ace59c08fb33a23b2d7530db3e7443dde90e304b5ff` |
| `job_roles.json` | `be56fe87c2b7d272e75b87126f58357cad933c9528fda3287822aedf6c1b45a3` |
| `offboarding_cases.csv` | `796f6b713147c2a931df413a3eb881f4a88c39f549957d81e39b4767ebf963e6` |
| `offboarding_cases.json` | `f46be62abe7b924869a140834d752ee9cbc23b18badaf8a58e3f879f8b1f8367` |
| `onboarding_cases.csv` | `c812fa0acc23270a469e12b814afe7c992fe68c7c4e73f45a178556d6d36221e` |
| `onboarding_cases.json` | `50ae8abb34f552840e782ce4d69da3f78e378aa16382b14282e8d511535a5cce` |
| `seed_data.sql` | `fb997a934d8bc5e693d33670f4bdfca26e6834e0fa4b4ba815f5d155bb766a1b` |
| `task_dependencies.csv` | `b5cda028684934bc445138374c0f6b046fc0aa0bfe6ae29e1c2de05b3ea34723` |
| `task_dependencies.json` | `3036c6c1ae20cea2a3b9aeac90c177305ede08448d23648ff5a3294da2891d8d` |
| `tasks.csv` | `8ab0b92a8128fd4e9e03890f6ed7191234239db14d9ef9ce49916900d2f7c30c` |
| `tasks.json` | `766c2d6d87991938ea8e89598304ac8f435a93b4e41f4c94e04d1453e92d13bb` |

The dataset can be deterministically reproduced at any time by executing:
```bash
python synthetic_data/generator.py 20261022 1.0
```
