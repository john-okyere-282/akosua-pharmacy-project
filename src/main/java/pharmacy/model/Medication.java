package pharmacy.model;

import java.util.Objects;

/**
 * Sealed abstract base class for all pharmacy medications.
 * 
 * Engineering Decision (ADR-001 & ADR-002):
 * Previous Defect (Rank 1 & 7): Public mutable stock and String-typed category.
 * Stock is held privately with no setters. Category behavior is polymorphic.
 */
public abstract sealed class Medication permits OtcMedication, PrescriptionMedication {
    private final int id;
    private final String name;
    private final Money price;
    private int stock;

    protected Medication(int id, String name, Money price, int initialStock) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "Medication name cannot be null");
        this.price = Objects.requireNonNull(price, "Price cannot be null");
        this.stock = initialStock;
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
        return stock;
    }

    public boolean hasSufficientStock(int qty) {
        return qty > 0 && this.stock >= qty;
    }

    public void deductStock(int qty) {
        this.stock -= qty;
    }

    public void addStock(int qty) {
        this.stock += qty;
    }

    public abstract String categoryName();

    public abstract boolean requiresPrescriber();

    @Override
    public String toString() {
        return name + " (" + categoryName() + "): " + stock + " in stock";
    }
}
