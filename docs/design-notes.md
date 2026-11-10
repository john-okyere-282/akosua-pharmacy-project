# Engineering Design Notes: Akosua's Pharmacy

These design notes document my refactoring journey, architectural decisions, and verification findings for the Akosua's Pharmacy Stock & Dispensing Tracker.

## 1. Core Architecture Overview
I replaced the legacy god-class implementation with a decoupled, domain-driven structure:
- **`pharmacy.model`**: Holds encapsulated entities and immutable value types (`Medication`, `OtcMedication`, `PrescriptionMedication`, `Customer`, `WalkInCustomer`, `RegisteredCustomer`, `StockLevel`, `Money`, `Transaction`).
- **`pharmacy.service`**: Encapsulates operation results (`DispenseResult`, `DispenseSuccess`, `DispenseRejected`).
- **`pharmacy.report`**: Isolates console formatting and reporting (`DispenseReporter`).
- **Default Package**: `Pharmacy` (thin aggregate coordinator) and `PharmacyTracker` (standard CLI entry point).

## 2. Invariant Defense Mechanism
The most critical requirement of this refactor was making negative inventory unrepresentable:
1. `StockLevel` value object validates `units >= 0` upon construction and returns new instances on arithmetic.
2. `Medication` holds `StockLevel` privately with no public mutators.
3. `Pharmacy.dispense()` checks `hasSufficientStock(qty)` before applying any changes.
4. On failure, stock is not touched, no transaction is logged, and no history entry is added.

## 3. Preserved Legacy Oddities
- **Prescriber Null Check**: The legacy system only validated `prescriber == null`, allowing blank strings (`""`). I kept this intact to preserve exact behavioral fidelity.
- **Double Floating Point**: `Money` wraps a standard Java `double` to produce byte-identical string representations (`25.0`, `28.5`, `3000.0`).

## 4. Open-Closed Extensibility
By declaring `PrescriptionMedication` as `non-sealed`, upcoming categories such as `ControlledMedication` can be introduced by creating a single new class, with zero edits to existing production code. This is verified by `PharmacyTest.testControlledMedicationExtensionWithoutEditingExistingClasses()`.
