package com.store.inventory.utils;

import com.store.inventory.errors.EmptyQuantityStockException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ValidationsUtilTest {

    @Test
    void exceptionWhenQuantityIsPositive() {
        assertDoesNotThrow(() -> ValidationsUtil.validateQuantity(10));
    }

    @Test
    void exceptionWhenQuantityIsZero() {
        assertThrows(EmptyQuantityStockException.class, () -> ValidationsUtil.validateQuantity(0));
    }

    @Test
    void exceptionWhenQuantityIsNegative() {
        assertThrows(EmptyQuantityStockException.class, () -> ValidationsUtil.validateQuantity(-5));
    }
}
