package pharmacy.model;

import java.util.Objects;

/**
 * Abstract base class for all pharmacy medications.
 * 
 * Engineering Decision (ADR-001 & ADR-002):
 * - Declared as an ordinary abstract class (unsealed) so upcoming categories such as
 *   CONTROLLED or VACCINE can be added by creating a new subclass with ZERO edits
 *   to existing production files (Open-Closed Principle).
 * - Stock is held privately as an immutable StockLevel value object with no setters.
 * - Invariant: Stock cannot become negative.
 */
public abstract class Medication {
    private final int id;
    private final String name;
    private final Money price;
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

    public boolean hasSufficientStock(int qty) {
        return stock.hasSufficient(qty);
    }

    public void deductStock(int qty) {
        this.stock = this.stock.deduct(qty);
    }

    public void addStock(int qty) {
        this.stock = this.stock.add(qty);
    }

    public abstract String categoryName();

    public abstract boolean requiresPrescriber();

    @Override
    public String toString() {
        return name + " (" + categoryName() + "): " + stock.units() + " in stock";
    }
}
