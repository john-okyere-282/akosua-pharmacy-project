package pharmacy.model;

import java.util.Objects;

/**
 * Immutable value type encapsulating inventory stock quantities.
 * 
 * Engineering Decision (ADR-002):
 * Previous Defect (Rank 1): Unvalidated int field allowed stock to plummet to -61.
 * StockLevel makes negative inventory unrepresentable in memory by throwing
 * an IllegalArgumentException whenever units < 0.
 */
public final class StockLevel {
    private final int units;

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
     * Determines whether there are sufficient units available for the requested quantity.
     */
    public boolean hasSufficient(int qty) {
        return qty > 0 && this.units >= qty;
    }

    /**
     * Returns a new StockLevel decremented by qty.
     * Enforces the non-negative invariant.
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
     * Returns a new StockLevel incremented by qty.
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

