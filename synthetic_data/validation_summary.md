# Validation Summary

The synthetic data generation pipeline executed the following checks:
1. **Primary Key Uniqueness**: Verified 0 duplicates across all 11 entities.
2. **Foreign Key Integrity**: Verified 100% of foreign references point to existing master rows.
3. **Enum Conformance**: Checked valid status, level, category, and action types.
4. **Chronological Consistency**: Offer accept date <= start date <= end date; provisioning dates correlate with employment lifecycle.
5. **Reconciliation Checks**:
   - Total Active Employees vs Active Onboarding Cases
   - Total Assets In Stock + Assigned + Recovered = Total Fleet
   - Total Active Grants vs Terminated Grants
6. **Edge Case Isolation**: Confirmed that all 20 anomalies are explicitly cataloged in `ground_truth.json` / `ground_truth.csv` and do not corrupt unrelated normal rows.

