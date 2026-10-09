package com.store.inventory.repository.impl;

import com.store.inventory.api.ProductCategory;
import com.store.inventory.models.CategoryRule;
import com.store.inventory.repository.ProductCategoryRepository;

import java.time.Duration;
import java.util.Map;

public class ProductCategoryRepositoryImpl implements ProductCategoryRepository {

    private final Map<ProductCategory, CategoryRule> categoryRules = Map.of(
        ProductCategory.STANDARD, new CategoryRule(Duration.ofMinutes(15), null),
        ProductCategory.PRE_ORDER, new CategoryRule(Duration.ofMinutes(24 * 60), null),
        ProductCategory.FLASH_SALE, new CategoryRule(Duration.ofMinutes(5), 2)
    );

    @Override
    public CategoryRule getCategoryRule(ProductCategory category) {
        return categoryRules.get(category);
    }


}
