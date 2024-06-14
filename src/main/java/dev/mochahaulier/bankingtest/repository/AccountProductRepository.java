package dev.mochahaulier.bankingtest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.mochahaulier.bankingtest.model.AccountProduct;

public interface AccountProductRepository extends JpaRepository<AccountProduct, Long> {
}
