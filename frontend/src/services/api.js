import axios from 'axios';

const API_BASE = 'http://localhost:8080/api/v1';

const apiClient = axios.create({
  baseURL: API_BASE,
  headers: {
    'Content-Type': 'application/json',
  },
  timeout: 10000,
});

export const api = {
  // Dashboard Metrics
  getDashboardMetrics: () => apiClient.get('/dashboard/metrics'),

  // Employees
  getEmployees: (page = 0, size = 25, search = '') =>
    apiClient.get(`/employees?page=${page}&size=${size}&search=${encodeURIComponent(search)}`),
  getEmployeeById: (id) => apiClient.get(`/employees/${id}`),

  // Onboarding
  getOnboardingCases: (page = 0, size = 25, search = '') =>
    apiClient.get(`/onboarding?page=${page}&size=${size}&search=${encodeURIComponent(search)}`),
  getOnboardingCaseById: (id) => apiClient.get(`/onboarding/${id}`),
  advanceCaseStage: (id) => apiClient.post(`/onboarding/${id}/advance`),

  // Tasks
  getTasks: (page = 0, size = 25, status = '', category = '') => {
    let url = `/tasks?page=${page}&size=${size}`;
    if (status) url += `&status=${status}`;
    if (category) url += `&category=${category}`;
    return apiClient.get(url);
  },
  getTasksByCase: (caseId) => apiClient.get(`/tasks/case/${caseId}`),
  executeTaskAction: (taskId, payload) => apiClient.post(`/tasks/${taskId}/action`, payload),

  // Access Grants
  getAccessGrants: (page = 0, size = 25, search = '') =>
    apiClient.get(`/access-grants?page=${page}&size=${size}&search=${encodeURIComponent(search)}`),
  requestAccessGrant: (payload) => apiClient.post('/access-grants/request', payload),
  approveAccessGrant: (id, approverId) => apiClient.post(`/access-grants/${id}/approve`, { approverId }),
  provisionAccessGrant: (id, operatorId) => apiClient.post(`/access-grants/${id}/provision`, { operatorId }),
  revokeAccessGrant: (id, operatorId, reason) => apiClient.post(`/access-grants/${id}/revoke`, { operatorId, reason }),

  // Assets
  getAssets: (page = 0, size = 25, search = '') =>
    apiClient.get(`/assets?page=${page}&size=${size}&search=${encodeURIComponent(search)}`),
  assignAsset: (id, payload) => apiClient.post(`/assets/${id}/assign`, payload),
  acknowledgeAsset: (id, employeeId) => apiClient.post(`/assets/${id}/acknowledge`, { employeeId }),
  recoverAsset: (id, payload) => apiClient.post(`/assets/${id}/recover`, payload),

  // Offboarding
  getOffboardingCases: (page = 0, size = 25, search = '') =>
    apiClient.get(`/offboarding?page=${page}&size=${size}&search=${encodeURIComponent(search)}`),
  initiateOffboarding: (payload) => apiClient.post('/offboarding/initiate', payload),
  completeOffboarding: (id) => apiClient.post(`/offboarding/${id}/complete`),

  // Reconciliation & Audit
  runReconciliationScan: () => apiClient.get('/reconciliation/scan'),
  getGroundTruth: () => apiClient.get('/reconciliation/ground-truth'),
  getAuditEvents: (page = 0, size = 25, search = '') =>
    apiClient.get(`/audit-events?page=${page}&size=${size}&search=${encodeURIComponent(search)}`),

  // Synthetic Data Management
  getSeedStatus: () => apiClient.get('/synthetic-data/status'),
  reloadSyntheticData: () => apiClient.post('/synthetic-data/load'),
};

