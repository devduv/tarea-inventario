package com.store.inventory.models;

import com.store.inventory.api.ProductCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Product {
    private String sku;
    private ProductCategory category;
}
