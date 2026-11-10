package pharmacy.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Sealed abstract base class for pharmacy customers.
 * 
 * Engineering Decision (ADR-003):
 * Previous Defect (Rank 5 & 7): Public mutable history list and string-typed customer kinds.
 * The discount calculation is owned directly by the customer type via discountMultiplier().
 * History is exposed strictly as an unmodifiable list.
 */
public abstract sealed class Customer permits WalkInCustomer, RegisteredCustomer {
    private final int id;
    private final String name;
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

    public List<String> history() {
        return Collections.unmodifiableList(history);
    }

    public void recordHistory(String entry) {
        this.history.add(Objects.requireNonNull(entry, "History entry cannot be null"));
    }

    public abstract String kind();

    public abstract double discountMultiplier();
}
