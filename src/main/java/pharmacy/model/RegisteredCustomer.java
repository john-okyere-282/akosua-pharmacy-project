package pharmacy.model;

import java.util.Objects;

/**
 * A registered loyalty patient with verified contact details on file.
 *
 * <p><b>Why this exists:</b> Enforces that registered patients must have contact phone numbers
 * on record and entitles them to an automatic 5% loyalty discount across all purchases.
 *
 * <p><b>Real Scenario:</b>
 * Ama Owusu (phone: "0244 000 000") buys 1 bottle of Amoxicillin at GHS 30.00.
 * Her 0.95 discount multiplier is applied -> total charged is GHS 28.5 (saving GHS 1.50).
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
