package com.store.inventory.repository.impl;

import com.store.inventory.api.ProductCategory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ProductCategoryRepositoryImplTest {

    @InjectMocks
    private ProductCategoryRepositoryImpl repository;

    @ParameterizedTest
    @CsvSource(value = {
        "STANDARD:15:null",
        "PRE_ORDER:1440:null",
        "FLASH_SALE:5:2"},
        delimiter = ':', nullValues = "null")
    void returnStandardCategoryRule(String category, int timeToPay, Integer orderLimit) {
        var categoryRule = repository.getCategoryRule(ProductCategory.valueOf(category));

        assertEquals(Duration.ofMinutes(timeToPay), categoryRule.getTimeToPay());
        assertEquals(orderLimit, categoryRule.getOrderLimit());
    }
}
