package dev.mochahaulier.bankingtest.service;

import dev.mochahaulier.bankingtest.model.Product;
import dev.mochahaulier.bankingtest.model.ProductDefinition;
import dev.mochahaulier.bankingtest.repository.ProductDefinitionRepository;
import dev.mochahaulier.bankingtest.repository.ProductRepository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final ProductDefinitionRepository productDefinitionRepository;

    @Transactional
    public Product createProduct(String productKey, BigDecimal customRate) {
        ProductDefinition productDefinition = productDefinitionRepository.findById(productKey)
                .orElseThrow(() -> new IllegalArgumentException("Product definition not found"));

        validateCustomRate(productDefinition, customRate);

        Product product = new Product();
        product.setProductDefinition(productDefinition);
        product.setRate(customRate);
        product.setProductType(productDefinition.getProductType());
        product.setRateType(productDefinition.getRateType());
        return productRepository.save(product);
    }

    @Transactional
    @Cacheable("products")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Transactional
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    @Transactional
    public Product updateProductRate(Long id, BigDecimal newRate) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        validateCustomRate(product.getProductDefinition(), newRate);

        product.setRate(newRate);
        return productRepository.save(product);
    }

    public List<Product> getProductsByDefinition(String productDefinitionKey) {
        ProductDefinition productDefinition = productDefinitionRepository.findById(productDefinitionKey)
                .orElseThrow(() -> new IllegalArgumentException("Product Definition not found"));
        return productRepository.findByProductDefinition(productDefinition);
    }

    private void validateCustomRate(ProductDefinition productDefinition, BigDecimal customRate) {
        switch (productDefinition.getRateType()) {
            case FIXED:
                validateFixedRate(productDefinition.getRate(), customRate);
                break;
            case PERCENTAGE:
                validatePercentageRate(productDefinition.getRate(), customRate);
                break;
            default:
                throw new IllegalArgumentException("Unknown rate type: " + productDefinition.getRateType());
        }
    }

    private void validateFixedRate(BigDecimal baseRate, BigDecimal customRate) {
        BigDecimal rateChange = customRate.subtract(baseRate);

        if (!isValidFixedRate(rateChange)) {
            throw new IllegalArgumentException("Custom rate out of allowed difference +-250");
        }

        if (customRate.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Final rate can't be negative: " + customRate);
        }
    }

    private void validatePercentageRate(BigDecimal baseRate, BigDecimal customRate) {
        BigDecimal rateChange = customRate.divide(baseRate).subtract(BigDecimal.ONE);

        if (!isValidPercentageRate(rateChange)) {
            throw new IllegalArgumentException("Custom rate out of allowed range +-0.2");
        }
    }

    public Boolean isValidFixedRate(BigDecimal rateChange) {
        if (rateChange.compareTo(BigDecimal.valueOf(-250)) < 0
                || rateChange.compareTo(BigDecimal.valueOf(250)) > 0)
            return false;
        return true;
    }

    public Boolean isValidPercentageRate(BigDecimal rateChange) {
        if (rateChange.compareTo(BigDecimal.valueOf(-0.2)) < 0
                || rateChange.compareTo(BigDecimal.valueOf(0.2)) > 0)
            return false;
        return true;
    }
}