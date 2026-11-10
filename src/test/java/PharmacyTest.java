import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pharmacy.model.ControlledMedication;
import pharmacy.model.Customer;
import pharmacy.model.Medication;
import pharmacy.model.Money;
import pharmacy.model.OtcMedication;
import pharmacy.model.PrescriptionMedication;
import pharmacy.model.RegisteredCustomer;
import pharmacy.model.StockLevel;
import pharmacy.model.Transaction;
import pharmacy.model.WalkInCustomer;
import pharmacy.service.DispenseRejected;
import pharmacy.service.DispenseResult;
import pharmacy.service.DispenseSuccess;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive verification test suite for Akosua's Pharmacy.
 * Covers totals, discounts, invariants, encapsulation, and open-closed extensibility.
 */
class PharmacyTest {

    private Pharmacy pharmacy;
    private Medication paracetamol;
    private Medication amoxicillin;
    private Customer kofi;
    private Customer ama;

    @BeforeEach
    void setUp() {
        pharmacy = new Pharmacy("Akosua's Pharmacy (Test)");
        paracetamol = new OtcMedication("Paracetamol 500mg", 12.50, 100);
        amoxicillin = new PrescriptionMedication("Amoxicillin 250mg", 30.00, 40);

        pharmacy.addMedication(paracetamol);
        pharmacy.addMedication(amoxicillin);

        kofi = new WalkInCustomer("Kofi Mensah");
        ama = new RegisteredCustomer("Ama Owusu", "0244 000 000");

        pharmacy.addCustomer(kofi);
        pharmacy.addCustomer(ama);
    }

    @Test
    @DisplayName("1. Characterization: Totals calculation and 5% registered discount")
    void testTotalsAndDiscount() {
        // Walk-in customer pays full price (12.50 * 2 = 25.0)
        DispenseResult r1 = pharmacy.dispense(kofi, paracetamol, 2, null);
        assertInstanceOf(DispenseSuccess.class, r1);
        DispenseSuccess s1 = (DispenseSuccess) r1;
        assertEquals(25.0, s1.total().amount(), 0.0001);
        assertEquals("25.0", s1.total().toString());

        // Registered customer gets 5% discount (30.00 * 1 * 0.95 = 28.5)
        DispenseResult r2 = pharmacy.dispense(ama, amoxicillin, 1, "Dr. Boateng");
        assertInstanceOf(DispenseSuccess.class, r2);
        DispenseSuccess s2 = (DispenseSuccess) r2;
        assertEquals(28.5, s2.total().amount(), 0.0001);
        assertEquals("28.5", s2.total().toString());
    }

    @Test
    @DisplayName("2. Missing prescriber on prescription medicine is rejected cleanly")
    void testMissingPrescriberRejected() {
        int initialStock = amoxicillin.currentStock();
        int initialTxCount = pharmacy.getTransactions().size();

        DispenseResult result = pharmacy.dispense(kofi, amoxicillin, 1, null);
        assertInstanceOf(DispenseRejected.class, result);
        DispenseRejected rejected = (DispenseRejected) result;

        assertEquals("Cannot dispense Amoxicillin 250mg without a prescriber", rejected.reason());
        assertEquals(initialStock, amoxicillin.currentStock(), "Stock must not be decremented on rejection");
        assertEquals(initialTxCount, pharmacy.getTransactions().size(), "No transaction recorded on rejection");
        assertTrue(kofi.history().isEmpty(), "No history recorded on rejection");
    }

    @Test
    @DisplayName("3. Restock increases stock and creates supplier transaction")
    void testRestock() {
        int initialStock = paracetamol.currentStock();
        pharmacy.restock(paracetamol, 50);

        assertEquals(initialStock + 50, paracetamol.currentStock());
        List<Transaction> txs = pharmacy.getTransactions();
        assertEquals(1, txs.size());
        Transaction tx = txs.getFirst();
        assertEquals("(supplier)", tx.customerName());
        assertEquals("Paracetamol 500mg", tx.medName());
        assertEquals(50, tx.qty());
        assertEquals(0.0, tx.total(), 0.0001);
        assertEquals("RESTOCK", tx.type());
        assertNull(tx.prescriber());
    }

    @Test
    @DisplayName("4. Over-dispense is rejected with stock unchanged and no transaction")
    void testOverDispenseRejected() {
        // Amoxicillin has 40 units. Request 100 units.
        DispenseResult result = pharmacy.dispense(kofi, amoxicillin, 100, "Dr. Boateng");
        assertInstanceOf(DispenseRejected.class, result);
        DispenseRejected rejected = (DispenseRejected) result;

        assertEquals("Cannot dispense Amoxicillin 250mg: only 40 in stock, requested 100", rejected.reason());
        assertEquals(40, amoxicillin.currentStock(), "Stock must remain exactly at 40");
        assertTrue(pharmacy.getTransactions().isEmpty(), "No transaction logged");
        assertTrue(kofi.history().isEmpty(), "Customer history remains empty");
    }

