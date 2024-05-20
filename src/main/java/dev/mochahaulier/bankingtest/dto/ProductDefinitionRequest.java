package dev.mochahaulier.bankingtest.dto;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class ProductDefinitionRequest {
    private List<DefinitionRequest> definitions;

    @Data
    public static class DefinitionRequest {
        @NotNull(message = "Operation is required.")
        @Pattern(regexp = "^[NU]$", message = "Operation must be 'N' or 'U'.")
        private String operation;
        @NotNull(message = "Product key is required.")
        @Size(min = 6, max = 6, message = "Product key must have 6 characters.")
        private String productKey;
        @NotNull(message = "Please provide a description.")
        private String description;
        // Just for the given example, there could probably be different types.
        @Pattern(regexp = "^(ACCOUNT|LOAN)$", message = "Type must be 'ACCOUNT' or 'LOAN'.")
        private String type;
        // An assumption here, but it's a bank so probably not gonna be negative or 0
        @DecimalMin(value = "0.0", inclusive = false, message = "Rate must be greater than 0.")
        private BigDecimal rate;
        @NotNull(message = "Pay rate unit is required.")
        private PayRateDto payRate;

        @Data
        public static class PayRateDto {
            @Pattern(regexp = "^(DAY|MONTH)$", message = "Unit must be 'DATE' or 'MONTH'.")
            private String unit;
            // Another assumption here
            @NotNull(message = "Pay rate value is required.")
            @DecimalMin(value = "1", message = "Pay rate value must be at least 1.")
            private int value;
        }
    }
}
