import React, { useState, useEffect } from 'react';
import { api } from '../services/api';

const safeStr = (val) => (val != null ? String(val) : '');
const safeLower = (val) => safeStr(val).toLowerCase();

export default function OnboardingWorkflow({ onSelectCase }) {
  const [cases, setCases] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [selectedCase, setSelectedCase] = useState(null);
  const [caseTasks, setCaseTasks] = useState([]);
  const [advancing, setAdvancing] = useState(false);

  useEffect(() => {
    loadCases();
  }, [search]);

  const loadCases = async () => {
    try {
      setLoading(true);
      const res = await api.getOnboardingCases(0, 30, search);
      setCases(res.data.content || []);
      if (!selectedCase && res.data.content && res.data.content.length > 0) {
        selectCase(res.data.content[0]);
      }
    } catch (err) {
      console.error('Failed to load onboarding cases', err);
    } finally {
      setLoading(false);
    }
  };

  const selectCase = async (c) => {
    setSelectedCase(c);
    if (onSelectCase) onSelectCase(c);
    try {
      const res = await api.getTasksByCase(c.id);
      setCaseTasks(res.data || []);
    } catch (err) {
      console.error('Failed to load case tasks', err);
    }
  };

  const handleAdvance = async () => {
    if (!selectedCase) return;
    try {
      setAdvancing(true);
      const res = await api.advanceCaseStage(selectedCase.id);
      setSelectedCase(res.data);
      const taskRes = await api.getTasksByCase(selectedCase.id);
      setCaseTasks(taskRes.data || []);
      loadCases();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to advance case stage');
    } finally {
      setAdvancing(false);
    }
  };

  const stages = ['PREBOARDING', 'IT_PROVISIONING', 'ASSET_DELIVERY', 'ORIENTATION', 'COMPLETED'];

  return (
    <div className="onboarding-view">
      <div className="view-header">
        <div>
          <h2>Cross-Department Onboarding Orchestrator</h2>
          <p className="subtitle">
            Synchronized progression of Candidate Identity, Background Verification, Hardware Delivery, and Entitlements.
          </p>
        </div>
        <div className="search-box">
          <input
            type="text"
            placeholder="Search candidate name or status..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>
      </div>

      <div className="workflow-layout">
        {/* Cases List */}
        <div className="cases-sidebar card">
          <div className="sidebar-header">
            <h3>Active Pipeline ({cases.length})</h3>
          </div>
          <div className="cases-scroll">
            {loading ? (
              <div className="spinner-small"></div>
            ) : (
              cases.map((c) => {
                const isSelected = selectedCase?.id === c.id;
                const candidate = c.candidate_name || c.candidateName;
                const startDate = c.scheduled_start_date || c.scheduledStartDate;
                return (
                  <div
                    key={c.id}
                    className={`case-item ${isSelected ? 'active' : ''}`}
                    onClick={() => selectCase(c)}
                  >
                    <div className="case-item-top">
                      <strong className="candidate-name">{candidate}</strong>
                      <span className={`status-pill ${safeLower(c.status)}`}>{c.status || '—'}</span>
                    </div>
                    <div className="case-item-meta">
                      <span>{c.id}</span> • <span>Start: {Array.isArray(startDate) ? `${startDate[0]}-${String(startDate[1]).padStart(2,'0')}-${String(startDate[2]).padStart(2,'0')}` : safeStr(startDate)}</span>
                    </div>
                    <div className="case-stage-tag">{c.stage}</div>
                  </div>
                );
              })
            )}
          </div>
        </div>

        {/* Selected Case Inspection */}
        <div className="case-detail-panel card">
          {selectedCase ? (
            <div>
              <div className="detail-top-bar">
                <div>
                  <span className="case-id-badge">{selectedCase.id}</span>
                  <h3>{selectedCase.candidate_name || selectedCase.candidateName}</h3>
                  <p className="meta-text">
                    Assigned HR Partner: <strong>{selectedCase.hr_partner_id || selectedCase.hrPartnerId}</strong> • Manager:{' '}
                    <strong>{selectedCase.manager_id || selectedCase.managerId}</strong>
                  </p>
                </div>
                <div className="detail-actions">
                  <button
                    className="btn btn-primary"
                    onClick={handleAdvance}
                    disabled={advancing || selectedCase.stage === 'COMPLETED'}
                  >
                    {advancing ? 'Advancing...' : 'Evaluate & Advance Stage'}
                  </button>
                </div>
              </div>

              {/* Stage Progress Stepper */}
              <div className="stepper-container">
                {stages.map((st, idx) => {
                  const currentIdx = stages.indexOf(selectedCase.stage);
                  const isDone = currentIdx > idx || selectedCase.stage === 'COMPLETED';
                  const isCurrent = currentIdx === idx && selectedCase.stage !== 'COMPLETED';

                  return (
                    <div key={st} className={`step-node ${isDone ? 'done' : ''} ${isCurrent ? 'current' : ''}`}>
                      <div className="step-circle">{isDone ? '✓' : idx + 1}</div>
                      <span className="step-label">{safeStr(st).replace('_', ' ')}</span>
                      {idx < stages.length - 1 && <div className="step-line"></div>}
                    </div>
                  );
                })}
              </div>

              {/* Case Orchestration Tasks */}
              <div className="case-tasks-section">
                <h4>Orchestration Tasks ({caseTasks.length})</h4>
                <div className="table-responsive">
                  <table className="data-table">
                    <thead>
                      <tr>
                        <th>Task Name</th>
                        <th>Queue / Role</th>
                        <th>Status</th>
                        <th>Priority</th>
                        <th>Due Date</th>
                        <th>Exceptions</th>
                      </tr>
                    </thead>
                    <tbody>
                      {caseTasks.map((t) => {
                        const name = t.task_name || t.taskName;
                        const role = t.assigned_role || t.assignedRole;
                        const due = t.due_date || t.dueDate;
                        const isEx = t.is_exception !== undefined ? t.is_exception : t.isException;
                        return (
                          <tr key={t.id}>
                            <td>
                              <strong>{name}</strong>
                              <div className="table-sub">{t.id}</div>
                            </td>
                            <td>
                              <span className="role-tag">{role}</span>
                            </td>
                            <td>
                              <span className={`status-pill ${safeLower(t.status)}`}>{t.status || '—'}</span>
                            </td>
                            <td>
                              <span className={`priority-tag ${safeLower(t.priority)}`}>{t.priority || '—'}</span>
                            </td>
                            <td>{Array.isArray(due) ? `${due[0]}-${String(due[1]).padStart(2,'0')}-${String(due[2]).padStart(2,'0')}` : safeStr(due)}</td>
                            <td>
                              {isEx ? (
                                <span className="badge badge-danger">Exception Flagged</span>
                              ) : (
                                <span className="text-muted">None</span>
                              )}
                            </td>
                          </tr>
                        );
                      })}
                    </tbody>
                  </table>
                </div>
              </div>
            </div>
          ) : (
            <div className="empty-selection">
              <p>Select a candidate onboarding case from the left panel to inspect workflow details.</p>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
