# Domain Model & Business Summary: Akosua's Pharmacy

## Business Background
Akosua's Pharmacy operates in Accra, Ghana. It dispenses medications to walk-in and registered patients, manages inventory, and keeps records of daily transactions.

## Medication Categories
1. **OTC (Over The Counter)**:
   - General retail medicines (e.g., Paracetamol).
   - Does not require a doctor's prescription.
   - Price is charged per unit multiplied by quantity.
2. **PRESCRIPTION**:
   - Prescription-only medicines (e.g., Amoxicillin antibiotics).
   - MUST have an identified doctor/prescriber on dispense. If prescriber is null or empty, the dispense must be refused.
3. **CONTROLLED (Upcoming Category)**:
   - Heavily regulated substances requiring strict audit trails.
   - Dispensing must record the licensed prescriber and enforce rigorous stock controls. Adding this category should require zero changes to existing category code.

## Customers
1. **WALK_IN**:
   - Occasional customers.
   - Pay standard list price.
   - No phone number requirement.
2. **REGISTERED**:
   - Regular pharmacy clients.
   - Have contact details (phone number) on file.
   - Receive an automatic 5% loyalty discount on all medication purchases.

## Core Invariant
**Stock must never go below zero.**
- In the original code, an order for 100 units of Amoxicillin when only 40 are in stock resulted in -61 stock and recorded a bogus 3000.0 GHS transaction.
- Over-dispensing must be rejected cleanly.
- Dispensing or restocking a quantity <= 0 must be rejected.
- On rejection, neither customer history nor transaction logs must be updated.

