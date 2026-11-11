# System Architecture: Akosua's Pharmacy

This document explains the architecture of the rebuilt pharmacy management system using visual diagrams and plain-language descriptions.

---

## 1. Complete Class Diagram of the Final Design

This diagram represents the actual production and test classes in the system.

```mermaid
classDiagram
    class StockLevel {
        -int units
        +StockLevel(int units)
        +units() int
        +hasSufficient(int qty) boolean
        +deduct(int qty) StockLevel
        +add(int qty) StockLevel
    }

    class Money {
        -double amount
        +Money(double amount)
        +amount() double
        +multiply(int qty) Money
        +applyDiscount(double multiplier) Money
        +toString() String
    }

    class Medication {
        <<abstract>>
        -int id
        -String name
        -Money price
        -StockLevel stock
        +Medication(int id, String name, Money price, StockLevel initialStock)
        +id() int
        +name() String
        +price() Money
        +currentStock() int
        +hasSufficientStock(int qty) boolean
        +deductStock(int qty) void
        +addStock(int qty) void
        +categoryName()* String
        +requiresPrescriber()* boolean
    }

    class OtcMedication {
        +categoryName() String
        +requiresPrescriber() boolean
    }

    class PrescriptionMedication {
        +categoryName() String
        +requiresPrescriber() boolean
    }

    class ControlledMedication {
        <<extension>>
        +categoryName() String
        +requiresPrescriber() boolean
    }

    Medication <|-- OtcMedication
    Medication <|-- PrescriptionMedication
    PrescriptionMedication <|-- ControlledMedication
    Medication *-- StockLevel
    Medication *-- Money

    class Customer {
        <<abstract>>
        -int id
        -String name
        -List~String~ history
        +Customer(int id, String name)
        +id() int
        +name() String
        +history() List~String~
        +recordHistory(String entry) void
        +kind()* String
        +discountMultiplier()* double
    }

    class WalkInCustomer {
        +kind() String
        +discountMultiplier() double
    }

    class RegisteredCustomer {
        -String phone
        +RegisteredCustomer(int id, String name, String phone)
        +phone() String
        +kind() String
        +discountMultiplier() double
    }

    Customer <|-- WalkInCustomer
    Customer <|-- RegisteredCustomer

    class Transaction {
        <<record>>
        +int id
        +String customerName
        +String medName
        +int qty
        +double total
        +String type
        +String prescriber
    }

    class DispenseResult {
        <<interface>>
        +isSuccess()* boolean
    }

    class DispenseSuccess {
        <<record>>
        +Customer customer
        +Medication medication
        +int qty
        +String prescriber
        +Money total
        +isSuccess() boolean
    }

    class DispenseRejected {
        <<record>>
        +String reason
        +isSuccess() boolean
    }

    DispenseResult <|-- DispenseSuccess
    DispenseResult <|-- DispenseRejected

    class DispenseReporter {
        +reportDispense(DispenseResult result) void
        +reportRestock(Medication m, int qty) void
        +reportEndOfDay(String pharmacyName, List~Medication~ meds, int txCount) void
    }

    class Pharmacy {
        -String name
        -List~Medication~ meds
        -List~Customer~ customers
        -List~Transaction~ transactions
        -DispenseReporter reporter
        -int nextTransactionId
        +Pharmacy(String name)
        +Pharmacy(String name, DispenseReporter reporter)
        +addMedication(Medication m) void
        +addCustomer(Customer c) void
        +dispense(Customer c, Medication m, int qty, String prescriber) DispenseResult
        +restock(Medication m, int qty) void
        +getTransactions() List~Transaction~
        +getMedications() List~Medication~
        +getCustomers() List~Customer~
        +printReport() void
    }

    class PharmacyTracker {
        +main(String[] args)$ void
    }

    Pharmacy o-- Medication
    Pharmacy o-- Customer
    Pharmacy o-- Transaction
    Pharmacy --> DispenseReporter
    PharmacyTracker ..> Pharmacy : boots and coordinates
```

