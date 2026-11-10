package pharmacy.model;

import java.util.Objects;

/**
 * Immutable audit record representing a pharmacy transaction.
 * 
 * Engineering Decisions (ADR-006 & ADR-008):
 * Previous Defect (Rank 8 & 9): Mutable fields, static counter, and omitted prescriber.
 * Implemented as a Java record with explicit prescriber tracking (null for OTC/restock)
 * and instance-scoped ID assignment.
 */
public record Transaction(
        int id,
        String customerName,
        String medName,
        int qty,
        double total,
        String type,
        String prescriber
) {
    public Transaction {
        Objects.requireNonNull(customerName, "Customer name cannot be null");
        Objects.requireNonNull(medName, "Medication name cannot be null");
        Objects.requireNonNull(type, "Transaction type cannot be null");
    }
}
