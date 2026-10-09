package com.store.inventory.repository.impl;

import com.store.inventory.models.ReservationState;
import com.store.inventory.repository.ReservationStateRepository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReservationStateRepositoryImpl implements ReservationStateRepository {
    private final Map<String, ReservationState> reservations = new ConcurrentHashMap<>();

    @Override
    public ReservationState getReservation(String orderId) {
        return reservations.get(orderId);
    }

    @Override
    public void removeReservation(String orderId) {
        reservations.remove(orderId);
    }

    @Override
    public void addReservation(String orderId, ReservationState reservationState) {
        reservations.put(orderId, reservationState);
    }
}
