package pharmacy.model;

import java.util.Objects;

/**
 * An immutable monetary amount in Ghana Cedis (GHS).
 *
 * <p><b>Why this exists:</b> Eliminates primitive obsession (using raw numbers everywhere for money)
 * while keeping the exact calculation order and double formatting required by Akosua's original receipts.
 *
 * <p><b>Real Scenario:</b>
 * Paracetamol costs GHS 12.50. Kofi buys 2 packets:
 * 12.50.multiply(2) -> GHS 25.0.
 * Ama buys 1 bottle of Amoxicillin at GHS 30.00 with 5% discount:
 * 30.00.multiply(1).applyDiscount(0.95) -> GHS 28.5.
 *
 * <p><b>Java feature note:</b> A <i>final class</i> cannot be subclassed, ensuring its immutability
 * and formatting guarantees cannot be altered by derived types.
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
     * Decision: Preserve the original arithmetic sequence by calculating (price * qty) first.
     * Example: GHS 12.50 * 2 -> GHS 25.0.
     */
    public Money multiply(int qty) {
        return new Money(this.amount * qty);
    }

    /**
     * Decision: Apply loyalty discount multiplier after total line item price is computed.
     * Example: GHS 30.0 * 0.95 -> GHS 28.5.
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
     * Decision: Delegate directly to Double.toString() so printed totals match the legacy stdout contract.
     * Example: 25.0 is printed as "25.0", not "25.00".
     */
    @Override
    public String toString() {
        return Double.toString(amount);
    }
}
