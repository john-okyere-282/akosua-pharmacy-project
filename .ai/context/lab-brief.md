# Lab Brief: Java Basics Lab A — Akosua's Pharmacy

## Overview
Akosua's pharmacy in Accra uses a small in-house Java program (`PharmacyTracker.java`) to track medication stock and dispense medicines to patients. The program was written quickly by an intern who has left. While it executes, it has critical architectural flaws, negative stock states, public mutable fields, and poor extensibility.

Your job is to diagnose the flaws, preserve the core behavior (with the critical invariant that stock cannot become negative), and refactor it into an object-oriented, robust, and clean design.

## Assessment Rubric
| Area | Weight | Description |
|---|---|---|
| **Diagnosis** | 20% | Identification of defects, root causes, business risk ranking, analysis of the coming controlled-substances change, clear explanation of the most dangerous flaw. |
| **Structure & Encapsulation** | 25% | Clean package structure, domain models, immutability, private guarded fields, no leaked mutable collections, proper responsibility separation. |
| **Design Reasoning (ADRs)** | 20% | Clear options considered, trade-offs evaluated, justification of choices, clear architectural narrative written in first person. |
| **Behavior Preservation & Invariant Enforcement** | 20% | Output consistency with baseline, rejection of invalid stock/quantities with clear reporting, no partial transaction/history side effects on failure. |
| **One Improvement Beyond the Brief** | 15% | Meaningful enhancement (e.g. comprehensive automated JUnit 5 test suite & new medication onboarding guide) with clear business justification. |

## Deliverables List
1. `baseline/original-output.txt`
2. `DIAGNOSIS.md`
3. `docs/decisions/` (ADR-001 through ADR-008)
4. Refactored source code in `src/main/java/` (thin `PharmacyTracker.java` in default package + domain packages)
5. `docs/behaviour-evidence.md`
6. JUnit 5 test suite in `src/test/java/`
7. Updated `README.md` (or `docs/design-notes.md`) written in first person
8. Defense readiness / drill notes

