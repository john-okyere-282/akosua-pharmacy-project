package pharmacy.model;

import java.util.Objects;

/**
 * A quantity of medication that can never be less than zero.
 *
 * <p><b>Why this exists:</b> In the original code, stock was a plain number that could
 * become negative if someone dispensed more than was on the shelf. This value object
 * makes a negative stock count impossible to represent in memory.
 *
 * <p><b>Real Scenario:</b>
 * The pharmacy has 40 bottles of Amoxicillin. Ama dispenses 1 bottle, leaving 39.
 * When Kofi requests 100 bottles, the system checks whether 39 is enough. Because 39 < 100,
 * the deduction is refused and stock remains safely at 39 (the old program dropped to -61).
 *
 * <p><b>Java feature note:</b> A <i>value object</i> is an immutable class whose identity is
 * defined entirely by its data, not by a memory address or ID.
 */
public final class StockLevel {

    // A private field: hidden from the outside world so callers cannot tamper with the count directly.
    private final int units;

    /**
     * Constructor validation: ensures invalid objects can never be constructed.
     * If units < 0, creation is rejected immediately with an IllegalArgumentException.
     */
    public StockLevel(int units) {
        if (units < 0) {
            throw new IllegalArgumentException("Stock level cannot be negative: " + units);
        }
        this.units = units;
    }

    public static StockLevel of(int units) {
        return new StockLevel(units);
    }

    public int units() {
        return units;
    }

    /**
     * Decision: Check sufficiency BEFORE any deduction occurs.
     * Rule: If current stock is 39 and request is 100, hasSufficient(100) returns false.
     */
    public boolean hasSufficient(int qty) {
        return qty > 0 && this.units >= qty;
    }

    /**
     * Decision: Deductions produce a brand new StockLevel rather than mutating this instance.
     * Rule: If qty <= 0, rejected. If qty > available, throws IllegalStateException.
     * Example: 40.deduct(1) -> returns new StockLevel(39).
     */
    public StockLevel deduct(int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("Deduction quantity must be positive: " + qty);
        }
        if (qty > this.units) {
            throw new IllegalStateException("Cannot deduct " + qty + " units; only " + this.units + " available");
        }
        return new StockLevel(this.units - qty);
    }

    /**
     * Decision: Restock additions must also be positive.
     * Example: 100.add(50) -> returns new StockLevel(150).
     */
    public StockLevel add(int qty) {
        if (qty <= 0) {
            throw new IllegalArgumentException("Restock quantity must be positive: " + qty);
        }
        return new StockLevel(this.units + qty);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StockLevel that)) return false;
        return units == that.units;
    }

    @Override
    public int hashCode() {
        return Objects.hash(units);
    }

    @Override
    public String toString() {
        return String.valueOf(units);
    }
}
