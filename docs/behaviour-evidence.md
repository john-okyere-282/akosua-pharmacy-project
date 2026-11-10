# Behavior Preservation and Invariant Evidence

## Executive Summary
I compared the rebuilt architecture against the legacy single-file program extracted directly from the starter commit (`5a2c701`).

The rebuilt program strictly preserves 100% of the original console formatting contract, financial calculation sequences, and customer history text, while enforcing the single critical business rule mandated by the brief: **stock must never become invalid**.

---

## 1. Output Comparison

### Original Baseline Output
```text
Dispensed 2 x Paracetamol 500mg to Kofi Mensah for GHS 25.0
Dispensed 1 x Amoxicillin 250mg to Ama Owusu (prescribed by Dr. Boateng) for GHS 28.5
Restocked 50 x Paracetamol 500mg
Dispensed 100 x Amoxicillin 250mg to Kofi Mensah (prescribed by Dr. Boateng) for GHS 3000.0

=== Akosua's Pharmacy - end of day ===
Stock levels:
  Paracetamol 500mg (OTC): 148 in stock
  Amoxicillin 250mg (PRESCRIPTION): -61 in stock
Transactions today: 4
```

### Rebuilt Program Output
```text
Dispensed 2 x Paracetamol 500mg to Kofi Mensah for GHS 25.0
Dispensed 1 x Amoxicillin 250mg to Ama Owusu (prescribed by Dr. Boateng) for GHS 28.5
Restocked 50 x Paracetamol 500mg
Cannot dispense Amoxicillin 250mg: only 39 in stock, requested 100

=== Akosua's Pharmacy - end of day ===
Stock levels:
  Paracetamol 500mg (OTC): 148 in stock
  Amoxicillin 250mg (PRESCRIPTION): 39 in stock
Transactions today: 3
```

---

## 2. Line-by-Line Diff and Rationale

```diff
@@ -4,6 +4,6 @@
-Dispensed 100 x Amoxicillin 250mg to Kofi Mensah (prescribed by Dr. Boateng) for GHS 3000.0
+Cannot dispense Amoxicillin 250mg: only 39 in stock, requested 100
 
 === Akosua's Pharmacy - end of day ===
 Stock levels:
   Paracetamol 500mg (OTC): 148 in stock
-  Amoxicillin 250mg (PRESCRIPTION): -61 in stock
-Transactions today: 4
+  Amoxicillin 250mg (PRESCRIPTION): 39 in stock
+Transactions today: 3
```

### Explanation of Differing Lines

#### 1. Line 4: Dispense Rejection
- **Original**: `Dispensed 100 x Amoxicillin 250mg to Kofi Mensah (prescribed by Dr. Boateng) for GHS 3000.0`
- **Rebuilt**: `Cannot dispense Amoxicillin 250mg: only 39 in stock, requested 100`
- **Why it changed**: The fundamental business invariant dictates that a pharmacy cannot dispense nonexistent medicine. Amoxicillin started with 40 units; after Ama dispensed 1 unit, only 39 units remained. Kofi's request for 100 units exceeded available inventory. The rebuilt engine cleanly rejected the over-dispensing request and reported the rejection using the approved tone.

#### 2. Line 8: Amoxicillin Stock Count
- **Original**: `Amoxicillin 250mg (PRESCRIPTION): -61 in stock`
- **Rebuilt**: `Amoxicillin 250mg (PRESCRIPTION): 39 in stock`
- **Why it changed**: In the original code, the unvalidated subtraction `39 - 100 = -61` corrupted the inventory level into negative territory. In the rebuilt system, the rejected dispense operation left inventory completely intact at 39.

#### 3. Line 9: Total Transactions
- **Original**: `Transactions today: 4`
- **Rebuilt**: `Transactions today: 3`
- **Why it changed**: On rejection, no transaction record is created and no customer history is appended. The 3 recorded transactions represent the valid operations: Paracetamol dispense (2 units), Amoxicillin dispense (1 unit), and Paracetamol restock (50 units).

---

## 3. Extra Scenarios Tested Across Both Versions

To guarantee that no accidental regressions were introduced, I executed three additional test scenarios through both the original and rebuilt engines:

### Scenario A: Registered Loyalty Discount on OTC
- **Setup**: Registered customer purchasing 2 units of Paracetamol (`12.50 * 2 = 25.0 * 0.95 = 23.75`).
- **Original**: `Dispensed 2 x Paracetamol 500mg to Ama Owusu for GHS 23.75`
- **Rebuilt**: `Dispensed 2 x Paracetamol 500mg to Ama Owusu for GHS 23.75`
- **Result**: **Identical match**. Floating-point discount calculation preserved.

### Scenario B: Missing Prescriber on Prescription Medication
- **Setup**: Dispensing Amoxicillin with `prescriber = null`.
- **Original**: `Cannot dispense Amoxicillin 250mg without a prescriber`
- **Rebuilt**: `Cannot dispense Amoxicillin 250mg without a prescriber`
- **Result**: **Identical match**. Rejection message and early return preserved.

### Scenario C: Inventory Restock
- **Setup**: Restocking 50 units of Paracetamol.
- **Original**: `Restocked 50 x Paracetamol 500mg`
- **Rebuilt**: `Restocked 50 x Paracetamol 500mg`
- **Result**: **Identical match**. Supplier transaction recorded without price.
