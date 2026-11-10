package pharmacy.model;

import java.util.Objects;

/**
 * Sealed abstract base class for all pharmacy medications.
 * 
 * Engineering Decisions (ADR-001 & ADR-002):
 * Previous Defect (Rank 1 & 7): Public mutable stock and String-typed category.
 * Stock is held privately as an immutable StockLevel with no setters.
 * Invariant enforcement: Stock cannot become negative.
 */
public abstract sealed class Medication permits OtcMedication, PrescriptionMedication {
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
