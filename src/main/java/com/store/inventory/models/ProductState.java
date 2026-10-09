package com.store.inventory.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
@Builder
public class ProductState {
    private Product product;
    private int totalStock;
    private boolean lowStockAlert;
    private Set<String> availableOrders;
}
