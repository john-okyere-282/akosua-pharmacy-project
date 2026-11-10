# ADR-008: Prescriber Tracking on Transactions

## Context
In the original program, `Transaction` recorded `customerName`, `medName`, `qty`, `total`, and `type`, but completely omitted `prescriber`. Consequently, audits could never determine which physician authorized a prescription dispense. Furthermore, adding the upcoming `CONTROLLED` category would force breaking schema changes onto `Transaction`.

## Options Considered
1. **Separate Subclasses of Transaction (`PrescriptionTransaction`)**: Adds class proliferation for what is fundamentally an immutable audit log record.
2. **Optional / Nullable Prescriber on `Transaction` Record (Chosen)**: Define `Transaction` as a Java record containing `String prescriber`.

## Decision
I chose Option 2: `Transaction` is an immutable Java record with a `prescriber` field:
```java
public record Transaction(int id, String customerName, String medName, int qty, double total, String type, String prescriber)
```
For OTC dispenses and restocks, `prescriber` is stored as `null` (even if an external caller passed a prescriber to OTC dispense, preserving the legacy behavior which ignored it). For prescription dispenses, the authorized prescriber name is recorded.

When `CONTROLLED` substances are added, `Transaction` already possesses the required audit field, requiring zero schema or constructor changes.

## Consequences & Trade-offs
- **Positive**: Complete auditability, immutability, zero boilerplate (Java record), and full readiness for controlled substances.
- **Accepted Trade-off**: `prescriber` is `null` for non-prescription transactions, which is standard and clean for nullable audit columns.
