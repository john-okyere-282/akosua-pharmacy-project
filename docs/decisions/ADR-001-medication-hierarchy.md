# ADR-001: Medication Hierarchy and Category Modeling

## Context
In the original single-file program, medication categories are represented as arbitrary string literals (`"OTC"` or `"PRESCRIPTION"`). The `Pharmacy.dispense()` method contains branching `if-else if` blocks that mix pricing calculations, prescriber validation, stock deductions, and string formatting. Adding a new category like `CONTROLLED` would force modification of `Pharmacy.dispense()` and risk breaking existing logic.

## Options Considered
1. **Enum Category with Behavior**: An enum defining `OTC`, `PRESCRIPTION`, and `CONTROLLED` with strategy methods. While type-safe, enums are closed at compile time; adding a new category requires modifying the enum file itself.
2. **Medication with Pluggable Strategy (DispensingPolicy)**: A single `Medication` class injecting a policy interface. This separates behavior cleanly but introduces unnecessary indirection for simple domain entities.
3. **Sealed Class Hierarchy (Chosen)**: A sealed abstract class `Medication` with explicit subclasses `OtcMedication` and `PrescriptionMedication`.

## Decision
I chose Option 3: a sealed abstract class `Medication` extended by `OtcMedication` and `PrescriptionMedication`. Each subclass encapsulates category-specific rules, such as `requiresPrescriber()`.

To keep models clean, I deliberately kept console string formatting OUT of the model classes. The model exposes domain queries and validation rules, while a dedicated reporter handles console output formatting.

`ControlledMedication` is deliberately NOT placed in `src/main`. It is implemented in `src/test` as a test-scope extension to prove that adding a new category requires zero edits to existing production code.

## Consequences & Trade-offs
- **Positive**: Adding a new category satisfies the Open-Closed Principle. Category validation is enforced at compile time.
- **Accepted Trade-off**: Adding a third category inside the main production code later would require opening the `permits` clause of `Medication` (or unsealing it), but for this lab, keeping the production hierarchy closed protects against arbitrary unsupported types while test-scope proves extensibility.
