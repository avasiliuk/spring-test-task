package com.avasiliuk.testtask.model.rest;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class Product {
    private UUID id;
    private String name;
    private BigDecimal price;
}
