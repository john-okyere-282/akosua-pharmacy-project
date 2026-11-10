package pharmacy.model;

import java.util.Objects;

/**
 * Immutable value type representing monetary currency in Ghana Cedis (GHS).
 * 
 * Engineering Decision (ADR-004):
 * Previous Defect (Rank 10): Primitive double used directly without domain encapsulation.
 * We deliberately maintain double arithmetic internally to guarantee 100% byte-identical
 * output formatting (e.g. 25.0, 28.5) against the baseline contract, while encapsulating
 * calculation order and currency operations.
 */
public final class Money {
    private final double amount;

    public Money(double amount) {
        this.amount = amount;
    }

    public static Money of(double amount) {
        return new Money(amount);
    }

    public static Money zero() {
        return new Money(0.0);
    }

    public double amount() {
        return amount;
    }

    /**
     * Multiplies unit price by integer quantity.
     * Arithmetic order preserved: (price * qty)
     */
    public Money multiply(int qty) {
        return new Money(this.amount * qty);
    }

    /**
     * Applies a discount multiplier (e.g. 0.95 for 5% loyalty discount).
     * Arithmetic order preserved: (price * qty) * multiplier
     */
    public Money applyDiscount(double multiplier) {
        return new Money(this.amount * multiplier);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Money money)) return false;
        return Double.compare(money.amount, amount) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount);
    }

    /**
     * Preserves exact Java Double.toString() formatting to match legacy stdout contract.
     */
    @Override
    public String toString() {
        return Double.toString(amount);
    }
}

