package pharmacy.report;

import java.util.List;
import java.util.Objects;
import pharmacy.model.Medication;
import pharmacy.service.DispenseRejected;
import pharmacy.service.DispenseResult;
import pharmacy.service.DispenseSuccess;

/**
 * Handles formatted console printing matching the exact legacy CLI output contract.
 *
 * <p><b>Why this exists:</b> Separates console output formatting (I/O) from domain business logic.
 * The domain models and Pharmacy coordinator compute outcomes; DispenseReporter simply displays them.
 *
 * <p><b>Real Scenario:</b>
 * Formats: "Dispensed 2 x Paracetamol 500mg to Kofi Mensah for GHS 25.0"
 * Formats: "Dispensed 1 x Amoxicillin 250mg to Ama Owusu (prescribed by Dr. Boateng) for GHS 28.5"
 * Formats: "Cannot dispense Amoxicillin 250mg: only 39 in stock, requested 100"
 */
public class DispenseReporter {

    /**
     * Prints success or rejection messages in the exact original format.
     */
    public void reportDispense(DispenseResult result) {
        Objects.requireNonNull(result, "DispenseResult cannot be null");
        if (result instanceof DispenseSuccess s) {
            String prescriberPart = s.medication().requiresPrescriber() ? " (prescribed by " + s.prescriber() + ")" : "";
            System.out.println("Dispensed " + s.qty() + " x " + s.medication().name() + " to " + s.customer().name()
                    + prescriberPart + " for GHS " + s.total());
        } else if (result instanceof DispenseRejected r) {
            System.out.println(r.reason());
        }
    }

    /**
     * Formats restock confirmations.
     * Example: "Restocked 50 x Paracetamol 500mg"
     */
    public void reportRestock(Medication m, int qty) {
        System.out.println("Restocked " + qty + " x " + m.name());
    }

    /**
     * Formats the daily end-of-day summary report.
     * Preserves the preceding blank line and layout from the original contract.
     */
    public void reportEndOfDay(String pharmacyName, List<Medication> meds, int txCount) {
        System.out.println();
        System.out.println("=== " + pharmacyName + " - end of day ===");
        System.out.println("Stock levels:");
        for (Medication m : meds) {
            System.out.println("  " + m.name() + " (" + m.categoryName() + "): " + m.currentStock() + " in stock");
        }
        System.out.println("Transactions today: " + txCount);
    }
}
