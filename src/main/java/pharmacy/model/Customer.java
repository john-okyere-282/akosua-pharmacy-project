package pharmacy.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * The base model for all pharmacy customers.
 *
 * <p><b>Why this exists:</b> The original program used raw string kinds ("WALK_IN", "REGISTERED")
 * and an exposed mutable history list. This class encapsulates customer medical history and
 * delegates loyalty discount multipliers directly to the customer type.
 *
 * <p><b>Real Scenario:</b>
 * Kofi Mensah (walk-in) pays 100% of price (multiplier 1.0).
 * Ama Owusu (registered) pays 95% of price (multiplier 0.95), receiving an automatic 5% loyalty discount.
 *
 * <p><b>Java feature note:</b> A <i>sealed class</i> restricts which other classes may extend it.
 * Here, Customer is sealed to WalkInCustomer and RegisteredCustomer because Akosua's pharmacy
 * currently recognizes exactly two loyalty pricing tiers.
 */
public abstract sealed class Customer permits WalkInCustomer, RegisteredCustomer {

    private final int id;
    private final String name;

    // Private mutable list encapsulated inside the class
    private final List<String> history = new ArrayList<>();

    protected Customer(int id, String name) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "Customer name cannot be null");
    }

    public int id() {
        return id;
    }

    public String name() {
        return name;
    }

    /**
     * Decision: Expose history as an unmodifiable list so external callers cannot clear or mutate it.
     * Rule: Calling history().clear() throws UnsupportedOperationException.
     */
    public List<String> history() {
        return Collections.unmodifiableList(history);
    }

    /**
     * Controlled recording: only successful dispense operations append to patient history.
     * Example: recordHistory("Got 2 x Paracetamol 500mg").
     */
    public void recordHistory(String entry) {
        this.history.add(Objects.requireNonNull(entry, "History entry cannot be null"));
    }

    public abstract String kind();

    /**
     * Returns the pricing multiplier owned by this customer type (e.g. 1.0 for walk-in, 0.95 for registered).
     */
    public abstract double discountMultiplier();
}
