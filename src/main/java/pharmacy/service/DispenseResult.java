package pharmacy.service;

/**
 * Sealed domain result representing the outcome of a dispense operation.
 * 
 * Engineering Decision (ADR-005):
 * Decouples domain logic from console I/O and exception flows.
 */
public sealed interface DispenseResult permits DispenseSuccess, DispenseRejected {
    boolean isSuccess();
}
