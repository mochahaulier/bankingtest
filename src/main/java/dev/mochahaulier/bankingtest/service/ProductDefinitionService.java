package dev.mochahaulier.bankingtest.service;

import dev.mochahaulier.bankingtest.dto.ProductDefinitionRequest;
import dev.mochahaulier.bankingtest.model.PayRate;
import dev.mochahaulier.bankingtest.model.PayRateUnit;
import dev.mochahaulier.bankingtest.model.Product;
import dev.mochahaulier.bankingtest.model.ProductDefinition;
import dev.mochahaulier.bankingtest.model.ProductType;
import dev.mochahaulier.bankingtest.repository.ProductDefinitionRepository;
import dev.mochahaulier.bankingtest.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductDefinitionService {

    @Autowired
    private ProductDefinitionRepository productDefinitionRepository;

    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public void processProductDefinitions(List<ProductDefinitionRequest.DefinitionRequest> requests) {
        for (ProductDefinitionRequest.DefinitionRequest request : requests) {
            ProductDefinition existingDefinition = productDefinitionRepository.findById(request.getProductKey())
                    .orElse(null);

            if ("N".equals(request.getOperation())) {
                if (existingDefinition != null) {
                    // Maybe just skip here, or need to process all?
                    throw new IllegalArgumentException(
                            "Product definition with key " + request.getProductKey() + " already exists.");
                }
                ProductDefinition productDefinition = new ProductDefinition();
                productDefinition.setProductKey(request.getProductKey());
                productDefinition.setDescription(request.getDescription());
                productDefinition.setType(ProductType.valueOf(request.getType()));
                productDefinition.setRate(request.getRate());
                PayRate payRate = new PayRate();
                payRate.setUnit(PayRateUnit.valueOf(request.getPayRate().getUnit()));
                payRate.setValue(request.getPayRate().getValue());
                productDefinition.setPayRate(payRate);
                productDefinitionRepository.save(productDefinition);
            } else if ("U".equals(request.getOperation())) {
                if (existingDefinition == null) {
                    // Maybe just skip here, or need to process all?
                    return;
                    // throw new IllegalArgumentException(
                    // "Product with key " + request.getProductKey() + " does not exist.");
                }
                // can only change payrate and rate
                // maybe add some tests here, if trying to change something you can't
                // or if nothing is changed
                existingDefinition.setRate(request.getRate());
                PayRate payRate = existingDefinition.getPayRate();
                payRate.setUnit(PayRateUnit.valueOf(request.getPayRate().getUnit()));
                payRate.setValue(request.getPayRate().getValue());
                productDefinitionRepository.save(existingDefinition);
                // check/update all products that use this
                // with the current setup, testing if not negative for fixed values is enough
                // but maybe the dbs need to be changed and modified and additional changes need
                // to be done here.
                updateDerivedProducts(existingDefinition);
            }
        }
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
            if (definition.getRateType().equals("fixed")) {
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
        }

    }
}