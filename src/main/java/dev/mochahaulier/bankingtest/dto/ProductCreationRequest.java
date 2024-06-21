package dev.mochahaulier.bankingtest.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCreationRequest {
    @NotNull(message = "Product key is required.")
    @Size(min = 6, max = 6, message = "Product key must have 6 characters.")
    private String productDefinitionKey;
    @NotNull(message = "Rate is required.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Rate must be greater than 0.")
    private BigDecimal adjustedRate;
}