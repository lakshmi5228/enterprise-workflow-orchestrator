# Enterprise Workflow Orchestrator

> **Pre-boarding · Access Provisioning · Equipment Delivery · Exit Management**
>
> Cross-functional workflow engine for HR, IT, and Facilities teams — built with Spring Boot 3, React 18, and MySQL 8.

---

## Table of Contents

1. [Project Overview](#project-overview)
2. [Architecture](#architecture)
3. [Prerequisites](#prerequisites)
4. [Quick Start](#quick-start)
5. [Running in H2 Mode (No MySQL)](#running-in-h2-mode-no-mysql)
6. [API Reference](#api-reference)
7. [Synthetic Data Generator](#synthetic-data-generator)
8. [Frontend Guide](#frontend-guide)
9. [Entity Model](#entity-model)
10. [Ground Truth Anomalies](#ground-truth-anomalies)
11. [Validation & Reproducibility](#validation--reproducibility)
12. [Project Structure](#project-structure)

---

## Project Overview

This system orchestrates employee lifecycle workflows across three departments:

| Department | Responsibilities |
|---|---|
| **HR** | Onboarding case creation, role assignment, exit approvals |
| **IT** | Access provisioning, SCIM integration, equipment staging |
| **Facilities** | Asset assignment, equipment delivery, asset recovery |

**Key capabilities:**

- Task dependency graph (DAG) with prerequisite enforcement
- Access grant lifecycle: `PENDING → APPROVED → PROVISIONED → REVOKED`
- Asset lifecycle: `IN_STOCK → ASSIGNED → RECOVERED`
- Reconciliation engine with precision/recall scoring against ground truth
- 20 injected labelled anomalies (5 types) for compliance testing
- Fixed-seed synthetic dataset: 1,000 employees, 8,000 tasks, 1,500 grants, 1,200 assets

---

## Architecture

```
┌─────────────────────────────────────────────────────┐
│  React 18 SPA (port 3000)                           │
│  Dashboard · Onboarding · Tasks · Access · Assets   │
│  Offboarding · Reconciliation · Synthetic Data Lab  │
└────────────────────┬────────────────────────────────┘
                     │ REST / JSON (SNAKE_CASE)
┌────────────────────▼────────────────────────────────┐
│  Spring Boot 3.2 REST API (port 8080)               │
│  9 Controllers · 6 Services · 11 JPA Entities       │
│  WorkflowEngine · AccessProvisioning · Reconcile    │
└────────────────────┬────────────────────────────────┘
                     │ JPA / Hibernate
┌────────────────────▼────────────────────────────────┐
│  MySQL 8  (default)  /  H2 in-memory (dev/test)     │
│  11 tables · Foreign keys · Audit event log         │
└─────────────────────────────────────────────────────┘
```

---

## Prerequisites

| Tool | Version | Notes |
|---|---|---|
| Java JDK | 17 or higher | Project compiled to Java 17 class files |
| Apache Maven | 3.9+ | Bundled at `tools/maven/apache-maven-3.9.6/` |
| MySQL | 8.0+ | For production mode; not needed for H2 mode |
| Python | 3.8+ | Only for regenerating synthetic data |
| Node.js / npm | 16+ | Only if rebuilding the frontend |

---

## Quick Start

### Option A — H2 in-memory (recommended for first run, no MySQL)

```powershell
# 1. Build the backend JAR
.\start-backend.ps1 -Profile h2

# In a second terminal, serve the frontend
.\start-frontend.ps1
```

Then open **http://localhost:3000** in your browser.

---

### Option B — MySQL production mode

```powershell
# 1. Create the database (run once)
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS yottaflex_db CHARACTER SET utf8mb4;"

# 2. Edit backend/src/main/resources/application.properties
#    Set spring.datasource.username and spring.datasource.password

# 3. Build & start backend
.\start-backend.ps1 -Profile default

# 4. Start frontend (separate terminal)
.\start-frontend.ps1
```

---

### Start both at once

```powershell
.\start-all.ps1                   # H2 mode, ports 8080 + 3000
.\start-all.ps1 -BackendProfile default   # MySQL mode
```

---

### Building the JAR manually

```powershell
$env:JAVA_HOME = "C:\Program Files\Java\jdk-25.0.3"
Set-Location backend
d:\yottaflex-project\tools\maven\apache-maven-3.9.6\bin\mvn.cmd package -DskipTests
# Output: backend/target/orchestrator-1.0.0.jar
```

---

## Running in H2 Mode (No MySQL)

The `h2` Spring profile replaces MySQL with an in-memory H2 database.  
On startup the `SyntheticDataLoaderService` auto-seeds all 11 entity tables from `synthetic_data/exports/`.

```
H2 Console: http://localhost:8080/h2-console
JDBC URL  : jdbc:h2:mem:yottaflex_db
User      : sa
Password  : (empty)
```

---

## API Reference

All responses use `snake_case` JSON. Base URL: `http://localhost:8080`

### Dashboard

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/dashboard/metrics` | KPI metrics: employees, tasks, grants, assets |

### Employees

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/employees` | List all employees (paginated, filterable) |
| GET | `/api/employees/{id}` | Get employee by ID |
| GET | `/api/employees/{id}/onboarding` | Get employee's onboarding case |
| GET | `/api/employees/{id}/access-grants` | Get employee's access grants |
| GET | `/api/employees/{id}/assets` | Get assets assigned to employee |

### Onboarding

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/onboarding` | List onboarding cases |
| GET | `/api/onboarding/{caseId}` | Case detail |
| GET | `/api/onboarding/{caseId}/tasks` | Tasks for a case |
| POST | `/api/onboarding/{caseId}/tasks/{taskId}/action` | Execute task action |

### Tasks

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/tasks` | All tasks (with filtering) |
| GET | `/api/tasks/{id}` | Task detail |
| GET | `/api/tasks/{id}/dependencies` | Task dependencies |
| POST | `/api/tasks/{id}/action` | Complete / block / reopen task |

### Access Grants

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/access-grants` | List grants |
| GET | `/api/access-grants/{id}` | Grant detail |
| POST | `/api/access-grants` | Provision new grant |
| PUT | `/api/access-grants/{id}/revoke` | Revoke grant |
| PUT | `/api/access-grants/{id}/approve` | Approve pending grant |

### Assets

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/assets` | List assets |
| GET | `/api/assets/{id}` | Asset detail |
| POST | `/api/assets/{id}/assign` | Assign to employee |
| PUT | `/api/assets/{id}/recover` | Mark as recovered |

### Offboarding

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/offboarding` | List offboarding cases |
| GET | `/api/offboarding/{id}` | Case detail |
| POST | `/api/offboarding` | Initiate offboarding for employee |
| PUT | `/api/offboarding/{id}/complete` | Mark case complete |

### Reconciliation

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/reconciliation/run` | Run full reconciliation scan |
| GET | `/api/reconciliation/ground-truth` | View ground truth anomaly set |

### Audit

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/audit` | Audit event log |
| GET | `/api/audit/employee/{id}` | Audit trail for employee |

### Synthetic Data Lab

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/synthetic/status` | Seeding status and counts |
| POST | `/api/synthetic/reload` | Re-seed database from JSON files |

---

## Synthetic Data Generator

```powershell
# Generate all exports (seed = 20261022, fixed for reproducibility)
cd synthetic_data
python generator.py

# Outputs in synthetic_data/exports/:
#   departments.json / .csv
#   job_roles.json / .csv
#   employees.json / .csv          (1,000 records)
#   onboarding_cases.json / .csv   (1,000 records)
#   tasks.json / .csv              (~8,000 records)
#   task_dependencies.json / .csv  (~6,000 records)
#   access_roles.json / .csv
#   access_grants.json / .csv      (1,500 records)
#   assets.json / .csv             (~1,200 records)
#   offboarding_cases.json / .csv  (~146 records)
#   audit_events.json / .csv       (1,000 records)
#   ground_truth.json              (20 labelled anomalies — never leaked)
#   source_manifest.json
#   seed_data.sql                  (MySQL INSERT statements)
```

Reproduce the same dataset byte-for-byte:

```python
# generator.py uses: random.seed(20261022) and numpy.random.seed(20261022)
# Running twice produces identical files (checksums verified in docs/REPRODUCIBILITY_REPORT.md)
```

---

## Frontend Guide

The React SPA has 8 tabs:

| Tab | Component | Description |
|---|---|---|
| **Dashboard** | `Dashboard.js` | KPI cards, workflow health, recent activity |
| **Onboarding** | `OnboardingWorkflow.js` | Onboarding cases, case detail, timeline |
| **Tasks** | `TaskKanban.js` | Kanban board by status, drag-free task actions |
| **Access Matrix** | `AccessMatrix.js` | Grant list, approval workflow, revocation |
| **Assets** | `AssetTracker.js` | Asset inventory, assignment, recovery |
| **Offboarding** | `OffboardingManager.js` | Exit cases, revocation checklist |
| **Reconciliation** | `ReconciliationAudit.js` | Anomaly scanner, precision/recall metrics |
| **Synthetic Data Lab** | `SyntheticDataLab.js` | Seeding status, reload trigger |

---

## Entity Model

```
Department ──< Employee >── JobRole
                 │
                 ├──< OnboardingCase >── CaseTask >── TaskDependency
                 │
                 ├──< AccessGrant >── AccessRole
                 │
                 ├──< Asset
                 │
                 └──< OffboardingCase

AuditEvent (references any entity via entity_type + entity_id)
```

**Key cardinalities:**
- `Department` 1:N `Employee`
- `Employee` 1:1 `OnboardingCase`
- `OnboardingCase` 1:N `CaseTask`
- `CaseTask` N:M `CaseTask` (via `TaskDependency` bridge table)
- `Employee` 1:N `AccessGrant`
- `AccessGrant` N:1 `AccessRole`
- `Employee` 1:N `Asset` (assigned_to_employee_id FK)
- `Employee` 1:1 `OffboardingCase`

---

## Ground Truth Anomalies

20 labelled anomalies are injected into the synthetic dataset (isolated in `ground_truth.json`).  
They are **never** merged into the main feature tables.

| Anomaly Type | Count | Severity | Detection Rule |
|---|---|---|---|
| `ACCESS_GRANTED_BEFORE_START_APPROVAL` | 5 | HIGH | `grant.provisioned_at < employee.start_date OR approval_status NOT IN ('APPROVED','AUTO_APPROVED')` |
| `DUPLICATE_ASSET_ASSIGNMENT` | 5 | MEDIUM | Same asset ID assigned to more than one active employee |
| `TERMINATED_EMPLOYEE_RETAINS_GRANT` | 6 | CRITICAL | `employee.status = 'TERMINATED' AND grant.status = 'PROVISIONED' AND revoked_at IS NULL` |
| `TASK_DEPENDENCY_CYCLE` | 4 | HIGH | DFS cycle detection in task prerequisite DAG |
| `RECOVERED_ASSET_REMAINS_ASSIGNED` | 5 | MEDIUM | `asset.status = 'RECOVERED' AND assigned_to_employee_id IS NOT NULL` |

The reconciliation engine detects these automatically and reports **precision, recall, and F1** against the known set.

---

## Validation & Reproducibility

See `docs/REPRODUCIBILITY_REPORT.md` for SHA-256 checksums and row-count table.

Validation rules checked by the generator:
1. Primary key uniqueness across all 11 tables
2. Foreign key referential integrity (no orphan records)
3. Enum value constraints on status fields
4. Date ordering (`start_date ≤ end_date`, `provisioned_at ≥ approved_at`)
5. Arithmetic reconciliation (asset counts, grant counts per employee)
6. Lifecycle transition validity (e.g., REVOKED grants have `revoked_at` set)

---

## Project Structure

```
yottaflex-project/
├── backend/                            Spring Boot 3 REST API
│   ├── pom.xml
│   └── src/main/java/com/yottaflex/orchestrator/
│       ├── OrchestratorApplication.java
│       ├── config/
│       │   ├── JacksonConfig.java      SNAKE_CASE + JSR310
│       │   └── WebConfig.java          CORS config
│       ├── controller/                 9 REST controllers
│       ├── dto/                        8 Data Transfer Objects
│       ├── exception/                  GlobalExceptionHandler
│       ├── model/                      11 JPA entities
│       ├── repository/                 11 Spring Data repos
│       └── service/
│           ├── AuditService.java
│           ├── WorkflowEngineService.java
│           ├── AccessProvisioningService.java
│           ├── AssetManagementService.java
│           ├── ReconciliationService.java
│           └── SyntheticDataLoaderService.java (CommandLineRunner)
│
├── frontend/                           React 18 SPA
│   ├── public/index.html
│   ├── src/
│   │   ├── App.js                     8-tab router
│   │   ├── App.css                    Enterprise styling
│   │   ├── services/api.js            Axios API client
│   │   └── components/                8 page components
│   └── build/                         Production build (ready to serve)
│
├── synthetic_data/
│   ├── generator.py                   Deterministic data generator (seed=20261022)
│   ├── exports/                       Generated JSON, CSV, SQL files
│   │   └── ground_truth.json          20 labelled anomalies (isolated)
│   └── validation_summary.md
│
├── docs/
│   ├── DATA_DICTIONARY.md             Field-level schema documentation
│   ├── ARCHITECTURE.md                System design and business rules
│   └── REPRODUCIBILITY_REPORT.md     SHA-256 checksums and volume table
│
├── tools/maven/apache-maven-3.9.6/    Bundled Maven (no install needed)
│
├── start-backend.ps1                  Launch backend (MySQL or H2)
├── start-frontend.ps1                 Serve frontend build
├── start-all.ps1                      Launch both in separate windows
└── README.md                          This file
```

---

## License & Data Provenance

All data is **fully synthetic** — generated programmatically with seed `20261022`.  
No real person data, no external datasets, no PII. Safe to share and redistribute.

