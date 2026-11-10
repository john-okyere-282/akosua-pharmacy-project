package pharmacy.report;

import java.util.List;
import java.util.Objects;
import pharmacy.model.Medication;
import pharmacy.service.DispenseRejected;
import pharmacy.service.DispenseResult;
import pharmacy.service.DispenseSuccess;

/**
 * Console reporter that formats system outputs matching the legacy CLI contract.
 * 
 * Engineering Decision (ADR-001 & ADR-005):
 * Removes console printing logic from domain entities while maintaining byte-exact output.
 */
public class DispenseReporter {

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

    public void reportRestock(Medication m, int qty) {
        System.out.println("Restocked " + qty + " x " + m.name());
    }

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
