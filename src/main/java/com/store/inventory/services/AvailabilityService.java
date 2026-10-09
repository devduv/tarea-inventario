package com.store.inventory.services;

import com.store.inventory.models.ProductState;

public interface AvailabilityService {
    int calculateAvailableStock(ProductState productState);
}
