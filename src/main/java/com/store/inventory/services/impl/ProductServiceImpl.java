package com.store.inventory.services.impl;

import com.store.inventory.api.ProductCategory;
import com.store.inventory.errors.EmptyQuantityStockException;
import com.store.inventory.errors.ProductAlreadyExistsException;
import com.store.inventory.models.ProductState;
import com.store.inventory.repository.ProductStateRepository;
import com.store.inventory.services.AlertService;
import com.store.inventory.services.AvailabilityService;
import com.store.inventory.services.ProductService;
import com.store.inventory.utils.ProductUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductStateRepository productStateRepository;
    private final AlertService alertService;
    private final AvailabilityService availabilityService;

    /**
     * Register product with sku.
     *
     * @param sku             stock keeping unit
     * @param productCategory product category
     */
    @Override
    public void registerProduct(String sku, ProductCategory productCategory) {
        log.info("Registering new product with sku: {} in category: {}", sku, productCategory);
        ProductState product = productStateRepository.getProduct(sku);
        if (product != null) {
            throw new ProductAlreadyExistsException(sku);
        }
        productStateRepository.registerProduct(sku, productCategory);
    }

    /**
     * Find available product by sku.
     *
     * @param sku stock keeping unit
     * @return 0 is product doesn't exist
     */
    @Override
    public int available(String sku) {
        log.info("Querying availability for product sku: {}", sku);
        ProductState product = productStateRepository.getProduct(sku);
        if (product == null) {
            return 0;
        }
        return availabilityService.calculateAvailableStock(product);
    }

    /**
     * Add stock to product by sku.
     *
     * @param sku      stock keeping unit
     * @param quantity quantity
     */
    @Override
    public void addStock(String sku, int quantity) {
        log.info("Adding {} units to stock of product sku: {}", quantity, sku);
        validateQuantity(quantity);
        ProductState productState = productStateRepository.getProduct(sku);
        ProductUtil.validateProduct(productState, sku);

        synchronized (productState) {
            int totalStock = productState.getTotalStock();
            totalStock = totalStock + quantity;
            productStateRepository.updateStock(totalStock, productState);
            alertService.checkLowStockAlert(productState);
        }
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new EmptyQuantityStockException();
        }
    }
}
