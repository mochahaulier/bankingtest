package dev.mochahaulier.bankingtest.repository;

import dev.mochahaulier.bankingtest.model.PayRate;
import dev.mochahaulier.bankingtest.model.PayRateUnit;
import dev.mochahaulier.bankingtest.model.ProductDefinition;
import dev.mochahaulier.bankingtest.model.ProductType;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ProductDefinitionRepositoryTest {

    @Autowired
    private ProductDefinitionRepository productDefinitionRepository;

    @Test
    public void testSaveAndFindByProductKey() {
        // Create a new ProductDefinition
        ProductDefinition productDefinition = new ProductDefinition();
        productDefinition.setProductKey("TEST01");
        productDefinition.setDescription("Test Product");
        productDefinition.setProductType(ProductType.ACCOUNT);
        productDefinition.setRate(BigDecimal.valueOf(100.00));
        productDefinition.setPayRate(new PayRate(PayRateUnit.MONTH, 1));

        // Save the ProductDefinition
        productDefinitionRepository.save(productDefinition);

        // Retrieve the ProductDefinition by productKey
        Optional<ProductDefinition> retrievedProductDefinition = productDefinitionRepository.findById("TEST01");

        // Verify the results
        assertTrue(retrievedProductDefinition.isPresent());
        assertEquals("TEST01", retrievedProductDefinition.get().getProductKey());
        assertEquals("Test Product",
                retrievedProductDefinition.get().getDescription());
        assertEquals(ProductType.ACCOUNT,
                retrievedProductDefinition.get().getProductType());
        assertEquals(BigDecimal.valueOf(100.00),
                retrievedProductDefinition.get().getRate());
        assertEquals(PayRateUnit.MONTH,
                retrievedProductDefinition.get().getPayRate().getUnit());
        assertEquals(1, retrievedProductDefinition.get().getPayRate().getValue());
    }

    @Test
    public void testFindByNonExistentProductKey() {
        // Attempt to retrieve a ProductDefinition by a non-existent productKey
        Optional<ProductDefinition> retrievedProductDefinition = productDefinitionRepository
                .findById("NON_EXISTENT");

        // Verify the result is empty
        assertFalse(retrievedProductDefinition.isPresent());
    }

    @Test
    public void testUpdateProductDefinition() {
        // Create and save a ProductDefinition
        ProductDefinition productDefinition = new ProductDefinition();
        productDefinition.setProductKey("UPDATE01");
        productDefinition.setDescription("Update Test Product");
        productDefinition.setProductType(ProductType.LOAN);
        productDefinition.setRate(BigDecimal.valueOf(200.00));
        productDefinition.setPayRate(new PayRate(PayRateUnit.DAY, 30));
        productDefinitionRepository.save(productDefinition);

        // Retrieve the ProductDefinition
        Optional<ProductDefinition> retrievedProductDefinition = productDefinitionRepository
                .findById("UPDATE01");
        assertTrue(retrievedProductDefinition.isPresent());

        // Update the ProductDefinition
        ProductDefinition productToUpdate = retrievedProductDefinition.get();
        productToUpdate.setRate(BigDecimal.valueOf(150.00));
        productToUpdate.setDescription("Updated Description");
        productDefinitionRepository.save(productToUpdate);

        // Retrieve the updated ProductDefinition
        Optional<ProductDefinition> updatedProductDefinition = productDefinitionRepository.findById("UPDATE01");
        assertTrue(updatedProductDefinition.isPresent());
        assertEquals("Updated Description",
                updatedProductDefinition.get().getDescription());
        assertEquals(BigDecimal.valueOf(150.00),
                updatedProductDefinition.get().getRate());
    }
}