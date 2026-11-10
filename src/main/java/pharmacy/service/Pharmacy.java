package pharmacy.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import pharmacy.model.Customer;
import pharmacy.model.Medication;
import pharmacy.model.Money;
import pharmacy.model.Transaction;
import pharmacy.report.DispenseReporter;

/**
 * Coordinates pharmacy inventory, dispensing operations, and daily reporting.
 * 
 * Engineering Decisions (ADR-005, ADR-006, ADR-007):
 * - Moved to pharmacy.service so all business logic lives in domain packages.
 * - Single responsibility: coordinates operations between entities without leaking mutable state.
 * - Holds all collections privately; returns unmodifiable views.
 * - Enforces business invariants: stock cannot drop below zero; quantities must be positive.
 * - On any rejection: leaves stock untouched and records NO customer history or transaction.
 */
public class Pharmacy {
    private final String name;
    private final List<Medication> meds = new ArrayList<>();
    private final List<Customer> customers = new ArrayList<>();
    private final List<Transaction> transactions = new ArrayList<>();
    private final DispenseReporter reporter;
    private int nextTransactionId = 1;

    public Pharmacy(String name) {
        this(name, new DispenseReporter());
    }

    public Pharmacy(String name, DispenseReporter reporter) {
        this.name = Objects.requireNonNull(name, "Pharmacy name cannot be null");
        this.reporter = Objects.requireNonNull(reporter, "Reporter cannot be null");
    }

    public String name() {
        return name;
    }

    public void addMedication(Medication m) {
        meds.add(Objects.requireNonNull(m, "Medication cannot be null"));
    }

    public void addCustomer(Customer c) {
        customers.add(Objects.requireNonNull(c, "Customer cannot be null"));
    }

    /**
     * Dispenses medication to a customer adhering strictly to domain checks in order:
     * 1. Prescriber check (null-only preserved from legacy contract)
     * 2. Quantity positivity check
     * 3. Stock sufficiency invariant check
     */
    public DispenseResult dispense(Customer c, Medication m, int qty, String prescriber) {
        // 1. Prescriber check (null-only check preserved per brief)
        if (m.requiresPrescriber() && prescriber == null) {
            DispenseResult rejected = new DispenseRejected("Cannot dispense " + m.name() + " without a prescriber");
            reporter.reportDispense(rejected);
            return rejected;
        }

        // 2. Quantity validity check
        if (qty <= 0) {
            DispenseResult rejected = new DispenseRejected("Cannot dispense " + m.name() + ": quantity must be positive");
            reporter.reportDispense(rejected);
            return rejected;
        }

        // 3. Stock sufficiency invariant check
        if (!m.hasSufficientStock(qty)) {
            DispenseResult rejected = new DispenseRejected(
                    "Cannot dispense " + m.name() + ": only " + m.currentStock() + " in stock, requested " + qty
            );
            reporter.reportDispense(rejected);
            return rejected;
        }

        // 4. Pricing calculation: order preserved (price * qty), then discount from customer
        Money total = m.price().multiply(qty);
        if (c.discountMultiplier() < 1.0) {
            total = total.applyDiscount(c.discountMultiplier());
        }

        // 5. Stock deduction (in exactly ONE place)
        m.deductStock(qty);

        // 6. Audit recording (only on success)
        c.recordHistory("Got " + qty + " x " + m.name());
        String recordedPrescriber = m.requiresPrescriber() ? prescriber : null;
        transactions.add(new Transaction(nextTransactionId++, c.name(), m.name(), qty, total.amount(), "DISPENSE", recordedPrescriber));

        // 7. Success reporting
        DispenseResult success = new DispenseSuccess(c, m, qty, recordedPrescriber, total);
        reporter.reportDispense(success);
        return success;
    }

    /**
     * Restocks medication with positive quantity check.
     */
    public void restock(Medication m, int qty) {
        if (qty <= 0) {
            System.out.println("Cannot restock " + m.name() + ": quantity must be positive");
            return;
        }
        m.addStock(qty);
        transactions.add(new Transaction(nextTransactionId++, "(supplier)", m.name(), qty, 0, "RESTOCK", null));
        reporter.reportRestock(m, qty);
    }

    public List<Transaction> getTransactions() {
        return Collections.unmodifiableList(transactions);
    }

    public List<Medication> getMedications() {
        return Collections.unmodifiableList(meds);
    }

    public List<Customer> getCustomers() {
        return Collections.unmodifiableList(customers);
    }

    public void printReport() {
        reporter.reportEndOfDay(name, meds, transactions.size());
    }
}
