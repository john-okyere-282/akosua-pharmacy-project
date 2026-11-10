package pharmacy.model;

/**
 * Test-scope Controlled Medication category.
 * 
 * Demonstrates Open-Closed Principle extensibility:
 * This new category is added in test scope and seamlessly works with Pharmacy,
 * requiring ZERO changes to any production classes in src/main.
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
