package com.store.inventory.utils;

import com.store.inventory.errors.ProductNotExistsException;
import com.store.inventory.models.ProductState;

public class ProductUtil {

    /**
     * Validate product.
     *
     * @param productState product state
     * @param sku          product sku
     */
    public static void validateProduct(ProductState productState, String sku) {
        if (productState == null) {
            throw new ProductNotExistsException(sku);
        }
    }
}
