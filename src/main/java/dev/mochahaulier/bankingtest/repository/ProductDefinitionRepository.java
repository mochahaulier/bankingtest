package dev.mochahaulier.bankingtest.repository;

import dev.mochahaulier.bankingtest.model.ProductDefinition;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDefinitionRepository extends JpaRepository<ProductDefinition, String> {
}