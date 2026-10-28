# Enterprise Workflow Orchestration - Master Data Dictionary
**Domain:** Cross-functional Pre-boarding, Access Provisioning, Equipment Delivery, and Exit Orchestration across HR, IT, and Facilities.
**Standard Seed:** 20261022
**Standard Nomenclature:**
- Employee: `EMP-000001`
- Onboarding Case: `ONB-2026-000001`
- Task: `TASK-ONB-2026-000001-001`
- Access Grant: `GRANT-EMP-000001-001`
- Asset: `AST-000001`
- Offboarding Case: `OFF-2026-000001`

---

## 1. Department (`departments`)
Master organization unit classification.

| Field Name | Type | Req/Opt | Allowed Values | Units | Uniqueness | Validation Rule |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `id` | VARCHAR(32) | Required | Pattern `DEP-[A-Z0-9]+` (e.g., `DEP-ENG`, `DEP-SALES`) | String | PK, Unique | Primary key, immutable string code |
| `name` | VARCHAR(100) | Required | E.g. Engineering, Sales, Product, HR, Finance, IT | Text | Unique | 3-100 characters |
| `cost_center` | VARCHAR(32) | Required | Pattern `CC-[A-Z]+-[0-9]{3}` | Code | Unique | Valid financial cost center format |
| `hr_partner_id` | VARCHAR(32) | Optional | Foreign key to `employees.id` | ID | Non-unique | Must reference valid employee record |
| `it_partner_id` | VARCHAR(32) | Optional | Foreign key to `employees.id` | ID | Non-unique | Must reference valid employee record |

---

## 2. Role (`job_roles`)
Job profiles that determine base access policies and equipment tier requirements.

| Field Name | Type | Req/Opt | Allowed Values | Units | Uniqueness | Validation Rule |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `id` | VARCHAR(32) | Required | Pattern `ROLE-[A-Z0-9-]+` (e.g., `ROLE-SWE-01`) | String | PK, Unique | Primary key |
| `title` | VARCHAR(120) | Required | Descriptive job title | Text | Non-unique | 3-120 characters |
| `department_id` | VARCHAR(32) | Required | Foreign key to `departments.id` | ID | Non-unique | Must exist in `departments` |
| `level` | VARCHAR(16) | Required | `L1`, `L2`, `L3`, `L4`, `L5`, `EXEC` | Hierarchy | Non-unique | Enum validation |
| `equipment_tier` | VARCHAR(32) | Required | `STANDARD_DEV`, `STANDARD_OFFICE`, `EXECUTIVE`, `FIELD_MOBILE` | Tier | Non-unique | Determines required asset kit |
| `base_access_profile`| VARCHAR(64) | Required | `DEVELOPER_PROFILE`, `BUSINESS_STANDARD`, `LEADERSHIP_CORE`, `HR_SECURE` | Profile | Non-unique | Mapping to standard system access bundles |

---

## 3. Employee (`employees`)
Primary individual identity record for candidates, active workers, and terminated staff.

| Field Name | Type | Req/Opt | Allowed Values | Units | Uniqueness | Validation Rule |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `id` | VARCHAR(32) | Required | Pattern `EMP-[0-9]{6}` (e.g. `EMP-000001`) | String | PK, Unique | Primary key, sequential deterministic format |
| `first_name` | VARCHAR(64) | Required | Alphabetic text | Text | Non-unique | 1-64 characters |
| `last_name` | VARCHAR(64) | Required | Alphabetic text | Text | Non-unique | 1-64 characters |
| `email` | VARCHAR(120) | Required | Standard RFC 5322 email | Text | Unique | Derived deterministically as `first.last@company.internal` |
| `department_id` | VARCHAR(32) | Required | Foreign key to `departments.id` | ID | Non-unique | Must exist in `departments` |
| `role_id` | VARCHAR(32) | Required | Foreign key to `job_roles.id` | ID | Non-unique | Must exist in `job_roles` |
| `manager_id` | VARCHAR(32) | Optional | Foreign key to `employees.id` | ID | Non-unique | Self-reference; cannot equal self; nullable for C-level |
| `employment_type` | VARCHAR(24) | Required | `FULL_TIME`, `CONTRACTOR`, `INTERN` | Enum | Non-unique | Governs access expiry and hardware requirements |
| `employment_status`| VARCHAR(24) | Required | `OFFER_ACCEPTED`, `PREBOARDING`, `ACTIVE`, `NOTICE_PERIOD`, `TERMINATED` | Lifecycle | Non-unique | Strict state-transition lifecycle |
| `hire_date` | DATE | Required | ISO-8601 `YYYY-MM-DD` | Date | Non-unique | Offer accept date <= start_date |
| `start_date` | DATE | Required | ISO-8601 `YYYY-MM-DD` | Date | Non-unique | First active working day; >= hire_date |
| `end_date` | DATE | Optional | ISO-8601 `YYYY-MM-DD` | Date | Non-unique | Populated if `TERMINATED` or `CONTRACT_END`; >= start_date |
| `location` | VARCHAR(64) | Required | `New York`, `San Francisco`, `London`, `Remote`, `Bangalore`, `Austin` | City | Non-unique | Used for facilities delivery routing |

