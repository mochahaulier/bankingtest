package dev.mochahaulier.bankingtest.service;

import dev.mochahaulier.bankingtest.model.ClientProduct;
import dev.mochahaulier.bankingtest.model.ProductDefinition;
import dev.mochahaulier.bankingtest.model.ProductType;
import dev.mochahaulier.bankingtest.model.RateType;
import dev.mochahaulier.bankingtest.repository.ClientProductRepository;
import lombok.AllArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.Lock;

@Service
@AllArgsConstructor
public class FeeEvaluationService {

    private final ClientProductRepository clientProductRepository;
    private final LockRegistry lockRegistry;

    private static final Logger logger = LoggerFactory.getLogger(FeeEvaluationService.class);

    @Transactional
    @Scheduled(cron = "${scheduling.fee-calculation-cron}")
    public void evaluateFees() {
        Lock lock = lockRegistry.obtain("feeCalculationLock");
        if (lock.tryLock()) {
            try {
                logger.info("Fee calculation task started");
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
                logger.info("Fee evaluation completed.");
            } catch (Exception e) {
                logger.error("Error executing fee evaluation.", e);
            } finally {
                lock.unlock();
            }
        } else {
            logger.info("Fee evaluation already running on another node.");
        }

    }

    private BigDecimal calculateFee(ClientProduct clientProduct) {
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
            if (productDefinition.getType() == ProductType.ACCOUNT) {
                return clientProduct.getBalance().multiply(rate);
            }
            if (productDefinition.getType() == ProductType.LOAN) {
                int numberOfDueDates = clientProduct.calculateNumberOfDueDates();
                BigDecimal interest = clientProduct.getOriginalAmount().multiply(rate)
                        .divide(BigDecimal.valueOf(numberOfDueDates), RoundingMode.HALF_EVEN);
                return clientProduct.getFixedInstallment().add(interest);
            }
        }

        // Shouldn't actually get here with the current setup.
        throw new IllegalArgumentException("Unknown product type: " + productDefinition.getType());
    }

    private void deductFee(ClientProduct clientProduct, BigDecimal fee) {
        // Find the first account of the client to deduct the fee
        // Not the best solution, does he need to have account, etc...
        // Get all clientproducts that are accounts
        List<ClientProduct> clientAccounts = clientProductRepository
                .findByClientAndProduct_ProductDefinition_Type(clientProduct.getClient(), ProductType.ACCOUNT);

        if (clientAccounts.isEmpty()) {
            throw new IllegalStateException("No account found for client " + clientProduct.getClient().getId());
        }

        // Select account with smallest ID
        ClientProduct account = clientAccounts.stream()
                .min(Comparator.comparing(ClientProduct::getId))
                .orElseThrow(() -> new RuntimeException("No account found."));

        // Deduct the fee from first account
        account.setBalance(account.getBalance().subtract(fee));
        clientProductRepository.save(account);
    }
}
