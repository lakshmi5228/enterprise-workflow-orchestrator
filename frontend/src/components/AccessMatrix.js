import React, { useState, useEffect } from 'react';
import { api } from '../services/api';

const safeStr = (val) => (val != null ? String(val) : '');
const safeLower = (val) => safeStr(val).toLowerCase();

export default function AccessMatrix() {
  const [grants, setGrants] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [actionMsg, setActionMsg] = useState(null);
  const [actionErr, setActionErr] = useState(null);

  useEffect(() => {
    loadGrants();
  }, [search, statusFilter]);

  const loadGrants = async () => {
    try {
      setLoading(true);
      const res = await api.getAccessGrants(0, 50, search);
      let list = res.data.content || [];
      if (statusFilter !== 'ALL') {
        list = list.filter((g) => g.status === statusFilter);
      }
      setGrants(list);
    } catch (err) {
      console.error('Failed to load grants', err);
    } finally {
      setLoading(false);
    }
  };

  const handleApprove = async (id) => {
    setActionMsg(null);
    setActionErr(null);
    try {
      await api.approveAccessGrant(id, 'security.director@company.internal');
      setActionMsg(`Grant ${id} successfully APPROVED.`);
      loadGrants();
    } catch (err) {
      setActionErr(err.response?.data?.message || 'Approval failed');
    }
  };

  const handleProvision = async (id) => {
    setActionMsg(null);
    setActionErr(null);
    try {
      await api.provisionAccessGrant(id, 'it.admin@company.internal');
      setActionMsg(`Grant ${id} successfully PROVISIONED in target identity system.`);
      loadGrants();
    } catch (err) {
      setActionErr(err.response?.data?.message || 'Provisioning failed due to rule violation');
    }
  };

  const handleRevoke = async (id) => {
    setActionMsg(null);
    setActionErr(null);
    try {
      await api.revokeAccessGrant(id, 'it.admin@company.internal', 'Manual administrative revocation');
      setActionMsg(`Grant ${id} successfully REVOKED.`);
      loadGrants();
    } catch (err) {
      setActionErr(err.response?.data?.message || 'Revocation failed');
    }
  };

  return (
    <div className="access-matrix-view">
      <div className="view-header">
        <div>
          <h2>IT Identity &amp; Access Provisioning Matrix</h2>
          <p className="subtitle">
            Governs principle of least privilege, approval gating, and Day-1 automated provisioning across enterprise systems.
          </p>
        </div>
        <div className="search-box">
          <input
            type="text"
            placeholder="Search employee or grant ID..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>
      </div>

      {actionMsg && <div className="alert alert-success">{actionMsg}</div>}
      {actionErr && <div className="alert alert-danger">{actionErr}</div>}

      <div className="filter-bar card">
        <div className="filter-group">
          <label>Filter by Entitlement State:</label>
          <div className="button-group">
            {['ALL', 'REQUESTED', 'APPROVED', 'PROVISIONED', 'REVOKED'].map((st) => (
              <button
                key={st}
                className={`filter-btn ${statusFilter === st ? 'active' : ''}`}
                onClick={() => setStatusFilter(st)}
              >
                {st}
              </button>
            ))}
          </div>
        </div>
      </div>

      <div className="card table-card">
        <div className="table-responsive">
          <table className="data-table">
            <thead>
              <tr>
                <th>Grant ID</th>
                <th>Employee</th>
                <th>Target Access Role</th>
                <th>Status</th>
                <th>Approval</th>
                <th>Requested By</th>
                <th>Provisioned At</th>
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
              ) : grants.length === 0 ? (
                <tr>
                  <td colSpan="8" className="text-center py-4">
                    No access grants found matching criteria.
                  </td>
                </tr>
              ) : (
                grants.map((g) => {
                  const empId = g.employee_id || g.employeeId;
                  const roleId = g.access_role_id || g.accessRoleId;
                  const approval = g.approval_status || g.approvalStatus || 'APPROVED';
                  const reqBy = g.requested_by || g.requestedBy;
                  const provAt = g.provisioned_at || g.provisionedAt;
                  return (
                    <tr key={g.id}>
                      <td>
                        <span className="mono-code">{g.id}</span>
                      </td>
                      <td>
                        <strong>{empId}</strong>
                      </td>
                      <td>
                        <span className="role-tag">{roleId}</span>
                      </td>
                      <td>
                        <span className={`status-pill ${safeLower(g.status)}`}>{g.status || '—'}</span>
                      </td>
                      <td>
                        <span className={`approval-pill ${safeLower(approval)}`}>{approval}</span>
                      </td>
                      <td>{reqBy}</td>
                      <td>
                        {provAt ? (
                          Array.isArray(provAt)
                            ? `${provAt[0]}-${String(provAt[1]).padStart(2, '0')}-${String(provAt[2]).padStart(2, '0')} ${String(provAt[3] || 0).padStart(2, '0')}:${String(provAt[4] || 0).padStart(2, '0')}`
                            : safeStr(provAt).replace('T', ' ')
                        ) : (
                          <span className="text-muted">—</span>
                        )}
                      </td>
                      <td>
                        <div className="table-actions">
                          {approval === 'PENDING_APPROVAL' && (
                            <button className="btn btn-xs btn-primary" onClick={() => handleApprove(g.id)}>
                              Approve
                            </button>
                          )}
                          {g.status !== 'PROVISIONED' && g.status !== 'REVOKED' && (
                            <button className="btn btn-xs btn-success" onClick={() => handleProvision(g.id)}>
                              Provision
                            </button>
                          )}
                          {g.status === 'PROVISIONED' && (
                            <button className="btn btn-xs btn-danger" onClick={() => handleRevoke(g.id)}>
                              Revoke
                            </button>
                          )}
                        </div>
                      </td>
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
}
