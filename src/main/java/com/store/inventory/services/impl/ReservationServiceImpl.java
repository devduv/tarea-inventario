package com.store.inventory.services.impl;

import com.store.inventory.api.InsufficientStockException;
import com.store.inventory.api.OrderLimitExceededException;
import com.store.inventory.api.Reservation;
import com.store.inventory.errors.ReservationNotActiveException;
import com.store.inventory.models.CategoryRule;
import com.store.inventory.models.ProductState;
import com.store.inventory.models.ReservationState;
import com.store.inventory.repository.ProductCategoryRepository;
import com.store.inventory.repository.ProductStateRepository;
import com.store.inventory.repository.ReservationStateRepository;
import com.store.inventory.services.AlertService;
import com.store.inventory.services.AvailabilityService;
import com.store.inventory.services.ReservationService;
import com.store.inventory.utils.ProductUtil;
import com.store.inventory.utils.ReservationsUtil;
import com.store.inventory.utils.ValidationsUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Clock;
import java.time.Instant;

@Slf4j
@AllArgsConstructor
public class ReservationServiceImpl implements ReservationService {
    private final Clock clock;
    private final ProductStateRepository productStateRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ReservationStateRepository reservationStateRepository;
    private final AlertService alertService;
    private final AvailabilityService availabilityService;

    /**
     * Reserve quantity product and create reservation.
     *
     * @param orderId  order id
     * @param sku      sku
     * @param quantity quantity
     * @return reservation registered or new reservation
     */
    @Override
    public Reservation reserve(String orderId, String sku, int quantity) {
        log.info("Reserver for order {} of product SKU: {} for {} units", orderId, sku, quantity);
        ValidationsUtil.validateQuantity(quantity);

        ProductState productState = productStateRepository.getProduct(sku);
        ProductUtil.validateProduct(productState, sku);

        CategoryRule categoryRule = productCategoryRepository.getCategoryRule(productState.getProduct().getCategory());
        validateOrderLimitExceeded(categoryRule, sku, quantity);


        synchronized (productState) {
            ReservationState reservationState = reservationStateRepository.getReservation(orderId);
            if (reservationState != null) {
                return reservationState.getReservation();
            }

            int availableStock = availabilityService.calculateAvailableStock(productState);
            validateAvailableStock(sku, quantity, availableStock);

            return createReservation(orderId, productState, sku, quantity, categoryRule);
        }
    }

    /**
     * Create Reservation.
     *
     * @param orderId      order id
     * @param productState product state
     * @param sku          sku
     * @param quantity     quantity
     * @param categoryRule category rule
     * @return reservation
     */
    private Reservation createReservation(String orderId,
                                          ProductState productState,
                                          String sku,
                                          int quantity,
                                          CategoryRule categoryRule) {
        Instant now = clock.instant();
        Instant expiresAt = now.plus(categoryRule.getTimeToPay());
        Reservation reservation = new Reservation(orderId, sku, quantity, expiresAt);
        ReservationState newReservationState = ReservationState.builder().reservation(reservation).build();

        reservationStateRepository.addReservation(orderId, newReservationState);
        productStateRepository.updateAvailableOrder(orderId, productState);

        alertService.checkLowStockAlert(productState);

        return reservation;
    }

    /**
     * Confirm reservation, user has paid the order, stock is reduced and available orders are removed.
     *
     * @param orderId order id.
     */
    @Override
    public void confirm(String orderId) {
        log.info("Confirm payment for reservation of order: {}", orderId);
        ReservationState reservationState = reservationStateRepository.getReservation(orderId);
        ReservationsUtil.validateReservation(reservationState, orderId);

        ProductState productState = productStateRepository.getProduct(reservationState.getReservation().sku());

        synchronized (productState) {
            Instant now = clock.instant();
            validateActiveReservation(reservationState, now, orderId);

            if (!reservationState.isConfirmed()) {
                reservationState.setConfirmed(true);
                int totalStock = productState.getTotalStock();
                totalStock = totalStock - reservationState.getReservation().quantity();
                productStateRepository.deleteAvailableOrder(totalStock, orderId, productState);
            }
        }
    }

    /**
     * Validate order limit exceeded.
     *
     * @param categoryRule category rule
     * @param sku          product sku
     * @param quantity     product quantity
     */
    private void validateOrderLimitExceeded(CategoryRule categoryRule, String sku, int quantity) {
        if (categoryRule.getOrderLimit() != null && quantity > categoryRule.getOrderLimit()) {
            throw new OrderLimitExceededException(sku, quantity, categoryRule.getOrderLimit());
        }
    }

    /**
     * Validate available stock.
     *
     * @param sku       product sku
     * @param quantity  product quantity
     * @param available available quantity
     */
    private void validateAvailableStock(String sku, int quantity, int available) {
        if (quantity > available) {
            throw new InsufficientStockException(sku, quantity, available);
        }
    }

    /**
     * Validate active reservation.
     *
     * @param reservationState reservation state
     * @param now              now
     * @param orderId          order id
     */
    private void validateActiveReservation(ReservationState reservationState, Instant now, String orderId) {
        if (!ReservationsUtil.validateActive(reservationState, now) && !reservationState.isConfirmed()) {
            throw new ReservationNotActiveException(orderId);
        }
    }
}
