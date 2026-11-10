# ADR-007: Package Layout and Namespace Separation

## Context
The starter codebase was a single file in the default package. While modularization is essential, the university lab and README instructions require that standard compilation and execution commands (`javac` and `mvn exec:java -Dexec.mainClass=PharmacyTracker`) continue to work seamlessly.

## Options Considered
1. **Flat Default Package**: Leaving all classes in the root default package. This creates namespace pollution and violates modular boundaries.
2. **Move Everything into Subpackages**: Moving `PharmacyTracker` into `com.amalitech.pharmacy` would break `mvn exec:java -Dexec.mainClass=PharmacyTracker` unless the POM or execution arguments were altered.
3. **Domain Packages with Thin Root Entry Point (Chosen)**: Place domain models in `pharmacy.model`, the aggregate coordinator in `pharmacy.service`, and reporting in `pharmacy.report`, leaving only `PharmacyTracker` in the default package.

## Decision
I chose Option 3:
- `pharmacy.model`: Domain entities and value types (`Medication`, `OtcMedication`, `PrescriptionMedication`, `Customer`, `WalkInCustomer`, `RegisteredCustomer`, `StockLevel`, `Money`, `Transaction`).
- `pharmacy.service`: The aggregate coordinator `Pharmacy` and result types (`DispenseResult`, `DispenseSuccess`, `DispenseRejected`).
- `pharmacy.report`: The console output handler `DispenseReporter`.
- Default package: Only `PharmacyTracker.java` (main runner).

This keeps all business entities and service logic in clear domain packages while ensuring that `PharmacyTracker.java` runs exactly as specified by the README and lab evaluation scripts.

## Consequences & Trade-offs
- **Positive**: Clean modular separation with zero breaking changes to external execution commands.
- **Accepted Trade-off**: Public visibility is required on classes consumed across package boundaries by `PharmacyTracker`.
