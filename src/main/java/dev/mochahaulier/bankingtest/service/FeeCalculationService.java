package dev.mochahaulier.bankingtest.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import dev.mochahaulier.bankingtest.model.AccountProduct;
import dev.mochahaulier.bankingtest.model.ClientProduct;
import dev.mochahaulier.bankingtest.model.LoanProduct;
import dev.mochahaulier.bankingtest.model.ProductDefinition;
import dev.mochahaulier.bankingtest.model.ProductType;
import dev.mochahaulier.bankingtest.model.RateType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FeeCalculationService {

    public BigDecimal calculateFee(ClientProduct clientProduct) {
        ProductDefinition productDefinition = clientProduct.getProductDefinition();

        // for RateType fixed the fee is just the rate
        if (productDefinition.getRateType() == RateType.FIXED) {
            return productDefinition.getRate().add(clientProduct.getRate());
        }

        // for percentage fixed the fee is just the rate
        if (productDefinition.getRateType() == RateType.PERCENTAGE) {
            // The main rate from the definition
            BigDecimal ratePD = productDefinition.getRate();
            // The custom clientproduct rate.
            BigDecimal rateCP = clientProduct.getRate();
            // The final rate: PD * (1 + CP)
            BigDecimal rate = ratePD.multiply(rateCP.add(BigDecimal.ONE));
            if (productDefinition.getProductType() == ProductType.ACCOUNT) {
                AccountProduct accountProduct = (AccountProduct) clientProduct;
                return accountProduct.getAccountBalance().multiply(rate);
            }
            if (productDefinition.getProductType() == ProductType.LOAN) {
                LoanProduct loanProduct = (LoanProduct) clientProduct;
                int numberOfDueDates = loanProduct.calculateNumberOfDueDates();
                BigDecimal interest = loanProduct.getOriginalAmount().multiply(rate)
                        .divide(BigDecimal.valueOf(numberOfDueDates), RoundingMode.HALF_EVEN);
                return loanProduct.getFixedInstallment().add(interest);
            }
        }

        // Shouldn't actually get here with the current setup.
        throw new IllegalArgumentException("Unknown product type: " + productDefinition.getProductType());
    }
}
