# Enterprise Workflow Orchestration - Architecture Specification

## 1. System Overview
The **Enterprise Workflow Orchestrator** is a cross-functional orchestration platform designed to automate and reconcile employee lifecycle events across Human Resources (HR), Information Technology (IT), and Facilities Operations.

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                    React SPA Frontend (Normal React Scripts)                     │
│  - Executive Dashboard           - Onboarding Orchestrator  - Task Kanban       │
│  - Access Provisioning Matrix    - Asset Fleet Manager      - Offboarding Center │
│  - Reconciliation & Audit Engine - Synthetic Data Lab                            │
└────────────────────────────────────────┬────────────────────────────────────────┘
                                         │ REST API (JSON / HTTP)
┌────────────────────────────────────────▼────────────────────────────────────────┐
│                   Spring Boot 3 Enterprise Backend Engine                       │
│  ┌───────────────────────┐ ┌───────────────────────┐ ┌───────────────────────┐ │
│  │ WorkflowEngineService │ │AccessProvisioningSvc  │ │ AssetManagementSvc    │ │
│  └───────────────────────┘ └───────────────────────┘ └───────────────────────┘ │
│  ┌───────────────────────┐ ┌───────────────────────┐ ┌───────────────────────┐ │
│  │ ReconciliationService │ │ AuditService (Events) │ │ SyntheticDataLoadSvc  │ │
│  └───────────────────────┘ └───────────────────────┘ └───────────────────────┘ │
└────────────────────────────────────────┬────────────────────────────────────────┘
                                         │ JPA / Hibernate ORM
┌────────────────────────────────────────▼────────────────────────────────────────┐
│                    MySQL 8.0 Database (yottaflex_db)                             │
│  - departments          - job_roles            - access_roles                   │
│  - employees            - onboarding_cases     - case_tasks                     │
│  - task_dependencies    - access_grants        - assets                         │
│  - offboarding_cases    - audit_events                                          │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Core Entities and Database Schema

### Master Data Entities
- **Department (`departments`)**: Corporate cost center and functional division.
- **JobRole (`job_roles`)**: Job profile defining required equipment tier (`STANDARD_DEV`, `STANDARD_OFFICE`, `EXECUTIVE`, `FIELD_MOBILE`) and base access profile.
- **Employee (`employees`)**: Master record supporting full-time employees, contractors, and interns, with structured lifecycle states (`OFFER_ACCEPTED`, `PREBOARDING`, `ACTIVE`, `NOTICE_PERIOD`, `TERMINATED`).
- **AccessRole (`access_roles`)**: Enterprise IT systems (Active Directory, Okta, GitHub, AWS, Workday, Salesforce, Slack) with privilege levels and approval flags.

### Workflow & Transactional Entities
- **OnboardingCase (`onboarding_cases`)**: Lifecycle container tracking candidates from offer acceptance to orientation.
- **CaseTask (`case_tasks`)**: Atomic tasks assigned to specific queues (`HR_PARTNER`, `IT_OPERATIONS`, `FACILITIES`, `MANAGER`, `EMPLOYEE`).
- **TaskDependency (`task_dependencies`)**: Finish-to-Start DAG prerequisites governing execution order.
- **AccessGrant (`access_grants`)**: Managed entitlement instances with dual approval and start-date gating.
- **Asset (`assets`)**: Physical IT equipment tracked by barcode tag, serial number, delivery receipt, and return inspection.
- **OffboardingCase (`offboarding_cases`)**: Exit workflows supporting voluntary resignations, contractor ends, and same-day urgent security terminations.
- **AuditEvent (`audit_events`)**: Append-only audit journal preserving state before and after every transition.

---

## 3. Business Logic & Governance Rules

1. **Dependency Verification**:
   - A task cannot transition to `COMPLETED` unless all prerequisite tasks in the DAG are in `COMPLETED` state.
   - Administrative overrides require documented operational justification and trigger an `OVERRIDE` audit event.

2. **Access Provisioning Gating**:
   - Access grants require formal security/manager sign-off before activation (`approval_status == 'APPROVED'`).
   - Access cannot be provisioned prior to the employee's start date (`provisioned_at >= start_date`).
   - Terminated employees cannot receive new access grants.

3. **Urgent Termination Flow**:
   - Marking an exit as `URGENT_TERMINATION` triggers immediate automated revocation of all active access grants across Okta, Active Directory, AWS, and GitHub.
   - Status changes directly to `ACCESS_REVOKED` while equipment recovery tasks are routed to Facilities.

4. **Asset Custodianship**:
   - Equipment must be in `IN_STOCK` inventory prior to employee assignment.
   - Serial numbers must be unique across all active hardware deployments.
   - Recovered equipment must clear employee references and return to stock upon inspection.

---

## 4. Reconciliation & Evaluation Engine

The reconciliation scanner checks the database against 5 core invariant rules and evaluates detection performance against the isolated ground truth catalog (`ground_truth.json`):

$$\text{Precision} = \frac{\text{True Positives}}{\text{True Positives} + \text{False Positives}}$$

$$\text{Recall} = \frac{\text{True Positives}}{\text{True Positives} + \text{False Negatives}}$$

$$F_1\text{-Score} = 2 \times \frac{\text{Precision} \times \text{Recall}}{\text{Precision} + \text{Recall}}$$

The 5 invariant rules audited:
1. `TERMINATED_EMPLOYEE_RETAINS_GRANT`
2. `ACCESS_GRANTED_BEFORE_START_APPROVAL`
3. `DUPLICATE_ASSET_ASSIGNMENT`
4. `TASK_DEPENDENCY_CYCLE`
5. `RECOVERED_ASSET_REMAINS_ASSIGNED`

