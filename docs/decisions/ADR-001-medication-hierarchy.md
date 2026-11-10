# ADR-001: Medication Hierarchy and Category Modeling

## Context
In the original single-file program, medication categories were represented as arbitrary string literals (`"OTC"` or `"PRESCRIPTION"`). The `Pharmacy.dispense()` method contained branching `if-else if` blocks that mixed pricing calculations, prescriber validation, stock deductions, and string formatting. Adding a new category like `CONTROLLED` forced modification of `Pharmacy.dispense()` and risked breaking existing logic.

## Options Considered
1. **Enum Category with Behavior**: An enum defining `OTC` and `PRESCRIPTION` with strategy methods. While type-safe, enums are closed at compile time; adding a new category requires editing the enum file itself.
2. **Sealed Abstract Class**: A sealed `Medication` permitting `OtcMedication` and `PrescriptionMedication`. While compile-time closed, adding `ControlledMedication` would require opening the `permits` list in `Medication.java`.
3. **Ordinary Abstract Class (Chosen)**: A standard non-sealed abstract class `Medication` extended by `OtcMedication` and `PrescriptionMedication`.

## Decision
I chose Option 3: an ordinary abstract class `Medication` extended by concrete classes `OtcMedication` and `PrescriptionMedication`. 

Because Akosua's business told me that `CONTROLLED` substances are actively coming, making `Medication` an ordinary abstract class allows new medication categories (like `ControlledMedication` or `VaccineMedication`) to be introduced in a brand new file with **zero edits** to existing production classes.

Meanwhile, I deliberately kept `sealed` on types that are truly closed in the business domain:
- `DispenseResult` is sealed (`permits DispenseSuccess, DispenseRejected`) because a dispensing attempt in this domain has exactly two logical outcomes: success or rejection.
- `Customer` is sealed (`permits WalkInCustomer, RegisteredCustomer`) because the pharmacy currently recognizes exactly two loyalty pricing tiers.

Console output formatting is kept strictly OUT of the model classes; a dedicated `DispenseReporter` formats messages.

## Consequences & Trade-offs
- **Positive**: Complete compliance with the Open-Closed Principle. Any new medication category plugs in seamlessly without touching existing code.
- **Accepted Trade-off**: The compiler does not prevent arbitrary subclasses from being written outside the package, but domain rules (such as prescriber validation and stock invariants) are enforced centrally by `Pharmacy`.
