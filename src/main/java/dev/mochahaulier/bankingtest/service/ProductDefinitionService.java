package dev.mochahaulier.bankingtest.service;

import dev.mochahaulier.bankingtest.dto.ProductDefinitionRequest;
import dev.mochahaulier.bankingtest.dto.ProductDefinitionResponse;
import dev.mochahaulier.bankingtest.model.PayRate;
import dev.mochahaulier.bankingtest.model.PayRateUnit;
import dev.mochahaulier.bankingtest.model.Product;
import dev.mochahaulier.bankingtest.model.ProductDefinition;
import dev.mochahaulier.bankingtest.model.ProductType;
import dev.mochahaulier.bankingtest.model.RateType;
import dev.mochahaulier.bankingtest.repository.ProductDefinitionRepository;
import dev.mochahaulier.bankingtest.repository.ProductRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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

        // if (!errors.isEmpty()) {
        // throw new ProcessingException(errors);
        // }
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
                updateExistingDefinition(existingDefinition, request);
                productDefinitionRepository.save(existingDefinition);
                updateDerivedProducts(existingDefinition);
                break;
            default:
                throw new IllegalArgumentException("Invalid operation: " + request.getOperation());
        }
    }

    private void updateExistingDefinition(ProductDefinition existingDefinition,
            ProductDefinitionRequest.DefinitionRequest request) {
        // can only change payrate and rate
        // maybe add some tests here, if trying to change something you can't
        // or if nothing is changed
        existingDefinition.setRate(request.getRate());
        PayRate payRate = existingDefinition.getPayRate();
        payRate.setUnit(PayRateUnit.valueOf(request.getPayRate().getUnit()));
        payRate.setValue(request.getPayRate().getValue());
        // check/update all products that use this
        // with the current setup, testing if not negative for fixed values is enough
        // but maybe the dbs need to be changed and modified and additional changes need
        // to be done here.
    }

    private void updateDerivedProducts(ProductDefinition definition) {
        // all products that are derived from the changed definition
        List<Product> products = productRepository.findByProductDefinition(definition);

        // just check if less than zero and adjust to zero
        // need to change that later depending on how to handle this
        // just throw an error?

        // TODO: check if changed from fixed to percentage...so actually need to test
        // both...
        for (Product product : products) {
            if (definition.getRateType() == RateType.FIXED) {
                BigDecimal newRate = definition.getRate().add(product.getRate());
                // new rate now smaller than zero.
                if (newRate.compareTo(BigDecimal.ZERO) < 0) {
                    // throw new IllegalArgumentException("Final rate can't be negative: " +
                    // newRate);
                    product.setRate(BigDecimal.ZERO);
                }
            }
            // here else "percentage" - not needed at this moment
            productRepository.save(product);

            // Maybe I could use this, but probably needs rules how to handle changes
            // productService.updateProductRate(product.getId(), product.getRate());
        }

    }

}
