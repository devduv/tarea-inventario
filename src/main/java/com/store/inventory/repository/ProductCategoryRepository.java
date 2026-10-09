package com.store.inventory.repository;

import com.store.inventory.api.ProductCategory;
import com.store.inventory.models.CategoryRule;

public interface ProductCategoryRepository {
    CategoryRule getCategoryRule(ProductCategory category);
}
