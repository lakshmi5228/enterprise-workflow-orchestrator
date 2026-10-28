import React, { useState, useEffect } from 'react';
import { api } from '../services/api';

const safeStr = (val) => (val != null ? String(val) : '');
const safeLower = (val) => safeStr(val).toLowerCase();

export default function OffboardingManager() {
  const [cases, setCases] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [showInitiateModal, setShowInitiateModal] = useState(false);
  const [feedback, setFeedback] = useState(null);

  // New offboarding form state
  const [empId, setEmpId] = useState('');
  const [termType, setTermType] = useState('VOLUNTARY');
  const [reason, setReason] = useState('');

  useEffect(() => {
    loadCases();
  }, [search]);

  const loadCases = async () => {
    try {
      setLoading(true);
      const res = await api.getOffboardingCases(0, 50, search);
      setCases(res.data.content || []);
    } catch (err) {
      console.error('Failed to load offboarding cases', err);
    } finally {
      setLoading(false);
    }
  };

  const handleInitiate = async (e) => {
    e.preventDefault();
    if (!empId.trim()) return;
    try {
      await api.initiateOffboarding({
        employeeId: empId.trim(),
        terminationType: termType,
        reason: reason || 'Standard workflow exit',
      });
      setFeedback(`Offboarding initiated for ${empId}!`);
      setShowInitiateModal(false);
      setEmpId('');
      setReason('');
      loadCases();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to initiate offboarding');
    }
  };

  const handleComplete = async (id) => {
    try {
      await api.completeOffboarding(id);
      setFeedback(`Offboarding case ${id} marked completed.`);
      loadCases();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to complete case');
    }
  };

  return (
    <div className="offboarding-view">
      <div className="view-header">
        <div>
          <h2>Employee Exit &amp; Offboarding Control Center</h2>
          <p className="subtitle">
            Orchestrates hardware retrieval, automated digital identity revocation, and exit compliance.
          </p>
        </div>
        <div className="header-actions">
          <button className="btn btn-danger" onClick={() => setShowInitiateModal(true)}>
            <span>🚪</span> Initiate Employee Exit
          </button>
        </div>
      </div>

      {feedback && <div className="alert alert-success">{feedback}</div>}

      <div className="card table-card">
        <div className="table-responsive">
          <table className="data-table">
            <thead>
              <tr>
                <th>Case ID</th>
                <th>Employee</th>
                <th>Exit Category</th>
                <th>Status</th>
                <th>Last Working Day</th>
                <th>HR Coordinator</th>
                <th>IT Security Lead</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan="8" className="text-center py-4">
                    <div className="spinner-small"></div>
                  </td>
                </tr>
              ) : cases.length === 0 ? (
                <tr>
                  <td colSpan="8" className="text-center py-4">
                    No offboarding cases found.
                  </td>
                </tr>
              ) : (
                cases.map((c) => {
                  const emp = c.employee_id || c.employeeId;
                  const term = c.termination_type || c.terminationType;
                  const lwd = c.last_working_day || c.lastWorkingDay;
                  const hr = c.hr_owner_id || c.hrOwnerId;
                  const it = c.it_owner_id || c.itOwnerId;
                  return (
                    <tr key={c.id}>
                      <td>
                        <span className="mono-code">{c.id}</span>
                      </td>
                      <td>
                        <strong>{emp}</strong>
                      </td>
                      <td>
                        <span
                          className={`type-tag ${
                            term === 'URGENT_TERMINATION' ? 'urgent' : 'standard'
                          }`}
                        >
                          {safeStr(term).replace('_', ' ')}
                        </span>
                      </td>
                      <td>
                        <span className={`status-pill ${safeLower(c.status)}`}>{c.status || '—'}</span>
                      </td>
                      <td>{lwd ? (Array.isArray(lwd) ? `${lwd[0]}-${String(lwd[1]).padStart(2, '0')}-${String(lwd[2]).padStart(2, '0')}` : safeStr(lwd)) : '—'}</td>
                      <td>{hr}</td>
                      <td>{it}</td>
                      <td>
                        {c.status !== 'COMPLETED' ? (
                          <button className="btn btn-xs btn-primary" onClick={() => handleComplete(c.id)}>
                            Complete Exit
                          </button>
                        ) : (
                          <span className="completed-badge">✓ Closed</span>
                        )}
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>
      </div>

      {/* Initiate Modal */}
      {showInitiateModal && (
        <div className="modal-backdrop">
          <div className="modal-card">
            <h3>Initiate Employee Exit / Offboarding</h3>
            <form onSubmit={handleInitiate}>
              <div className="form-group">
                <label>Employee ID:</label>
                <input
                  type="text"
                  placeholder="e.g. EMP-000042"
                  value={empId}
                  onChange={(e) => setEmpId(e.target.value)}
                  required
                />
              </div>

              <div className="form-group">
                <label>Termination Category:</label>
                <select value={termType} onChange={(e) => setTermType(e.target.value)}>
                  <option value="VOLUNTARY">Voluntary Resignation</option>
                  <option value="INVOLUNTARY">Involuntary Termination</option>
                  <option value="CONTRACT_END">Contract End</option>
                  <option value="URGENT_TERMINATION">Urgent Security Termination</option>
                </select>
                {termType === 'URGENT_TERMINATION' && (
                  <p className="alert alert-danger mt-2">
                    Urgent Termination triggers immediate automated SCIM revocation of all active Okta, AWS, and Git tokens.
                  </p>
                )}
              </div>

              <div className="form-group">
                <label>Rationale / Notes:</label>
                <textarea
                  rows="2"
                  value={reason}
                  onChange={(e) => setReason(e.target.value)}
                  placeholder="Operational context for HR and IT leads..."
                ></textarea>
              </div>

              <div className="modal-actions">
                <button type="button" className="btn btn-secondary" onClick={() => setShowInitiateModal(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-danger">
                  Confirm Exit Initiation
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
