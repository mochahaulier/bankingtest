package dev.mochahaulier.bankingtest.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductUpdateRequest {
    @NotNull(message = "Product ID is required.")
    private Long productDefinitionKey;
    @NotNull(message = "Rate is required.")
    @DecimalMin(value = "0.0", inclusive = false, message = "Rate must be greater than 0.")
    private BigDecimal adjustedRate;
}