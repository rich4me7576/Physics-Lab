# NaijaPhysLab 3D — Firestore Security Specification (Phase 0 TDD)

## 1. Data Invariants
1. **Strict Ownership Isolation**: Every document in `/lab_attempts/{attemptId}` and `/class_assignments/{assignmentId}` belongs exclusively to `userId == request.auth.uid`. Cross-user reads, lists, updates, and deletes are strictly forbidden.
2. **Schema & Boundary Integrity**: All string fields have bounded `.size()` limits; numeric scores (`0 <= score <= 100`, `1 <= maxScore <= 100`) and tolerances (`1 <= targetAccuracyPercent <= 25`) are strictly validated via `isValidLabAttempt()` and `isValidClassAssignment()`.
3. **Immutable Audit Fields**: `userId` and `createdAt` can never be mutated during updates (`incoming().userId == existing().userId && incoming().createdAt == existing().createdAt`).
4. **Terminal State Locking**: Once a `ClassAssignment` reaches `status == 'closed'`, no subsequent updates are permitted.
5. **Query Enforcer**: `allow list` rules explicitly check `existing().userId == request.auth.uid`, forcing client queries to include `.whereEqualTo("userId", uid)`.

## 2. The "Dirty Dozen" Payloads
1. **Unauthenticated Read**: `GET /lab_attempts/att_1` with `auth == null` -> REJECT.
2. **Cross-User Read (PII/Score Leak)**: `GET /lab_attempts/bob_att` as `alice_123` -> REJECT.
3. **Unfiltered Collection List Scraping**: `LIST /lab_attempts` without `where("userId", "==", uid)` -> REJECT.
4. **Identity Spoofing on Create**: `CREATE /lab_attempts/att_1` by `alice_123` with `userId: "bob_456"` -> REJECT.
5. **Shadow Update (Ghost Field Injection)**: `UPDATE /lab_attempts/att_1` adding `isVerifiedByWaec: true` -> REJECT via `affectedKeys().hasOnly(...)`.
6. **Value Poisoning on Update**: `UPDATE /lab_attempts/att_1` setting `score: 9999` or `score: "A1"` -> REJECT via `isValidLabAttempt(incoming())`.
7. **Immutable Field Tampering**: `UPDATE /lab_attempts/att_1` mutating `createdAt` or `userId` -> REJECT.
8. **Future Timestamp Spoofing**: `CREATE /lab_attempts/att_1` with `createdAt > request.time` -> REJECT.
9. **Resource Exhaustion (1MB String)**: `CREATE /lab_attempts/att_1` with 10,000-char `aiFeedback` -> REJECT via `.size() <= 2000`.
10. **Invalid Enum Injection**: `CREATE /lab_attempts/att_1` with `examBoard: "INVALID_BOARD"` -> REJECT.
11. **Terminal State Bypass**: `UPDATE /class_assignments/ass_1` when `existing().status == 'closed'` -> REJECT.
12. **Cross-User Delete**: `DELETE /class_assignments/bob_ass` as `alice_123` -> REJECT.
