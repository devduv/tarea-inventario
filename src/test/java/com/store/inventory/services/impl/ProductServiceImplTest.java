package com.store.inventory.services.impl;

import com.store.inventory.api.ProductCategory;
import com.store.inventory.errors.EmptyQuantityStockException;
import com.store.inventory.errors.ProductAlreadyExistsException;
import com.store.inventory.errors.ProductNotExistsException;
import com.store.inventory.models.Product;
import com.store.inventory.models.ProductState;
import com.store.inventory.repository.ProductStateRepository;
import com.store.inventory.services.AlertService;
import com.store.inventory.services.AvailabilityService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductStateRepository productStateRepository;

    @Mock
    private AlertService alertService;

    @Mock
    private AvailabilityService availabilityService;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void registerProductIsOk() {
        var sku = "SKU-123";
        var category = ProductCategory.STANDARD;

        assertDoesNotThrow(() -> productService.registerProduct(sku, category));

        verify(productStateRepository).registerProduct(sku, category);
    }

    @Test
    void exceptionWhenProductAlreadyExists() {
        var sku = "SKU-123";
        var category = ProductCategory.STANDARD;
        var productState = ProductState.builder()
            .product(Product.builder().sku(sku).build()).build();

        when(productStateRepository.getProduct(anyString())).thenReturn(productState);

        assertThrows(ProductAlreadyExistsException.class, () -> productService.registerProduct(sku, category));
    }

    @Test
    void returnZeroWhenProductNotExists() {
        var sku = "SKU-404";

        when(productStateRepository.getProduct(anyString())).thenReturn(null);

        var available = productService.available(sku);

        assertEquals(0, available);
    }

    @Test
    void returnAvailableUnitsWhenProductExists() {
        var sku = "SKU-123";
        var productState = mock(ProductState.class);

        when(productStateRepository.getProduct(anyString())).thenReturn(productState);
        when(availabilityService.calculateAvailableStock(any())).thenReturn(10);

        var available = productService.available(sku);

        assertEquals(10, available);
    }

    @Test
    void addStockIsOk() {
        var sku = "SKU-123";
        var initialStock = 5;
        var addedStock = 10;
        var productState = ProductState.builder()
            .totalStock(initialStock)
            .lowStockAlert(true)
            .build();
        when(productStateRepository.getProduct(anyString())).thenReturn(productState);

        assertDoesNotThrow(() -> productService.addStock(sku, addedStock));

        verify(productStateRepository).updateStock(addedStock + initialStock, productState);
        verify(alertService).checkLowStockAlert(productState);
    }

    @Test
    void exceptionAddStockWhenQuantityIsZero() {
        var sku = "SKU-123";

        assertThrows(EmptyQuantityStockException.class, () -> productService.addStock(sku, 0));
    }

    @Test
    void exceptionAddStockWhenProductNotExists() {
        var sku = "SKU-404";
        when(productStateRepository.getProduct(anyString())).thenReturn(null);

        assertThrows(ProductNotExistsException.class, () -> productService.addStock(sku, 10));
    }
}
