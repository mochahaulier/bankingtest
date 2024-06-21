package dev.mochahaulier.bankingtest.service;

import dev.mochahaulier.bankingtest.model.AccountProduct;
import dev.mochahaulier.bankingtest.model.Client;
import dev.mochahaulier.bankingtest.model.ClientProduct;
import dev.mochahaulier.bankingtest.model.LoanProduct;
import dev.mochahaulier.bankingtest.model.PayRate;
import dev.mochahaulier.bankingtest.model.PayRateUnit;
import dev.mochahaulier.bankingtest.model.Product;
import dev.mochahaulier.bankingtest.model.ProductDefinition;
import dev.mochahaulier.bankingtest.model.ProductType;
import dev.mochahaulier.bankingtest.repository.AccountProductRepository;
import dev.mochahaulier.bankingtest.repository.ClientProductRepository;
import dev.mochahaulier.bankingtest.repository.ClientRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.integration.support.locks.LockRegistry;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.Lock;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

@SpringBootTest
public class FeeEvaluationServiceTest {

    @Autowired
    private FeeEvaluationService feeEvaluationService;

    @MockBean
    private ClientProductRepository clientProductRepository;

    @MockBean
    private AccountProductRepository accountProductRepository;

    @MockBean
    private ClientRepository clientRepository;

    @MockBean
    private LockRegistry lockRegistry;

    @MockBean
    private FeeCalculationService feeCalculationService;

    @MockBean
    private TransactionService transactionService;

    private Client client;
    private ProductDefinition accountProductDefinition;
    private ProductDefinition account2ProductDefinition;
    private ProductDefinition loanProductDefinition;
    private Product accountProduct;
    private Product account2Product;
    private Product loanProduct;
    private AccountProduct clientAccountProduct;
    private AccountProduct clientAccount2Product;
    private LoanProduct clientLoanProduct;

    @BeforeEach
    public void setUp() {
        // Mock the lock registry
        Lock mockLock = Mockito.mock(Lock.class);
        when(lockRegistry.obtain(any(String.class))).thenReturn(mockLock);
        when(mockLock.tryLock()).thenReturn(true);

        // Create ProductDefinitions
        accountProductDefinition = new ProductDefinition();
        accountProductDefinition.setProductKey("PA004A");
        accountProductDefinition.setProductType(ProductType.ACCOUNT);
        accountProductDefinition.setRate(new BigDecimal("150"));
        accountProductDefinition.setPayRate(new PayRate(PayRateUnit.DAY, 14));

        account2ProductDefinition = new ProductDefinition();
        account2ProductDefinition.setProductKey("PA004B");
        account2ProductDefinition.setProductType(ProductType.ACCOUNT);
        account2ProductDefinition.setRate(new BigDecimal("0.5"));
        account2ProductDefinition.setPayRate(new PayRate(PayRateUnit.DAY, 1));

        loanProductDefinition = new ProductDefinition();
        loanProductDefinition.setProductKey("CL48S5");
        loanProductDefinition.setProductType(ProductType.LOAN);
        loanProductDefinition.setRate(new BigDecimal("0.5"));
        loanProductDefinition.setPayRate(new PayRate(PayRateUnit.MONTH, 3));

        // Create Products
        accountProduct = new Product();
        accountProduct.setProductDefinition(accountProductDefinition);
        accountProduct.setRate(new BigDecimal("350"));

        account2Product = new Product();
        account2Product.setProductDefinition(account2ProductDefinition);
        account2Product.setRate(new BigDecimal("0.45"));

        loanProduct = new Product();
        loanProduct.setProductDefinition(loanProductDefinition);
        loanProduct.setRate(new BigDecimal("0.5"));

        // Create Client
        client = new Client();
        client.setId(1L);
        client.setFirstName("Client");
        client.setLastName("One");

        // Create ClientProducts
        clientAccountProduct = new AccountProduct();
        clientAccountProduct.setClient(client);
        clientAccountProduct.setProduct(accountProduct);
        clientAccountProduct.setAccountBalance(new BigDecimal("10000"));
        clientAccountProduct.setLastChargeDate(LocalDate.now().minusDays(15));
        clientAccountProduct.setStartDate(LocalDate.now().minusMonths(1));

        clientAccount2Product = new AccountProduct();
        clientAccount2Product.setClient(client);
        clientAccount2Product.setProduct(account2Product);
        clientAccount2Product.setAccountBalance(new BigDecimal("1000"));
        clientAccount2Product.setLastChargeDate(LocalDate.now().minusDays(15));
        clientAccount2Product.setStartDate(LocalDate.now().minusMonths(1));

        clientLoanProduct = new LoanProduct();
        clientLoanProduct.setClient(client);
        clientLoanProduct.setProduct(loanProduct);
        clientLoanProduct.setOriginalAmount(new BigDecimal("10000"));
        clientLoanProduct.setFixedInstallment(new BigDecimal("1000"));
        clientLoanProduct.setStartDate(LocalDate.now().minusMonths(3));
        clientLoanProduct.setEndDate(LocalDate.now().plusMonths(9));
        clientLoanProduct.setLastChargeDate(LocalDate.now().minusMonths(5));

        List<ClientProduct> clientProducts = Arrays.asList(clientAccountProduct, clientAccount2Product,
                clientLoanProduct);

        // Mocking repository methods
        when(clientProductRepository.findAll()).thenReturn(clientProducts);
        when(accountProductRepository.findByClient(any(Client.class)))
                .thenReturn(Arrays.asList(clientAccountProduct, clientAccount2Product));

        // Mocking fee calculation
        when(feeCalculationService.calculateFee(clientAccountProduct)).thenReturn(new BigDecimal("350"));
        when(feeCalculationService.calculateFee(clientLoanProduct)).thenReturn(new BigDecimal("2250"));
        when(feeCalculationService.calculateFee(clientAccount2Product)).thenReturn(new BigDecimal("450"));

        // Ensure transaction service is called correctly and deduct from the correct
        // account
        doAnswer(invocation -> {
            BigDecimal fee = invocation.getArgument(1);
            AccountProduct targetAccount = (AccountProduct) clientAccountProduct;
            targetAccount.setAccountBalance(targetAccount.getAccountBalance().subtract(fee));
            return null;
        }).when(transactionService).processFeeDeduction(any(ClientProduct.class), any(BigDecimal.class));

    }

    @Test
    public void testEvaluateFees() {
        // Run the fee evaluation
        feeEvaluationService.evaluateFees();

        // System.out.println("Account Product Balance: " +
        // clientAccountProduct.getAccountBalance());
        // System.out.println("Account2 Product Balance: " +
        // clientAccount2Product.getAccountBalance());

        // Assertions to verify fee deduction
        BigDecimal expectedAccountBalance = new BigDecimal("6950");

        // Verify the deduction from the account product
        assert clientAccountProduct.getAccountBalance().compareTo(expectedAccountBalance) == 0;
        // Verify no deduction from the second account product
        assert clientAccount2Product.getAccountBalance().compareTo(new BigDecimal("1000")) == 0;
    }
}
