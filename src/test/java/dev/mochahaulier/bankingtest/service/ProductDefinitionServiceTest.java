package dev.mochahaulier.bankingtest.service;

import dev.mochahaulier.bankingtest.dto.ProductDefinitionRequest.DefinitionRequest;
import dev.mochahaulier.bankingtest.dto.ProductDefinitionRequest.DefinitionRequest.PayRateDto;
import dev.mochahaulier.bankingtest.exception.ProcessingException;
import dev.mochahaulier.bankingtest.model.Operation;
import dev.mochahaulier.bankingtest.model.PayRate;
import dev.mochahaulier.bankingtest.model.PayRateUnit;
import dev.mochahaulier.bankingtest.model.Product;
import dev.mochahaulier.bankingtest.model.ProductDefinition;
import dev.mochahaulier.bankingtest.model.ProductType;
import dev.mochahaulier.bankingtest.model.RateType;
import dev.mochahaulier.bankingtest.repository.ProductDefinitionRepository;
import dev.mochahaulier.bankingtest.repository.ProductRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductDefinitionServiceTest {

    @Mock
    private ProductDefinitionRepository productDefinitionRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductDefinitionService productDefinitionService;

    private DefinitionRequest newDefinitionRequest;
    private DefinitionRequest updateDefinitionRequest;
    private ProductDefinition newProductDefinition;
    private ProductDefinition existingProductDefinition;

    @BeforeEach
    public void setUp() {
        // Setting up the new product definition request
        newDefinitionRequest = new DefinitionRequest();
        newDefinitionRequest.setOperation(Operation.NEW);
        newDefinitionRequest.setProductKey("CL48S5");
        newDefinitionRequest.setDescription("consumer loan");
        newDefinitionRequest.setType(ProductType.LOAN.name());
        newDefinitionRequest.setRate(BigDecimal.valueOf(0.12));
        PayRateDto newPayRateDto = new PayRateDto("MONTH", 3);
        newDefinitionRequest.setPayRate(newPayRateDto);

        // Setting up the update product definition request
        updateDefinitionRequest = new DefinitionRequest();
        updateDefinitionRequest.setOperation(Operation.UPDATE);
        updateDefinitionRequest.setProductKey("MO0154");
        updateDefinitionRequest.setRate(BigDecimal.valueOf(10));
        PayRateDto updatePayRateDto = new PayRateDto("MONTH", 2);
        updateDefinitionRequest.setPayRate(updatePayRateDto);

        // Setting up the new product definition entity
        newProductDefinition = new ProductDefinition();
        newProductDefinition.setProductKey("CL48S5");
        newProductDefinition.setDescription("consumer loan");
        newProductDefinition.setProductType(ProductType.LOAN);
        newProductDefinition.setRate(BigDecimal.valueOf(0.12));
        newProductDefinition.setPayRate(new PayRate(PayRateUnit.MONTH, 3));

        // Setting up the existing product definition entity
        existingProductDefinition = new ProductDefinition();
        existingProductDefinition.setProductKey("MO0154");
        existingProductDefinition.setDescription("mortgage loan");
        existingProductDefinition.setProductType(ProductType.LOAN);
        existingProductDefinition.setRate(BigDecimal.valueOf(20));
        existingProductDefinition.setPayRate(new PayRate(PayRateUnit.MONTH, 1));
    }

    @Test
    public void testProcessProductDefinitions_NewProduct() {
        when(productDefinitionRepository.findById(newDefinitionRequest.getProductKey())).thenReturn(Optional.empty());
        when(productDefinitionRepository.save(any(ProductDefinition.class))).thenReturn(newProductDefinition);

        productDefinitionService.processProductDefinitions(Collections.singletonMap(0,
                newDefinitionRequest));

        verify(productDefinitionRepository,
                times(1)).save(any(ProductDefinition.class));
    }

    @Test
    public void testProcessProductDefinitions_UpdateProduct() {
        when(productDefinitionRepository.findById(updateDefinitionRequest.getProductKey()))
                .thenReturn(Optional.of(existingProductDefinition));
        when(productRepository.findByProductDefinition(existingProductDefinition)).thenReturn(Collections.emptyList());

        productDefinitionService.processProductDefinitions(Collections.singletonMap(0,
                updateDefinitionRequest));

        assertEquals(BigDecimal.valueOf(10), existingProductDefinition.getRate());
        assertEquals(PayRateUnit.MONTH,
                existingProductDefinition.getPayRate().getUnit());
        assertEquals(2, existingProductDefinition.getPayRate().getValue());
        verify(productDefinitionRepository,
                times(1)).findById(updateDefinitionRequest.getProductKey());
        verify(productDefinitionRepository,
                times(1)).save(existingProductDefinition);
    }

    @Test
    public void testProcessProductDefinitions_UpdateNonExistentProduct() {
        when(productDefinitionRepository.findById(updateDefinitionRequest.getProductKey()))
                .thenReturn(Optional.empty());

        productDefinitionService.processProductDefinitions(Collections.singletonMap(0,
                updateDefinitionRequest));

        verify(productDefinitionRepository,
                times(1)).findById(updateDefinitionRequest.getProductKey());
        verify(productDefinitionRepository,
                times(0)).save(any(ProductDefinition.class));
    }

    @Test
    public void testProcessProductDefinitions_UpdateProduct_DerivedProductRateFix() {
        // Arrange
        when(productDefinitionRepository.findById(updateDefinitionRequest.getProductKey()))
                .thenReturn(Optional.of(existingProductDefinition));

        Product product = new Product();
        product.setId(1L);
        product.setProductDefinition(existingProductDefinition);
        product.setRateType(RateType.FIXED);
        product.setRate(BigDecimal.valueOf(10));

        List<Product> products = Collections.singletonList(product);

        when(productRepository.findByProductDefinition(existingProductDefinition)).thenReturn(products);

        // Act
        productDefinitionService.processProductDefinitions(Collections.singletonMap(0,
                updateDefinitionRequest));

        // Assert
        assertEquals(BigDecimal.valueOf(10), existingProductDefinition.getRate());
        assertEquals(PayRateUnit.MONTH,
                existingProductDefinition.getPayRate().getUnit());
        assertEquals(2, existingProductDefinition.getPayRate().getValue());
        verify(productDefinitionRepository,
                times(1)).findById(updateDefinitionRequest.getProductKey());
        verify(productDefinitionRepository,
                times(1)).save(existingProductDefinition);

        // Verify that updateDerivedProducts sets the value to ZERO in this simple
        // version
        verify(productRepository,
                times(1)).findByProductDefinition(existingProductDefinition);
        assertEquals(BigDecimal.ZERO, product.getRate());
    }

    @Test
    public void testProcessProductDefinitions_UpdateProduct_DerivedProductRateToZero() {
        // Arrange
        when(productDefinitionRepository.findById(updateDefinitionRequest.getProductKey()))
                .thenReturn(Optional.of(existingProductDefinition));

        Product product = new Product();
        product.setId(1L);
        product.setProductDefinition(existingProductDefinition);
        product.setRateType(RateType.FIXED);
        product.setRate(BigDecimal.valueOf(5));

        List<Product> products = Collections.singletonList(product);

        when(productRepository.findByProductDefinition(existingProductDefinition)).thenReturn(products);

        // Act
        productDefinitionService.processProductDefinitions(Collections.singletonMap(0,
                updateDefinitionRequest));

        // Assert
        assertEquals(BigDecimal.valueOf(10), existingProductDefinition.getRate());
        assertEquals(PayRateUnit.MONTH,
                existingProductDefinition.getPayRate().getUnit());
        assertEquals(2, existingProductDefinition.getPayRate().getValue());
        verify(productDefinitionRepository,
                times(1)).findById(updateDefinitionRequest.getProductKey());
        verify(productDefinitionRepository,
                times(1)).save(existingProductDefinition);

        // Verify that updateDerivedProducts sets the value to ZERO in this simple
        // version
        verify(productRepository,
                times(1)).findByProductDefinition(existingProductDefinition);
        assertEquals(BigDecimal.ZERO, product.getRate());
    }
}