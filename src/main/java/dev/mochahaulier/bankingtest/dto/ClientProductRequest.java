package dev.mochahaulier.bankingtest.dto;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ClientProductRequest {

    @NotNull
    private Long clientId;

    @NotNull
    private Long productId;

    private BigDecimal initialBalance;

    private BigDecimal loanAmount;

    @NotNull
    private LocalDate startDate;

    private LocalDate endDate;

    private BigDecimal fixedInstallment;
}