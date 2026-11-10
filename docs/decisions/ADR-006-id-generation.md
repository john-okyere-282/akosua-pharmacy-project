# ADR-006: Instance-Scoped ID Generation

## Context
The starter classes used static counters (`static int counter = 1;` in `Medication`, `Customer`, and `Transaction`) that auto-incremented inside constructors. Static mutable state causes unpredictable IDs across unit tests, prevents running isolated test instances, and is not thread-safe.

## Options Considered
1. **Retain Static Counters**: Leaves mutable global state in memory.
2. **External ID / UUID Strings**: Passing arbitrary UUID strings. This would break the integer ID representation (`1`, `2`, `3`) used in transactions and entity tracking.
3. **Pharmacy-Owned Instance Sequences (Chosen)**: `Pharmacy` maintains private instance counters (`nextMedicationId`, `nextCustomerId`, `nextTransactionId`) and assigns final IDs when entities are added or transactions created.

## Decision
I chose Option 3: `Pharmacy` manages sequence counters locally for transactions and entities.

For initial constructors where caller code may instantiate a `Medication` or `Customer` directly before registration, constructors accept an explicit `int id`, or `Pharmacy` assigns a stable sequence ID upon registration. `Transaction` records receive their sequential ID from `Pharmacy.createTransaction()`.

## Consequences & Trade-offs
- **Positive**: Eliminates all static mutable state. Every test creating a new `Pharmacy` instance starts with clean, isolated, deterministic ID sequences.
- **Accepted Trade-off**: Entities receive or verify their IDs via the managing aggregate (`Pharmacy`).
