package pharmacy.service;

/**
 * A sealed outcome representing the result of an attempted medication dispense.
 *
 * <p><b>Why this exists:</b> Decouples business rules from console printing and avoids using
 * exceptions for normal business occurrences (e.g. asking for more stock than is on the shelf).
 *
 * <p><b>Real Scenario:</b>
 * Dispensing 1 Amoxicillin to Ama returns {@link DispenseSuccess}.
 * Dispensing 100 Amoxicillin when 39 remain returns {@link DispenseRejected} with reason:
 * "Cannot dispense Amoxicillin 250mg: only 39 in stock, requested 100".
 *
 * <p><b>Java feature note:</b> A <i>sealed interface</i> permits only a closed, exhaustive set of
 * implementations ({@link DispenseSuccess} and {@link DispenseRejected}), because a dispense
 * operation has exactly two logical outcomes: success or failure.
 */
public sealed interface DispenseResult permits DispenseSuccess, DispenseRejected {

    /**
     * Returns true if the dispense was approved and completed, false if rejected.
     */
    boolean isSuccess();
}