---

## 4. OnboardingCase (`onboarding_cases`)
Workflow container tracking the pre-boarding and first-30-day lifecycle.

| Field Name | Type | Req/Opt | Allowed Values | Units | Uniqueness | Validation Rule |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `id` | VARCHAR(32) | Required | Pattern `ONB-YYYY-[0-9]{6}` (e.g. `ONB-2026-000001`) | String | PK, Unique | Primary key |
| `employee_id` | VARCHAR(32) | Required | Foreign key to `employees.id` | ID | Unique (1:1 active) | Must exist in `employees` |
| `candidate_name` | VARCHAR(128) | Required | Full candidate name | Text | Non-unique | Matches employee first + last |
| `stage` | VARCHAR(32) | Required | `PREBOARDING`, `IT_PROVISIONING`, `ASSET_DELIVERY`, `ORIENTATION`, `COMPLETED`, `CANCELLED` | Stage | Non-unique | Sequential workflow progression |
| `status` | VARCHAR(24) | Required | `PENDING`, `IN_PROGRESS`, `COMPLETED`, `BLOCKED`, `EXCEPTION` | State | Non-unique | Overall case health |
| `hr_partner_id` | VARCHAR(32) | Required | Foreign key to `employees.id` | ID | Non-unique | Assigned HR coordinator |
| `manager_id` | VARCHAR(32) | Required | Foreign key to `employees.id` | ID | Non-unique | Assigned direct manager |
| `scheduled_start_date` | DATE | Required | ISO-8601 `YYYY-MM-DD` | Date | Non-unique | Planned start date |
| `actual_start_date` | DATE | Optional | ISO-8601 `YYYY-MM-DD` | Date | Non-unique | Updated if delayed start occurs |
| `created_at` | TIMESTAMP | Required | ISO-8601 timestamp | Time | Non-unique | Timestamp of case initialization |
| `updated_at` | TIMESTAMP | Required | ISO-8601 timestamp | Time | Non-unique | Timestamp of most recent state update |

---

## 5. Task (`case_tasks`)
Atomic unit of work assigned across HR, IT, and Facilities.

| Field Name | Type | Req/Opt | Allowed Values | Units | Uniqueness | Validation Rule |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `id` | VARCHAR(64) | Required | Pattern `TASK-ONB-YYYY-[0-9]{6}-[0-9]{3}` | String | PK, Unique | Primary key |
| `case_id` | VARCHAR(32) | Required | Foreign key to `onboarding_cases.id` or `offboarding_cases.id` | ID | Non-unique | Must exist in parent case |
| `task_name` | VARCHAR(128) | Required | E.g. `Background Check Verification`, `Identity Verification`, `Laptop Allocation`, `Okta Account Creation`, `Hardware Dispatch`, `Direct Deposit Setup`, `Manager Check-In` | Text | Non-unique | Descriptive task identifier |
| `category` | VARCHAR(32) | Required | `HR_PREBOARDING`, `IT_ACCESS`, `EQUIPMENT_DELIVERY`, `ORIENTATION`, `IT_REVOCATION`, `ASSET_RECOVERY` | Domain | Non-unique | Functional stream |
| `assigned_role` | VARCHAR(32) | Required | `HR_PARTNER`, `IT_OPERATIONS`, `FACILITIES`, `MANAGER`, `EMPLOYEE` | Role | Non-unique | Operating queue |
| `assigned_to_user` | VARCHAR(64) | Optional | User identifier or email | Text | Non-unique | Specific worker handling task |
| `status` | VARCHAR(24) | Required | `PENDING`, `READY`, `IN_PROGRESS`, `WAITING_APPROVAL`, `COMPLETED`, `FAILED`, `OVERRIDDEN` | State | Non-unique | Prerequisites must be COMPLETED before moving to READY/IN_PROGRESS |
| `priority` | VARCHAR(16) | Required | `LOW`, `MEDIUM`, `HIGH`, `URGENT` | Rank | Non-unique | SLA priority |
| `due_date` | DATE | Required | ISO-8601 `YYYY-MM-DD` | Date | Non-unique | Must be within workflow window |
| `completed_at` | TIMESTAMP | Optional | ISO-8601 timestamp | Time | Non-unique | Mandatory when status is `COMPLETED` |
| `notes` | TEXT | Optional | Freeform operator notes | Text | Non-unique | Context and override log |
| `is_exception` | BOOLEAN | Required | `true`, `false` | Flag | Non-unique | Default false; true if flagged |

