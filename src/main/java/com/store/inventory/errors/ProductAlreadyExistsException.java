package com.store.inventory.errors;

public class ProductAlreadyExistsException extends RuntimeException {

    public ProductAlreadyExistsException(String sku) {
        super("Product with sku: " + sku + " is already exists");
    }
}
