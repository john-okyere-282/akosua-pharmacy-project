package pharmacy.model;

/**
 * Prescription-only medication.
 * Requires an authorized doctor/prescriber upon dispensing.
 */
public final class PrescriptionMedication extends Medication {

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
