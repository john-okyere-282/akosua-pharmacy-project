# ADR-005: Failure Handling and Domain Results

## Context
When a dispense or restock fails (e.g., missing prescriber, insufficient stock, negative quantity), the system must reject the operation cleanly without corrupting stock, customer history, or transaction records.

## Options Considered
1. **Exceptions (`InsufficientStockException`)**: Throwing checked or unchecked exceptions on business rejection. While idiomatic for fatal errors, handling normal business rejections via exceptions in a CLI batch workflow would disrupt the report flow or require cluttering `PharmacyTracker` with try-catch blocks.
2. **Sealed `DispenseResult` Hierarchy with Dedicated Reporter (Chosen)**: A sealed domain result type (`DispenseSuccess` vs `DispenseRejected`) paired with a `DispenseReporter` that prints console messages.

## Decision
I chose Option 2: `Pharmacy.dispense()` returns a sealed `DispenseResult`.

The domain evaluation strictly adheres to the original order of checks:
1. Prescriber check (if required by medication: `prescriber == null`)
2. Quantity check (`qty <= 0`)
3. Stock check (`qty > stock`)

If validation passes, stock is decremented in exactly one place, history is updated, a `Transaction` is logged, and a `DispenseSuccess` result is returned. If validation fails, a `DispenseRejected` result is returned with the exact rejection reason, leaving all state untouched.

`Pharmacy` delegates console output to `DispenseReporter`, preserving the original message style while keeping domain classes free of `System.out.println` statements.

## Consequences & Trade-offs
- **Positive**: Domain methods remain testable and decoupled from console I/O. Callers receive explicit result types without unexpected exceptions crashing the application.
- **Accepted Trade-off**: Requires a small result hierarchy (`DispenseResult`), which is idiomatic and clean in modern Java 21.
