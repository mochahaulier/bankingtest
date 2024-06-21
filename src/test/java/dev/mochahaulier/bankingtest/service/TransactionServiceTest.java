package dev.mochahaulier.bankingtest.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import dev.mochahaulier.bankingtest.model.AccountProduct;
import dev.mochahaulier.bankingtest.model.Client;
import dev.mochahaulier.bankingtest.model.LoanProduct;
import dev.mochahaulier.bankingtest.model.ProductType;
import dev.mochahaulier.bankingtest.model.Transaction;
import dev.mochahaulier.bankingtest.model.TransactionType;
import dev.mochahaulier.bankingtest.repository.AccountProductRepository;
import dev.mochahaulier.bankingtest.repository.ClientProductRepository;
import dev.mochahaulier.bankingtest.repository.TransactionRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {

    private TransactionService transactionService;

    private ClientProductRepository clientProductRepository;
    private AccountProductRepository accountProductRepository;
    private TransactionRepository transactionRepository;

    private Client client;
    private AccountProduct accountProduct1;
    private AccountProduct accountProduct2;
    private LoanProduct loanProduct;

    @BeforeEach
    public void setUp() {
        clientProductRepository = Mockito.mock(ClientProductRepository.class);
        accountProductRepository = Mockito.mock(AccountProductRepository.class);
        transactionRepository = Mockito.mock(TransactionRepository.class);

        transactionService = new TransactionService(transactionRepository, clientProductRepository,
                accountProductRepository);

        // Set up client
        client = new Client();
        client.setId(1L);
        client.setFirstName("Client");
        client.setLastName("One");

        // Set up account products
        accountProduct1 = new AccountProduct();
        accountProduct1.setId(1L);
        accountProduct1.setClient(client);
        accountProduct1.setAccountBalance(new BigDecimal("10000"));

        accountProduct2 = new AccountProduct();
        accountProduct2.setId(2L);
        accountProduct2.setClient(client);
        accountProduct2.setAccountBalance(new BigDecimal("1000"));

        // Set up loan product
        loanProduct = new LoanProduct();
        loanProduct.setId(3L);
        loanProduct.setClient(client);
        loanProduct.setOriginalAmount(new BigDecimal("10000"));
        loanProduct.setFixedInstallment(new BigDecimal("1000"));
        loanProduct.setStartDate(LocalDate.now().minusMonths(3));
        loanProduct.setEndDate(LocalDate.now().plusMonths(9));

        when(accountProductRepository.findByClient(any(Client.class)))
                .thenReturn(Arrays.asList(accountProduct1, accountProduct2));
        when(clientProductRepository.findByClientAndProduct_ProductDefinition_ProductType(client, ProductType.ACCOUNT))
                .thenReturn(Arrays.asList(accountProduct1, accountProduct2));
    }

    @Test
    public void testProcessFeeDeduction() {
        BigDecimal fee = new BigDecimal("350");

        // Call the method to test
        transactionService.processFeeDeduction(loanProduct, fee);

        // Verify that the fee was deducted from the account with the smallest ID
        assertEquals(new BigDecimal("9650"), accountProduct1.getAccountBalance());

        // Capture the transaction saved to the repository
        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionCaptor.capture());

        // Verify the transaction details
        Transaction savedTransaction = transactionCaptor.getValue();
        assertEquals(loanProduct, savedTransaction.getClientProduct());
        assertEquals(TransactionType.FEE_DEDUCTION, savedTransaction.getTransactionType());
        assertEquals(fee.negate(), savedTransaction.getAmount());
        assertEquals(LocalDateTime.now().getDayOfMonth(), savedTransaction.getTransactionDate().getDayOfMonth());
    }

    @Test
    public void testProcessFeeDeductionNoAccounts() {
        when(accountProductRepository.findByClient(any(Client.class))).thenReturn(Arrays.asList());

        BigDecimal fee = new BigDecimal("350");

        // Expect an IllegalStateException to be thrown
        assertThrows(IllegalStateException.class, () -> {
            transactionService.processFeeDeduction(loanProduct, fee);
        });
    }

    @Test
    public void testProcessLoanAddition() {
        BigDecimal loanAmount = new BigDecimal("5000");

        // Call the method to test
        transactionService.processLoanAddition(loanProduct, loanAmount);

        // Verify that the loan amount was added to the account with the smallest ID
        assertEquals(new BigDecimal("15000"), accountProduct1.getAccountBalance());

        // Capture the transaction saved to the repository
        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionCaptor.capture());

        // Verify the transaction details
        Transaction savedTransaction = transactionCaptor.getValue();
        assertEquals(loanProduct, savedTransaction.getClientProduct());
        assertEquals(TransactionType.LOAN_ADDITION, savedTransaction.getTransactionType());
        assertEquals(loanAmount, savedTransaction.getAmount());
        assertEquals(LocalDateTime.now().getDayOfMonth(), savedTransaction.getTransactionDate().getDayOfMonth());
    }

    @Test
    public void testProcessAccountDeposit() {
        BigDecimal depositAmount = new BigDecimal("2000");

        // Call the method to test
        transactionService.processAccountDeposit(accountProduct1, depositAmount);

        // Verify that the deposit amount was added to the account balance
        assertEquals(new BigDecimal("12000"), accountProduct1.getAccountBalance());

        // Capture the transaction saved to the repository
        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionCaptor.capture());

        // Verify the transaction details
        Transaction savedTransaction = transactionCaptor.getValue();
        assertEquals(accountProduct1, savedTransaction.getClientProduct());
        assertEquals(TransactionType.ACCOUNT_DEPOSIT, savedTransaction.getTransactionType());
        assertEquals(depositAmount, savedTransaction.getAmount());
        assertEquals(LocalDateTime.now().getDayOfMonth(), savedTransaction.getTransactionDate().getDayOfMonth());
    }
}