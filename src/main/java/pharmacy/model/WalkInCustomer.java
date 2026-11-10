package pharmacy.model;

/**
 * An occasional walk-in customer who pays standard retail prices without discounts.
 *
 * <p><b>Why this exists:</b> Separates walk-in patients from registered account holders so
 * no phone number is required and no loyalty discounts are applied.
 *
 * <p><b>Real Scenario:</b>
 * Kofi Mensah buys 2 Paracetamol packets at GHS 12.50.
 * Multiplier is 1.0 -> pays standard price GHS 25.0.
 */
public final class WalkInCustomer extends Customer {

    public WalkInCustomer(int id, String name) {
        super(id, name);
    }

    public WalkInCustomer(String name) {
        super(1, name);
    }

    @Override
    public String kind() {
        return "WALK_IN";
    }

    @Override
    public double discountMultiplier() {
        return 1.0;
    }
}