### Explanation:
- `StockLevel` and `Money` are immutable value objects ensuring safe arithmetic and output preservation.
- `Medication` is an open abstract class, allowing categories like `ControlledMedication` to plug in with zero changes to production code.
- `Customer` and `DispenseResult` are sealed hierarchies because customer discount tiers and operation outcomes represent strictly closed domain sets.
- `Pharmacy` acts as the aggregate root coordinator, returning only unmodifiable views.

---

## 2. Package Dependency Diagram

```mermaid
flowchart TD
    subgraph Root["Default Package"]
        Tracker["PharmacyTracker.java"]
    end

    subgraph Service["pharmacy.service"]
        Pharmacy["Pharmacy.java"]
        DResult["DispenseResult.java"]
        DSuccess["DispenseSuccess.java"]
        DRejected["DispenseRejected.java"]
    end

    subgraph Model["pharmacy.model"]
        Med["Medication.java"]
        OTC["OtcMedication.java"]
        Rx["PrescriptionMedication.java"]
        Cust["Customer.java"]
        WalkIn["WalkInCustomer.java"]
        Reg["RegisteredCustomer.java"]
        Stock["StockLevel.java"]
        Mon["Money.java"]
        Tx["Transaction.java"]
    end

    subgraph Report["pharmacy.report"]
        Reporter["DispenseReporter.java"]
    end

    Tracker --> Pharmacy
    Tracker --> Med
    Pharmacy --> Med
    Pharmacy --> Cust
    Pharmacy --> Tx
    Pharmacy --> Reporter
    Pharmacy --> DResult
    Reporter --> Med
    Reporter --> DResult
```

### Plain Explanation:
- `PharmacyTracker` stays in the root default package so terminal commands (`javac` and `mvn exec:java`) remain identical to the lab instructions.
- `pharmacy.service` depends on `pharmacy.model` and `pharmacy.report`, but `pharmacy.model` depends on nothing outside itself. There are no circular dependencies.

---

## 3. Sequence Diagram: Kofi Dispenses Paracetamol (OTC Path)

This trace illustrates what happens when Kofi (walk-in) buys 2 packets of Paracetamol at GHS 12.50.

```mermaid
sequenceDiagram
    autonumber
    actor Kofi as Kofi (Walk-in)
    participant Tracker as PharmacyTracker
    participant Pharm as Pharmacy
    participant Med as Paracetamol (OtcMedication)
    participant Stock as StockLevel (100)
    participant Cust as Kofi (WalkInCustomer)
    participant Rep as DispenseReporter

    Tracker->>Pharm: dispense(kofi, paracetamol, 2, null)
    Pharm->>Med: requiresPrescriber()
    Med-->>Pharm: false (OTC does not need doctor)
    Pharm->>Med: hasSufficientStock(2)
    Med-->>Pharm: true (100 >= 2)
    Pharm->>Med: price()
    Med-->>Pharm: Money(12.50)
    Pharm->>Cust: discountMultiplier()
    Cust-->>Pharm: 1.0 (no discount)
    Pharm->>Med: deductStock(2)
    Med->>Stock: deduct(2)
    Stock-->>Med: StockLevel(98)
    Pharm->>Cust: recordHistory("Got 2 x Paracetamol 500mg")
    Pharm->>Pharm: record Transaction #1 (total GHS 25.0)
    Pharm->>Rep: reportDispense(DispenseSuccess)
    Rep-->>Tracker: stdout: "Dispensed 2 x Paracetamol 500mg to Kofi Mensah for GHS 25.0"
```

---

## 4. Sequence Diagram: Prescription Dispensing with Rejections

This trace shows both happy and unhappy paths when dispensing prescription medication.

