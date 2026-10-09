package com.store.inventory.repository.impl;

import com.store.inventory.api.ProductCategory;
import com.store.inventory.models.Product;
import com.store.inventory.models.ProductState;
import com.store.inventory.repository.ProductStateRepository;

import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProductStateRepositoryImpl implements ProductStateRepository {

    private final Map<String, ProductState> products = new ConcurrentHashMap<>();

    @Override
    public void
    registerProduct(String sku, ProductCategory productCategory) {
        Product product = Product.builder().sku(sku).category(productCategory).build();
        ProductState productState = ProductState.builder().product(product).availableOrders(HashSet.newHashSet(5)).build();

        products.putIfAbsent(sku, productState);
    }

    @Override
    public ProductState getProduct(String sku) {
        return products.get(sku);
    }

    @Override
    public void updateStock(int stock, ProductState productState) {
        productState.setTotalStock(stock);
        productState.setLowStockAlert(false);
    }

    @Override
    public void updateAvailableOrder(String orderId, ProductState productState) {
        productState.getAvailableOrders().add(orderId);
    }

    @Override
    public void deleteAvailableOrder(int stock, String orderId, ProductState productState) {
        productState.setTotalStock(stock);
        productState.getAvailableOrders().remove(orderId);
    }
}
