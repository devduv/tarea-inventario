package com.store.inventory.repository;

import com.store.inventory.api.ProductCategory;
import com.store.inventory.models.ProductState;

public interface ProductStateRepository {

    void registerProduct(String sku, ProductCategory productCategory);

    ProductState getProduct(String sku);

    void updateStock(int quantity, ProductState productState);

    void updateAvailableOrder(String orderId, ProductState productState);

    void deleteAvailableOrder(int stock, String orderId, ProductState productState);
}
