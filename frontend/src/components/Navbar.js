import React from 'react';

export default function Navbar({ activeTab, setActiveTab, healthScore, anomalyCount }) {
  const tabs = [
    { id: 'dashboard', label: 'Executive Dashboard', icon: '📊' },
    { id: 'onboarding', label: 'Onboarding Orchestrator', icon: '🚀' },
    { id: 'tasks', label: 'Task Execution Board', icon: '📋' },
    { id: 'access', label: 'Access Provisioning Matrix', icon: '🔐' },
    { id: 'assets', label: 'Asset Fleet Manager', icon: '💻' },
    { id: 'offboarding', label: 'Offboarding & Exits', icon: '🚪' },
    { id: 'reconciliation', label: 'Reconciliation & Audit', icon: '🛡️', badge: anomalyCount },
    { id: 'datalab', label: 'Synthetic Data Lab', icon: '🧪' },
  ];

  return (
    <header className="navbar-container">
      <div className="navbar-top">
        <div className="navbar-brand">
          <div className="brand-badge">Enterprise</div>
          <h1>Cross-Functional Workflow Orchestrator</h1>
          <span className="domain-tag">HR • IT • Facilities</span>
        </div>

        <div className="navbar-status-bar">
          <div className="status-indicator">
            <span className="status-dot pulse-green"></span>
            <span>Workflow Engine: <strong>ACTIVE</strong></span>
          </div>
          <div className="health-badge">
            Reconciliation Health: <strong>{healthScore !== undefined ? healthScore : 100}%</strong>
          </div>
          <div className="actor-profile">
            <div className="avatar-circle">AD</div>
            <div className="actor-meta">
              <span className="actor-name">System Administrator</span>
              <span className="actor-role">Global Operations</span>
            </div>
          </div>
        </div>
      </div>

      <nav className="navbar-nav">
        {tabs.map((tab) => (
          <button
            key={tab.id}
            className={`nav-tab-btn ${activeTab === tab.id ? 'active' : ''}`}
            onClick={() => setActiveTab(tab.id)}
          >
            <span className="tab-icon">{tab.icon}</span>
            <span className="tab-label">{tab.label}</span>
            {tab.badge > 0 && <span className="tab-badge">{tab.badge}</span>}
          </button>
        ))}
      </nav>
    </header>
  );
}

