package dev.mochahaulier.bankingtest.service;

import dev.mochahaulier.bankingtest.model.Product;
import dev.mochahaulier.bankingtest.model.ProductDefinition;
import dev.mochahaulier.bankingtest.model.RateType;
import dev.mochahaulier.bankingtest.repository.ProductDefinitionRepository;
import dev.mochahaulier.bankingtest.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import jakarta.transaction.Transactional;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductDefinitionRepository productDefinitionRepository;

    @Transactional
    public Product createProduct(String productKey, BigDecimal customRate) {
        ProductDefinition productDefinition = productDefinitionRepository.findById(productKey)
                .orElseThrow(() -> new IllegalArgumentException("Product definition not found"));

        validateCustomRate(productDefinition, customRate);

        Product product = new Product();
        product.setProductDefinition(productDefinition);
        product.setRate(customRate);
        return productRepository.save(product);
    }

    @Transactional
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
        // Maybe productDefinition.getType() == ProductType.ACCOUNT, if always fixed for
        // accounts, but usede solution more general I supppose...
        if (productDefinition.getRateType() == RateType.FIXED) {
            if (customRate.compareTo(BigDecimal.valueOf(-250)) < 0
                    || customRate.compareTo(BigDecimal.valueOf(250)) > 0) {
                throw new IllegalArgumentException("Custom rate out of allowed range +-250");
            }
            // Needs to be checked with Product Updates. How to handle? For now set to zero
            // if negatve...
            BigDecimal newRate = productDefinition.getRate().add(customRate);
            if (newRate.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Final rate can't be negative: " + newRate + "(" + customRate + ")");
            }
        } else if (productDefinition.getRateType() == RateType.PERCENTAGE) {
            if (customRate.compareTo(BigDecimal.valueOf(-0.2)) < 0
                    || customRate.compareTo(BigDecimal.valueOf(0.2)) > 0) {
                throw new IllegalArgumentException("Custom rate out of allowed range +-0.2");
            }
        }
    }
}
