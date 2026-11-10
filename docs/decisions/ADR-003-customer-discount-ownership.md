# ADR-003: Customer Hierarchy and Loyalty Discount Ownership

## Context
In the original code, customer types were tracked as raw strings (`"WALK_IN"` vs `"REGISTERED"`), and a 5% discount was hardcoded inside `Pharmacy.dispense()` using `if (c.kind.equals("REGISTERED")) total = total * 0.95;`. This violated Tell-Don't-Ask and duplicated logic across medication branches.

## Options Considered
1. **Customer Enum with Multiplier**: An enum property inside a single `Customer` class. This handles multipliers but fails to model structural differences (e.g., registered patients requiring phone numbers).
2. **Sealed Class Hierarchy (Chosen)**: A sealed abstract `Customer` class extended by `WalkInCustomer` and `RegisteredCustomer`.

## Decision
I chose Option 2: a sealed hierarchy where `WalkInCustomer` has a discount multiplier of `1.0` and `RegisteredCustomer` has a discount multiplier of `0.95`.

`RegisteredCustomer` strictly requires a contact phone number in its constructor, enforcing domain validity at creation time. Furthermore, `Customer.history` is exposed as an unmodifiable list via defensive view (`Collections.unmodifiableList`), and can only be appended through a controlled `recordHistory()` method.

## Consequences & Trade-offs
- **Positive**: Pricing rules belong to the customer domain model. Dispensing logic no longer needs to inspect customer kinds. Patient contact rules are enforced at construction.
- **Accepted Trade-off**: Adding new customer categories (e.g. `SeniorCustomer`) requires extending the hierarchy, which is clean, explicit, and type-safe.
