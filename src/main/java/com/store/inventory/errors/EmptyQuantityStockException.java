package com.store.inventory.errors;

public class EmptyQuantityStockException extends IllegalArgumentException {

    public EmptyQuantityStockException() {
        super("Quantity must be positive");
    }
}
