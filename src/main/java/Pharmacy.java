import java.util.ArrayList;
import java.util.List;
import pharmacy.model.Customer;
import pharmacy.model.Medication;
import pharmacy.model.Money;
import pharmacy.model.Transaction;

/**
 * Pharmacy coordinator (Step 6: Dispensing rules extracted, unified stock deduction, no category if/else).
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
        // 1. Check prescriber requirement polymorphically (no category strings)
        if (m.requiresPrescriber() && prescriber == null) {
            System.out.println("Cannot dispense " + m.name() + " without a prescriber");
            return;
        }

        // 2. Pricing calculation: order preserved (price * qty), then discount from customer type
        Money total = m.price().multiply(qty);
        if (c.discountMultiplier() < 1.0) {
            total = total.applyDiscount(c.discountMultiplier());
        }

        // 3. Stock deduction exists in exactly ONE place
        m.deductStock(qty);

        // 4. Console reporting without category if/else
        String prescriberPart = m.requiresPrescriber() ? " (prescribed by " + prescriber + ")" : "";
        System.out.println("Dispensed " + qty + " x " + m.name() + " to " + c.name()
                + prescriberPart + " for GHS " + total);

        // 5. Audit recording
        c.recordHistory("Got " + qty + " x " + m.name());
        String recordedPrescriber = m.requiresPrescriber() ? prescriber : null;
        transactions.add(new Transaction(nextTransactionId++, c.name(), m.name(), qty, total.amount(), "DISPENSE", recordedPrescriber));
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
