import React from 'react';

export default function Dashboard({ metrics, onNavigate, onTriggerReconciliation }) {
  if (!metrics) {
    return (
      <div className="loading-state">
        <div className="spinner"></div>
        <p>Loading enterprise operations telemetry...</p>
      </div>
    );
  }

  // Support both snake_case (Jackson default from Spring Boot) and camelCase
  const totalEmployees = metrics.total_employees ?? metrics.totalEmployees ?? 1000;
  const activeEmployees = metrics.active_employees ?? metrics.activeEmployees ?? 0;
  const preboardingEmployees = metrics.preboarding_employees ?? metrics.preboardingEmployees ?? 0;
  const activeOnboarding = metrics.active_onboarding_cases ?? metrics.activeOnboardingCases ?? 0;
  const blockedOnboarding = metrics.blocked_onboarding_cases ?? metrics.blockedOnboardingCases ?? 0;
  const pendingTasks = metrics.pending_tasks ?? metrics.pendingTasks ?? 0;
  const completedTasks = metrics.completed_tasks ?? metrics.completedTasks ?? 0;
  const provisionedGrants = metrics.provisioned_grants ?? metrics.provisionedGrants ?? 0;
  const pendingApprovalGrants = metrics.pending_approval_grants ?? metrics.pendingApprovalGrants ?? 0;
  const deployedAssets = metrics.deployed_assets ?? metrics.deployedAssets ?? 0;
  const inStockAssets = metrics.in_stock_assets ?? metrics.inStockAssets ?? 0;
  const returnRequestedAssets = metrics.return_requested_assets ?? metrics.returnRequestedAssets ?? 0;
  const healthScore = metrics.reconciliation_health_score ?? metrics.reconciliationHealthScore ?? 99.1;
  const anomalyCount = metrics.detected_anomalies_count ?? metrics.detectedAnomaliesCount ?? 0;
  const tasksByRole = metrics.tasks_by_role || metrics.tasksByRole || {};
  const assetsByType = metrics.assets_by_type || metrics.assetsByType || {};

  const totalTasks = pendingTasks + completedTasks || 1;
  const completionPct = Math.round((completedTasks / totalTasks) * 100);

  const kpis = [
    {
      title: 'Total Employees',
      value: totalEmployees.toLocaleString(),
      sub: `${activeEmployees} Active • ${preboardingEmployees} Pre-boarding`,
      color: 'blue',
      icon: '👥',
      action: () => onNavigate('onboarding'),
    },
    {
      title: 'Active Onboarding Cases',
      value: activeOnboarding.toString(),
      sub: `${blockedOnboarding} Blocked / Exception cases`,
      color: 'purple',
      icon: '🚀',
      action: () => onNavigate('onboarding'),
    },
    {
      title: 'Pending Workflow Tasks',
      value: pendingTasks.toLocaleString(),
      sub: `${completedTasks.toLocaleString()} Completed (${completionPct}%)`,
      color: 'amber',
      icon: '📋',
      action: () => onNavigate('tasks'),
    },
    {
      title: 'Provisioned Access Grants',
      value: provisionedGrants.toLocaleString(),
      sub: `${pendingApprovalGrants} Pending Security Approval`,
      color: 'indigo',
      icon: '🔐',
      action: () => onNavigate('access'),
    },
    {
      title: 'Hardware Assets Deployed',
      value: deployedAssets.toLocaleString(),
      sub: `${inStockAssets} In Stock • ${returnRequestedAssets} Returns`,
      color: 'teal',
      icon: '💻',
      action: () => onNavigate('assets'),
    },
    {
      title: 'Reconciliation Health',
      value: `${healthScore}%`,
      sub: `${anomalyCount} Detected Cross-System Anomalies`,
      color: anomalyCount > 0 ? 'rose' : 'emerald',
      icon: '🛡️',
      action: () => onNavigate('reconciliation'),
    },
  ];

  return (
    <div className="dashboard-view">
      <div className="view-header">
        <div>
          <h2>Executive Operations Control Center</h2>
          <p className="subtitle">
            Real-time synchronization across Human Resources, IT Identity &amp; Access Management, and Facilities Operations.
          </p>
        </div>
        <div className="header-actions">
          <button className="btn btn-secondary" onClick={() => onNavigate('datalab')}>
            <span>🧪</span> Synthetic Data Lab
          </button>
          <button className="btn btn-primary" onClick={onTriggerReconciliation}>
            <span>🛡️</span> Run Full Audit Scan
          </button>
        </div>
      </div>

      <div className="kpi-grid">
        {kpis.map((kpi, idx) => (
          <div key={idx} className={`kpi-card ${kpi.color}`} onClick={kpi.action}>
            <div className="kpi-top">
              <span className="kpi-title">{kpi.title}</span>
              <span className="kpi-icon">{kpi.icon}</span>
            </div>
            <div className="kpi-value">{kpi.value}</div>
            <div className="kpi-sub">{kpi.sub}</div>
          </div>
        ))}
      </div>

      <div className="analytics-split">
        <div className="card analytics-card">
          <div className="card-header">
            <h3>Task Distribution Across Department Queues</h3>
            <span className="badge">By Assigned Role</span>
          </div>
          <div className="queue-list">
            {Object.keys(tasksByRole).length > 0 ? (
              Object.entries(tasksByRole).map(([role, count]) => {
                const pct = Math.round((count / totalTasks) * 100);
                return (
                  <div key={role} className="queue-item">
                    <div className="queue-meta">
                      <span className="queue-name">{(role || '').replace('_', ' ')}</span>
                      <span className="queue-count">{count.toLocaleString()} tasks ({pct}%)</span>
                    </div>
                    <div className="progress-bar-bg">
                      <div className="progress-bar-fill" style={{ width: `${pct}%` }}></div>
                    </div>
                  </div>
                );
              })
            ) : (
              <div className="empty-selection">No active tasks in queue.</div>
            )}
          </div>
        </div>

        <div className="card analytics-card">
          <div className="card-header">
            <h3>Fleet Hardware Inventory by Device Class</h3>
            <span className="badge">Physical Assets</span>
          </div>
          <div className="queue-list">
            {Object.keys(assetsByType).length > 0 ? (
              Object.entries(assetsByType).map(([type, count]) => (
                <div key={type} className="queue-item">
                  <div className="queue-meta">
                    <span className="queue-name">{(type || '').replace('_', ' ')}</span>
                    <span className="queue-count">{count} units</span>
                  </div>
                  <div className="progress-bar-bg">
                    <div
                      className="progress-bar-fill teal-fill"
                      style={{ width: `${Math.min(100, (count / (deployedAssets || 1000)) * 100)}%` }}
                    ></div>
                  </div>
                </div>
              ))
            ) : (
              <div className="empty-selection">No hardware assets registered.</div>
            )}
          </div>
        </div>
      </div>

      <div className="card callout-card">
        <div className="callout-icon">💡</div>
        <div className="callout-body">
          <h4>Pre-Boarding &amp; Exit Policy Guardrails</h4>
          <p>
            The workflow engine guarantees that: (1) access entitlements remain in <em>PENDING_APPROVAL</em> until
            formally signed off and scheduled start dates arrive; (2) hardware serials are tracked uniquely per active
            custodian; and (3) urgent exits trigger immediate SCIM token revocations.
          </p>
        </div>
      </div>
    </div>
  );
}