```mermaid
sequenceDiagram
    autonumber
    participant Client as Caller
    participant Pharm as Pharmacy
    participant Med as Amoxicillin 250mg
    participant Rep as DispenseReporter

    Note over Client,Pharm: Case 1: Missing Prescriber
    Client->>Pharm: dispense(kofi, amoxicillin, 1, null)
    Pharm->>Med: requiresPrescriber()
    Med-->>Pharm: true
    Pharm->>Rep: reportDispense(DispenseRejected: "without a prescriber")
    Note over Pharm: Return early; stock & ledger UNTOUCHED

    Note over Client,Pharm: Case 2: Negative Quantity
    Client->>Pharm: dispense(kofi, amoxicillin, -5, "Dr. Boateng")
    Pharm->>Rep: reportDispense(DispenseRejected: "quantity must be positive")
    Note over Pharm: Return early; stock & ledger UNTOUCHED

    Note over Client,Pharm: Case 3: Over-dispensing (Stock=39, Request=100)
    Client->>Pharm: dispense(kofi, amoxicillin, 100, "Dr. Boateng")
    Pharm->>Med: requiresPrescriber()
    Med-->>Pharm: true (valid doctor)
    Pharm->>Med: hasSufficientStock(100)
    Med-->>Pharm: false (only 39 in stock)
    Pharm->>Rep: reportDispense(DispenseRejected: "only 39 in stock, requested 100")
    Note over Pharm: Stock STAYS 39; NO history; NO transaction
```

---

## 5. Flowchart: The Three-Step Validation Order

The business requires validation checks to occur in a specific, deterministic order before any state mutation can take place.

```mermaid
flowchart TD
    Start(["Call: dispense(customer, med, qty, prescriber)"]) --> CheckPrescriber{"Is prescriber required<br/>AND prescriber == null?"}
    
    CheckPrescriber -->|Yes| RejectPrescriber["Reject: 'Cannot dispense med without a prescriber'<br/>(Leave stock untouched; record no transaction)"]
    CheckPrescriber -->|No| CheckQty{"Is qty <= 0?"}
    
    CheckQty -->|Yes| RejectQty["Reject: 'Cannot dispense med: quantity must be positive'<br/>(Leave stock untouched; record no transaction)"]
    CheckQty -->|No| CheckStock{"Does med have sufficient stock?<br/>(currentStock >= qty)"}
    
    CheckStock -->|No| RejectStock["Reject: 'Cannot dispense med: only N in stock, requested qty'<br/>(Leave stock untouched; record no transaction)"]
    CheckStock -->|Yes| ApplyDeduction["1. Calculate total with customer discount<br/>2. Deduct stock in ONE place: med.deductStock(qty)<br/>3. Record customer history<br/>4. Append immutable Transaction record<br/>5. Report DispenseSuccess"]

    RejectPrescriber --> Done(["End Operation"])
    RejectQty --> Done
    RejectStock --> Done
    ApplyDeduction --> Done
```

---

## 6. "Adding CONTROLLED" Extensibility Diagram

When Akosua's pharmacy adds regulated controlled substances, here is exactly what changes:

```mermaid
flowchart LR
    subgraph NewCode["1 New File Added"]
        Controlled["pharmacy.model.ControlledMedication.java<br/>(Extends PrescriptionMedication)"]
    end

    subgraph UntouchedCode["Existing Files UNTOUCHED (0 edits)"]
        Pharm["Pharmacy.java (0 lines)"]
        Med["Medication.java (0 lines)"]
        OTC["OtcMedication.java (0 lines)"]
        Rx["PrescriptionMedication.java (0 lines)"]
        Cust["Customer.java (0 lines)"]
        Tx["Transaction.java (0 lines)"]
        Stock["StockLevel.java (0 lines)"]
        Rep["DispenseReporter.java (0 lines)"]
    end

    Controlled -.->|Inherits prescriber rule| Rx
    Controlled -->|Works out-of-the-box with| Pharm
```

---

## 7. Old Design vs. New Design Comparison

```mermaid
flowchart TD
    subgraph Old["Old Intern Code (Single Fragile God Class)"]
        OldGod["PharmacyTracker.java<br/>- Public mutable fields (stock, history, phone)<br/>- String category matching with if-else chains<br/>- Amoxicillin stock decrements to -61<br/>- Leaked internal transaction list<br/>- Static counters"]
    end

    subgraph New["New Object-Oriented Architecture"]
        NewVal["Immutable Value Objects<br/>(StockLevel, Money)"]
        NewPoly["Open/Closed Domain Models<br/>(Medication, Customer)"]
        NewResult["Sealed Result Hierarchy<br/>(DispenseResult)"]
        NewCoord["Encapsulated Coordinator<br/>(Pharmacy in pharmacy.service)"]
        NewRep["Decoupled Console Reporter<br/>(DispenseReporter)"]
    end

    OldGod -.->|Refactored Into| NewCoord
```
