package com.store.inventory.utils;

import com.store.inventory.errors.EmptyQuantityStockException;

public final class ValidationsUtil {

    /**
     * Validate quantity.
     *
     * @param quantity quantity
     */
    public static void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new EmptyQuantityStockException();
        }
    }
}
