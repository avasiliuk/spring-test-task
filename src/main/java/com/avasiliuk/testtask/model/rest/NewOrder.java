package com.avasiliuk.testtask.model.rest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewOrder {
    @NotEmpty
    private List<UUID> productIds;
    @NotNull
    @Email
    private String buyerEmail;
}
