# ADR-007: Package Structure and Entry Point Compatibility

## Context
The starter codebase was a single file in the default package. While modularization is essential, the university lab and README instructions require that standard compilation and execution commands (`javac` and `mvn exec:java -Dexec.mainClass=PharmacyTracker`) continue to work seamlessly.

## Options Considered
1. **Keep Everything in the Default Package**: Eliminates compilation structure advantages and produces a flat namespace.
2. **Move Everything into Subpackages**: Moving `PharmacyTracker` into `com.amalitech.pharmacy` would break `mvn exec:java -Dexec.mainClass=PharmacyTracker` unless the POM or execution arguments were altered.
3. **Domain Subpackages with Thin Default-Package Entry Point (Chosen)**: Place domain models in `pharmacy.model`, coordination services in `pharmacy.service`, and reporting in `pharmacy.report`, while keeping `PharmacyTracker` in the default package.

## Decision
I chose Option 3:
- `pharmacy.model`: `Medication`, `OtcMedication`, `PrescriptionMedication`, `Customer`, `WalkInCustomer`, `RegisteredCustomer`, `StockLevel`, `Money`, `Transaction`.
- `pharmacy.service`: `DispenseResult`, `DispenseSuccess`, `DispenseRejected`.
- `pharmacy.report`: `DispenseReporter`.
- Default package: `Pharmacy` (thin facade) and `PharmacyTracker` (main runner).

This guarantees that `PharmacyTracker.java` runs exactly as specified by the README and lab evaluation scripts.

## Consequences & Trade-offs
- **Positive**: Clean modular separation with zero breaking changes to external execution commands.
- **Accepted Trade-off**: Classes in subpackages must be marked `public` where consumed by `PharmacyTracker` across package boundaries.
