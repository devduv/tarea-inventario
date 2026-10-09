package com.store.inventory.repository.impl;

import com.store.inventory.models.ReservationState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReservationStateRepositoryImplTest {

    @InjectMocks
    private ReservationStateRepositoryImpl repository;

    @Test
    void addReservationIsOk() {
        var orderId = "ORDER-1";
        var reservationState = ReservationState.builder().build();

        assertDoesNotThrow(() -> repository.addReservation(orderId, reservationState));

    }

    @Test
    void returnReservationWhenExist() {
        var orderId = "ORDER-1";
        var reservationState = ReservationState.builder().build();

        repository.addReservation(orderId, reservationState);

        var retrievedReservation = repository.getReservation(orderId);

        assertEquals(reservationState, retrievedReservation);
    }

    @Test
    void removeReservationIsOk() {
        var orderId = "ORDER-1";
        var reservationState = ReservationState.builder().build();
        repository.addReservation(orderId, reservationState);

        repository.removeReservation(orderId);

        var retrievedReservation = repository.getReservation(orderId);

        assertNull(retrievedReservation);
    }

    @Test
    void returnNullWhenReservationNotExist() {
        var orderId = "ORDER-404";

        var retrievedReservation = repository.getReservation(orderId);

        assertNull(retrievedReservation);
    }
}
