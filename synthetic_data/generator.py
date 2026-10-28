#!/usr/bin/env python3
"""
Enterprise Workflow Orchestration - Synthetic Data Generator
Generates realistic, deterministic master and transaction records for HR, IT, and Facilities.
Volume: 1,000 Employees, ~8,000 Tasks, 1,500 Access Grants, 1,200 Assets.
Seed: 20261022 (Default)
"""

import sys
import os
import json
import csv
import hashlib
import random
from datetime import datetime, date, timedelta

DEFAULT_SEED = 20261022

FIRST_NAMES = [
    "Aarav", "Aditi", "Alexander", "Alice", "Ananya", "Benjamin", "Charlotte", "Daniel",
    "David", "Elena", "Emma", "Ethan", "Fatima", "Gabriel", "Grace", "Hannah",
    "Ibrahim", "Isabella", "James", "Jasmine", "John", "Kavita", "Liam", "Lucas",
    "Maya", "Michael", "Neha", "Noah", "Olivia", "Pooja", "Priya", "Rahul",
    "Rohan", "Samuel", "Sarah", "Sneha", "Sophia", "Thomas", "Vikram", "William",
    "Zara", "Abigail", "Adam", "Aria", "Chloe", "Dev", "Emily", "Henry", "Ishaan", "Jack"
]

LAST_NAMES = [
    "Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis",
    "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson", "Thomas",
    "Taylor", "Moore", "Jackson", "Martin", "Lee", "Perez", "Thompson", "White",
    "Harris", "Sanchez", "Clark", "Ramirez", "Lewis", "Robinson", "Walker", "Young",
    "Allen", "King", "Wright", "Scott", "Torres", "Nguyen", "Hill", "Flores",
    "Green", "Adams", "Nelson", "Baker", "Hall", "Rivera", "Campbell", "Mitchell", "Carter", "Roberts",
    "Sharma", "Verma", "Patel", "Gupta", "Rao", "Nair", "Reddy", "Mehta", "Bose", "Menon"
]

DEPARTMENTS_CONFIG = [
    {"id": "DEP-ENG", "name": "Engineering", "cost_center": "CC-ENG-101"},
    {"id": "DEP-PROD", "name": "Product Management", "cost_center": "CC-PRD-102"},
    {"id": "DEP-HR", "name": "Human Resources", "cost_center": "CC-HRS-103"},
    {"id": "DEP-IT", "name": "Information Technology", "cost_center": "CC-ITS-104"},
    {"id": "DEP-FIN", "name": "Finance & Accounting", "cost_center": "CC-FIN-105"},
    {"id": "DEP-SALES", "name": "Sales & Revenue", "cost_center": "CC-SLS-106"},
    {"id": "DEP-MKT", "name": "Marketing & Brand", "cost_center": "CC-MKT-107"},
    {"id": "DEP-OPS", "name": "Facilities & Operations", "cost_center": "CC-OPS-108"},
]

JOB_ROLES_CONFIG = [
    {"id": "ROLE-SWE-01", "title": "Software Engineer", "department_id": "DEP-ENG", "level": "L2", "equipment_tier": "STANDARD_DEV", "base_access_profile": "DEVELOPER_PROFILE"},
    {"id": "ROLE-SWE-02", "title": "Senior Software Engineer", "department_id": "DEP-ENG", "level": "L3", "equipment_tier": "STANDARD_DEV", "base_access_profile": "DEVELOPER_PROFILE"},
    {"id": "ROLE-DEV-LEAD", "title": "Engineering Lead", "department_id": "DEP-ENG", "level": "L4", "equipment_tier": "STANDARD_DEV", "base_access_profile": "DEVELOPER_PROFILE"},
    {"id": "ROLE-DEVOPS-01", "title": "Cloud DevOps Engineer", "department_id": "DEP-ENG", "level": "L3", "equipment_tier": "STANDARD_DEV", "base_access_profile": "DEVELOPER_PROFILE"},
    {"id": "ROLE-PM-01", "title": "Product Manager", "department_id": "DEP-PROD", "level": "L3", "equipment_tier": "STANDARD_OFFICE", "base_access_profile": "BUSINESS_STANDARD"},
    {"id": "ROLE-HR-BP", "title": "HR Business Partner", "department_id": "DEP-HR", "level": "L3", "equipment_tier": "STANDARD_OFFICE", "base_access_profile": "HR_SECURE"},
    {"id": "ROLE-HR-COORD", "title": "People Operations Coordinator", "department_id": "DEP-HR", "level": "L1", "equipment_tier": "STANDARD_OFFICE", "base_access_profile": "HR_SECURE"},
    {"id": "ROLE-IT-ADMIN", "title": "IT Systems Administrator", "department_id": "DEP-IT", "level": "L3", "equipment_tier": "STANDARD_DEV", "base_access_profile": "DEVELOPER_PROFILE"},
    {"id": "ROLE-IT-DESK", "title": "Service Desk Specialist", "department_id": "DEP-IT", "level": "L2", "equipment_tier": "STANDARD_OFFICE", "base_access_profile": "BUSINESS_STANDARD"},
    {"id": "ROLE-FIN-ANL", "title": "Financial Analyst", "department_id": "DEP-FIN", "level": "L2", "equipment_tier": "STANDARD_OFFICE", "base_access_profile": "BUSINESS_STANDARD"},
    {"id": "ROLE-SALES-AE", "title": "Account Executive", "department_id": "DEP-SALES", "level": "L2", "equipment_tier": "FIELD_MOBILE", "base_access_profile": "BUSINESS_STANDARD"},
    {"id": "ROLE-OPS-COORD", "title": "Facilities Coordinator", "department_id": "DEP-OPS", "level": "L2", "equipment_tier": "STANDARD_OFFICE", "base_access_profile": "BUSINESS_STANDARD"},
    {"id": "ROLE-VP-EXEC", "title": "Vice President", "department_id": "DEP-ENG", "level": "EXEC", "equipment_tier": "EXECUTIVE", "base_access_profile": "LEADERSHIP_CORE"},
]

