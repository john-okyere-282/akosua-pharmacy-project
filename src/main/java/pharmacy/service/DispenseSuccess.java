package pharmacy.service;

import java.util.Objects;
import pharmacy.model.Customer;
import pharmacy.model.Medication;
import pharmacy.model.Money;

/**
 * Represents a successful dispense operation where inventory was decremented and payment calculated.
 *
 * <p><b>Why this exists:</b> Carries the verified details of a completed transaction so the reporter
 * can format the confirmation message without recalculating business rules.
 *
 * <p><b>Real Scenario:</b>
 * Kofi buys 2 Paracetamol 500mg: holds customer=Kofi, med=Paracetamol, qty=2, prescriber=null, total=GHS 25.0.
 */
public record DispenseSuccess(
        Customer customer,
        Medication medication,
        int qty,
        String prescriber,
        Money total
) implements DispenseResult {

    public DispenseSuccess {
        Objects.requireNonNull(customer, "Customer cannot be null");
        Objects.requireNonNull(medication, "Medication cannot be null");
        Objects.requireNonNull(total, "Total cannot be null");
    }

    @Override
    public boolean isSuccess() {
        return true;
    }
}
