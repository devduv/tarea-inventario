package com.store.inventory.services.impl;

import com.store.inventory.api.Reservation;
import com.store.inventory.models.Product;
import com.store.inventory.models.ProductState;
import com.store.inventory.models.ReservationState;
import com.store.inventory.repository.ReservationStateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AvailabilityServiceImplTest {

    @Mock
    private Clock clock;

    @Mock
    private ReservationStateRepository reservationStateRepository;

    @InjectMocks
    private AvailabilityServiceImpl availabilityService;

    @Test
    void returnAvailableStockWhenReservationIsActiveAndNotConfirmed() {
        var orderId = "ORDER-1";
        var now = Instant.parse("2026-01-01T10:00:00Z");
        var expiresAt = Instant.parse("2026-01-01T10:15:00Z");

        var activeOrders = new HashSet<String>();
        activeOrders.add(orderId);

        var productState = ProductState.builder()
            .product(Product.builder().sku("SKU-1").build())
            .totalStock(10).availableOrders(activeOrders).build();

        var reservation = new Reservation(orderId, "SKU-1", 3, expiresAt);

        var reservationState = ReservationState.builder().reservation(reservation).confirmed(false).build();

        when(clock.instant()).thenReturn(now);
        when(reservationStateRepository.getReservation(anyString())).thenReturn(reservationState);

        var available = availabilityService.calculateAvailableStock(productState);

        assertEquals(7, available);
    }

    @Test
    void returnAvailableStocksAndRemoveOrderWhenReservationIsNull() {
        var orderId = "ORDER-1";
        var now = Instant.parse("2026-09-09T10:00:00Z");

        var activeOrders = new HashSet<String>();
        activeOrders.add(orderId);
        var productState = ProductState.builder()
            .product(Product.builder().sku("SKU-1").build())
            .totalStock(10)
            .availableOrders(activeOrders)
            .build();

        when(clock.instant()).thenReturn(now);
        when(reservationStateRepository.getReservation(orderId)).thenReturn(null);

        var available = availabilityService.calculateAvailableStock(productState);

        assertEquals(10, available);
        assertTrue(productState.getAvailableOrders().isEmpty());
        verify(reservationStateRepository, never()).removeReservation(orderId);
    }

    @Test
    void returnAvailableStocksAndRemoveOrderWhenReservationIsConfirmed() {
        var orderId = "ORDER-1";
        var now = Instant.parse("2026-09-09T10:00:00Z");

        var activeOrders = new HashSet<String>();
        activeOrders.add(orderId);

        var productState = ProductState.builder()
            .product(Product.builder().sku("SKU-1").build())
            .totalStock(10).availableOrders(activeOrders).build();

        var reservation = new Reservation(orderId, "SKU-1", 3, now.plusSeconds(300));
        var reservationState = ReservationState.builder().reservation(reservation).confirmed(true).build();

        when(clock.instant()).thenReturn(now);
        when(reservationStateRepository.getReservation(anyString())).thenReturn(reservationState);

        var available = availabilityService.calculateAvailableStock(productState);

        assertEquals(10, available);
        assertTrue(productState.getAvailableOrders().isEmpty());
        verify(reservationStateRepository, never()).removeReservation(orderId);
    }

    @Test
    void returnAvailableStocksAndRemoveAndDeleteReservationWhenExpired() {
        var orderId = "ORDER-1";
        var now = Instant.parse("2026-09-09T10:00:00Z");
        var expiresAt = Instant.parse("2026-09-09T09:00:00Z");

        var activeOrders = new HashSet<String>();
        activeOrders.add(orderId);
        var productState = ProductState.builder()
            .product(Product.builder().sku("SKU-1").build())
            .totalStock(10).availableOrders(activeOrders).build();

        var reservation = new Reservation(orderId, "SKU-1", 3, expiresAt);
        var reservationState = ReservationState.builder().reservation(reservation).confirmed(false).build();

        when(clock.instant()).thenReturn(now);
        when(reservationStateRepository.getReservation(anyString())).thenReturn(reservationState);

        var available = availabilityService.calculateAvailableStock(productState);

        assertEquals(10, available);
        assertTrue(productState.getAvailableOrders().isEmpty());
        verify(reservationStateRepository).removeReservation(orderId);
    }
}
