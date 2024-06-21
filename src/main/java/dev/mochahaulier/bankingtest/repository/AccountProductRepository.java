package dev.mochahaulier.bankingtest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import dev.mochahaulier.bankingtest.model.AccountProduct;
import dev.mochahaulier.bankingtest.model.Client;

public interface AccountProductRepository extends JpaRepository<AccountProduct, Long> {
    List<AccountProduct> findByClient(Client client);
}
