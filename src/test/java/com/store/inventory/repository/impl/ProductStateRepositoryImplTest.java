package com.store.inventory.repository.impl;

import com.store.inventory.api.ProductCategory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProductStateRepositoryImplTest {

    @InjectMocks
    private ProductStateRepositoryImpl repository;

    @Test
    void registerProductIsOk() {
        var sku = "SKU-123";
        var category = ProductCategory.STANDARD;

        assertDoesNotThrow(() -> repository.registerProduct(sku, category));


    }

    @Test
    void returnProductWhenExist() {
        var sku = "SKU-123";
        var category = ProductCategory.STANDARD;

        repository.registerProduct(sku, category);

        var productState = repository.getProduct(sku);

        assertNotNull(productState);
        assertEquals(sku, productState.getProduct().getSku());
        assertEquals(category, productState.getProduct().getCategory());
        assertNotNull(productState.getAvailableOrders());
    }

    @Test
    void returnNullWhenProductNotExist() {
        var sku = "SKU-404";

        var productState = repository.getProduct(sku);

        assertNull(productState);
    }

    @Test
    void updateStockIsOk() {
        var sku = "SKU-123";
        var category = ProductCategory.STANDARD;

        repository.registerProduct(sku, category);

        var productState = repository.getProduct(sku);
        productState.setLowStockAlert(true);

        assertDoesNotThrow(() -> repository.updateStock(50, productState));

        assertEquals(50, productState.getTotalStock());
        assertFalse(productState.isLowStockAlert());
    }

    @Test
    void updateAvailableOrderIsOk() {
        var sku = "SKU-123";
        var category = ProductCategory.STANDARD;

        repository.registerProduct(sku, category);

        var productState = repository.getProduct(sku);
        var orderId = "ORDER-1";

        repository.updateAvailableOrder(orderId, productState);

        assertTrue(productState.getAvailableOrders().contains(orderId));
    }

    @Test
    void deleteAvailableOrderSuccessfully() {
        var sku = "SKU-123";
        var category = ProductCategory.STANDARD;

        repository.registerProduct(sku, category);

        var productState = repository.getProduct(sku);
        var orderId = "ORDER-1";

        repository.updateAvailableOrder(orderId, productState);

        repository.deleteAvailableOrder(30, orderId, productState);

        assertEquals(30, productState.getTotalStock());
        assertFalse(productState.getAvailableOrders().contains(orderId));
    }
}
