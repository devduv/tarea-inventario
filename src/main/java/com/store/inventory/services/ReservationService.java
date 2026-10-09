package com.store.inventory.services;

import com.store.inventory.api.Reservation;

public interface ReservationService {
    Reservation reserve(String orderId, String sku, int quantity);

    void confirm(String orderId);
}
