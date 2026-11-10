package pharmacy.model;

import java.util.Objects;

/**
 * An immutable audit ledger record of an inventory or dispensing event.
 *
 * <p><b>Why this exists:</b> The original Transaction class had public mutable fields, a global static
 * counter that caused cross-test pollution, and completely omitted the prescriber. This record provides
 * immutable audit logging with explicit prescriber tracking.
 *
 * <p><b>Real Scenario:</b>
 * Dispensing 1 Amoxicillin to Ama Owusu creates a transaction record:
 * id=2, customer="Ama Owusu", med="Amoxicillin 250mg", qty=1, total=28.5, type="DISPENSE", prescriber="Dr. Boateng".
 * An OTC dispense or restock leaves prescriber as null.
 *
 * <p><b>Java feature note:</b> A <i>record</i> is a concise, immutable data carrier that automatically
 * generates final fields, constructor, getters, equals, hashCode, and toString.
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
