package pharmacy.model;

/**
 * Over-the-counter (OTC) medication.
 * Does not require a doctor's prescription.
 */
public final class OtcMedication extends Medication {

    public OtcMedication(int id, String name, Money price, int initialStock) {
        super(id, name, price, initialStock);
    }

    public OtcMedication(String name, double price, int initialStock) {
        super(1, name, Money.of(price), initialStock);
    }

    @Override
    public String categoryName() {
        return "OTC";
    }

    @Override
    public boolean requiresPrescriber() {
        return false;
    }
}
