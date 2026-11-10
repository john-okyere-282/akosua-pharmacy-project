# ADR-002: Encapsulation of the Stock Invariant

## Context
The most dangerous defect in the legacy codebase is an unvalidated, public `stock` field that allowed Amoxicillin inventory to fall to `-61` while issuing a sale. A pharmacy cannot dispense nonexistent physical medications.

## Options Considered
1. **Guarded Primitive Inside Medication**: Keep `stock` as a private `int` within `Medication` with guarded methods like `deductStock(int qty)`. While simple, the primitive `int` can still technically hold negative values if an internal method contains an arithmetic defect.
2. **Dedicated Immutable `StockLevel` Value Object (Chosen)**: An immutable value type `StockLevel` whose constructor strictly enforces `units >= 0`. Operations such as `deduct(int qty)` and `add(int qty)` return a new `StockLevel` instance or throw an `IllegalArgumentException` if the result would be negative.

## Decision
I chose Option 2: introducing a dedicated `StockLevel` value type. 

`StockLevel` makes negative stock unrepresentable in memory. `Medication` holds an internal `StockLevel` privately, completely eliminating public mutable fields and public setters. Deductions are only applied after checking `hasSufficient(qty)`.

## Consequences & Trade-offs
- **Positive**: Impossible to construct or transition into an invalid or negative inventory state anywhere in the application.
- **Accepted Trade-off**: Creates small immutable objects on restock and dispense operations, but in modern JVMs (JDK 21), object allocation overhead for short-lived value types is negligible compared to the guarantee of business safety.
