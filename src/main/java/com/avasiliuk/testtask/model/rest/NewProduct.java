package com.avasiliuk.testtask.model.rest;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
public class NewProduct {
    @NotNull
    private String name;
    @NotNull
    @Positive
    private BigDecimal price;
}
