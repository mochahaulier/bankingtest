package dev.mochahaulier.bankingtest.service;

import dev.mochahaulier.bankingtest.model.Client;
import dev.mochahaulier.bankingtest.model.ClientProduct;
import dev.mochahaulier.bankingtest.model.PayRate;
import dev.mochahaulier.bankingtest.model.PayRateUnit;
import dev.mochahaulier.bankingtest.model.Product;
import dev.mochahaulier.bankingtest.model.ProductDefinition;
import dev.mochahaulier.bankingtest.model.ProductType;
import dev.mochahaulier.bankingtest.repository.ClientProductRepository;
import dev.mochahaulier.bankingtest.repository.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class FeeEvaluationServiceTest {

    @Autowired
    private FeeEvaluationService feeEvaluationService;

    @MockBean
    private ClientProductRepository clientProductRepository;

    @MockBean
    private ClientRepository clientRepository;

    private Client client;
    private ProductDefinition accountProductDefinition;
    private ProductDefinition account2ProductDefinition;
    private ProductDefinition loanProductDefinition;
    private Product accountProduct;
    private Product account2Product;
    private Product loanProduct;
    private ClientProduct clientAccountProduct;
    private ClientProduct clientAccount2Product;
    private ClientProduct clientLoanProduct;

    @BeforeEach
    public void setUp() {

        // Create ProductDefinitions
        accountProductDefinition = new ProductDefinition();
        accountProductDefinition.setProductKey("PA004A");
        accountProductDefinition.setType(ProductType.ACCOUNT);
        accountProductDefinition.setRate(new BigDecimal("150"));
        accountProductDefinition.setPayRate(new PayRate(PayRateUnit.DAY, 14));

        account2ProductDefinition = new ProductDefinition();
        account2ProductDefinition.setProductKey("PA004B");
        account2ProductDefinition.setType(ProductType.ACCOUNT);
        account2ProductDefinition.setRate(new BigDecimal("0.5"));
        account2ProductDefinition.setPayRate(new PayRate(PayRateUnit.DAY, 14));

        loanProductDefinition = new ProductDefinition();
        loanProductDefinition.setProductKey("CL48S5");
        loanProductDefinition.setType(ProductType.LOAN);
        loanProductDefinition.setRate(new BigDecimal("0.5"));
        loanProductDefinition.setPayRate(new PayRate(PayRateUnit.MONTH, 3));

        // Create Products
        accountProduct = new Product();
        accountProduct.setProductDefinition(accountProductDefinition);
        accountProduct.setRate(new BigDecimal("200"));

        account2Product = new Product();
        account2Product.setProductDefinition(account2ProductDefinition);
        account2Product.setRate(new BigDecimal("-0.1"));

        loanProduct = new Product();
        loanProduct.setProductDefinition(loanProductDefinition);
        loanProduct.setRate(new BigDecimal("0.0"));

        // Create Client
        client = new Client();
        client.setId(1L);
        client.setName("Client One");

        // Create ClientProducts
        clientAccountProduct = new ClientProduct();
        clientAccountProduct.setClient(client);
        clientAccountProduct.setProduct(accountProduct);
        clientAccountProduct.setBalance(new BigDecimal("10000"));
        clientAccountProduct.setLastChargeDate(LocalDate.now().minusDays(15));
        clientAccountProduct.setStartDate(LocalDate.now().minusMonths(1));

        clientAccount2Product = new ClientProduct();
        clientAccount2Product.setClient(client);
        clientAccount2Product.setProduct(account2Product);
        clientAccount2Product.setBalance(new BigDecimal("1000"));
        clientAccount2Product.setLastChargeDate(LocalDate.now().minusDays(15));
        clientAccount2Product.setStartDate(LocalDate.now().minusMonths(1));

        clientLoanProduct = new ClientProduct();
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
        when(clientProductRepository.findByClientAndProduct_ProductDefinition_Type(any(Client.class),
                any(ProductType.class)))
                .thenReturn(Arrays.asList(clientAccountProduct));
    }

    @Test
    public void testEvaluateFees() {
        // Run the fee evaluation
        feeEvaluationService.evaluateFees();

        // Assertions to verify fee deduction
        // 10000 (balance) - 350 (account fee) - 2250 (loan fee) - 450 (account2 fee)
        BigDecimal expectedAccountBalance = new BigDecimal("6950");

        // Verify the deduction from the account product
        assert clientAccountProduct.getBalance().compareTo(expectedAccountBalance) == 0;
        // Verify no deduction from the second account product
        assert clientAccount2Product.getBalance().compareTo(new BigDecimal("1000")) == 0;
    }
}