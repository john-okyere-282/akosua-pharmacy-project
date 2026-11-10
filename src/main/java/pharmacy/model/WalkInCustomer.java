package pharmacy.model;

/**
 * Occasional walk-in customer who pays standard retail prices.
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

