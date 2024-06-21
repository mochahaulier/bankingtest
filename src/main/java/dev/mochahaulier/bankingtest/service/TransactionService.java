package dev.mochahaulier.bankingtest.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import dev.mochahaulier.bankingtest.model.AccountProduct;
import dev.mochahaulier.bankingtest.model.ClientProduct;
import dev.mochahaulier.bankingtest.model.ProductType;
import dev.mochahaulier.bankingtest.model.Transaction;
import dev.mochahaulier.bankingtest.model.TransactionType;
import dev.mochahaulier.bankingtest.repository.AccountProductRepository;
import dev.mochahaulier.bankingtest.repository.ClientProductRepository;
import dev.mochahaulier.bankingtest.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final ClientProductRepository clientProductRepository;
    private final AccountProductRepository accountProductRepository;

    @Transactional
    public void processFeeDeduction(ClientProduct clientProduct, BigDecimal fee) {
        List<AccountProduct> clientAccounts = accountProductRepository.findByClient(clientProduct.getClient());

        // List<AccountProduct> clientAccounts = clientProductRepository
        // .findByClientAndProduct_ProductDefinition_ProductType(clientProduct.getClient(),
        // ProductType.ACCOUNT)
        // .stream()
        // .map(AccountProduct.class::cast)
        // .collect(Collectors.toList());

        if (clientAccounts.isEmpty()) {
            throw new IllegalStateException("No account found for client " + clientProduct.getClient().getId());
        }

        // Select account with smallest ID, maybe let client choose which account to use
        AccountProduct account = clientAccounts.stream()
                .min(Comparator.comparing(ClientProduct::getId))
                .orElseThrow(() -> new RuntimeException("No account found."));

        // Deduct the fee from first account
        account.setAccountBalance(account.getAccountBalance().subtract(fee));
        clientProductRepository.save(account);

        // Save transaction
        Transaction transaction = new Transaction();
        transaction.setClientProduct(clientProduct);
        transaction.setTransactionType(TransactionType.FEE_DEDUCTION);
        transaction.setAmount(fee.negate());
        transaction.setTransactionDate(LocalDateTime.now());

        transactionRepository.save(transaction);
        clientProductRepository.save(account);
    }

    @Transactional
    public void processLoanAddition(ClientProduct clientProduct, BigDecimal loanAmount) {
        List<AccountProduct> clientAccounts = clientProductRepository
                .findByClientAndProduct_ProductDefinition_ProductType(clientProduct.getClient(), ProductType.ACCOUNT)
                .stream()
                .map(AccountProduct.class::cast)
                .collect(Collectors.toList());

        if (clientAccounts.isEmpty()) {
            throw new IllegalStateException("No account found for client " + clientProduct.getClient().getId());
        }

        // Select account with smallest ID
        AccountProduct account = clientAccounts.stream()
                .min(Comparator.comparing(ClientProduct::getId))
                .orElseThrow(() -> new RuntimeException("No account found."));

        // Deduct the fee from first account
        account.setAccountBalance(account.getAccountBalance().add(loanAmount));
        clientProductRepository.save(account);

        // Save transaction
        Transaction transaction = new Transaction();
        transaction.setClientProduct(clientProduct);
        transaction.setTransactionType(TransactionType.LOAN_ADDITION);
        transaction.setAmount(loanAmount);
        transaction.setTransactionDate(LocalDateTime.now());

        transactionRepository.save(transaction);
        clientProductRepository.save(account);
    }

    @Transactional
    public void processAccountDeposit(AccountProduct account, BigDecimal amount) {
        // Deduct the fee from first account
        account.setAccountBalance(account.getAccountBalance().add(amount));
        // clientProductRepository.save(account);

        // Save transaction
        Transaction transaction = new Transaction();
        transaction.setClientProduct(account);
        transaction.setTransactionType(TransactionType.ACCOUNT_DEPOSIT);
        transaction.setAmount(amount);
        transaction.setTransactionDate(LocalDateTime.now());

        transactionRepository.save(transaction);
        // clientProductRepository.save(account);
    }
}