---

## 6. Dependency (`task_dependencies`)
Prerequisite links governing DAG execution order.

| Field Name | Type | Req/Opt | Allowed Values | Units | Uniqueness | Validation Rule |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `id` | BIGINT | Required | Auto-increment / deterministic integer | Integer | PK, Unique | Primary key |
| `task_id` | VARCHAR(64) | Required | Foreign key to `case_tasks.id` | ID | Composite unique | Dependent successor task |
| `prerequisite_task_id`| VARCHAR(64) | Required | Foreign key to `case_tasks.id` | ID | Composite unique | Mandatory predecessor task |
| `dependency_type` | VARCHAR(32) | Required | `FINISH_TO_START`, `APPROVAL_REQUIRED` | Type | Non-unique | Nature of dependency constraint |
| `is_mandatory` | BOOLEAN | Required | `true`, `false` | Flag | Non-unique | Default true |
| `created_at` | TIMESTAMP | Required | ISO-8601 timestamp | Time | Non-unique | Link creation time |

---

## 7. AccessRole (`access_roles`)
Catalog of managed IT systems and role definitions.

| Field Name | Type | Req/Opt | Allowed Values | Units | Uniqueness | Validation Rule |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `id` | VARCHAR(32) | Required | Pattern `ACC-ROLE-[0-9]{3}` | String | PK, Unique | Primary key |
| `system_name` | VARCHAR(64) | Required | `Active Directory`, `Okta`, `GitHub Enterprise`, `AWS Cloud`, `Workday HR`, `Salesforce`, `Jira/Confluence`, `Slack` | System | Non-unique | Target application platform |
| `role_name` | VARCHAR(64) | Required | Standard role name in system | Text | Non-unique | E.g. `Developer`, `Billing Admin`, `Standard User` |
| `access_level` | VARCHAR(24) | Required | `READ_ONLY`, `STANDARD_USER`, `POWER_USER`, `ADMIN`, `BILLING_ADMIN` | Level | Non-unique | Privilege tier |
| `approval_required` | BOOLEAN | Required | `true`, `false` | Flag | Non-unique | True for elevated/sensitive privileges |
| `requires_active_status`| BOOLEAN | Required | `true`, `false` | Flag | Non-unique | True if employment must be ACTIVE |

---

## 8. AccessGrant (`access_grants`)
Explicit instance of an IT system entitlement provisioned to an employee.