    @Test
    @DisplayName("5. Non-positive quantities (<= 0) are rejected for dispense and restock")
    void testNonPositiveQuantitiesRejected() {
        DispenseResult r0 = pharmacy.dispense(kofi, paracetamol, 0, null);
        assertInstanceOf(DispenseRejected.class, r0);
        assertEquals("Cannot dispense Paracetamol 500mg: quantity must be positive", ((DispenseRejected) r0).reason());

        DispenseResult rNeg = pharmacy.dispense(kofi, paracetamol, -5, null);
        assertInstanceOf(DispenseRejected.class, rNeg);
        assertEquals("Cannot dispense Paracetamol 500mg: quantity must be positive", ((DispenseRejected) rNeg).reason());

        assertEquals(100, paracetamol.currentStock(), "Stock unchanged after invalid dispense");

        // Restock <= 0
        pharmacy.restock(paracetamol, 0);
        pharmacy.restock(paracetamol, -10);
        assertEquals(100, paracetamol.currentStock(), "Stock unchanged after invalid restock");
        assertTrue(pharmacy.getTransactions().isEmpty());
    }

    @Test
    @DisplayName("6. Invariant verification: Stock never falls below zero across random operations")
    void testStockNeverNegativeUnderRandomOperations() {
        Random random = new Random(42);
        for (int i = 0; i < 100; i++) {
            int qty = random.nextInt(250) - 50; // Random quantity between -50 and 199
            boolean isDispense = random.nextBoolean();

            if (isDispense) {
                pharmacy.dispense(kofi, paracetamol, qty, null);
            } else {
                pharmacy.restock(paracetamol, qty);
            }

            assertTrue(paracetamol.currentStock() >= 0,
                    "Stock must NEVER fall below zero! Current: " + paracetamol.currentStock());
        }
    }

    @Test
    @DisplayName("7. Encapsulation: Leaked collections cannot be modified externally")
    void testEncapsulationAndImmutability() {
        pharmacy.dispense(kofi, paracetamol, 2, null);

        List<Transaction> transactions = pharmacy.getTransactions();
        assertThrows(UnsupportedOperationException.class, transactions::clear,
                "getTransactions() must return an unmodifiable list");

        List<Medication> meds = pharmacy.getMedications();
        assertThrows(UnsupportedOperationException.class, meds::clear,
                "getMedications() must return an unmodifiable list");

        List<Customer> customers = pharmacy.getCustomers();
        assertThrows(UnsupportedOperationException.class, customers::clear,
                "getCustomers() must return an unmodifiable list");

        List<String> history = kofi.history();
        assertThrows(UnsupportedOperationException.class, () -> history.add("Unauthorized entry"),
                "customer.history() must return an unmodifiable list");
    }

    @Test
    @DisplayName("8. Discount belongs to the Customer type")
    void testDiscountOwnedByCustomerType() {
        assertEquals(1.0, kofi.discountMultiplier(), 0.0001);
        assertEquals(0.95, ama.discountMultiplier(), 0.0001);

        assertThrows(NullPointerException.class, () -> new RegisteredCustomer("Bad Customer", null),
                "Registered customer must require a valid phone number");
    }

    @Test
    @DisplayName("9. Extensibility: Add CONTROLLED category with ZERO edits to existing classes")
    void testControlledMedicationExtensionWithoutEditingExistingClasses() {
        Medication morphine = new ControlledMedication("Morphine 10mg", 50.00, 20);
        pharmacy.addMedication(morphine);

        assertEquals("CONTROLLED", morphine.categoryName());
        assertTrue(morphine.requiresPrescriber());

        // Reject without prescriber
        DispenseResult rejected = pharmacy.dispense(ama, morphine, 2, null);
        assertInstanceOf(DispenseRejected.class, rejected);
        assertEquals("Cannot dispense Morphine 10mg without a prescriber", ((DispenseRejected) rejected).reason());
        assertEquals(20, morphine.currentStock());

        // Dispense successfully with prescriber
        DispenseResult success = pharmacy.dispense(ama, morphine, 2, "Dr. Boateng");
        assertInstanceOf(DispenseSuccess.class, success);
        assertEquals(18, morphine.currentStock());

        // Total: 50.00 * 2 = 100.0 * 0.95 = 95.0
        assertEquals(95.0, ((DispenseSuccess) success).total().amount(), 0.0001);

        // Transaction records prescriber
        Transaction tx = pharmacy.getTransactions().getLast();
        assertEquals("Dr. Boateng", tx.prescriber());
        assertEquals("Morphine 10mg", tx.medName());
    }
}
