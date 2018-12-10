package com.avasiliuk.testtask.model.rest;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class Order {
    private UUID id;
    private String buyerEmail;
    private Instant created;
    private BigDecimal totalAmount;
    private List<Product> products;
}