| Field Name | Type | Req/Opt | Allowed Values | Units | Uniqueness | Validation Rule |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `id` | VARCHAR(64) | Required | Pattern `GRANT-EMP-[0-9]{6}-[0-9]{3}` | String | PK, Unique | Primary key |
| `employee_id` | VARCHAR(32) | Required | Foreign key to `employees.id` | ID | Composite unique | Must exist in `employees` |
| `access_role_id` | VARCHAR(32) | Required | Foreign key to `access_roles.id` | ID | Composite unique | Must exist in `access_roles` |
| `status` | VARCHAR(24) | Required | `REQUESTED`, `APPROVED`, `PROVISIONED`, `REVOKED`, `EXPIRED`, `SUSPENDED` | Lifecycle | Non-unique | Cannot be `PROVISIONED` without approval or prior to employee start date |
| `approval_status` | VARCHAR(24) | Required | `PENDING_APPROVAL`, `APPROVED`, `REJECTED`, `AUTO_APPROVED` | Approval | Non-unique | Must be `APPROVED` or `AUTO_APPROVED` before provisioning |
| `requested_by` | VARCHAR(64) | Required | Identity string | Text | Non-unique | Requester record |
| `approved_by` | VARCHAR(64) | Optional | Identity string | Text | Non-unique | Approver signature |
| `provisioned_at` | TIMESTAMP | Optional | ISO-8601 timestamp | Time | Non-unique | Populated once active; must be >= start_date |
| `revoked_at` | TIMESTAMP | Optional | ISO-8601 timestamp | Time | Non-unique | Populated upon offboarding or revocation |
| `issue_flag` | VARCHAR(64) | Optional | Null in normal rows; tagged in ground truth edge cases | Flag | Non-unique | Anomaly classifier |

---

## 9. Asset (`assets`)
Physical equipment and hardware tracked through procurement, shipping, receipt, and recovery.

| Field Name | Type | Req/Opt | Allowed Values | Units | Uniqueness | Validation Rule |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `id` | VARCHAR(32) | Required | Pattern `AST-[0-9]{6}` (e.g. `AST-000001`) | String | PK, Unique | Primary key |
| `asset_tag` | VARCHAR(32) | Required | Pattern `TAG-[A-Z]+-[0-9]{6}` | Tag | Unique | Physical barcode tag |
| `asset_type` | VARCHAR(32) | Required | `LAPTOP`, `MONITOR`, `SECURITY_KEY`, `MOBILE_DEVICE`, `ACCESS_BADGE` | Type | Non-unique | Equipment category |
| `model` | VARCHAR(100) | Required | E.g. `MacBook Pro 16" M3`, `Dell XPS 15`, `Dell UltraSharp 27"`, `YubiKey 5C NFC` | Text | Non-unique | Hardware model specification |
| `serial_number` | VARCHAR(64) | Required | Alphanumeric serial number | Code | Unique | OEM serial number |
| `status` | VARCHAR(24) | Required | `IN_STOCK`, `ASSIGNED`, `IN_TRANSIT`, `DELIVERED`, `ACKNOWLEDGED`, `RETURN_REQUESTED`, `RECOVERED`, `RETIRED` | Lifecycle | Non-unique | Must reconcile with employee assignment |
| `assigned_to_employee_id`| VARCHAR(32)| Optional | Foreign key to `employees.id` | ID | Non-unique (Single active per asset) | Null if IN_STOCK or RETIRED |
| `assigned_date` | DATE | Optional | ISO-8601 `YYYY-MM-DD` | Date | Non-unique | Assignment date |
| `acknowledged_at`| TIMESTAMP | Optional | ISO-8601 timestamp | Time | Non-unique | Digital delivery receipt acknowledgment |
| `recovered_at` | TIMESTAMP | Optional | ISO-8601 timestamp | Time | Non-unique | Return inspection timestamp |
| `condition_status`| VARCHAR(24) | Required | `NEW`, `GOOD`, `DAMAGED`, `SCRAPPED` | Quality | Non-unique | Hardware inspection grade |

---

## 10. OffboardingCase (`offboarding_cases`)
Workflow container tracking employee exits, asset recoveries, and access revocations.

| Field Name | Type | Req/Opt | Allowed Values | Units | Uniqueness | Validation Rule |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `id` | VARCHAR(32) | Required | Pattern `OFF-YYYY-[0-9]{6}` (e.g. `OFF-2026-000001`) | String | PK, Unique | Primary key |
| `employee_id` | VARCHAR(32) | Required | Foreign key to `employees.id` | ID | Unique | Must exist in `employees` |
| `termination_type` | VARCHAR(32) | Required | `VOLUNTARY`, `INVOLUNTARY`, `CONTRACT_END`, `URGENT_TERMINATION` | Type | Non-unique | Determines task SLA (URGENT_TERMINATION = same-day revocation) |
| `status` | VARCHAR(24) | Required | `INITIATED`, `IN_PROGRESS`, `ASSETS_RETURNED`, `ACCESS_REVOKED`, `COMPLETED`, `FLAGGED_EXCEPTION` | State | Non-unique | Case status |
| `last_working_day` | DATE | Required | ISO-8601 `YYYY-MM-DD` | Date | Non-unique | Final employment day |
| `hr_owner_id` | VARCHAR(32) | Required | Foreign key to `employees.id` | ID | Non-unique | HR offboarding specialist |
| `it_owner_id` | VARCHAR(32) | Required | Foreign key to `employees.id` | ID | Non-unique | IT security specialist |
| `created_at` | TIMESTAMP | Required | ISO-8601 timestamp | Time | Non-unique | Case initiation time |
| `completed_at` | TIMESTAMP | Optional | ISO-8601 timestamp | Time | Non-unique | Populated once all assets recovered and all access revoked |

