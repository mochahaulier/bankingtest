package dev.mochahaulier.bankingtest.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

import dev.mochahaulier.bankingtest.model.Operation;
import dev.mochahaulier.bankingtest.validation.ValidationGroup;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class ProductDefinitionRequest {
        @Valid
        @NotNull(message = "Definitions list cannot be null.")
        private List<DefinitionRequest> definitions;

        @Getter
        @Setter
        public static class DefinitionRequest {
                @NotNull(message = "Operation is required.")
                // @Pattern(regexp = "^[NU]$", message = "Operation must be 'N' or 'U'.")
                private Operation operation;
                // private String operation;
                @NotNull(message = "Product key is required.", groups = {
                                ValidationGroup.NewOperation.class, ValidationGroup.UpdateOperation.class })
                @Size(min = 6, max = 6, message = "Product key must have 6 characters.", groups = {
                                ValidationGroup.NewOperation.class, ValidationGroup.UpdateOperation.class })
                private String productKey;
                @NotNull(message = "Please provide a description.", groups = ValidationGroup.NewOperation.class)
                private String description;
                // Just for the given example, there could probably be different types.
                @NotNull(message = "Type is required.", groups = ValidationGroup.NewOperation.class)
                @Pattern(regexp = "^(ACCOUNT|LOAN)$", message = "Type must be 'ACCOUNT' or 'LOAN'.", groups = ValidationGroup.NewOperation.class)
                private String type;
                // An assumption here, but it's a bank so probably not gonna be negative or 0
                @NotNull(message = "Rate is required.", groups = ValidationGroup.NewOperation.class)
                @DecimalMin(value = "0.0", inclusive = false, message = "Rate must be greater than 0.", groups = {
                                ValidationGroup.NewOperation.class, ValidationGroup.UpdateOperation.class })
                private BigDecimal rate;
                @NotNull(message = "Pay rate unit is required.", groups = ValidationGroup.NewOperation.class)
                private PayRateDto payRate;

                @Getter
                @Setter
                @AllArgsConstructor
                @RequiredArgsConstructor
                public static class PayRateDto {
                        @NotNull(message = "Pay rate unit is required.", groups = ValidationGroup.NewOperation.class)
                        @Pattern(regexp = "^(DAY|MONTH)$", message = "Unit must be 'DATE' or 'MONTH'.", groups = {
                                        ValidationGroup.NewOperation.class, ValidationGroup.UpdateOperation.class })
                        private String unit;
                        // Another assumption here
                        @NotNull(message = "Pay rate value is required.", groups = ValidationGroup.NewOperation.class)
                        @DecimalMin(value = "1", message = "Pay rate value must be at least 1.", groups = {
                                        ValidationGroup.NewOperation.class, ValidationGroup.UpdateOperation.class })
                        private Integer value;
                }
        }
}
