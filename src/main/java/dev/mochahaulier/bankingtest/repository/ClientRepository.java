package dev.mochahaulier.bankingtest.repository;

import dev.mochahaulier.bankingtest.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
}