---

## 11. AuditEvent (`audit_events`)
Immutable event journal tracking all business state transitions and administrative overrides.

| Field Name | Type | Req/Opt | Allowed Values | Units | Uniqueness | Validation Rule |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `id` | VARCHAR(64) | Required | Pattern `AUD-[0-9]{8}` | String | PK, Unique | Primary key, chronological order |
| `entity_name` | VARCHAR(32) | Required | `Employee`, `OnboardingCase`, `Task`, `AccessGrant`, `Asset`, `OffboardingCase` | Text | Non-unique | Target entity table |
| `entity_id` | VARCHAR(64) | Required | Valid ID of target entity | ID | Non-unique | Foreign key target |
| `action_type` | VARCHAR(32) | Required | `CREATE`, `UPDATE`, `APPROVE`, `PROVISION`, `REASSIGN`, `REVOKE`, `OVERRIDE`, `DETECT_ANOMALY` | Action | Non-unique | Type of state change |
| `actor_id` | VARCHAR(64) | Required | User ID or System daemon | ID | Non-unique | Who triggered the action |
| `actor_role` | VARCHAR(32) | Required | `HR_PARTNER`, `IT_ADMIN`, `FACILITIES_LEAD`, `MANAGER`, `SYSTEM_DAEMON` | Role | Non-unique | Actor authorization role |
| `old_state` | TEXT | Optional | JSON or textual snapshot before change | JSON | Non-unique | State before change |
| `new_state` | TEXT | Optional | JSON or textual snapshot after change | JSON | Non-unique | State after change |
| `timestamp` | TIMESTAMP | Required | ISO-8601 timestamp | Time | Non-unique | Exact clock time of event |
| `details` | TEXT | Optional | Narrative description or override rationale | Text | Non-unique | Context and justification |

---

## 12. Relationship Cardinality Matrix

| Parent Entity | Child Entity | Cardinality | Join Keys | Description |
| :--- | :--- | :--- | :--- | :--- |
| `Department` | `Role` | 1 : N | `Role.department_id = Department.id` | A department hosts multiple job roles |
| `Department` | `Employee` | 1 : N | `Employee.department_id = Department.id` | Employees belong to a single department |
| `Role` | `Employee` | 1 : N | `Employee.role_id = Role.id` | Employees occupy a defined role |
| `Employee` (Manager) | `Employee` (Report) | 1 : N | `Employee.manager_id = Employee.id` | Self-referencing reporting hierarchy |
| `Employee` | `OnboardingCase` | 1 : 1 | `OnboardingCase.employee_id = Employee.id` | One onboarding case per hired employee |
| `OnboardingCase` | `Task` | 1 : N | `Task.case_id = OnboardingCase.id` | Case contains an orchestrated DAG of tasks |
| `Task` | `TaskDependency` | 1 : N | `TaskDependency.task_id = Task.id` | A task can have multiple prerequisite tasks |
| `Employee` | `AccessGrant` | 1 : N | `AccessGrant.employee_id = Employee.id` | Employee holds multiple system entitlements |
| `AccessRole` | `AccessGrant` | 1 : N | `AccessGrant.access_role_id = AccessRole.id`| System role provisioned to many users |
| `Employee` | `Asset` | 1 : N | `Asset.assigned_to_employee_id = Employee.id`| Employee can hold multiple hardware assets |
| `Employee` | `OffboardingCase` | 1 : 1 | `OffboardingCase.employee_id = Employee.id` | One offboarding case upon termination |
| *Any Entity* | `AuditEvent` | 1 : N | `AuditEvent.entity_id = Entity.id` | Full audit trail across lifecycle |

