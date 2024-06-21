package dev.mochahaulier.bankingtest.service;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.mochahaulier.bankingtest.dto.ProductCreationRequest;
import dev.mochahaulier.bankingtest.dto.ProductUpdateRequest;
import dev.mochahaulier.bankingtest.model.Product;
import dev.mochahaulier.bankingtest.model.ProductDefinition;
import dev.mochahaulier.bankingtest.repository.ProductDefinitionRepository;
import dev.mochahaulier.bankingtest.repository.ProductRepository;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductDefinitionRepository productDefinitionRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void testCreateProduct_Success() {
        // Arrange
        String productKey = "CL48S5";
        BigDecimal customRate = BigDecimal.valueOf(250);
        ProductDefinition productDefinition = new ProductDefinition();
        productDefinition.setRate(BigDecimal.valueOf(20));

        ProductCreationRequest productRequest = new ProductCreationRequest();
        productRequest.setProductDefinitionKey(productKey);
        productRequest.setAdjustedRate(customRate);

        when(productDefinitionRepository.findById(productKey)).thenReturn(Optional.of(productDefinition));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Product product = productService.createProduct(productRequest);

        // Assert
        assertEquals(productDefinition, product.getProductDefinition());
        assertEquals(customRate, product.getRate());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testCreateProduct_InvalidFixedRateNegative() {
        // Arrange
        String productKey = "CL48S5";
        BigDecimal invalidRate = BigDecimal.valueOf(-21);
        ProductDefinition productDefinition = new ProductDefinition();
        productDefinition.setRate(BigDecimal.valueOf(20));

        ProductCreationRequest productRequest = new ProductCreationRequest();
        productRequest.setProductDefinitionKey(productKey);
        productRequest.setAdjustedRate(invalidRate);

        when(productDefinitionRepository.findById(productKey)).thenReturn(Optional.of(productDefinition));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.createProduct(productRequest);
        });
        assertEquals("Final rate can't be negative: -21",
                exception.getMessage());
    }

    @Test
    void testCreateProduct_InvalidFixedRate() {
        // Arrange
        String productKey = "CL48S5";
        BigDecimal invalidRate = BigDecimal.valueOf(271);
        ProductDefinition productDefinition = new ProductDefinition();
        productDefinition.setRate(BigDecimal.valueOf(20));

        ProductCreationRequest productRequest = new ProductCreationRequest();
        productRequest.setProductDefinitionKey(productKey);
        productRequest.setAdjustedRate(invalidRate);

        when(productDefinitionRepository.findById(productKey)).thenReturn(Optional.of(productDefinition));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.createProduct(productRequest);
        });
        assertEquals("Custom rate out of allowed difference +-250",

                exception.getMessage());
    }

    @Test
    void testCreateProduct_InvalidPercentageRate() {
        // Arrange
        String productKey = "CL48S5";
        BigDecimal invalidRate = BigDecimal.valueOf(0.13);
        ProductDefinition productDefinition = new ProductDefinition();
        productDefinition.setRate(BigDecimal.valueOf(0.1));

        ProductCreationRequest productRequest = new ProductCreationRequest();
        productRequest.setProductDefinitionKey(productKey);
        productRequest.setAdjustedRate(invalidRate);

        when(productDefinitionRepository.findById(productKey)).thenReturn(Optional.of(productDefinition));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.createProduct(productRequest);
        });
        assertEquals("Custom rate out of allowed range +-0.2",
                exception.getMessage());
    }

    @Test
    void testCreateProduct_ProductDefinitionNotFound() {
        // Arrange
        String productKey = "CL48S5";
        BigDecimal customRate = BigDecimal.valueOf(10);

        ProductCreationRequest productRequest = new ProductCreationRequest();
        productRequest.setProductDefinitionKey(productKey);
        productRequest.setAdjustedRate(customRate);

        when(productDefinitionRepository.findById(productKey)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> productService.createProduct(productRequest));
    }

    @Test
    void testGetAllProducts() {
        // Arrange
        List<Product> productList = List.of(new Product(), new Product());
        when(productRepository.findAll()).thenReturn(productList);

        // Act
        List<Product> result = productService.getAllProducts();

        // Assert
        assertEquals(productList, result);
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testGetProductById_Success() {
        // Arrange
        Long productId = 1L;
        Product product = new Product();
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Act
        Optional<Product> result = productService.getProductById(productId);

        // Assert
        assertEquals(Optional.of(product), result);
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void testGetProductById_NotFound() {
        // Arrange
        Long productId = 1L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act
        Optional<Product> result = productService.getProductById(productId);

        // Assert
        assertEquals(Optional.empty(), result);
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    void testGetProductsByDefinition_Success() {
        // Arrange
        String productDefinitionKey = "CL48S5";
        ProductDefinition productDefinition = new ProductDefinition();
        List<Product> productList = List.of(new Product(), new Product());

        when(productDefinitionRepository.findById(productDefinitionKey)).thenReturn(Optional.of(productDefinition));
        when(productRepository.findByProductDefinition(productDefinition)).thenReturn(productList);

        // Act
        List<Product> result = productService.getProductsByDefinition(productDefinitionKey);

        // Assert
        assertEquals(productList, result);
        verify(productDefinitionRepository, times(1)).findById(productDefinitionKey);
        verify(productRepository, times(1)).findByProductDefinition(productDefinition);
    }

    @Test
    void testGetProductsByDefinition_ProductDefinitionNotFound() {
        // Arrange
        String productDefinitionKey = "CL48S5";

        when(productDefinitionRepository.findById(productDefinitionKey)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> productService.getProductsByDefinition(productDefinitionKey));
    }

    @Test
    void testUpdateProductRate_Success() {
        // Arrange
        Long productId = 1L;
        BigDecimal newRate = BigDecimal.valueOf(30);
        ProductDefinition productDefinition = new ProductDefinition();
        productDefinition.setRate(BigDecimal.valueOf(20));

        Product product = new Product();
        product.setProductDefinition(productDefinition);

        ProductUpdateRequest productRequest = new ProductUpdateRequest();
        productRequest.setProductDefinitionKey(productId);
        productRequest.setAdjustedRate(newRate);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Product updatedProduct = productService.updateProductRate(productRequest);

        // Assert
        assertEquals(newRate, updatedProduct.getRate());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testUpdateProductRate_InvalidRate() {
        // Arrange
        Long productId = 1L;
        BigDecimal invalidRate = BigDecimal.valueOf(271);
        ProductDefinition productDefinition = new ProductDefinition();
        productDefinition.setRate(BigDecimal.valueOf(20));

        Product product = new Product();
        product.setProductDefinition(productDefinition);

        ProductUpdateRequest productRequest = new ProductUpdateRequest();
        productRequest.setProductDefinitionKey(productId);
        productRequest.setAdjustedRate(invalidRate);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.updateProductRate(productRequest);
        });
        assertEquals("Custom rate out of allowed difference +-250",

                exception.getMessage());
    }

    @Test
    void testUpdateProductRate_InvalidRateNegative() {
        // Arrange
        Long productId = 1L;
        BigDecimal invalidRate = BigDecimal.valueOf(-21);
        ProductDefinition productDefinition = new ProductDefinition();
        productDefinition.setRate(BigDecimal.valueOf(20));

        Product product = new Product();
        product.setProductDefinition(productDefinition);

        ProductUpdateRequest productRequest = new ProductUpdateRequest();
        productRequest.setProductDefinitionKey(productId);
        productRequest.setAdjustedRate(invalidRate);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.updateProductRate(productRequest);
        });
        assertEquals("Final rate can't be negative: -21",

                exception.getMessage());
    }

    @Test
    void testUpdateProductRate_ProductNotFound() {
        // Arrange
        Long productId = 1L;
        BigDecimal newRate = BigDecimal.valueOf(30);

        ProductUpdateRequest productRequest = new ProductUpdateRequest();
        productRequest.setProductDefinitionKey(productId);
        productRequest.setAdjustedRate(newRate);

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> productService.updateProductRate(productRequest));
    }
}