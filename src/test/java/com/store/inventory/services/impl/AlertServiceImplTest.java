package com.store.inventory.services.impl;

import com.store.inventory.api.StockAlertListener;
import com.store.inventory.models.Product;
import com.store.inventory.models.ProductState;
import com.store.inventory.services.AvailabilityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertServiceImplTest {

    @Mock
    private StockAlertListener alertListener;

    @Mock
    private AvailabilityService availabilityService;

    @InjectMocks
    private AlertServiceImpl alertService;

    @Test
    void alertWhenStockIsFiveOrLess() {
        var sku = "SKU-123";
        var product = mock(Product.class);

        when(product.getSku()).thenReturn(sku);

        var productState = ProductState.builder()
            .product(product)
            .totalStock(5)
            .lowStockAlert(false)
            .availableOrders(new HashSet<String>()).build();

        when(availabilityService.calculateAvailableStock(any())).thenReturn(5);

        alertService.checkLowStockAlert(productState);

        assertTrue(productState.isLowStockAlert());
        verify(alertListener).onLowStock(sku, 5);
    }

    @ParameterizedTest
    @CsvSource({
        "6, false",
        "5, true"})
    void notAlertWhenStockIsAboveFiveOrLowStockAlert(int stock, boolean initialAlertState) {
        var sku = "SKU-123";
        var productState = ProductState.builder()
            .product(Product.builder().sku(sku).build())
            .totalStock(stock)
            .lowStockAlert(initialAlertState)
            .availableOrders(new HashSet<String>())
            .build();

        when(availabilityService.calculateAvailableStock(any())).thenReturn(stock);

        alertService.checkLowStockAlert(productState);

        assertEquals(initialAlertState, productState.isLowStockAlert());
        verify(alertListener, never()).onLowStock(sku, stock);
    }
}
