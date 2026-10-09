package com.store.inventory.utils;

import com.store.inventory.errors.ReservationNotRegisteredException;
import com.store.inventory.models.ReservationState;

import java.time.Instant;

public class ReservationsUtil {

    /**
     * Validate if reservation is active for Instant time.
     *
     * @param reservationState reservation state
     * @param now              now
     * @return true/false
     */
    public static boolean validateActive(ReservationState reservationState, Instant now) {
        return !reservationState.isConfirmed() && now.isBefore(reservationState.getReservation().expiresAt());
    }

    /**
     * Validate Reservation.
     *
     * @param reservationState reservation state
     * @param orderId          order id9
     */
    public static void validateReservation(ReservationState reservationState, String orderId) {
        if (reservationState == null) {
            throw new ReservationNotRegisteredException(orderId);
        }
    }

    /**
     * Validate if reservation state is null or is confirmed or is not active.
     *
     * @param reservationState reservation state
     * @param now              now
     * @return true/false
     */
    public static boolean validateIfReservationConfirmedOrExpired(ReservationState reservationState, Instant now) {
        return reservationState == null || reservationState.isConfirmed()
            || !ReservationsUtil.validateActive(reservationState, now);
    }

    /**
     * Validate if reservation state is not null and is not confirmed.
     *
     * @param reservationState reservation state
     * @return true/false
     */
    public static boolean validateReservationNotConfirmed(ReservationState reservationState) {
        return reservationState != null && !reservationState.isConfirmed();
    }

}
