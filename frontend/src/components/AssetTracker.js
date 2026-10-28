import React, { useState, useEffect } from 'react';
import { api } from '../services/api';

const safeStr = (val) => (val != null ? String(val) : '');
const safeLower = (val) => safeStr(val).toLowerCase();

export default function AssetTracker() {
  const [assets, setAssets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState('');
  const [assignModalAsset, setAssignModalAsset] = useState(null);
  const [assignTargetEmp, setAssignTargetEmp] = useState('');
  const [feedback, setFeedback] = useState(null);
  const [errorMsg, setErrorMsg] = useState(null);

  useEffect(() => {
    loadAssets();
  }, [search]);

  const loadAssets = async () => {
    try {
      setLoading(true);
      const res = await api.getAssets(0, 50, search);
      setAssets(res.data.content || []);
    } catch (err) {
      console.error('Failed to load assets', err);
    } finally {
      setLoading(false);
    }
  };

  const handleAssign = async () => {
    if (!assignTargetEmp.trim()) return;
    setErrorMsg(null);
    setFeedback(null);
    try {
      await api.assignAsset(assignModalAsset.id, {
        employeeId: assignTargetEmp.trim(),
        actorId: 'EMP-000012',
        notes: 'Assigned via Asset Fleet Manager',
      });
      setFeedback(`Asset ${assignModalAsset.id} assigned to ${assignTargetEmp}!`);
      setAssignModalAsset(null);
      setAssignTargetEmp('');
      loadAssets();
    } catch (err) {
      setErrorMsg(err.response?.data?.message || 'Assignment failed');
    }
  };

  const handleAcknowledge = async (asset) => {
    setErrorMsg(null);
    setFeedback(null);
    try {
      const assignedEmp = asset.assigned_to_employee_id || asset.assignedToEmployeeId;
      await api.acknowledgeAsset(asset.id, assignedEmp);
      setFeedback(`Asset ${asset.id} receipt acknowledged by ${assignedEmp}`);
      loadAssets();
    } catch (err) {
      setErrorMsg(err.response?.data?.message || 'Acknowledgment failed');
    }
  };

  const handleRecover = async (asset) => {
    setErrorMsg(null);
    setFeedback(null);
    try {
      await api.recoverAsset(asset.id, {
        inspectorId: 'facilities.tech@company.internal',
        conditionStatus: 'GOOD',
      });
      setFeedback(`Asset ${asset.id} recovered and returned to stock.`);
      loadAssets();
    } catch (err) {
      setErrorMsg(err.response?.data?.message || 'Recovery failed');
    }
  };

  return (
    <div className="asset-tracker-view">
      <div className="view-header">
        <div>
          <h2>Facilities &amp; Hardware Asset Fleet Manager</h2>
          <p className="subtitle">
            Physical device inventory lifecycle: Procurement, Provisioning, Courier Delivery, Receipt Acknowledgment, and Recovery.
          </p>
        </div>
        <div className="search-box">
          <input
            type="text"
            placeholder="Search asset ID, serial, or employee..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </div>
      </div>

      {feedback && <div className="alert alert-success">{feedback}</div>}
      {errorMsg && <div className="alert alert-danger">{errorMsg}</div>}

      <div className="card table-card">
        <div className="table-responsive">
          <table className="data-table">
            <thead>
              <tr>
                <th>Asset ID</th>
                <th>Barcode Tag</th>
                <th>Type</th>
                <th>Model Specifications</th>
                <th>Serial Number</th>
                <th>Status</th>
                <th>Assigned To</th>
                <th>Condition</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {loading ? (
                <tr>
                  <td colSpan="9" className="text-center py-4">
                    <div className="spinner-small"></div>
                  </td>
                </tr>
              ) : assets.length === 0 ? (
                <tr>
                  <td colSpan="9" className="text-center py-4">
                    No hardware assets found.
                  </td>
                </tr>
              ) : (
                assets.map((a) => {
                  const tag = a.asset_tag || a.assetTag;
                  const type = a.asset_type || a.assetType;
                  const serial = a.serial_number || a.serialNumber;
                  const assignedTo = a.assigned_to_employee_id || a.assignedToEmployeeId;
                  const condition = a.condition_status || a.conditionStatus;
                  return (
                    <tr key={a.id}>
                      <td>
                        <span className="mono-code">{a.id}</span>
                      </td>
                      <td>{tag}</td>
                      <td>
                        <span className="role-tag">{type}</span>
                      </td>
                      <td>
                        <strong>{a.model}</strong>
                      </td>
                      <td>
                        <code className="serial-code">{serial}</code>
                      </td>
                      <td>
                        <span className={`status-pill ${safeLower(a.status)}`}>{a.status || '—'}</span>
                      </td>
                      <td>{assignedTo || <span className="text-muted">Unassigned</span>}</td>
                      <td>
                        <span className="condition-tag">{condition}</span>
                      </td>
                      <td>
                        <div className="table-actions">
                          {a.status === 'IN_STOCK' && (
                            <button
                              className="btn btn-xs btn-primary"
                              onClick={() => {
                                setAssignModalAsset(a);
                                setAssignTargetEmp('');
                              }}
                            >
                              Assign
                            </button>
                          )}
                          {a.status === 'DELIVERED' && (
                            <button className="btn btn-xs btn-success" onClick={() => handleAcknowledge(a)}>
                              Acknowledge
                            </button>
                          )}
                          {(a.status === 'ASSIGNED' || a.status === 'ACKNOWLEDGED' || a.status === 'RETURN_REQUESTED') && (
                            <button className="btn btn-xs btn-secondary" onClick={() => handleRecover(a)}>
                              Recover
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

      {/* Assign Modal */}
      {assignModalAsset && (
        <div className="modal-backdrop">
          <div className="modal-card">
            <h3>Assign Hardware: {assignModalAsset.id}</h3>
            <p className="modal-sub">
              {assignModalAsset.model} (Tag: {assignModalAsset.asset_tag || assignModalAsset.assetTag})
            </p>
            <div className="form-group">
              <label>Target Employee ID (e.g., EMP-000042):</label>
              <input
                type="text"
                value={assignTargetEmp}
                onChange={(e) => setAssignTargetEmp(e.target.value)}
                placeholder="Enter valid employee ID..."
              />
            </div>
            <div className="modal-actions">
              <button className="btn btn-secondary" onClick={() => setAssignModalAsset(null)}>
                Cancel
              </button>
              <button className="btn btn-primary" onClick={handleAssign}>
                Confirm Assignment
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
