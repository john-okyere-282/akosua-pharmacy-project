package pharmacy.service;

import java.util.Objects;

/**
 * Represents a rejected dispense attempt where no state changes occurred.
 *
 * <p><b>Why this exists:</b> Encapsulates the exact business reason why an operation was declined,
 * guaranteeing that no inventory, transactions, or patient records were altered.
 *
 * <p><b>Real Scenario:</b>
 * Requesting 100 bottles of Amoxicillin when only 39 exist returns DispenseRejected with reason:
 * "Cannot dispense Amoxicillin 250mg: only 39 in stock, requested 100".
 */
public record DispenseRejected(String reason) implements DispenseResult {

    public DispenseRejected {
        Objects.requireNonNull(reason, "Rejection reason cannot be null");
    }

    @Override
    public boolean isSuccess() {
        return false;
    }
}
