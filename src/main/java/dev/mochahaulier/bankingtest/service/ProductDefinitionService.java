package dev.mochahaulier.bankingtest.service;

import dev.mochahaulier.bankingtest.dto.ProductDefinitionRequest;
import dev.mochahaulier.bankingtest.dto.ProductDefinitionResponse;
import dev.mochahaulier.bankingtest.model.PayRate;
import dev.mochahaulier.bankingtest.model.PayRateUnit;
import dev.mochahaulier.bankingtest.model.Product;
import dev.mochahaulier.bankingtest.model.ProductDefinition;
import dev.mochahaulier.bankingtest.model.ProductType;
import dev.mochahaulier.bankingtest.repository.ProductDefinitionRepository;
import dev.mochahaulier.bankingtest.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProductDefinitionService {

    private final ProductDefinitionRepository productDefinitionRepository;

    private final ProductRepository productRepository;

    @Transactional
    public ProductDefinitionResponse processProductDefinitions(
            Map<Integer, ProductDefinitionRequest.DefinitionRequest> definitions) {
        List<String> errors = new ArrayList<>();
        List<String> successes = new ArrayList<>();

        for (Map.Entry<Integer, ProductDefinitionRequest.DefinitionRequest> entry : definitions.entrySet()) {
            int index = entry.getKey();
            ProductDefinitionRequest.DefinitionRequest definition = entry.getValue();
            try {
                processSingleDefinition(definition);
                successes.add("[" + index + "]: [PROCESSING SUCCESS]: Definition " + definition.getProductKey()
                        + " processed.");
            } catch (Exception e) {
                errors.add("[" + index + "]: [PROCESSING ERROR]: " + e.getMessage());
            }
        }

        return new ProductDefinitionResponse(errors, successes);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processSingleDefinition(ProductDefinitionRequest.DefinitionRequest request) {
        ProductDefinition existingDefinition = productDefinitionRepository.findById(request.getProductKey())
                .orElse(null);
        switch (request.getOperation()) {
            case NEW:
                if (existingDefinition != null) {
                    throw new IllegalArgumentException(
                            "Product definition with key " + request.getProductKey() + " already exists");
                }
                ProductDefinition productDefinition = new ProductDefinition();
                productDefinition.setProductKey(request.getProductKey());
                productDefinition.setDescription(request.getDescription());
                productDefinition.setProductType(ProductType.valueOf(request.getType()));
                productDefinition.setRate(request.getRate());
                PayRate payRate = new PayRate();
                payRate.setUnit(PayRateUnit.valueOf(request.getPayRate().getUnit()));
                payRate.setValue(request.getPayRate().getValue());
                productDefinition.setPayRate(payRate);
                productDefinitionRepository.save(productDefinition);
                break;
            case UPDATE:
                if (existingDefinition == null) {
                    throw new IllegalArgumentException(
                            "Product definition with key " + request.getProductKey() + " doesn't exist!");
                }
                // save the oldrate so we can update the derived products
                BigDecimal oldRate = existingDefinition.getRate();
                updateExistingDefinition(existingDefinition, request);
                updateDerivedProducts(existingDefinition, oldRate);
                break;
            default:
                throw new IllegalArgumentException("Invalid operation: " + request.getOperation());
        }
    }

    @Transactional
    private void updateExistingDefinition(ProductDefinition existingDefinition,
            ProductDefinitionRequest.DefinitionRequest request) {
        boolean updated = false;

        if (request.getRate() != null && !request.getRate().equals(existingDefinition.getRate())) {
            existingDefinition.setRate(request.getRate());
            updated = true;
        }

        if (request.getPayRate() != null) {
            PayRate payRate = existingDefinition.getPayRate();
            if (request.getPayRate().getUnit() != null
                    && !request.getPayRate().getUnit().equals(payRate.getUnit().name())) {
                payRate.setUnit(PayRateUnit.valueOf(request.getPayRate().getUnit()));
                updated = true;
            }
            if (request.getPayRate().getValue() != null
                    && !request.getPayRate().getValue().equals(payRate.getValue())) {
                payRate.setValue(request.getPayRate().getValue());
                updated = true;
            }
        }

        if (updated) {
            productDefinitionRepository.save(existingDefinition);
        }
    }

    @Transactional
    public void updateDerivedProducts(ProductDefinition definition, BigDecimal oldRate) {
        List<Product> products = productRepository.findByProductDefinition(definition);

        for (Product product : products) {
            if (product.getRateType() != definition.getRateType()) {
                resetProductRate(product, definition);
                markProductForReview(product);
            } else {
                updateProductRate(product, definition, oldRate);
            }

            productRepository.save(product);
        }
    }

    private void resetProductRate(Product product, ProductDefinition definition) {
        product.setRateType(definition.getRateType());
        product.setRate(definition.getRate());
    }

    private void updateProductRate(Product product, ProductDefinition definition, BigDecimal oldRate) {
        BigDecimal newRate;
        switch (definition.getRateType()) {
            case FIXED:
                newRate = calculateNewFixedRate(product, definition, oldRate);
                break;
            case PERCENTAGE:
                newRate = calculateNewPercentageRate(product, definition, oldRate);
                break;
            default:
                throw new IllegalArgumentException("Unknown rate type: " + definition.getRateType());
        }

        product.setRate(newRate);
    }

    private BigDecimal calculateNewFixedRate(Product product, ProductDefinition definition, BigDecimal oldRate) {
        BigDecimal rateModifier = product.getRate().subtract(oldRate);
        BigDecimal newRate = definition.getRate().add(rateModifier);
        return newRate.max(BigDecimal.ZERO);
    }

    private BigDecimal calculateNewPercentageRate(Product product, ProductDefinition definition, BigDecimal oldRate) {
        BigDecimal productRate = product.getRate();
        BigDecimal definitionRate = definition.getRate();
        BigDecimal rateModifier = productRate.divide(oldRate, 4, RoundingMode.HALF_EVEN).subtract(BigDecimal.ONE);
        BigDecimal newRate = definitionRate.multiply(rateModifier.add(BigDecimal.ONE));
        return newRate;
    }

    private void markProductForReview(Product product) {

    }
}
