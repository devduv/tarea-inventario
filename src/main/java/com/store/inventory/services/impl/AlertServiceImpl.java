package com.store.inventory.services.impl;

import com.store.inventory.api.StockAlertListener;
import com.store.inventory.models.ProductState;
import com.store.inventory.services.AlertService;
import com.store.inventory.services.AvailabilityService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class AlertServiceImpl implements AlertService {
    private final StockAlertListener alertListener;
    private final AvailabilityService availabilityService;

    @Override
    public void checkLowStockAlert(ProductState productState) {
        log.info("Checking low stock alert for product sku: {}", productState.getProduct().getSku());
        int available = availabilityService.calculateAvailableStock(productState);
        if (!productState.isLowStockAlert() && available <= 5) {
            productState.setLowStockAlert(true);
            alertListener.onLowStock(productState.getProduct().getSku(), available);
        }
    }

}
