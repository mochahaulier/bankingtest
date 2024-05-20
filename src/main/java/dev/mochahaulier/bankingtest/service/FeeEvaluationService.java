package dev.mochahaulier.bankingtest.service;

import dev.mochahaulier.bankingtest.model.ClientProduct;
import dev.mochahaulier.bankingtest.model.ProductDefinition;
import dev.mochahaulier.bankingtest.model.ProductType;
import dev.mochahaulier.bankingtest.repository.ClientProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Service
public class FeeEvaluationService {

    @Autowired
    private ClientProductRepository clientProductRepository;

    public FeeEvaluationService(ClientProductRepository clientProductRepository) {
        this.clientProductRepository = clientProductRepository;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void evaluateFees() {
        // Runs for all Clients and their products.
        List<ClientProduct> clientProducts = clientProductRepository.findAll();

        for (ClientProduct clientProduct : clientProducts) {
            if (clientProduct.isDueForCharge()) {
                BigDecimal fee = calculateFee(clientProduct);
                deductFee(clientProduct, fee);
                clientProduct.setLastChargeDate(LocalDate.now());
                clientProductRepository.save(clientProduct);
            }
        }
    }

    private BigDecimal calculateFee(ClientProduct clientProduct) {
        ProductDefinition productDefinition = clientProduct.getProductDefinition();

        // for RateType fixed the fee is just the rate
        if (productDefinition.getRateType().equals("fixed")) {
            return productDefinition.getRate().add(clientProduct.getRate());
        }

        // for percentage fixed the fee is just the rate
        if (productDefinition.getRateType().equals("percentage")) {
            // The main rate from the definition
            BigDecimal ratePD = productDefinition.getRate();
            // The custom clientproduct rate.
            BigDecimal rateCP = clientProduct.getRate();
            // The final rate: PD * (1 + CP)
            BigDecimal rate = ratePD.multiply(rateCP.add(BigDecimal.ONE));
            if (productDefinition.getType() == ProductType.ACCOUNT) {
                return clientProduct.getBalance().multiply(rate);
            }
            if (productDefinition.getType() == ProductType.LOAN) {
                int numberOfDueDates = clientProduct.calculateNumberOfDueDates();
                BigDecimal interest = clientProduct.getOriginalAmount().multiply(rate)
                        .divide(BigDecimal.valueOf(numberOfDueDates), RoundingMode.HALF_UP);
                return clientProduct.getFixedInstallment().add(interest);
            }
        }

        // Shouldn't actually get here with the current setup.
        throw new IllegalArgumentException("Unknown product type: " + productDefinition.getType());
    }

    private void deductFee(ClientProduct clientProduct, BigDecimal fee) {
        // Find the first account of the client to deduct the fee
        // Not the best solution, does he need to have account...
        List<ClientProduct> clientAccounts = clientProductRepository
                .findByClientAndProduct_ProductDefinition_Type(clientProduct.getClient(), ProductType.ACCOUNT);
        if (!clientAccounts.isEmpty()) {
            ClientProduct account = clientAccounts.get(0);
            account.setBalance(account.getBalance().subtract(fee));
            clientProductRepository.save(account);
        } else {
            throw new IllegalStateException("No account found for client " + clientProduct.getClient().getId());
        }
    }
}
