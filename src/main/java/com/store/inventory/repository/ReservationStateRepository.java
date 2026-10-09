package com.store.inventory.repository;

import com.store.inventory.models.ReservationState;

public interface ReservationStateRepository {
    ReservationState getReservation(String orderId);

    void removeReservation(String orderId);

    void addReservation(String orderId, ReservationState reservationState);
}
