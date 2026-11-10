package pharmacy.model;

/**
 * Legacy Transaction model extracted to pharmacy.model in Step 1.
 * 
 * Engineering Note:
 * Previous Defect (Rank 9 & Rank 8): Mutable fields, static counter, and missing prescriber field.
 * In subsequent build steps, this will be converted to an immutable Java record with prescriber auditing.
 */
public class Transaction {
    public int id;
    public String customerName;
    public String medName;
    public int qty;
    public double total;
    public String type; // "DISPENSE" or "RESTOCK"
    public static int counter = 1;

    public Transaction(String customerName, String medName, int qty, double total, String type) {
        this.id = counter++;
        this.customerName = customerName;
        this.medName = medName;
        this.qty = qty;
        this.total = total;
        this.type = type;
    }
}
