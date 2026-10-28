import React, { useState, useEffect } from 'react';
import { api } from '../services/api';

const safeStr = (val) => (val != null ? String(val) : '');
const safeLower = (val) => safeStr(val).toLowerCase();

export default function ReconciliationAudit() {
  const [report, setReport] = useState(null);
  const [loading, setLoading] = useState(false);
  const [activeSubTab, setActiveSubTab] = useState('anomalies');
  const [auditLogs, setAuditLogs] = useState([]);
  const [logsLoading, setLogsLoading] = useState(false);
  const [logSearch, setLogSearch] = useState('');

  useEffect(() => {
    runScan();
    loadAuditLogs();
  }, []);

  const runScan = async () => {
    try {
      setLoading(true);
      const res = await api.runReconciliationScan();
      setReport(res.data);
    } catch (err) {
      console.error('Failed to run reconciliation scan', err);
    } finally {
      setLoading(false);
    }
  };

  const loadAuditLogs = async () => {
    try {
      setLogsLoading(true);
      const res = await api.getAuditEvents(0, 50, logSearch);
      setAuditLogs(res.data.content || []);
    } catch (err) {
      console.error('Failed to load audit logs', err);
    } finally {
      setLogsLoading(false);
    }
  };

  const health = report?.reconciliation_health_score !== undefined ? report.reconciliation_health_score : (report?.reconciliationHealthScore || 100);
  const prec = report?.precision !== undefined ? report.precision : 1;
  const rec = report?.recall !== undefined ? report.recall : 1;
  const f1 = report?.f1_score !== undefined ? report.f1_score : (report?.f1Score || 1);
  const totalAnomalies = report?.total_anomalies_detected !== undefined ? report.total_anomalies_detected : (report?.totalAnomaliesDetected || 0);
  const knownGt = report?.known_ground_truth_count !== undefined ? report.known_ground_truth_count : (report?.knownGroundTruthCount || 0);
  const anomalyList = report?.anomaly_details || report?.anomalyDetails || [];

  return (
    <div className="reconciliation-view">
      <div className="view-header">
        <div>
          <h2>Automated Rule-Based Reconciliation &amp; Audit Engine</h2>
          <p className="subtitle">
            Cross-system invariant checks across HR status, Access Grants, Equipment Fleet, and Task Dependency DAGs.
          </p>
        </div>
        <div className="header-actions">
          <button className="btn btn-primary" onClick={runScan} disabled={loading}>
            <span>🔄</span> {loading ? 'Scanning Records...' : 'Execute Live Audit Scan'}
          </button>
        </div>
      </div>

      {report && (
        <div className="metrics-summary-bar card">
          <div className="summary-stat">
            <span className="stat-label">Health Score</span>
            <span className="stat-value text-emerald">{health}%</span>
          </div>
          <div className="summary-stat">
            <span className="stat-label">Precision</span>
            <span className="stat-value">{(prec * 100).toFixed(1)}%</span>
          </div>
          <div className="summary-stat">
            <span className="stat-label">Recall</span>
            <span className="stat-value">{(rec * 100).toFixed(1)}%</span>
          </div>
          <div className="summary-stat">
            <span className="stat-label">F1-Score</span>
            <span className="stat-value">{(f1 * 100).toFixed(1)}%</span>
          </div>
          <div className="summary-stat">
            <span className="stat-label">Detected Anomalies</span>
            <span className="stat-value text-rose">{totalAnomalies}</span>
          </div>
          <div className="summary-stat">
            <span className="stat-label">Ground Truth Baseline</span>
            <span className="stat-value">{knownGt} Items</span>
          </div>
        </div>
      )}

      {/* Sub Navigation */}
      <div className="sub-nav-tabs">
        <button
          className={`sub-tab-btn ${activeSubTab === 'anomalies' ? 'active' : ''}`}
          onClick={() => setActiveSubTab('anomalies')}
        >
          Detected Anomalies &amp; Invariant Violations ({totalAnomalies})
        </button>
        <button
          className={`sub-tab-btn ${activeSubTab === 'audit' ? 'active' : ''}`}
          onClick={() => {
            setActiveSubTab('audit');
            loadAuditLogs();
          }}
        >
          Immutable Audit Event Journal
        </button>
      </div>

      {activeSubTab === 'anomalies' ? (
        <div className="anomalies-list">
          {loading ? (
            <div className="loading-state">
              <div className="spinner"></div>
              <p>Executing reconciliation rules against database records...</p>
            </div>
          ) : anomalyList.length > 0 ? (
            anomalyList.map((a) => {
              const anomId = a.anomaly_id || a.anomalyId;
              const issue = a.issue_type || a.issueType;
              const sev = a.severity || 'MEDIUM';
              const isGt = a.known_ground_truth !== undefined ? a.known_ground_truth : a.knownGroundTruth;
              const entName = a.entity_name || a.entityName;
              const entId = a.entity_id || a.entityId;
              const secId = a.secondary_entity_id || a.secondaryEntityId;
              const rule = a.detection_rule || a.detectionRule;
              const resol = a.resolution_recommendation || a.resolutionRecommendation;
              return (
                <div key={anomId} className={`card anomaly-card severity-${safeLower(sev)}`}>
                  <div className="anomaly-header">
                    <div className="anomaly-title-box">
                      <span className={`severity-badge ${safeLower(sev)}`}>{sev}</span>
                      <strong className="anomaly-type">{issue}</strong>
                      {isGt && <span className="ground-truth-tag">✓ Verified Ground Truth</span>}
                    </div>
                    <span className="anomaly-id">{anomId}</span>
                  </div>

                  <p className="anomaly-desc">{a.description}</p>

                  <div className="anomaly-details-grid">
                    <div className="detail-box">
                      <span className="detail-label">Entity / Foreign Reference:</span>
                      <code>
                        {entName}: {entId} {secId ? `→ ${secId}` : ''}
                      </code>
                    </div>
                    <div className="detail-box">
                      <span className="detail-label">Detection Rule:</span>
                      <span className="rule-text">{rule}</span>
                    </div>
                  </div>

                  <div className="remediation-box">
                    <strong>Recommended Remediation:</strong> {resol}
                  </div>
                </div>
              );
            })
          ) : (
            <div className="empty-selection">No anomalies detected. System is 100% reconciled.</div>
          )}
        </div>
      ) : (
        <div className="card table-card">
          <div className="audit-search-bar">
            <input
              type="text"
              placeholder="Search entity, action type, or actor..."
              value={logSearch}
              onChange={(e) => setLogSearch(e.target.value)}
              onKeyDown={(e) => e.key === 'Enter' && loadAuditLogs()}
            />
            <button className="btn btn-sm btn-secondary" onClick={loadAuditLogs}>
              Search
            </button>
          </div>

          <div className="table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Audit ID</th>
                  <th>Timestamp</th>
                  <th>Entity</th>
                  <th>Action</th>
                  <th>Actor</th>
                  <th>Role</th>
                  <th>State Transition</th>
                  <th>Details</th>
                </tr>
              </thead>
              <tbody>
                {logsLoading ? (
                  <tr>
                    <td colSpan="8" className="text-center py-4">
                      <div className="spinner-small"></div>
                    </td>
                  </tr>
                ) : auditLogs.length === 0 ? (
                  <tr>
                    <td colSpan="8" className="text-center py-4">
                      No audit events recorded.
                    </td>
                  </tr>
                ) : (
                  auditLogs.map((log) => {
                    const entName = log.entity_name || log.entityName;
                    const entId = log.entity_id || log.entityId;
                    const actType = log.action_type || log.actionType;
                    const actId = log.actor_id || log.actorId;
                    const actRole = log.actor_role || log.actorRole;
                    const oldSt = log.old_state || log.oldState;
                    const newSt = log.new_state || log.newState;
                    const ts = log.timestamp;
                    return (
                      <tr key={log.id}>
                        <td>
                          <span className="mono-code">{log.id}</span>
                        </td>
                        <td className="timestamp-cell">
                          {Array.isArray(ts)
                            ? `${ts[0]}-${String(ts[1]).padStart(2, '0')}-${String(ts[2]).padStart(2, '0')} ${String(ts[3] || 0).padStart(2, '0')}:${String(ts[4] || 0).padStart(2, '0')}`
                            : safeStr(ts).replace('T', ' ')}
                        </td>
                        <td>
                          <strong>{entName}</strong>
                          <div className="table-sub">{entId}</div>
                        </td>
                        <td>
                          <span className={`action-badge ${safeLower(actType)}`}>{actType || '—'}</span>
                        </td>
                        <td>{actId}</td>
                        <td>
                          <span className="role-tag">{actRole}</span>
                        </td>
                        <td>
                          <div className="transition-cell">
                            {oldSt ? <span className="old-val">{oldSt}</span> : <span className="text-muted">null</span>}
                            <span className="arrow">→</span>
                            {newSt ? <span className="new-val">{newSt}</span> : <span className="text-muted">null</span>}
                          </div>
                        </td>
                        <td className="details-cell">{log.details}</td>
                      </tr>
                    );
                  })
                )}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
}
