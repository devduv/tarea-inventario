package com.store.inventory.services;

import com.store.inventory.api.ProductCategory;

public interface ProductService {

    void registerProduct(String sku, ProductCategory category);

    int available(String sku);

    void addStock(String sku, int quantity);
}
