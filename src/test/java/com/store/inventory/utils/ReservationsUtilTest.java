package com.store.inventory.utils;

import com.store.inventory.api.Reservation;
import com.store.inventory.errors.ReservationNotRegisteredException;
import com.store.inventory.models.ReservationState;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ReservationsUtilTest {

    @Test
    void returnTrueWhenReservationIsNotConfirmedAndNotExpired() {
        var now = Instant.parse("2026-09-09T10:00:00Z");
        var expiresAt = now.plusSeconds(300);
        var reservation = new Reservation("ORDER-1", "SKU-1", 1, expiresAt);
        var state = ReservationState.builder().reservation(reservation).confirmed(false).build();

        assertTrue(ReservationsUtil.validateActive(state, now));
    }

    @Test
    void returnFalseWhenReservationIsConfirmed() {
        var now = Instant.parse("2026-09-09T10:00:00Z");
        var expiresAt = now.plusSeconds(300);
        var reservation = new Reservation("ORDER-1", "SKU-1", 1, expiresAt);
        var state = ReservationState.builder().reservation(reservation).confirmed(true).build();

        assertFalse(ReservationsUtil.validateActive(state, now));
    }

    @Test
    void returnFalseWhenReservationIsExpired() {
        var now = Instant.parse("2026-09-09T10:00:00Z");
        var expiresAt = now.minusSeconds(300);
        var reservation = new Reservation("ORDER-1", "SKU-1", 1, expiresAt);
        var state = ReservationState.builder()
            .reservation(reservation)
            .confirmed(false)
            .build();

        assertFalse(ReservationsUtil.validateActive(state, now));
    }

    @Test
    void notExceptionWhenReservationIsNotNull() {
        var orderId = "ORDER-1";
        var state = ReservationState.builder().build();

        assertDoesNotThrow(() -> ReservationsUtil.validateReservation(state, orderId));
    }

    @Test
    void exceptionWhenReservationIsNull() {
        var orderId = "ORDER-1";

        assertThrows(ReservationNotRegisteredException.class, () -> ReservationsUtil.validateReservation(null, orderId));
    }

    @Test
    void returnTrueWhenReservationIsNull() {
        var now = Instant.parse("2026-09-09T10:00:00Z");
        assertTrue(ReservationsUtil.validateIfReservationConfirmedOrExpired(null, now));
    }

    @Test
    void returnTrueWhenReservationIsConfirmed() {
        var now = Instant.parse("2026-09-09T10:00:00Z");
        var state = ReservationState.builder().confirmed(true).build();
        assertTrue(ReservationsUtil.validateIfReservationConfirmedOrExpired(state, now));
    }

    @Test
    void returnTrueWhenReservationIsExpired() {
        var now = Instant.parse("2026-09-09T10:00:00Z");
        var expiresAt = now.minusSeconds(300);
        var reservation = new Reservation("ORDER-1", "SKU-1", 1, expiresAt);
        var state = ReservationState.builder().reservation(reservation).confirmed(false).build();
        assertTrue(ReservationsUtil.validateIfReservationConfirmedOrExpired(state, now));
    }

    @Test
    void returnFalseWhenReservationIsActive() {
        var now = Instant.parse("2026-01-01T10:00:00Z");
        var expiresAt = now.plusSeconds(300);
        var reservation = new Reservation("ORDER-1", "SKU-1", 1, expiresAt);
        var state = ReservationState.builder().reservation(reservation).confirmed(false).build();

        assertFalse(ReservationsUtil.validateIfReservationConfirmedOrExpired(state, now));
    }

    @Test
    void returnFalseWhenValidateReservationNotConfirmed() {
        var state = ReservationState.builder().confirmed(true).build();

        assertFalse(ReservationsUtil.validateReservationNotConfirmed(state));
    }

    @Test
    void returnTrueWhenValidateReservationNotConfirmed() {
        var state = ReservationState.builder().confirmed(false).build();

        assertTrue(ReservationsUtil.validateReservationNotConfirmed(state));
    }
}
