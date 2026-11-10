package pharmacy.model;

/**
 * Over-the-counter (OTC) medication.
 * General retail medicines (e.g. Paracetamol) that do not require a doctor's prescription.
 */
public class OtcMedication extends Medication {

    public OtcMedication(int id, String name, Money price, StockLevel initialStock) {
        super(id, name, price, initialStock);
    }

    public OtcMedication(String name, double price, int initialStock) {
        super(1, name, Money.of(price), StockLevel.of(initialStock));
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
