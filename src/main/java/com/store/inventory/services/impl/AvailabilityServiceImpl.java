package com.store.inventory.services.impl;

import com.store.inventory.models.ProductState;
import com.store.inventory.models.ReservationState;
import com.store.inventory.repository.ReservationStateRepository;
import com.store.inventory.services.AvailabilityService;
import com.store.inventory.utils.ReservationsUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Clock;
import java.time.Instant;
import java.util.Iterator;

@Slf4j
@AllArgsConstructor
public class AvailabilityServiceImpl implements AvailabilityService {
    private final Clock clock;
    private final ReservationStateRepository reservationStateRepository;

    /**
     * Calculate available stock of product.
     *
     * @param productState product state.
     * @return available stock quantity
     */
    @Override
    public synchronized int calculateAvailableStock(ProductState productState) {
        log.info("Calculating available stock for product sku: {}", productState.getProduct().getSku());
        Instant now = clock.instant();
        int activeReserations = 0;

        Iterator<String> availableOrders = productState.getAvailableOrders().iterator();
        while (availableOrders.hasNext()) {
            String orderId = availableOrders.next();

            ReservationState reservationState = reservationStateRepository.getReservation(orderId);

            if (ReservationsUtil.validateIfReservationConfirmedOrExpired(reservationState, now)) {
                availableOrders.remove();

                if (ReservationsUtil.validateReservationNotConfirmed(reservationState)) {
                    reservationStateRepository.removeReservation(orderId);
                }
            } else {
                activeReserations = activeReserations + reservationState.getReservation().quantity();
            }
        }

        return productState.getTotalStock() - activeReserations;
    }

}
