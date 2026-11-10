package pharmacy.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Legacy Customer model extracted to pharmacy.model in Step 1.
 * 
 * Engineering Note:
 * Previous Defect (Rank 5 & 7): Exposed mutable history list and string-typed customer kind.
 * In subsequent build steps, this will be refactored into a sealed hierarchy where discount logic
 * is owned by the customer type.
 */
public class Customer {
    public String name;
    public String kind; // "WALK_IN" or "REGISTERED"
    public String phone;
    public int id;
    public List<String> history = new ArrayList<>();
    public static int counter = 1;

    public Customer(String name, String kind) {
        this.id = counter++;
        this.name = name;
        this.kind = kind;
    }
}
