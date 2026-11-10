package pharmacy.model;

import java.util.Objects;

/**
 * Regular registered patient with contact details on file.
 * Entitled to an automatic 5% loyalty discount on all purchases.
 */
public final class RegisteredCustomer extends Customer {
    private final String phone;

    public RegisteredCustomer(int id, String name, String phone) {
        super(id, name);
        this.phone = Objects.requireNonNull(phone, "Registered patient must have phone on file");
    }

    public RegisteredCustomer(String name, String phone) {
        super(2, name);
        this.phone = Objects.requireNonNull(phone, "Registered patient must have phone on file");
    }

    public String phone() {
        return phone;
    }

    @Override
    public String kind() {
        return "REGISTERED";
    }

    @Override
    public double discountMultiplier() {
        return 0.95;
    }
}
