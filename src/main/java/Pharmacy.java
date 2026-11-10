import java.util.ArrayList;
import java.util.List;
import pharmacy.model.Customer;
import pharmacy.model.Medication;
import pharmacy.model.Transaction;

/**
 * Pharmacy coordinator extracted into its own file in default package (Step 1).
 * 
 * Engineering Note:
 * Previous Defect (Rank 4): God class coordinating domain rules, pricing, inventory mutation,
 * and console formatting. In subsequent steps, domain logic and reporting will be cleanly extracted.
 */
public class Pharmacy {
    public String name;
    public List<Medication> meds = new ArrayList<>();
    public List<Customer> customers = new ArrayList<>();
    private List<Transaction> transactions = new ArrayList<>();

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
        if (m.category.equals("OTC")) {
            total = m.price * qty;
            if (c.kind.equals("REGISTERED")) {
                total = total * 0.95;
            }
            m.stock = m.stock - qty;
            System.out.println("Dispensed " + qty + " x " + m.name + " to " + c.name
                    + " for GHS " + total);
        } else if (m.category.equals("PRESCRIPTION")) {
            if (prescriber == null) {
                System.out.println("Cannot dispense " + m.name + " without a prescriber");
                return;
            }
            total = m.price * qty;
            if (c.kind.equals("REGISTERED")) {
                total = total * 0.95;
            }
            m.stock = m.stock - qty;
            System.out.println("Dispensed " + qty + " x " + m.name + " to " + c.name
                    + " (prescribed by " + prescriber + ") for GHS " + total);
        }
        c.history.add("Got " + qty + " x " + m.name);
        transactions.add(new Transaction(c.name, m.name, qty, total, "DISPENSE"));
    }

    public void restock(Medication m, int qty) {
        m.stock = m.stock + qty;
        transactions.add(new Transaction("(supplier)", m.name, qty, 0, "RESTOCK"));
        System.out.println("Restocked " + qty + " x " + m.name);
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void printReport() {
        System.out.println();
        System.out.println("=== " + name + " - end of day ===");
        System.out.println("Stock levels:");
        for (Medication m : meds) {
            System.out.println("  " + m.name + " (" + m.category + "): " + m.stock + " in stock");
        }
        System.out.println("Transactions today: " + transactions.size());
    }
}
