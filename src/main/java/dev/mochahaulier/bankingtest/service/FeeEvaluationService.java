package dev.mochahaulier.bankingtest.service;

import dev.mochahaulier.bankingtest.model.ClientProduct;
import dev.mochahaulier.bankingtest.repository.ClientProductRepository;
import lombok.AllArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.support.locks.LockRegistry;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.locks.Lock;

@Service
@AllArgsConstructor
public class FeeEvaluationService {

    private final ClientProductRepository clientProductRepository;
    private final FeeCalculationService feeCalculationService;
    private final TransactionService transactionService;
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
                        BigDecimal fee = feeCalculationService.calculateFee(clientProduct);
                        transactionService.processFeeDeduction(clientProduct, fee);
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
}
