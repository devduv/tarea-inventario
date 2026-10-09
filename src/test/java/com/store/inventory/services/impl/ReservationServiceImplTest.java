package com.store.inventory.services.impl;

import com.store.inventory.api.InsufficientStockException;
import com.store.inventory.api.OrderLimitExceededException;
import com.store.inventory.api.ProductCategory;
import com.store.inventory.api.Reservation;
import com.store.inventory.errors.EmptyQuantityStockException;
import com.store.inventory.errors.ReservationNotActiveException;
import com.store.inventory.models.CategoryRule;
import com.store.inventory.models.Product;
import com.store.inventory.models.ProductState;
import com.store.inventory.models.ReservationState;
import com.store.inventory.repository.ProductCategoryRepository;
import com.store.inventory.repository.ProductStateRepository;
import com.store.inventory.repository.ReservationStateRepository;
import com.store.inventory.services.AlertService;
import com.store.inventory.services.AvailabilityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceImplTest {

    @Mock
    private Clock clock;

    @Mock
    private ProductStateRepository productStateRepository;

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @Mock
    private ReservationStateRepository reservationStateRepository;

    @Mock
    private AlertService alertService;

    @Mock
    private AvailabilityService availabilityService;

    @InjectMocks
    private ReservationServiceImpl reservationService;

    @Test
    void reserveStockIsOk() {
        var sku = "SKU-123";
        var orderId = "ORDER-1";
        var quantity = 2;
        var now = Instant.parse("2026-09-09T10:00:00Z");

        var product = mock(Product.class);
        when(product.getCategory()).thenReturn(ProductCategory.STANDARD);

        var activeOrders = new HashSet<String>();
        var productState = ProductState.builder().product(product).availableOrders(activeOrders).build();

        var categoryRule = new CategoryRule(Duration.ofMinutes(15), null);

        when(productStateRepository.getProduct(anyString())).thenReturn(productState);
        when(productCategoryRepository.getCategoryRule(any())).thenReturn(categoryRule);
        when(clock.instant()).thenReturn(now);
        when(reservationStateRepository.getReservation(anyString())).thenReturn(null);
        when(availabilityService.calculateAvailableStock(any())).thenReturn(10);

        var result = reservationService.reserve(orderId, sku, quantity);

        assertEquals(orderId, result.orderId());
        assertEquals(sku, result.sku());
        assertEquals(quantity, result.quantity());
        assertEquals(now.plus(Duration.ofMinutes(15)), result.expiresAt());
        verify(reservationStateRepository).addReservation(eq(orderId), any(ReservationState.class));
        verify(alertService).checkLowStockAlert(productState);
    }

    @Test
    void exceptionWhenExceedingLimit() {
        var sku = "SKU-FLASH";
        var orderId = "ORDER-1";
        var quantity = 3;

        var product = mock(Product.class);
        when(product.getCategory()).thenReturn(ProductCategory.FLASH_SALE);

        var productState = ProductState.builder().product(product).build();
        var categoryRule = new CategoryRule(Duration.ofMinutes(5), 2);

        when(productStateRepository.getProduct(anyString())).thenReturn(productState);
        when(productCategoryRepository.getCategoryRule(any())).thenReturn(categoryRule);

        assertThrows(OrderLimitExceededException.class, () -> reservationService.reserve(orderId, sku, quantity));
    }

    @Test
    void returnReservationWhenOrderAlreadyExists() {
        var sku = "SKU-123";
        var orderId = "ORDER-1";

        var product = mock(Product.class);
        when(product.getCategory()).thenReturn(ProductCategory.STANDARD);

        var productState = ProductState.builder().product(product).build();
        var categoryRule = new CategoryRule(Duration.ofMinutes(15), null);
        var existingReservation = new Reservation(orderId, sku, 2, Instant.now());
        var existingReservationState = ReservationState.builder().reservation(existingReservation).build();

        when(productStateRepository.getProduct(anyString())).thenReturn(productState);
        when(productCategoryRepository.getCategoryRule(any())).thenReturn(categoryRule);
        when(reservationStateRepository.getReservation(anyString())).thenReturn(existingReservationState);

        var result = reservationService.reserve(orderId, sku, 2);

        assertEquals(existingReservation, result);
    }

    @Test
    void confirmReservationIsOk() {
        var sku = "SKU-123";
        var orderId = "ORDER-1";
        var now = Instant.parse("2026-09-09T10:00:00Z");

        var reservation = new Reservation(orderId, sku, 2, now.plusSeconds(300));
        var reservationState = ReservationState.builder().reservation(reservation).confirmed(false).build();

        var activeOrders = new HashSet<String>();
        activeOrders.add(orderId);
        var productState = ProductState.builder().totalStock(10).availableOrders(activeOrders).build();

        when(reservationStateRepository.getReservation(anyString())).thenReturn(reservationState);
        when(productStateRepository.getProduct(anyString())).thenReturn(productState);
        when(clock.instant()).thenReturn(now);

        reservationService.confirm(orderId);

        assertTrue(reservationState.isConfirmed());
    }

    @Test
    void exceptionWhenReservationIsExpired() {
        var sku = "SKU-123";
        var orderId = "ORDER-1";
        var now = Instant.parse("2026-09-09T10:00:00Z");

        var reservation = new Reservation(orderId, sku, 2, now.minusSeconds(300));
        var reservationState = ReservationState.builder().reservation(reservation).confirmed(false).build();

        var activeOrders = new HashSet<String>();
        activeOrders.add(orderId);
        var productState = ProductState.builder().totalStock(10).availableOrders(activeOrders).build();

        when(reservationStateRepository.getReservation(anyString())).thenReturn(reservationState);
        when(productStateRepository.getProduct(anyString())).thenReturn(productState);
        when(clock.instant()).thenReturn(now);

        assertThrows(ReservationNotActiveException.class, () -> reservationService.confirm(orderId));
    }

    @Test
    void exceptionWhenQuantityIsInvalid() {
        assertThrows(EmptyQuantityStockException.class,
            () -> reservationService.reserve("ORDER-1", "SKU-123", -1));
    }

    @Test
    void exceptionWhenInsufficientStock() {
        var product = mock(Product.class);
        when(product.getCategory()).thenReturn(ProductCategory.STANDARD);

        var productState = ProductState.builder().product(product).build();
        var categoryRule = new CategoryRule(Duration.ofMinutes(15), null);

        when(productStateRepository.getProduct(anyString())).thenReturn(productState);
        when(productCategoryRepository.getCategoryRule(any())).thenReturn(categoryRule);
        when(reservationStateRepository.getReservation(anyString())).thenReturn(null);
        when(availabilityService.calculateAvailableStock(any())).thenReturn(2);

        assertThrows(InsufficientStockException.class,
            () -> reservationService.reserve("ORDER-1", "SKU-123", 5));
    }
}
