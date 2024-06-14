package dev.mochahaulier.bankingtest.repository;

import dev.mochahaulier.bankingtest.model.ProductDefinition;

import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDefinitionRepository extends JpaRepository<ProductDefinition, String> {
    @Cacheable("productDefinitions")
    Optional<ProductDefinition> findByProductKey(String productKey);
}