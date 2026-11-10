import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import pharmacy.model.Customer;
import pharmacy.model.Medication;
import pharmacy.model.Money;
import pharmacy.model.Transaction;
import pharmacy.report.DispenseReporter;
import pharmacy.service.DispenseRejected;
import pharmacy.service.DispenseResult;
import pharmacy.service.DispenseSuccess;

/**
 * Pharmacy coordinator (Step 7: DispenseResult, DispenseReporter, encapsulated unmodifiable views).
 * 
 * Engineering Decisions (ADR-005, ADR-006, ADR-007):
 * Previous Defect (Rank 4 & 5): God class with leaked mutable collections.
 * Collections are private, getters return unmodifiable views, and console I/O is delegated to DispenseReporter.
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

    public DispenseResult dispense(Customer c, Medication m, int qty, String prescriber) {
        // 1. Prescriber check (null-only check preserved per brief)
        if (m.requiresPrescriber() && prescriber == null) {
            DispenseResult rejected = new DispenseRejected("Cannot dispense " + m.name() + " without a prescriber");
            reporter.reportDispense(rejected);
            return rejected;
        }

        // 2. Pricing calculation: order preserved (price * qty), then discount from customer
        Money total = m.price().multiply(qty);
        if (c.discountMultiplier() < 1.0) {
            total = total.applyDiscount(c.discountMultiplier());
        }

        // 3. Stock deduction
        m.deductStock(qty);

        // 4. Audit recording
        c.recordHistory("Got " + qty + " x " + m.name());
        String recordedPrescriber = m.requiresPrescriber() ? prescriber : null;
        transactions.add(new Transaction(nextTransactionId++, c.name(), m.name(), qty, total.amount(), "DISPENSE", recordedPrescriber));

        // 5. Success reporting
        DispenseResult success = new DispenseSuccess(c, m, qty, recordedPrescriber, total);
        reporter.reportDispense(success);
        return success;
    }

    public void restock(Medication m, int qty) {
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
