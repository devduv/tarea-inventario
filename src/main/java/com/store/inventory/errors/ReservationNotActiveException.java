package com.store.inventory.errors;

public class ReservationNotActiveException extends IllegalStateException {

    public ReservationNotActiveException(String orderId) {
        super("Order: " + orderId + " is not active because reservation time has expired");
    }
}