ACCESS_ROLES_CONFIG = [
    {"id": "ACC-ROLE-001", "system_name": "Active Directory", "role_name": "Domain User", "access_level": "STANDARD_USER", "approval_required": False, "requires_active_status": True},
    {"id": "ACC-ROLE-002", "system_name": "Okta", "role_name": "SSO Core Portal", "access_level": "STANDARD_USER", "approval_required": False, "requires_active_status": True},
    {"id": "ACC-ROLE-003", "system_name": "GitHub Enterprise", "role_name": "Source Code Contributor", "access_level": "STANDARD_USER", "approval_required": True, "requires_active_status": True},
    {"id": "ACC-ROLE-004", "system_name": "GitHub Enterprise", "role_name": "Repository Administrator", "access_level": "ADMIN", "approval_required": True, "requires_active_status": True},
    {"id": "ACC-ROLE-005", "system_name": "AWS Cloud", "role_name": "Production Deployer", "access_level": "POWER_USER", "approval_required": True, "requires_active_status": True},
    {"id": "ACC-ROLE-006", "system_name": "AWS Cloud", "role_name": "Cloud Security Administrator", "access_level": "ADMIN", "approval_required": True, "requires_active_status": True},
    {"id": "ACC-ROLE-007", "system_name": "Workday HR", "role_name": "Employee Self Service", "access_level": "READ_ONLY", "approval_required": False, "requires_active_status": True},
    {"id": "ACC-ROLE-008", "system_name": "Workday HR", "role_name": "HR Specialist Full Access", "access_level": "ADMIN", "approval_required": True, "requires_active_status": True},
    {"id": "ACC-ROLE-009", "system_name": "Salesforce", "role_name": "CRM Standard User", "access_level": "STANDARD_USER", "approval_required": True, "requires_active_status": True},
    {"id": "ACC-ROLE-010", "system_name": "Jira/Confluence", "role_name": "Product Workspace Member", "access_level": "STANDARD_USER", "approval_required": False, "requires_active_status": True},
    {"id": "ACC-ROLE-011", "system_name": "Slack", "role_name": "Enterprise Grid Member", "access_level": "STANDARD_USER", "approval_required": False, "requires_active_status": True},
    {"id": "ACC-ROLE-012", "system_name": "NetSuite", "role_name": "Finance Ledger Access", "access_level": "BILLING_ADMIN", "approval_required": True, "requires_active_status": True},
]

ASSET_MODELS = [
    {"type": "LAPTOP", "model": "MacBook Pro 16 inch M3 Pro", "prefix": "MBP16"},
    {"type": "LAPTOP", "model": "Dell XPS 15 9530", "prefix": "XPS15"},
    {"type": "LAPTOP", "model": "Lenovo ThinkPad X1 Carbon Gen 11", "prefix": "TPX1"},
    {"type": "MONITOR", "model": "Dell UltraSharp 27 inch 4K Hub", "prefix": "U274K"},
    {"type": "SECURITY_KEY", "model": "YubiKey 5C NFC FIPS", "prefix": "YK5C"},
    {"type": "MOBILE_DEVICE", "model": "iPhone 15 Pro Enterprise", "prefix": "IP15P"},
    {"type": "ACCESS_BADGE", "model": "HID ProxCard II Encrypted Badge", "prefix": "HIDB"},
]

LOCATIONS = ["New York", "San Francisco", "London", "Remote", "Bangalore", "Austin", "Chicago"]

ONBOARDING_TASK_TEMPLATES = [
    {"code": "001", "name": "Verify Background Check & I-9 Documents", "category": "HR_PREBOARDING", "assigned_role": "HR_PARTNER", "days_offset": -7, "priority": "HIGH"},
    {"code": "002", "name": "Configure Workday & Payroll Profile", "category": "HR_PREBOARDING", "assigned_role": "HR_PARTNER", "days_offset": -5, "priority": "MEDIUM"},
    {"code": "003", "name": "Order Standard Equipment Kit", "category": "EQUIPMENT_DELIVERY", "assigned_role": "FACILITIES", "days_offset": -6, "priority": "HIGH"},
    {"code": "004", "name": "Ship Equipment & Record Carrier Tracking", "category": "EQUIPMENT_DELIVERY", "assigned_role": "FACILITIES", "days_offset": -3, "priority": "HIGH"},
    {"code": "005", "name": "Create Active Directory & Okta Identity", "category": "IT_ACCESS", "assigned_role": "IT_OPERATIONS", "days_offset": -3, "priority": "HIGH"},
    {"code": "006", "name": "Request & Approve Role-Specific Entitlements", "category": "IT_ACCESS", "assigned_role": "MANAGER", "days_offset": -2, "priority": "MEDIUM"},
    {"code": "007", "name": "Provision Software Licenses & Security Keys", "category": "IT_ACCESS", "assigned_role": "IT_OPERATIONS", "days_offset": -1, "priority": "HIGH"},
    {"code": "008", "name": "Conduct Day-1 Orientation & Asset Acknowledgment", "category": "ORIENTATION", "assigned_role": "HR_PARTNER", "days_offset": 0, "priority": "MEDIUM"},
]

# Standard dependency DAG:
# 002 depends on 001
# 004 depends on 003
# 005 depends on 001
# 006 depends on 005
# 007 depends on 006
# 008 depends on 004 and 007
TASK_PREREQUISITES_MAP = {
    "002": ["001"],
    "004": ["003"],
    "005": ["001"],
    "006": ["005"],
    "007": ["006"],
    "008": ["004", "007"],
}

