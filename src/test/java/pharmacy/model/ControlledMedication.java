package pharmacy.model;

/**
 * Test-scope Controlled Medication category (e.g. Morphine).
 *
 * <p><b>Why this exists:</b> Proves that Akosua's upcoming CONTROLLED category can be added
 * with ZERO modifications to existing production classes in src/main.
 *
 * <p><b>Scenario:</b>
 * Morphine 10mg is added with 20 units at GHS 50.00.
 * Attempting to dispense without a prescriber is rejected.
 * Dispensing 2 units to Ama Owusu with "Dr. Boateng" succeeds: charges GHS 95.0 (5% loyalty discount),
 * reduces stock to 18, and records the prescriber on the Transaction ledger.
 */
public class ControlledMedication extends PrescriptionMedication {

    public ControlledMedication(int id, String name, Money price, StockLevel initialStock) {
        super(id, name, price, initialStock);
    }

    public ControlledMedication(String name, double price, int initialStock) {
        super(10, name, Money.of(price), StockLevel.of(initialStock));
    }

    @Override
    public String categoryName() {
        return "CONTROLLED";
    }

    @Override
    public boolean requiresPrescriber() {
        return true;
    }
}
