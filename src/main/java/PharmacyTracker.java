import pharmacy.model.Customer;
import pharmacy.model.Medication;
import pharmacy.model.OtcMedication;
import pharmacy.model.PrescriptionMedication;
import pharmacy.model.RegisteredCustomer;
import pharmacy.model.WalkInCustomer;
import pharmacy.service.Pharmacy;

/**
 * Main application entry point for Akosua's Pharmacy Stock & Dispensing Tracker.
 *
 * <p><b>Why this exists:</b> Resides in the root (default) package to keep the original
 * README run commands (javac and mvn exec:java) functioning seamlessly without modification.
 *
 * <p><b>Scenario Executed:</b>
 * - Paracetamol 500mg initialized with 100 units at GHS 12.50 (OTC).
 * - Amoxicillin 250mg initialized with 40 units at GHS 30.00 (Prescription).
 * - Kofi Mensah (Walk-in) dispenses 2 Paracetamol -> GHS 25.0.
 * - Ama Owusu (Registered) dispenses 1 Amoxicillin (Dr. Boateng) -> 5% off -> GHS 28.5.
 * - Paracetamol restocked by 50 units -> 148 remaining.
 * - Kofi Mensah attempts to dispense 100 Amoxicillin (only 39 exist) -> REJECTED.
 * - End of day report printed with accurate stock (39 Amoxicillin) and 3 transactions.
 */
public class PharmacyTracker {

    public static void main(String[] args) {
        Pharmacy p = new Pharmacy("Akosua's Pharmacy");

        Medication paracetamol = new OtcMedication("Paracetamol 500mg", 12.50, 100);
        Medication amoxicillin = new PrescriptionMedication("Amoxicillin 250mg", 30.00, 40);
        p.addMedication(paracetamol);
        p.addMedication(amoxicillin);

        Customer kofi = new WalkInCustomer("Kofi Mensah");
        Customer ama = new RegisteredCustomer("Ama Owusu", "0244 000 000"); // registered patients have contact details on file
        p.addCustomer(kofi);
        p.addCustomer(ama);

        // A normal morning
        p.dispense(kofi, paracetamol, 2, null);
        p.dispense(ama, amoxicillin, 1, "Dr. Boateng"); // prescription-only
        p.restock(paracetamol, 50);

        // The sort of thing that keeps Akosua up at night (rejected by rebuilt invariant engine)
        p.dispense(kofi, amoxicillin, 100, "Dr. Boateng"); // far more than we have in stock

        p.printReport();
    }
}
