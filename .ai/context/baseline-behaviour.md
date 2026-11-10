# Baseline Behavior Analysis

## 1. Concrete Behavior of the Original Code

### A. OTC Dispense
- **Without 5% discount (WALK_IN)**:
  - `total = m.price * qty`
  - Stock is unconditionally decremented: `m.stock = m.stock - qty`
  - Printed output: `Dispensed {qty} x {m.name} to {c.name} for GHS {total}` (e.g., `Dispensed 2 x Paracetamol 500mg to Kofi Mensah for GHS 25.0`)
  - Customer history: appends `"Got " + qty + " x " + m.name` (e.g., `"Got 2 x Paracetamol 500mg"`)
  - Transactions: adds `new Transaction(c.name, m.name, qty, total, "DISPENSE")`
- **With 5% discount (REGISTERED)**:
  - `total = (m.price * qty) * 0.95`
  - If a registered customer buys 2 x Paracetamol (price 12.50), total is `25.0 * 0.95 = 23.75`.
  - Same printing and recording pattern.

### B. PRESCRIPTION Dispense
- **With a prescriber**:
  - `total = m.price * qty` (with 5% discount if `c.kind.equals("REGISTERED")`)
  - Stock is decremented: `m.stock = m.stock - qty`
  - Printed output: `Dispensed {qty} x {m.name} to {c.name} (prescribed by {prescriber}) for GHS {total}`
    (e.g., `Dispensed 1 x Amoxicillin 250mg to Ama Owusu (prescribed by Dr. Boateng) for GHS 28.5`)
  - Customer history: appends `"Got " + qty + " x " + m.name` (prescriber is NOT recorded in history).
  - Transactions: adds `new Transaction(c.name, m.name, qty, total, "DISPENSE")` (prescriber is NOT recorded in `Transaction`).
- **With null prescriber**:
  - Prints: `Cannot dispense {m.name} without a prescriber`
  - Returns immediately without decrementing stock.
  - Nothing added to customer history, nothing added to transactions list.
  - *Note*: An empty string `""` or whitespace prescriber is accepted because the check is only `prescriber == null`.

### C. Insufficient Stock (Over-dispensing)
- When dispensing 100 units of Amoxicillin with only 40 in stock (after Ama took 1, only 39 remained):
  - In the original code, there is NO stock check.
  - Calculation: `39 - 100 = -61`.
  - Prints: `Dispensed 100 x Amoxicillin 250mg to Kofi Mensah (prescribed by Dr. Boateng) for GHS 3000.0`
  - Customer history: `"Got 100 x Amoxicillin 250mg"` is appended.
  - Transaction: logged as `DISPENSE` of 100 units for GHS 3000.0.
  - End of day report displays: `Amoxicillin 250mg (PRESCRIPTION): -61 in stock`.

### D. Unknown Category, Zero or Negative Quantities
- **Unknown category** (e.g. `"CONTROLLED"`, `"VACCINE"`):
  - The `if-else if` chain does not match either branch.
  - Nothing is printed for the dispense.
  - Stock is NOT decremented.
  - HOWEVER, the code falls through to lines 133-134:
    - Customer history still logs: `c.history.add("Got " + qty + " x " + m.name)`!
    - Transaction is logged: `transactions.add(new Transaction(c.name, m.name, qty, 0.0, "DISPENSE"))` with total = `0.0`!
- **Quantity <= 0**:
  - Dispense with `qty = -5`: decrements stock by `-5` (increases stock by 5!), calculates negative total, prints dispense message, logs transaction.
  - Restock with `qty = -10`: decrements stock by 10, logs `RESTOCK` transaction with qty -10.

### E. Number Formatting and Report Layout
- Numbers are formatted using standard Java `double` toString conversion (e.g., `25.0`, `28.5`, `3000.0`).
- Report layout:
  ```
  === {name} - end of day ===
  Stock levels:
    {m.name} ({m.category}): {m.stock} in stock
  Transactions today: {transactions.size()}
  ```

---

## 2. Bug vs Preserve Classification

| Behavior | Classification | Rationale |
|---|---|---|
| Over-dispensing allowed (stock goes negative) | **FIX** | Critical business and medical danger. Stock must never fall below zero. Rejection must not mutate history or transactions. |
| Dispense / restock with `qty <= 0` | **FIX** | Negative or zero quantities corrupt inventory counts and financial reporting. |
| Fall-through on unknown medication category | **FIX** | Legacy code logs a transaction with 0.0 total and appends customer history despite not dispensing anything. |
| Prescriber check allows empty/blank string `""` | **FIX** | Blank string bypasses doctor requirement; only genuine prescriber names should be valid. |
| Direct public field mutation (`m.stock`, `c.history`, `ama.phone`) | **FIX** | Encapsulation leak; stock and history can be altered from anywhere without validation. |
| Leaked internal `List<Transaction>` in `getTransactions()` | **FIX** | Callers can clear or tamper with pharmacy transaction audit logs. |
| Static `counter` for IDs across classes | **FIX** | Static mutable state; breaks thread safety and isolation. |
| Standard OTC dispense message format & calculation | **PRESERVE** | Exact text `"Dispensed {qty} x {name} to {customer} for GHS {total}"` matches contract. |
| Prescription dispense message format | **PRESERVE** | Exact text `"Dispensed {qty} x {name} to {customer} (prescribed by {prescriber}) for GHS {total}"`. |
| Null prescriber rejection message | **PRESERVE** | Exact text `"Cannot dispense {name} without a prescriber"`. |
| Restock message format | **PRESERVE** | Exact text `"Restocked {qty} x {name}"`. |
| 5% discount for REGISTERED customers | **PRESERVE** | 5% off list price. |
| End-of-day report header and layout | **PRESERVE** | Exact formatting and structure of the summary report. |
| `double` number printing style (`25.0`, `28.5`, `3000.0`) | **PRESERVE** | Prevents output mismatches against baseline expectations. |
