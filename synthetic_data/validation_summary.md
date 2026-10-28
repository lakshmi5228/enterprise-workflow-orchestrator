# Synthetic Dataset Validation Summary

**Seed:** `20261022` | **Scale:** `1.0` | **Generated:** 2026-10-28

---

## 1. Record Counts

| Entity | Records |
|--------|---------|
| Employees | 1,000 |
| Onboarding Cases | 1,000 |
| Tasks | 8,000 |
| Task Dependencies | 7,018+ |
| Access Grants | 1,500 |
| Assets | 1,205 (1,200 base + 5 injected duplicates) |
| Offboarding Cases | 139 |
| Audit Events | 1,000 |
| Ground Truth Anomalies | **25** |

---

## 2. Intentional Anomalies (25 Total)

| Rule | Type | Count | Entity IDs |
|------|------|-------|-----------|
| ANOM-001 | ACCESS_GRANTED_BEFORE_START_APPROVAL | 5 | GRANT-EMP-000079-335, GRANT-EMP-000101-425, GRANT-EMP-000313-1274, GRANT-EMP-000326-1330, GRANT-EMP-000335-1365 |
| ANOM-002 | DUPLICATE_ASSET_ASSIGNMENT | 5 | AST-000001 through AST-000005 (serial duplicated as AST-990000..AST-990004) |
| ANOM-003 | TERMINATED_EMPLOYEE_RETAINS_GRANT | 6 | GRANT-EMP-000006-021, GRANT-EMP-000008-033, GRANT-EMP-000020-087, GRANT-EMP-000038-164, GRANT-EMP-000045-194, GRANT-EMP-000059-251 |
| ANOM-004 | TASK_DEPENDENCY_CYCLE | 4 | Cases ONB-2026-000001 through ONB-2026-000004 (dep IDs 8001, 8003, 8005, 8007) |
| ANOM-005 | RECOVERED_ASSET_REMAINS_ASSIGNED | 5 | AST-000011 through AST-000015 |
| **Total** | | **25** | |

> **Note:** Rule 2 (AST-000001..5) and Rule 5 (AST-000011..15) operate on completely **disjoint** asset ID ranges, ensuring 1:1 entity-to-anomaly mapping with no overlap.

---

## 3. Validation Results (11 Entities)

All checks run by `validate_dataset()` in `generator.py`:

| Check | Entities Covered | Result |
|-------|-----------------|--------|
| Primary Key Uniqueness | All 11 entities | ✅ PASS |
| FK: JobRole → Department | JobRole | ✅ PASS |
| FK: Employee → Department, JobRole, Manager | Employee | ✅ PASS |
| FK: OnboardingCase → Employee (3 roles) | OnboardingCase | ✅ PASS |
| FK: Task → OnboardingCase | Task | ✅ PASS |
| FK: TaskDependency → Task (both task_id + prereq) | TaskDependency | ✅ PASS |
| FK: AccessGrant → Employee, AccessRole | AccessGrant | ✅ PASS |
| FK: Asset → Employee (assigned_to) | Asset | ✅ PASS |
| FK: OffboardingCase → Employee (3 roles) | OffboardingCase | ✅ PASS |
| Enum: Employee employment_type, employment_status | Employee | ✅ PASS |
| Enum: OnboardingCase stage, status | OnboardingCase | ✅ PASS |
| Enum: Task status, priority, category | Task | ✅ PASS |
| Enum: TaskDependency dependency_type | TaskDependency | ✅ PASS |
| Enum: AccessGrant status, approval_status | AccessGrant | ✅ PASS |
| Enum: Asset status, condition_status | Asset | ✅ PASS |
| Enum: OffboardingCase termination_type, status | OffboardingCase | ✅ PASS |
| Date Order: hire_date ≤ start_date | Employee | ✅ PASS |
| Date Order: start_date ≤ end_date | Employee | ✅ PASS |
| Date Order: assigned_date ≤ acknowledged_at | Asset | ✅ PASS |
| Cycle Detection Rule 4 | Task dependencies | ✅ 4 cycles (matching GT) |
| Reconciliation Rule 1 | AccessGrant | ✅ 5/5 detected |
| Reconciliation Rule 2 | Asset (serial) | ✅ 5/5 detected |
| Reconciliation Rule 3 | AccessGrant | ✅ 6/6 detected |
| Reconciliation Rule 4 | TaskDependency | ✅ 4/4 cases with cycles |
| Reconciliation Rule 5 | Asset (recovered) | ✅ 5/5 detected |

