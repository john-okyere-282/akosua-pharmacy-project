package pharmacy.model;

/**
 * An over-the-counter (OTC) medication that anyone can purchase without a doctor's prescription.
 *
 * <p><b>Why this exists:</b> Separates general retail pharmaceuticals (e.g. pain relievers, vitamins)
 * from prescription medicines so that prescriber validation is unnecessary.
 *
 * <p><b>Real Scenario:</b>
 * Kofi Mensah buys 2 packets of Paracetamol 500mg (price 12.50). Because requiresPrescriber() is false,
 * dispensing succeeds immediately with a null prescriber for GHS 25.0.
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
