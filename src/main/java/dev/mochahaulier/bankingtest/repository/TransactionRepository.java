package dev.mochahaulier.bankingtest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import dev.mochahaulier.bankingtest.model.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

}