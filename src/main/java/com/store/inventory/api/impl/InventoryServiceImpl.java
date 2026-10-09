package com.store.inventory.api.impl;

import com.store.inventory.api.InventoryService;
import com.store.inventory.api.ProductCategory;
import com.store.inventory.api.Reservation;
import com.store.inventory.services.ProductService;
import com.store.inventory.services.ReservationService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private ProductService productService;
    private ReservationService reservationService;

    @Override
    public void registerProduct(String sku, ProductCategory category) {
      productService.registerProduct(sku, category);
    }

    @Override
    public void addStock(String sku, int quantity) {
        productService.addStock(sku, quantity);
    }

    @Override
    public Reservation reserve(String orderId, String sku, int quantity) {
        return reservationService.reserve(orderId, sku, quantity);
    }

    @Override
    public void confirm(String orderId) {
        reservationService.confirm(orderId);
    }

    @Override
    public int available(String sku) {
        return productService.available(sku);
    }
}
