import pharmacy.model.Customer;
import pharmacy.model.Medication;
import pharmacy.model.OtcMedication;
import pharmacy.model.PrescriptionMedication;
import pharmacy.model.RegisteredCustomer;
import pharmacy.model.WalkInCustomer;
import pharmacy.service.Pharmacy;

/**
 * Akosua's Pharmacy - stock and dispensing tracker.
 * 
 * Entry point remaining in the default package so standard javac and Maven run
 * commands continue to work without reconfiguration. All domain logic is cleanly
 * delegated to pharmacy.service and pharmacy.model.
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

        // a normal morning
        p.dispense(kofi, paracetamol, 2, null);
        p.dispense(ama, amoxicillin, 1, "Dr. Boateng"); // prescription-only
        p.restock(paracetamol, 50);

        // ... and the sort of thing that keeps Akosua up at night:
        p.dispense(kofi, amoxicillin, 100, "Dr. Boateng"); // far more than we have in stock

        p.printReport();
    }
}