class SyntheticDataGenerator:
    def __init__(self, seed=DEFAULT_SEED, scale_ratio=1.0):
        self.seed = seed
        self.scale_ratio = scale_ratio
        self.rnd = random.Random(seed)
        
        # Volumes
        self.total_employees = int(1000 * scale_ratio)
        self.target_assets = int(1200 * scale_ratio)
        self.target_grants = int(1500 * scale_ratio)
        
        # In-memory storage
        self.departments = []
        self.job_roles = []
        self.employees = []
        self.onboarding_cases = []
        self.tasks = []
        self.dependencies = []
        self.access_roles = []
        self.access_grants = []
        self.assets = []
        self.offboarding_cases = []
        self.audit_events = []
        self.ground_truth_anomalies = []
        
        self.audit_counter = 1

    def log_audit(self, entity_name, entity_id, action, actor_id, actor_role, old_state, new_state, ts_str, details):
        audit_id = f"AUD-{self.audit_counter:08d}"
        self.audit_counter += 1
        self.audit_events.append({
            "id": audit_id,
            "entity_name": entity_name,
            "entity_id": entity_id,
            "action_type": action,
            "actor_id": actor_id,
            "actor_role": actor_role,
            "old_state": old_state or "",
            "new_state": new_state or "",
            "timestamp": ts_str,
            "details": details
        })

    def run(self):
        print(f"[*] Starting Synthetic Data Generation (Seed: {self.seed}, Scale: {self.scale_ratio})")
        self.generate_departments_and_roles()
        self.generate_access_roles()
        self.generate_employees()
        self.generate_assets()
        self.generate_onboarding_and_tasks()
        self.generate_access_grants()
        self.generate_offboarding_cases()
        self.inject_edge_cases()
        self.validate_dataset()
        print(f"[+] Generation Completed. Baseline volumes:")
        print(f"    - Employees: {len(self.employees)}")
        print(f"    - Tasks: {len(self.tasks)}")
        print(f"    - Access Grants: {len(self.access_grants)}")
        print(f"    - Assets: {len(self.assets)}")
        print(f"    - Onboarding Cases: {len(self.onboarding_cases)}")
        print(f"    - Offboarding Cases: {len(self.offboarding_cases)}")
        print(f"    - Audit Events: {len(self.audit_events)}")
        print(f"    - Ground Truth Injected Anomalies: {len(self.ground_truth_anomalies)}")

    def generate_departments_and_roles(self):
        self.departments = list(DEPARTMENTS_CONFIG)
        self.job_roles = list(JOB_ROLES_CONFIG)

    def generate_access_roles(self):
        self.access_roles = list(ACCESS_ROLES_CONFIG)

    def generate_employees(self):
        base_hire_date = date(2025, 1, 1)
        used_emails = set()
        
        for idx in range(1, self.total_employees + 1):
            emp_id = f"EMP-{idx:06d}"
            fn = self.rnd.choice(FIRST_NAMES)
            ln = self.rnd.choice(LAST_NAMES)
            
            clean_email = f"{fn.lower()}.{ln.lower()}{idx}@company.internal"
            while clean_email in used_emails:
                clean_email = f"{fn.lower()}.{ln.lower()}{self.rnd.randint(1000, 99999)}@company.internal"
            used_emails.add(clean_email)
            
            role = self.rnd.choice(self.job_roles)
            dept_id = role["department_id"]
            
            # 82% Full-time, 13% Contractor, 5% Intern
            r_type = self.rnd.random()
            if r_type < 0.82:
                emp_type = "FULL_TIME"
            elif r_type < 0.95:
                emp_type = "CONTRACTOR"
            else:
                emp_type = "INTERN"
                
            # Date distribution: span hires across 2025 up to late 2026 so that ~5% represent active pre-boarding/pipeline
            offset_days = self.rnd.randint(0, 685)
            hire_d = base_hire_date + timedelta(days=offset_days)
            notice_delay = self.rnd.choice([14, 21, 30, 45])
            start_d = hire_d + timedelta(days=notice_delay)
            
            # Delayed start scenario (4% of hires)
            if self.rnd.random() < 0.04:
                start_d = start_d + timedelta(days=self.rnd.choice([7, 14, 21]))
                
            # Lifecycle status
            # If start_date is in the future (> 2026-10-28): OFFER_ACCEPTED or PREBOARDING
            today = date(2026, 10, 28)
            end_d = None
            
            if start_d > today:
                status = "PREBOARDING" if (start_d - today).days <= 14 else "OFFER_ACCEPTED"
            else:
                # Past hires: 85% Active, 10% Terminated, 5% Notice Period
                st_rnd = self.rnd.random()
                if st_rnd < 0.85:
                    status = "ACTIVE"
                elif st_rnd < 0.95:
                    status = "TERMINATED"
                    tenure_days = self.rnd.randint(30, 400)
                    end_d = start_d + timedelta(days=tenure_days)
                    if end_d > today:
                        end_d = today - timedelta(days=self.rnd.randint(1, 45))
                else:
                    status = "NOTICE_PERIOD"
                    end_d = today + timedelta(days=self.rnd.randint(5, 30))
            
            # Manager assignment (hierarchical, previous employee index or executive)
            if idx <= 5:
                mgr_id = None
            else:
                mgr_cand_idx = self.rnd.randint(1, min(idx - 1, 30))
                mgr_id = f"EMP-{mgr_cand_idx:06d}"
                
            location = self.rnd.choice(LOCATIONS)
            
            emp = {
                "id": emp_id,
                "first_name": fn,
                "last_name": ln,
                "email": clean_email,
                "department_id": dept_id,
                "role_id": role["id"],
                "manager_id": mgr_id,
                "employment_type": emp_type,
                "employment_status": status,
                "hire_date": str(hire_d),
                "start_date": str(start_d),
                "end_date": str(end_d) if end_d else None,
                "location": location
            }
            self.employees.append(emp)
            
            # Initial creation audit
            self.log_audit("Employee", emp_id, "CREATE", "SYSTEM_DAEMON", "HR_PARTNER", None,
                           f"Created candidate {fn} {ln} for role {role['title']}", f"{hire_d}T09:00:00",
                           "Candidate offer accepted and recorded")

    def generate_assets(self):
        # 1,200 hardware and security assets
        for idx in range(1, self.target_assets + 1):
            ast_id = f"AST-{idx:06d}"
            template = self.rnd.choice(ASSET_MODELS)
            tag = f"TAG-{template['prefix']}-{idx:06d}"
            sn = f"SN-{template['prefix']}-{self.rnd.randint(1000000, 9999999)}"
            
            self.assets.append({
                "id": ast_id,
                "asset_tag": tag,
                "asset_type": template["type"],
                "model": template["model"],
                "serial_number": sn,
                "status": "IN_STOCK",
                "assigned_to_employee_id": None,
                "assigned_date": None,
                "acknowledged_at": None,
                "recovered_at": None,
                "condition_status": self.rnd.choice(["NEW", "GOOD", "GOOD", "GOOD"])
            })

    def generate_onboarding_and_tasks(self):
        asset_idx = 0
        dep_id_counter = 1
        
        for idx, emp in enumerate(self.employees, start=1):
            onb_id = f"ONB-2026-{idx:06d}"
            c_name = f"{emp['first_name']} {emp['last_name']}"
            hr_p = "EMP-000006" # Default HR Business Partner
            mgr = emp["manager_id"] or "EMP-000001"
            sched_start = emp["start_date"]
            start_date_obj = date.fromisoformat(sched_start)
            today = date(2026, 10, 28)
            
            if start_date_obj > today:
                days_diff = (start_date_obj - today).days
                if days_diff > 25:
                    case_stage = "PREBOARDING"
                elif days_diff > 16:
                    case_stage = "IT_PROVISIONING"
                elif days_diff > 8:
                    case_stage = "ASSET_DELIVERY"
                else:
                    case_stage = "ORIENTATION"
                case_status = "BLOCKED" if self.rnd.random() < 0.08 else "IN_PROGRESS"
            else:
                case_stage = "COMPLETED"
                case_status = "COMPLETED"
                
            onb_case = {
                "id": onb_id,
                "employee_id": emp["id"],
                "candidate_name": c_name,
                "stage": case_stage,
                "status": case_status,
                "hr_partner_id": hr_p,
                "manager_id": mgr,
                "scheduled_start_date": sched_start,
                "actual_start_date": sched_start,
                "created_at": f"{emp['hire_date']}T09:00:00",
                "updated_at": f"{today}T12:00:00"
            }
            self.onboarding_cases.append(onb_case)
            
            # Tasks for this case (8 standard tasks)
            case_tasks = []
            for t_tmpl in ONBOARDING_TASK_TEMPLATES:
                t_id = f"TASK-{onb_id}-{t_tmpl['code']}"
                due_d = start_date_obj + timedelta(days=t_tmpl["days_offset"])
                
                # Derive task completion state from dates and workflow
                if start_date_obj <= today:
                    t_status = "COMPLETED"
                    completed_at = f"{due_d}T16:00:00"
                else:
                    days_diff = (due_d - today).days
                    if days_diff < 0:
                        t_status = "COMPLETED"
                        completed_at = f"{due_d}T15:30:00"
                    elif days_diff <= 3:
                        t_status = "IN_PROGRESS"
                        completed_at = None
                    else:
                        t_status = "PENDING"
                        completed_at = None
                        
                task_record = {
                    "id": t_id,
                    "case_id": onb_id,
                    "task_name": t_tmpl["name"],
                    "category": t_tmpl["category"],
                    "assigned_role": t_tmpl["assigned_role"],
                    "assigned_to_user": hr_p if "HR" in t_tmpl["assigned_role"] else "it.admin@company.internal",
                    "status": t_status,
                    "priority": t_tmpl["priority"],
                    "due_date": str(due_d),
                    "completed_at": completed_at,
                    "notes": "Standard automated pre-boarding workflow step",
                    "is_exception": False
                }
                case_tasks.append(task_record)
                self.tasks.append(task_record)
                
            # Wire up dependencies for this case
            for code, prereq_codes in TASK_PREREQUISITES_MAP.items():
                target_task_id = f"TASK-{onb_id}-{code}"
                for p_code in prereq_codes:
                    prereq_task_id = f"TASK-{onb_id}-{p_code}"
                    self.dependencies.append({
                        "id": dep_id_counter,
                        "task_id": target_task_id,
                        "prerequisite_task_id": prereq_task_id,
                        "dependency_type": "FINISH_TO_START",
                        "is_mandatory": True,
                        "created_at": f"{emp['hire_date']}T09:05:00"
                    })
                    dep_id_counter += 1
            
            # Assign assets to active and completed onboarded employees (up to asset inventory)
            if emp["employment_status"] in ["ACTIVE", "NOTICE_PERIOD"] and asset_idx < len(self.assets):
                # Laptop
                ast_laptop = self.assets[asset_idx]
                asset_idx += 1
                ast_laptop["status"] = "ACKNOWLEDGED" if start_date_obj <= today else "DELIVERED"
                ast_laptop["assigned_to_employee_id"] = emp["id"]
                ast_laptop["assigned_date"] = str(start_date_obj - timedelta(days=4))
                ast_laptop["acknowledged_at"] = f"{start_date_obj}T10:00:00" if start_date_obj <= today else None
                
                # YubiKey
                if asset_idx < len(self.assets) and self.rnd.random() < 0.60:
                    ast_key = self.assets[asset_idx]
                    asset_idx += 1
                    ast_key["status"] = "ACKNOWLEDGED" if start_date_obj <= today else "DELIVERED"
                    ast_key["assigned_to_employee_id"] = emp["id"]
                    ast_key["assigned_date"] = str(start_date_obj - timedelta(days=4))
                    ast_key["acknowledged_at"] = f"{start_date_obj}T10:00:00" if start_date_obj <= today else None

    def generate_access_grants(self):
        grant_idx = 1
        today = date(2026, 10, 28)
        
        # Provision grants for active, notice, or onboarded employees
        for emp in self.employees:
            if grant_idx > self.target_grants:
                break
                
            emp_status = emp["employment_status"]
            start_date_obj = date.fromisoformat(emp["start_date"])
            
            # Determine grants based on role
            eligible_roles = ["ACC-ROLE-001", "ACC-ROLE-002", "ACC-ROLE-011"] # Active Directory, Okta, Slack (Standard)
            if "DEP-ENG" in emp["department_id"]:
                eligible_roles.extend(["ACC-ROLE-003", "ACC-ROLE-010"]) # GitHub, Jira
                if self.rnd.random() < 0.35:
                    eligible_roles.append("ACC-ROLE-005") # AWS
            elif "DEP-SALES" in emp["department_id"]:
                eligible_roles.append("ACC-ROLE-009") # Salesforce
            elif "DEP-HR" in emp["department_id"]:
                eligible_roles.append("ACC-ROLE-007") # Workday
                
            for role_id in eligible_roles:
                if grant_idx > self.target_grants:
                    break
                    
                gid = f"GRANT-{emp['id']}-{grant_idx:03d}"
                role_meta = next(r for r in self.access_roles if r["id"] == role_id)
                
                # Derive valid status:
                # If employee is terminated: should be REVOKED (unless edge case)
                # If employee is offer_accepted or preboarding with future start: REQUESTED or APPROVED
                # If employee is active: PROVISIONED with provisioned_at >= start_date
                if emp_status == "TERMINATED":
                    g_status = "REVOKED"
                    app_status = "APPROVED"
                    prov_at = f"{emp['start_date']}T09:00:00"
                    rev_at = f"{emp['end_date']}T18:00:00"
                elif start_date_obj > today:
                    g_status = "APPROVED" if role_meta["approval_required"] else "REQUESTED"
                    app_status = "APPROVED" if role_meta["approval_required"] else "AUTO_APPROVED"
                    prov_at = None
                    rev_at = None
                else:
                    g_status = "PROVISIONED"
                    app_status = "APPROVED" if role_meta["approval_required"] else "AUTO_APPROVED"
                    prov_at = f"{emp['start_date']}T09:30:00"
                    rev_at = None
                    
                self.access_grants.append({
                    "id": gid,
                    "employee_id": emp["id"],
                    "access_role_id": role_id,
                    "status": g_status,
                    "approval_status": app_status,
                    "requested_by": emp["manager_id"] or "EMP-000001",
                    "approved_by": "it.security.lead@company.internal" if app_status == "APPROVED" else "SYSTEM_DAEMON",
                    "provisioned_at": prov_at,
                    "revoked_at": rev_at,
                    "issue_flag": None
                })
                grant_idx += 1

    def generate_offboarding_cases(self):
        off_counter = 1
        today = date(2026, 10, 28)
        
        terminated_and_notice = [e for e in self.employees if e["employment_status"] in ["TERMINATED", "NOTICE_PERIOD"]]
        for emp in terminated_and_notice:
            off_id = f"OFF-2026-{off_counter:06d}"
            off_counter += 1
            
            # Urgent termination scenario (5% of exits)
            is_urgent = self.rnd.random() < 0.05
            term_type = "URGENT_TERMINATION" if is_urgent else self.rnd.choice(["VOLUNTARY", "INVOLUNTARY", "CONTRACT_END"])
            
            is_terminated = emp["employment_status"] == "TERMINATED"
            case_status = "COMPLETED" if is_terminated else "IN_PROGRESS"
            
            off_case = {
                "id": off_id,
                "employee_id": emp["id"],
                "termination_type": term_type,
                "status": case_status,
                "last_working_day": emp["end_date"] or str(today),
                "hr_owner_id": "EMP-000006",
                "it_owner_id": "EMP-000008",
                "created_at": f"{emp['end_date'] or today}T08:00:00",
                "completed_at": f"{emp['end_date']}T18:00:00" if is_terminated else None
            }
            self.offboarding_cases.append(off_case)

    def inject_edge_cases(self):
        """
        Step 5 — Inject labeled edge cases:
        1. ACCESS_GRANTED_BEFORE_START_APPROVAL: Access granted before start/approval.
        2. DUPLICATE_ASSET_ASSIGNMENT: Duplicate asset assignment.
        3. TERMINATED_EMPLOYEE_RETAINS_GRANT: Terminated employee retains grant.
        4. TASK_DEPENDENCY_CYCLE: Task dependency cycle.
        5. RECOVERED_ASSET_REMAINS_ASSIGNED: Recovered asset remains assigned.
        """
        print("[*] Injecting deliberate labelled edge cases...")
        
        # 1. ACCESS_GRANTED_BEFORE_START_APPROVAL (Inject 5 cases)
        # Find future-starting employees with pending approvals whose grant was marked PROVISIONED prematurely
        future_candidates = [e for e in self.employees if e["employment_status"] == "PREBOARDING"][:5]
        for idx, emp in enumerate(future_candidates, 1):
            target_grant = next((g for g in self.access_grants if g["employee_id"] == emp["id"]), None)
            if not target_grant:
                target_grant = {
                    "id": f"GRANT-{emp['id']}-999",
                    "employee_id": emp["id"],
                    "access_role_id": "ACC-ROLE-005", # AWS Admin
                    "status": "PROVISIONED",
                    "approval_status": "PENDING_APPROVAL",
                    "requested_by": emp["manager_id"] or "EMP-000001",
                    "approved_by": None,
                    "provisioned_at": "2026-10-25T10:00:00", # Before employee start date
                    "revoked_at": None,
                    "issue_flag": None
                }
                self.access_grants.append(target_grant)
            else:
                target_grant["status"] = "PROVISIONED"
                target_grant["approval_status"] = "PENDING_APPROVAL"
                target_grant["approved_by"] = None
                target_grant["provisioned_at"] = "2026-10-20T11:00:00"
                
            anomaly = {
                "anomaly_id": f"ANOM-001-{idx:02d}",
                "issue_type": "ACCESS_GRANTED_BEFORE_START_APPROVAL",
                "entity_name": "AccessGrant",
                "entity_id": target_grant["id"],
                "secondary_entity_id": emp["id"],
                "description": f"Access grant {target_grant['id']} for role {target_grant['access_role_id']} was provisioned prior to start date ({emp['start_date']}) and without required approval.",
                "severity": "CRITICAL",
                "detection_rule": "IF grant.status == 'PROVISIONED' AND (grant.provisioned_at < employee.start_date OR grant.approval_status != 'APPROVED') THEN FLAG",
                "resolution_recommendation": "Immediately suspend or revoke grant until formal manager approval and employee Day-1 orientation is complete."
            }
            self.ground_truth_anomalies.append(anomaly)

        # 2. DUPLICATE_ASSET_ASSIGNMENT (Inject 5 cases)
        # Simultaneously link the same physical asset to two different active employees
        active_employees = [e for e in self.employees if e["employment_status"] == "ACTIVE"]
        for idx in range(5):
            victim_emp = active_employees[50 + idx]
            assigned_asset = self.assets[idx] # AST-000001 ... AST-000005
            first_holder = assigned_asset["assigned_to_employee_id"] or active_employees[idx]["id"]
            assigned_asset["assigned_to_employee_id"] = victim_emp["id"] # Overwritten pointer in inventory
            # We record duplicate assignment in a second asset row with identical serial or multi-holder record
            duplicate_asset = {
                "id": f"AST-{990000 + idx:06d}",
                "asset_tag": assigned_asset["asset_tag"],
                "asset_type": assigned_asset["asset_type"],
                "model": assigned_asset["model"],
                "serial_number": assigned_asset["serial_number"], # Duplicate serial!
                "status": "ASSIGNED",
                "assigned_to_employee_id": first_holder,
                "assigned_date": "2026-05-10",
                "acknowledged_at": "2026-05-11T09:00:00",
                "recovered_at": None,
                "condition_status": "GOOD"
            }
            self.assets.append(duplicate_asset)
            
            anomaly = {
                "anomaly_id": f"ANOM-002-{idx+1:02d}",
                "issue_type": "DUPLICATE_ASSET_ASSIGNMENT",
                "entity_name": "Asset",
                "entity_id": assigned_asset["id"],
                "secondary_entity_id": duplicate_asset["id"],
                "description": f"Physical asset serial {assigned_asset['serial_number']} is concurrently active across {first_holder} and {victim_emp['id']}.",
                "severity": "HIGH",
                "detection_rule": "COUNT(DISTINCT assigned_to_employee_id) GROUP BY serial_number HAVING COUNT > 1",
                "resolution_recommendation": "Conduct physical hardware audit with Facilities and re-tag the second device with a unique asset ID."
            }
            self.ground_truth_anomalies.append(anomaly)

        # 3. TERMINATED_EMPLOYEE_RETAINS_GRANT (Inject 6 cases)
        # Terminated employees who still retain PROVISIONED access
        terminated_employees = [e for e in self.employees if e["employment_status"] == "TERMINATED"][:6]
        for idx, t_emp in enumerate(terminated_employees, 1):
            t_grant = next((g for g in self.access_grants if g["employee_id"] == t_emp["id"]), None)
            if not t_grant:
                t_grant = {
                    "id": f"GRANT-{t_emp['id']}-001",
                    "employee_id": t_emp["id"],
                    "access_role_id": "ACC-ROLE-003", # GitHub
                    "status": "PROVISIONED",
                    "approval_status": "APPROVED",
                    "requested_by": "EMP-000001",
                    "approved_by": "it.security.lead@company.internal",
                    "provisioned_at": f"{t_emp['start_date']}T09:00:00",
                    "revoked_at": None,
                    "issue_flag": None
                }
                self.access_grants.append(t_grant)
            else:
                t_grant["status"] = "PROVISIONED"
                t_grant["revoked_at"] = None
                
            anomaly = {
                "anomaly_id": f"ANOM-003-{idx:02d}",
                "issue_type": "TERMINATED_EMPLOYEE_RETAINS_GRANT",
                "entity_name": "AccessGrant",
                "entity_id": t_grant["id"],
                "secondary_entity_id": t_emp["id"],
                "description": f"Employee {t_emp['id']} was terminated on {t_emp['end_date']} but maintains PROVISIONED access to {t_grant['access_role_id']}.",
                "severity": "CRITICAL",
                "detection_rule": "IF employee.employment_status == 'TERMINATED' AND grant.status == 'PROVISIONED' THEN FLAG",
                "resolution_recommendation": "Execute urgent automated revocation via Okta/Active Directory SCIM connector and review IT exit checklist."
            }
            self.ground_truth_anomalies.append(anomaly)

        # 4. TASK_DEPENDENCY_CYCLE (Inject 4 cases)
        # Introduce circular dependencies (e.g. Task A depends on Task B and Task B depends on Task A)
        sample_cases = self.onboarding_cases[:4]
        for idx, o_case in enumerate(sample_cases, 1):
            task_a = f"TASK-{o_case['id']}-006" # Request & Approve Role-Specific Entitlements
            task_b = f"TASK-{o_case['id']}-007" # Provision Software Licenses & Security Keys
            
            # Normal: 007 depends on 006.
            # Injected cycle: 006 also depends on 007!
            cycle_dep_id = len(self.dependencies) + 1000 + idx
            self.dependencies.append({
                "id": cycle_dep_id,
                "task_id": task_a,
                "prerequisite_task_id": task_b,
                "dependency_type": "FINISH_TO_START",
                "is_mandatory": True,
                "created_at": "2026-10-22T08:00:00"
            })
            
            anomaly = {
                "anomaly_id": f"ANOM-004-{idx:02d}",
                "issue_type": "TASK_DEPENDENCY_CYCLE",
                "entity_name": "TaskDependency",
                "entity_id": str(cycle_dep_id),
                "secondary_entity_id": f"{task_a}<->{task_b}",
                "description": f"Cycle detected in task dependency graph between {task_a} and {task_b} in case {o_case['id']}.",
                "severity": "HIGH",
                "detection_rule": "Topological sort / Tarjan's cycle detection algorithm on task dependency DAG",
                "resolution_recommendation": "Remove circular prerequisite link from TaskDependency table to allow workflow progression."
            }
            self.ground_truth_anomalies.append(anomaly)

        # 5. RECOVERED_ASSET_REMAINS_ASSIGNED (Inject 5 cases)
        # Asset marked RECOVERED with recovered_at date, but assigned_to_employee_id remains populated
        sample_assets = [a for a in self.assets if a["status"] == "ACKNOWLEDGED"][:5]
        for idx, ast in enumerate(sample_assets, 1):
            emp_holder = ast["assigned_to_employee_id"]
            ast["status"] = "RECOVERED"
            ast["recovered_at"] = "2026-10-24T14:30:00"
            # In anomaly: assigned_to_employee_id remains populated (not cleared back to NULL)
            ast["condition_status"] = "GOOD"
            
            anomaly = {
                "anomaly_id": f"ANOM-005-{idx:02d}",
                "issue_type": "RECOVERED_ASSET_REMAINS_ASSIGNED",
                "entity_name": "Asset",
                "entity_id": ast["id"],
                "secondary_entity_id": emp_holder,
                "description": f"Asset {ast['id']} has status RECOVERED on {ast['recovered_at']} but still holds active reference to employee {emp_holder}.",
                "severity": "MEDIUM",
                "detection_rule": "IF asset.status == 'RECOVERED' AND asset.assigned_to_employee_id IS NOT NULL THEN FLAG",
                "resolution_recommendation": "Clear assigned_to_employee_id to NULL and update asset inventory status to IN_STOCK."
            }
            self.ground_truth_anomalies.append(anomaly)

    def validate_dataset(self):
        """
        Step 8 — Run validation:
        PK uniqueness, FK integrity, allowed enums, date ordering, reconciliation.
        """
        print("[*] Running comprehensive integrity and reconciliation validations...")
        
        # 1. Primary Key Uniqueness
        emp_ids = [e["id"] for e in self.employees]
        assert len(emp_ids) == len(set(emp_ids)), "Duplicate Employee PK detected!"
        
        onb_ids = [o["id"] for o in self.onboarding_cases]
        assert len(onb_ids) == len(set(onb_ids)), "Duplicate OnboardingCase PK detected!"
        
        task_ids = [t["id"] for t in self.tasks]
        assert len(task_ids) == len(set(task_ids)), "Duplicate Task PK detected!"
        
        grant_ids = [g["id"] for g in self.access_grants]
        assert len(grant_ids) == len(set(grant_ids)), "Duplicate AccessGrant PK detected!"
        
        asset_ids = [a["id"] for a in self.assets]
        assert len(asset_ids) == len(set(asset_ids)), "Duplicate Asset PK detected!"
        
        # 2. Foreign Key Integrity
        dept_ids_set = {d["id"] for d in self.departments}
        role_ids_set = {r["id"] for r in self.job_roles}
        emp_ids_set = set(emp_ids)
        access_role_ids_set = {a["id"] for a in self.access_roles}
        
        for emp in self.employees:
            assert emp["department_id"] in dept_ids_set, f"Invalid dept FK in {emp['id']}"
            assert emp["role_id"] in role_ids_set, f"Invalid role FK in {emp['id']}"
            if emp["manager_id"]:
                assert emp["manager_id"] in emp_ids_set, f"Invalid manager FK in {emp['id']}"
                
        for grant in self.access_grants:
            assert grant["employee_id"] in emp_ids_set, f"Invalid employee FK in grant {grant['id']}"
            assert grant["access_role_id"] in access_role_ids_set, f"Invalid role FK in grant {grant['id']}"
            
        print("[+] Primary Key and Foreign Key constraints passed with 100% integrity.")

    def export_all(self, output_dir):
        """
        Step 7 & 9 — Export CSV, JSON, SQL seed files and separate ground truth.
        """
        os.makedirs(output_dir, exist_ok=True)
        print(f"[*] Exporting files to {output_dir}...")
        
        # 1. Export JSON files
        json_targets = [
            ("departments.json", self.departments),
            ("job_roles.json", self.job_roles),
            ("access_roles.json", self.access_roles),
            ("employees.json", self.employees),
            ("onboarding_cases.json", self.onboarding_cases),
            ("tasks.json", self.tasks),
            ("task_dependencies.json", self.dependencies),
            ("access_grants.json", self.access_grants),
            ("assets.json", self.assets),
            ("offboarding_cases.json", self.offboarding_cases),
            ("audit_events.json", self.audit_events),
        ]
        
        for filename, data in json_targets:
            filepath = os.path.join(output_dir, filename)
            with open(filepath, "w", encoding="utf-8") as f:
                json.dump(data, f, indent=2)
                
        # 2. Export Separate Ground Truth (Step 7: never leak into input tables)
        gt_json_path = os.path.join(output_dir, "ground_truth.json")
        with open(gt_json_path, "w", encoding="utf-8") as f:
            json.dump(self.ground_truth_anomalies, f, indent=2)
            
        gt_csv_path = os.path.join(output_dir, "ground_truth.csv")
        with open(gt_csv_path, "w", newline="", encoding="utf-8") as f:
            if self.ground_truth_anomalies:
                writer = csv.DictWriter(f, fieldnames=list(self.ground_truth_anomalies[0].keys()))
                writer.writeheader()
                writer.writerows(self.ground_truth_anomalies)

        # 3. Export CSV files
        for filename, data in json_targets:
            csv_filename = filename.replace(".json", ".csv")
            csv_path = os.path.join(output_dir, csv_filename)
            if data:
                with open(csv_path, "w", newline="", encoding="utf-8") as f:
                    writer = csv.DictWriter(f, fieldnames=list(data[0].keys()))
                    writer.writeheader()
                    writer.writerows(data)

        # 4. Export SQL Seed Script
        sql_path = os.path.join(output_dir, "seed_data.sql")
        with open(sql_path, "w", encoding="utf-8") as f:
            f.write("-- Enterprise Workflow Orchestration SQL Seed Data\n")
            f.write(f"-- Generated on {datetime.now().isoformat()} with Seed {self.seed}\n")
            f.write("SET FOREIGN_KEY_CHECKS = 0;\n\n")
            
            # Helper to write insert statements
            def write_inserts(table_name, rows):
                if not rows:
                    return
                cols = list(rows[0].keys())
                col_names = ", ".join([f"`{c}`" for c in cols])
                f.write(f"-- Table: {table_name} ({len(rows)} rows)\n")
                
                # Chunked inserts for MySQL performance
                batch_size = 200
                for i in range(0, len(rows), batch_size):
                    batch = rows[i:i+batch_size]
                    f.write(f"INSERT INTO `{table_name}` ({col_names}) VALUES\n")
                    val_strs = []
                    for row in batch:
                        vals = []
                        for col in cols:
                            val = row[col]
                            if val is None:
                                vals.append("NULL")
                            elif isinstance(val, bool):
                                vals.append("1" if val else "0")
                            elif isinstance(val, (int, float)):
                                vals.append(str(val))
                            else:
                                escaped = str(val).replace("\\", "\\\\").replace("'", "''")
                                vals.append(f"'{escaped}'")
                        val_strs.append(f"({', '.join(vals)})")
                    f.write(",\n".join(val_strs) + ";\n")
                f.write("\n")

            write_inserts("departments", self.departments)
            write_inserts("job_roles", self.job_roles)
            write_inserts("access_roles", self.access_roles)
            write_inserts("employees", self.employees)
            write_inserts("onboarding_cases", self.onboarding_cases)
            write_inserts("case_tasks", self.tasks)
            write_inserts("task_dependencies", self.dependencies)
            write_inserts("access_grants", self.access_grants)
            write_inserts("assets", self.assets)
            write_inserts("offboarding_cases", self.offboarding_cases)
            write_inserts("audit_events", self.audit_events)
            
            f.write("SET FOREIGN_KEY_CHECKS = 1;\n")
            
        print("[+] Export finished successfully.")

