# Behaviour Preservation Rules

- The original output is the contract.
- The ONLY intended behavior change is:
  1. Stock can never become invalid: reject over-dispensing.
  2. Reject quantity <= 0 on dispense and restock.
- On rejection: print an informative console message in the original tone, do NOT decrement stock, do NOT add to customer history, and do NOT record a transaction.
- Every deviation must be documented in `docs/design-notes.md` or `docs/behaviour-evidence.md`.

