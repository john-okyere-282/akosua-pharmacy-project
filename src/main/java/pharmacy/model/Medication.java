package pharmacy.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Legacy Medication model extracted to pharmacy.model in Step 1.
 * 
 * Engineering Note:
 * Previous Defect (Rank 1): public int stock was completely unguarded and mutable by any caller.
 * In subsequent build steps, this will be replaced by an immutable StockLevel and a sealed hierarchy.
 */
public class Medication {
    public String name;
    public double price;
    public int stock;
    public String category; // "OTC" or "PRESCRIPTION"
    public int id;
    public static int counter = 1;

    public Medication(String name, double price, int stock, String category) {
        this.id = counter++;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.category = category;
    }
}
