package pharmacy.service;

import java.util.Objects;
import pharmacy.model.Customer;
import pharmacy.model.Medication;
import pharmacy.model.Money;

/**
 * Successful dispense result record.
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
