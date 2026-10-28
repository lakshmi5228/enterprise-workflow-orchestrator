import React, { useState, useEffect } from 'react';
import { api } from '../services/api';

const safeStr = (val) => (val != null ? String(val) : '');
const safeLower = (val) => safeStr(val).toLowerCase();

export default function TaskKanban() {
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activeRoleFilter, setActiveRoleFilter] = useState('ALL');
  const [activeStatusFilter, setActiveStatusFilter] = useState('ALL');
  const [actionModalTask, setActionModalTask] = useState(null);
  const [overrideNotes, setOverrideNotes] = useState('');
  const [actionError, setActionError] = useState(null);
  const [actionSuccess, setActionSuccess] = useState(null);

  useEffect(() => {
    loadTasks();
  }, [activeRoleFilter, activeStatusFilter]);

  const loadTasks = async () => {
    try {
      setLoading(true);
      const res = await api.getTasks(0, 50, activeStatusFilter === 'ALL' ? '' : activeStatusFilter, '');
      let list = res.data.content || [];
      if (activeRoleFilter !== 'ALL') {
        list = list.filter((t) => (t.assigned_role || t.assignedRole) === activeRoleFilter);
      }
      setTasks(list);
    } catch (err) {
      console.error('Failed to load tasks', err);
    } finally {
      setLoading(false);
    }
  };

  const handleExecuteAction = async (task, actionType, notes = '') => {
    setActionError(null);
    setActionSuccess(null);
    try {
      await api.executeTaskAction(task.id, {
        actorId: 'EMP-000008',
        actorRole: 'IT_OPERATIONS',
        action: actionType,
        notes: notes || 'Executed from Task Execution Board',
      });
      setActionSuccess(`Task ${task.id} successfully marked as ${actionType}!`);
      setActionModalTask(null);
      setOverrideNotes('');
      loadTasks();
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to complete task action.';
      setActionError(msg);
    }
  };

  const roles = ['ALL', 'HR_PARTNER', 'IT_OPERATIONS', 'FACILITIES', 'MANAGER'];
  const statuses = ['ALL', 'PENDING', 'READY', 'IN_PROGRESS', 'WAITING_APPROVAL', 'COMPLETED'];

  return (
    <div className="task-board-view">
      <div className="view-header">
        <div>
          <h2>Cross-Department Task Execution Board</h2>
          <p className="subtitle">
            Enforces strict dependency DAG completion. Tasks cannot be completed out of order without authorized override.
          </p>
        </div>
      </div>

      {actionError && <div className="alert alert-danger">{actionError}</div>}
      {actionSuccess && <div className="alert alert-success">{actionSuccess}</div>}

      <div className="filter-bar card">
        <div className="filter-group">
          <label>Department Role Queue:</label>
          <div className="button-group">
            {roles.map((r) => (
              <button
                key={r}
                className={`filter-btn ${activeRoleFilter === r ? 'active' : ''}`}
                onClick={() => setActiveRoleFilter(r)}
              >
                {safeStr(r).replace('_', ' ')}
              </button>
            ))}
          </div>
        </div>

        <div className="filter-group">
          <label>Lifecycle Status:</label>
          <select value={activeStatusFilter} onChange={(e) => setActiveStatusFilter(e.target.value)}>
            {statuses.map((s) => (
              <option key={s} value={s}>
                {s}
              </option>
            ))}
          </select>
        </div>
      </div>

      <div className="tasks-grid">
        {loading ? (
          <div className="loading-state">
            <div className="spinner"></div>
            <p>Loading workflow tasks...</p>
          </div>
        ) : tasks.length === 0 ? (
          <div className="empty-selection">No tasks found matching the active criteria.</div>
        ) : (
          tasks.map((t) => {
            const priority = t.priority || 'MEDIUM';
            const name = t.task_name || t.taskName;
            const role = t.assigned_role || t.assignedRole;
            const caseId = t.case_id || t.caseId;
            const due = t.due_date || t.dueDate;
            return (
              <div key={t.id} className={`task-card card priority-${safeLower(priority)}`}>
                <div className="task-card-header">
                  <span className="task-id">{t.id}</span>
                  <span className={`status-pill ${safeLower(t.status)}`}>{t.status || '—'}</span>
                </div>
                <h4 className="task-title">{name}</h4>
                <div className="task-meta-rows">
                  <div className="meta-row">
                    <span className="meta-label">Queue:</span>
                    <span className="role-tag">{role}</span>
                  </div>
                  <div className="meta-row">
                    <span className="meta-label">Case:</span>
                    <span>{caseId}</span>
                  </div>
                  <div className="meta-row">
                    <span className="meta-label">Due Date:</span>
                    <span>{Array.isArray(due) ? `${due[0]}-${String(due[1]).padStart(2,'0')}-${String(due[2]).padStart(2,'0')}` : safeStr(due)}</span>
                  </div>
                </div>

                {t.notes && <div className="task-notes-snippet">"{t.notes}"</div>}

                <div className="task-card-actions">
                  {t.status !== 'COMPLETED' && t.status !== 'OVERRIDDEN' ? (
                    <>
                      <button
                        className="btn btn-sm btn-primary"
                        onClick={() => handleExecuteAction(t, 'COMPLETE')}
                      >
                        Complete
                      </button>
                      <button
                        className="btn btn-sm btn-secondary"
                        onClick={() => setActionModalTask(t)}
                      >
                        Override
                      </button>
                    </>
                  ) : (
                    <span className="completed-badge">✓ Completed</span>
                  )}
                </div>
              </div>
            );
          })
        )}
      </div>

      {/* Override Modal */}
      {actionModalTask && (
        <div className="modal-backdrop">
          <div className="modal-card">
            <h3>Administrative Override: {actionModalTask.id}</h3>
            <p className="modal-sub">
              Task: <strong>{actionModalTask.task_name || actionModalTask.taskName}</strong>
            </p>
            <p className="alert alert-warning">
              Warning: Overriding bypasses prerequisite verification checks. An immutable record will be appended to the
              Audit Log.
            </p>
            <div className="form-group">
              <label>Justification / Reason:</label>
              <textarea
                rows="3"
                value={overrideNotes}
                onChange={(e) => setOverrideNotes(e.target.value)}
                placeholder="Enter regulatory or operational justification for bypass..."
              ></textarea>
            </div>
            <div className="modal-actions">
              <button className="btn btn-secondary" onClick={() => setActionModalTask(null)}>
                Cancel
              </button>
              <button
                className="btn btn-danger"
                onClick={() => handleExecuteAction(actionModalTask, 'OVERRIDE', overrideNotes)}
              >
                Confirm Override
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
