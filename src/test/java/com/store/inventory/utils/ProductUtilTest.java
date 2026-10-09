package com.store.inventory.utils;

import com.store.inventory.errors.ProductNotExistsException;
import com.store.inventory.models.ProductState;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class ProductUtilTest {

    @Test
    void notExceptionWhenProductExist() {
        var sku = "SKU-123";
        var productState = mock(ProductState.class);

        assertDoesNotThrow(() -> ProductUtil.validateProduct(productState, sku));
    }

    @Test
    void exceptionWhenProductNotExist() {
        var sku = "SKU-404";

        assertThrows(ProductNotExistsException.class, () -> ProductUtil.validateProduct(null, sku));
    }
}
