package com.store.inventory.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Duration;

@Data
@AllArgsConstructor
public class CategoryRule {
    private Duration timeToPay;
    private Integer orderLimit;
}
