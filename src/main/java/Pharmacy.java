import java.util.ArrayList;
import java.util.List;
import pharmacy.model.Customer;
import pharmacy.model.Medication;
import pharmacy.model.Transaction;

/**
 * Pharmacy coordinator (Step 5: Immutable Transaction record and instance-scoped IDs).
 */
public class Pharmacy {
    public String name;
    public List<Medication> meds = new ArrayList<>();
    public List<Customer> customers = new ArrayList<>();
    private List<Transaction> transactions = new ArrayList<>();
    private int nextTransactionId = 1;

    public Pharmacy(String name) {
        this.name = name;
    }

    public void addMedication(Medication m) {
        meds.add(m);
    }

    public void addCustomer(Customer c) {
        customers.add(c);
    }

    public void dispense(Customer c, Medication m, int qty, String prescriber) {
        double total = 0;
        String recordedPrescriber = null;

        if (m.categoryName().equals("OTC")) {
            total = m.price().amount() * qty;
            if (c.discountMultiplier() < 1.0) {
                total = total * c.discountMultiplier();
            }
            m.deductStock(qty);
            System.out.println("Dispensed " + qty + " x " + m.name() + " to " + c.name()
                    + " for GHS " + total);
        } else if (m.categoryName().equals("PRESCRIPTION")) {
            if (prescriber == null) {
                System.out.println("Cannot dispense " + m.name() + " without a prescriber");
                return;
            }
            recordedPrescriber = prescriber;
            total = m.price().amount() * qty;
            if (c.discountMultiplier() < 1.0) {
                total = total * c.discountMultiplier();
            }
            m.deductStock(qty);
            System.out.println("Dispensed " + qty + " x " + m.name() + " to " + c.name()
                    + " (prescribed by " + prescriber + ") for GHS " + total);
        }
        c.recordHistory("Got " + qty + " x " + m.name());
        transactions.add(new Transaction(nextTransactionId++, c.name(), m.name(), qty, total, "DISPENSE", recordedPrescriber));
    }

    public void restock(Medication m, int qty) {
        m.addStock(qty);
        transactions.add(new Transaction(nextTransactionId++, "(supplier)", m.name(), qty, 0, "RESTOCK", null));
        System.out.println("Restocked " + qty + " x " + m.name());
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void printReport() {
        System.out.println();
        System.out.println("=== " + name + " - end of day ===");
        System.out.println("Stock levels:");
        for (Medication m : meds) {
            System.out.println("  " + m.name() + " (" + m.categoryName() + "): " + m.currentStock() + " in stock");
        }
        System.out.println("Transactions today: " + transactions.size());
    }
}