**Unexpected errors: 0** | **Intentional anomalies: exactly 25**

---

## 4. Reproducibility

Generator run twice with seed `20261022` and scale `1.0`:

| Result | Status |
|--------|--------|
| Total files | 25 |
| SHA-256 mismatches | 0 |
| Verdict | ✅ 100% bit-for-bit reproducible |

### SHA-256 Checksums (seed 20261022)

| File | SHA-256 |
|------|---------|
| access_grants.csv | `083b1e3f7371aac1cccbeb9d06734b72aff4a77ef5bd3fbb0326c2abeafb53a3` |
| access_grants.json | `a54da24586741a79bc2b185f3b621c33200764fd5157e1b89905af4f47479680` |
| access_roles.csv | `1215f81e1a1ca29b1560642190fe016a33daff07d61dae4a7714de0cda0c3c35` |
| access_roles.json | `8ca3c32ff8bccf5f166955c0c6d24bd6d5c0bbc8ce9dd310b988a86a3c4b344b` |
| assets.csv | `17c94444e2d2a6d088902d3c5fe6694ed64f08a4775a344d53ded2fc4e0a0648` |
| assets.json | `1f53b4b83e57e32659f83b817e7a91bedbcc2a9e8a0299959723284486c5fcca` |
| audit_events.csv | `b95a59b4df074b0752120cafd79740105e0634a5c383192ef5ce43ccf3b8322f` |
| audit_events.json | `5a5b5de0bd9cc8b37e029cd52a860119737e0e3cdf8274ad0c4a96f162728ee8` |
| departments.csv | `418e54823cbe176e1285c75335217292210ef95ce25f8e9928cdc61c379959be` |
| departments.json | `f0ee81fa46cff0f681fc76f5c9ed68a2dfd481ee035769af3e65422bcdc519d6` |
| employees.csv | `3c027075444544eaa45479c70a573679aa29322834ba84e02e5edd45fc08ffed` |
| employees.json | `95a422cdedac37709feb059ae035a24dc3cc3f3aaa59a0e6a630ba5cc268a89e` |
| ground_truth.csv | `4c110d8e1a36085aeebea64eafd8b8178fae42d7630b1a586a2cbf5d72715cba` |
| ground_truth.json | `c4f7005d3f8129246b9526deebd65ac315228c409ef7d994194cd83a13d3241c` |
| job_roles.csv | `2791ea311bedb242ea1c9ace59c08fb33a23b2d7530db3e7443dde90e304b5ff` |
| job_roles.json | `be56fe87c2b7d272e75b87126f58357cad933c9528fda3287822aedf6c1b45a3` |
| offboarding_cases.csv | `796f6b713147c2a931df413a3eb881f4a88c39f549957d81e39b4767ebf963e6` |
| offboarding_cases.json | `f46be62abe7b924869a140834d752ee9cbc23b18badaf8a58e3f879f8b1f8367` |
| onboarding_cases.csv | `c812fa0acc23270a469e12b814afe7c992fe68c7c4e73f45a178556d6d36221e` |
| onboarding_cases.json | `50ae8abb34f552840e782ce4d69da3f78e378aa16382b14282e8d511535a5cce` |
| seed_data.sql | `fb997a934d8bc5e693d33670f4bdfca26e6834e0fa4b4ba815f5d155bb766a1b` |
| task_dependencies.csv | `b5cda028684934bc445138374c0f6b046fc0aa0bfe6ae29e1c2de05b3ea34723` |
| task_dependencies.json | `3036c6c1ae20cea2a3b9aeac90c177305ede08448d23648ff5a3294da2891d8d` |
| tasks.csv | `8ab0b92a8128fd4e9e03890f6ed7191234239db14d9ef9ce49916900d2f7c30c` |
| tasks.json | `766c2d6d87991938ea8e89598304ac8f435a93b4e41f4c94e04d1453e92d13bb` |
