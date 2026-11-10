# Workflow: Incremental Implementation

1. Implement package layout and thin `PharmacyTracker` entry point.
2. Implement domain value types (`StockLevel`, `Money` if selected).
3. Implement `Medication` model hierarchy.
4. Implement `Customer` hierarchy and loyalty discount logic.
5. Implement immutable `Transaction` record with prescriber field.
6. Extract dispensing and restocking rules into dedicated services.
7. Refactor `Pharmacy` into an unmodifiable coordinator.
8. Enforce the non-negative stock invariant and rejected transaction handling.
9. Verify builds and outputs at each discrete step with single-purpose commits.

