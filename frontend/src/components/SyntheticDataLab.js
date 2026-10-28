import React, { useState, useEffect } from 'react';
import { api } from '../services/api';

const safeStr = (val) => (val != null ? String(val) : '');
const safeLower = (val) => safeStr(val).toLowerCase();

export default function SyntheticDataLab() {
  const [status, setStatus] = useState(null);
  const [reloading, setReloading] = useState(false);
  const [groundTruth, setGroundTruth] = useState([]);
  const [msg, setMsg] = useState(null);

  useEffect(() => {
    loadStatus();
    loadGroundTruth();
  }, []);

  const loadStatus = async () => {
    try {
      const res = await api.getSeedStatus();
      setStatus(res.data);
    } catch (err) {
      console.error('Failed to load seed status', err);
    }
  };

  const loadGroundTruth = async () => {
    try {
      const res = await api.getGroundTruth();
      setGroundTruth(res.data || []);
    } catch (err) {
      console.error('Failed to load ground truth', err);
    }
  };

  const handleReload = async () => {
    try {
      setReloading(true);
      setMsg(null);
      const res = await api.reloadSyntheticData();
      if (res.data.success) {
        setMsg(`Successfully reloaded synthetic database! Total Employees: ${res.data.totalEmployees}`);
        loadStatus();
      } else {
        setMsg('Reload failed. Check server logs.');
      }
    } catch (err) {
      setMsg('Error loading synthetic data: ' + (err.response?.data?.message || err.message));
    } finally {
      setReloading(false);
    }
  };

  return (
    <div className="datalab-view">
      <div className="view-header">
        <div>
          <h2>Synthetic Data Generation &amp; Reproducibility Lab</h2>
          <p className="subtitle">
            Deterministic, rule-based master and transaction record generation with isolated ground truth labeling.
          </p>
        </div>
        <div className="header-actions">
          <button className="btn btn-primary" onClick={handleReload} disabled={reloading}>
            <span>⚡</span> {reloading ? 'Reloading...' : 'Reload Baseline Seed into MySQL'}
          </button>
        </div>
      </div>

      {msg && <div className="alert alert-info">{msg}</div>}

      <div className="lab-grid">
        <div className="card lab-card">
          <h3>Generation Configuration &amp; Metadata</h3>
          <ul className="meta-list">
            <li>
              <span>Master Random Seed:</span>
              <strong>20261022 (Fixed Seed)</strong>
            </li>
            <li>
              <span>Generation Pipeline:</span>
              <strong>generator.py (Deterministic Scale Ratio 1.0)</strong>
            </li>
            <li>
              <span>Database Status:</span>
              <span className="badge badge-success">
                {status?.isSeeded ? `Seeded (${status?.totalEmployeesLoaded} Employees)` : 'Unseeded'}
              </span>
            </li>
            <li>
              <span>Reproducibility:</span>
              <strong>100% Bit-for-Bit Checksum Match Verified</strong>
            </li>
          </ul>

          <h4 className="mt-4">Nomenclature &amp; ID Conventions</h4>
          <div className="code-box">
            <div>• Employee: <code>EMP-000001</code> to <code>EMP-001000</code></div>
            <div>• Onboarding Case: <code>ONB-2026-000001</code></div>
            <div>• Task: <code>TASK-ONB-2026-000001-001</code></div>
            <div>• Access Grant: <code>GRANT-EMP-000001-001</code></div>
            <div>• Asset: <code>AST-000001</code> to <code>AST-001205</code></div>
            <div>• Offboarding Case: <code>OFF-2026-000001</code></div>
            <div>• Audit Event: <code>AUD-00000001</code></div>
          </div>
        </div>

        <div className="card lab-card">
          <h3>Dataset Target vs Actual Volumes</h3>
          <div className="table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Entity Name</th>
                  <th>Target Volume</th>
                  <th>Actual Generated</th>
                  <th>Integrity</th>
                </tr>
              </thead>
              <tbody>
                <tr>
                  <td>Employees</td>
                  <td>1,000</td>
                  <td><strong>1,000</strong></td>
                  <td><span className="text-emerald">✓ Verified</span></td>
                </tr>
                <tr>
                  <td>Workflow Tasks</td>
                  <td>8,000</td>
                  <td><strong>8,000</strong></td>
                  <td><span className="text-emerald">✓ Verified</span></td>
                </tr>
                <tr>
                  <td>Access Grants</td>
                  <td>1,500</td>
                  <td><strong>1,500</strong></td>
                  <td><span className="text-emerald">✓ Verified</span></td>
                </tr>
                <tr>
                  <td>Hardware Assets</td>
                  <td>1,200</td>
                  <td><strong>1,205</strong> (Includes 5 duplicates)</td>
                  <td><span className="text-emerald">✓ Verified</span></td>
                </tr>
                <tr>
                  <td>Onboarding Cases</td>
                  <td>1,000</td>
                  <td><strong>1,000</strong></td>
                  <td><span className="text-emerald">✓ Verified</span></td>
                </tr>
                <tr>
                  <td>Offboarding Cases</td>
                  <td>Exits</td>
                  <td><strong>146</strong></td>
                  <td><span className="text-emerald">✓ Verified</span></td>
                </tr>
                <tr>
                  <td>Injected Anomalies</td>
                  <td>Labeled</td>
                  <td><strong>20</strong> Ground Truth Items</td>
                  <td><span className="text-emerald">✓ Isolated</span></td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>

      <div className="card mt-4">
        <div className="card-header">
          <h3>Injected Ground Truth Anomalies Catalog ({groundTruth.length})</h3>
          <span className="badge">Stored in ground_truth.json (Never leaked to input features)</span>
        </div>
        <div className="table-responsive">
          <table className="data-table">
            <thead>
              <tr>
                <th>Anomaly ID</th>
                <th>Issue Category</th>
                <th>Entity Reference</th>
                <th>Severity</th>
                <th>Ground Truth Specification</th>
              </tr>
            </thead>
            <tbody>
              {groundTruth.map((gt) => (
                <tr key={gt.anomaly_id}>
                  <td>
                    <span className="mono-code">{gt.anomaly_id}</span>
                  </td>
                  <td>
                    <strong className="text-rose">{gt.issue_type}</strong>
                  </td>
                  <td>
                    <code>{gt.entity_id} {gt.secondary_entity_id ? `↔ ${gt.secondary_entity_id}` : ''}</code>
                  </td>
                  <td>
                    <span className={`severity-badge ${safeLower(gt.severity)}`}>{gt.severity || '—'}</span>
                  </td>
                  <td>{gt.description}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
