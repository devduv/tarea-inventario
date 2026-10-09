package com.store.inventory.errors;

public class ReservationNotRegisteredException extends RuntimeException {

    public ReservationNotRegisteredException(String orderId) {
        super("Reservation with order id: " + orderId + " not registered");
    }
}
