package pharmacy.service;

import java.util.Objects;

/**
 * Rejected dispense result record with specific failure reason.
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
