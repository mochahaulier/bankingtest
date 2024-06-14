package dev.mochahaulier.bankingtest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.mochahaulier.bankingtest.model.LoanProduct;

public interface LoanProductRepository extends JpaRepository<LoanProduct, Long> {
}
