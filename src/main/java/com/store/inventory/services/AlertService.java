package com.store.inventory.services;

import com.store.inventory.models.ProductState;

public interface AlertService {
    void checkLowStockAlert(ProductState productState);
}
