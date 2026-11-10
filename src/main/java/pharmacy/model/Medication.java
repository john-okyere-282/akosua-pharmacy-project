package pharmacy.model;

import java.util.Objects;

/**
 * The base model for all pharmaceutical medications stocked by Akosua.
 *
 * <p><b>Why this exists:</b> The original program used a single class with string tags ("OTC", "PRESCRIPTION")
 * and public mutable stock fields. This abstract base class guards its stock privately using {@link StockLevel}
 * and delegates category-specific rules to polymorphic subclasses.
 *
 * <p><b>Real Scenario:</b>
 * Paracetamol 500mg starts with 100 packets in stock at GHS 12.50.
 * Amoxicillin 250mg starts with 40 bottles in stock at GHS 30.00.
 * When Paracetamol is restocked with 50 units, its internal stock becomes 150.
 *
 * <p><b>Java feature note:</b> <i>Polymorphism</i> ("many shapes") allows the pharmacy dispensing engine
 * to treat any medication uniformly without checking what category string it has.
 *
 * <p><b>How to extend:</b>
 * When the coming CONTROLLED category arrives, simply create a new class:
 * {@code public class ControlledMedication extends Medication} (or extends {@link PrescriptionMedication}).
 * Implement {@link #categoryName()} returning "CONTROLLED" and {@link #requiresPrescriber()} returning true.
 * No existing classes under src/main need to be touched.
 */
public abstract class Medication {

    private final int id;
    private final String name;
    private final Money price;

    // Guarded private field: only accessible via validated methods in this class
    private StockLevel stock;

    protected Medication(int id, String name, Money price, StockLevel initialStock) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "Medication name cannot be null");
        this.price = Objects.requireNonNull(price, "Price cannot be null");
        this.stock = Objects.requireNonNull(initialStock, "Initial stock cannot be null");
    }

    public int id() {
        return id;
    }

    public String name() {
        return name;
    }

    public Money price() {
        return price;
    }

    public int currentStock() {
        return stock.units();
    }

    /**
     * Checks if requested quantity can be safely fulfilled from inventory.
     * Example: currentStock = 39, qty = 100 -> returns false.
     */
    public boolean hasSufficientStock(int qty) {
        return stock.hasSufficient(qty);
    }

    /**
     * Deducts stock using the immutable StockLevel.
     * Rule: Invariant is enforced; if qty > stock, StockLevel throws an IllegalStateException.
     */
    public void deductStock(int qty) {
        this.stock = this.stock.deduct(qty);
    }

    /**
     * Restocks inventory by creating an incremented StockLevel.
     */
    public void addStock(int qty) {
        this.stock = this.stock.add(qty);
    }

    /**
     * Returns the plain category name for reporting (e.g. "OTC", "PRESCRIPTION").
     */
    public abstract String categoryName();

    /**
     * Tells the dispensing engine whether a licensed prescriber is legally required.
     */
    public abstract boolean requiresPrescriber();

    @Override
    public String toString() {
        return name + " (" + categoryName() + "): " + stock.units() + " in stock";
    }
}
