package dev.mochahaulier.bankingtest.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductRequest {
    private Long productDefinitionId;
    private String productDefinitionKey;
    private BigDecimal adjustedRate;
}