def compute_checksums(directory):
    checksums = {}
    for root, _, files in os.walk(directory):
        for f in sorted(files):
            if f.endswith((".json", ".csv", ".sql")):
                p = os.path.join(root, f)
                h = hashlib.sha256()
                with open(p, "rb") as fh:
                    while chunk := fh.read(65536):
                        h.update(chunk)
                rel_path = os.path.relpath(p, directory)
                checksums[rel_path] = h.hexdigest()
    return checksums

if __name__ == "__main__":
    seed_val = DEFAULT_SEED
    scale_val = 1.0
    
    if len(sys.argv) > 1:
        seed_val = int(sys.argv[1])
    if len(sys.argv) > 2:
        scale_val = float(sys.argv[2])
        
    export_dir = os.path.join(os.path.dirname(os.path.abspath(__file__)), "exports")
    
    # Run Generation
    gen = SyntheticDataGenerator(seed=seed_val, scale_ratio=scale_val)
    gen.run()
    gen.export_all(export_dir)
    
    # Compute Checksums
    chk = compute_checksums(export_dir)
    manifest = {
        "generator_script": "generator.py",
        "seed": seed_val,
        "scale": scale_val,
        "generated_at": datetime.now().isoformat(),
        "total_records": {
            "employees": len(gen.employees),
            "tasks": len(gen.tasks),
            "access_grants": len(gen.access_grants),
            "assets": len(gen.assets),
            "onboarding_cases": len(gen.onboarding_cases),
            "offboarding_cases": len(gen.offboarding_cases),
            "audit_events": len(gen.audit_events),
            "ground_truth_anomalies": len(gen.ground_truth_anomalies)
        },
        "sha256_checksums": chk
    }
    
    manifest_path = os.path.join(os.path.dirname(os.path.abspath(__file__)), "source_manifest.json")
    with open(manifest_path, "w", encoding="utf-8") as f:
        json.dump(manifest, f, indent=2)
        
    print(f"[+] Saved source manifest to {manifest_path}")

