package com.store.inventory.errors;

public class ProductNotExistsException extends IllegalStateException {

    public ProductNotExistsException(String sku) {
        super("Product with sku: " + sku + " not exists");
    }
}
