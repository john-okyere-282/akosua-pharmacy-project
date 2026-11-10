package pharmacy.model;

/**
 * A prescription-only medication (e.g. antibiotics) that legally requires a licensed physician's sign-off.
 *
 * <p><b>Why this exists:</b> Ensures that restricted medicines cannot be dispensed unless an
 * authorized doctor or prescriber is provided.
 *
 * <p><b>Real Scenario:</b>
 * Ama Owusu dispenses 1 bottle of Amoxicillin 250mg with prescriber "Dr. Boateng" -> approved.
 * If someone attempts to dispense Amoxicillin with null prescriber -> rejected with:
 * "Cannot dispense Amoxicillin 250mg without a prescriber".
 *
 * <p><b>How to extend:</b>
 * If a specialized prescription category is needed (e.g. ControlledMedication), it can extend
 * PrescriptionMedication and inherit the prescriber requirement.
 */
public class PrescriptionMedication extends Medication {

    public PrescriptionMedication(int id, String name, Money price, StockLevel initialStock) {
        super(id, name, price, initialStock);
    }

    public PrescriptionMedication(String name, double price, int initialStock) {
        super(2, name, Money.of(price), StockLevel.of(initialStock));
    }

    @Override
    public String categoryName() {
        return "PRESCRIPTION";
    }

    @Override
    public boolean requiresPrescriber() {
        return true;
    }
}
