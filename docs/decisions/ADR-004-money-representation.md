# ADR-004: Money Representation and Output Fidelity

## Context
The legacy application used IEEE 754 binary floating-point `double` for prices and totals (`12.50`, `30.00`, `25.0`, `28.5`, `3000.0`). While binary floating-point accumulates rounding errors over long periods, the lab specification mandates strict output preservation against `baseline/original-output.txt`.

## Options Considered
1. **Raw `double`**: Continue using primitive `double` everywhere. While simple, it perpetuates primitive obsession and lacks domain semantics.
2. **`BigDecimal`**: The standard financial choice for commercial banking. However, `BigDecimal.toString()` formats numbers differently (e.g. `25.00` or `28.50`), which directly breaks the baseline output contract (`25.0`, `28.5`).
3. **Custom Immutable `Money` Value Type (Chosen)**: A lightweight domain value object wrapping a `double`, whose `toString()` delegates exactly to `Double.toString(amount)`.

## Decision
I chose Option 3: a custom immutable `Money` value type.

Arithmetic is kept in the exact sequence as the original: `(price * qty)` calculated first, followed by `* 0.95` for registered customers. `Money.toString()` prints byte-identically to Java's original `double` formatting.

I deliberately did NOT switch to `BigDecimal` in this iteration because preserving the legacy output contract is a hard requirement. I document `BigDecimal` with integer minor units (pesewas) as a future production improvement.

## Consequences & Trade-offs
- **Positive**: Prevents primitive obsession, provides clear domain vocabulary (`money.multiply(qty)`, `money.applyDiscount(multiplier)`), and ensures 100% byte compatibility with baseline output.
- **Accepted Trade-off**: Retains underlying floating-point precision characteristics of the original program.
