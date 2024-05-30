package dev.mochahaulier.bankingtest.service;

import dev.mochahaulier.bankingtest.model.Client;
import dev.mochahaulier.bankingtest.model.ClientProduct;
import dev.mochahaulier.bankingtest.model.Product;
import dev.mochahaulier.bankingtest.repository.ClientProductRepository;
import dev.mochahaulier.bankingtest.repository.ClientRepository;
import dev.mochahaulier.bankingtest.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClientProductService {

    private final ClientProductRepository clientProductRepository;

    private final ClientRepository clientRepository;

    private final ProductRepository productRepository;

    @Transactional
    public ClientProduct createClientProduct(Long clientId, Long productId, BigDecimal initialBalance,
            BigDecimal loanAmount, LocalDate startDate, LocalDate endDate, BigDecimal fixedInstallment) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        ClientProduct clientProduct = new ClientProduct();
        clientProduct.setClient(client);
        clientProduct.setProduct(product);
        clientProduct.setBalance(initialBalance);
        clientProduct.setOriginalAmount(loanAmount);
        clientProduct.setStartDate(startDate);
        clientProduct.setEndDate(endDate);
        clientProduct.setFixedInstallment(fixedInstallment);
        clientProduct.setLastChargeDate(startDate);

        return clientProductRepository.save(clientProduct);
    }

    @Transactional
    public List<ClientProduct> getAllClientProducts() {
        return clientProductRepository.findAll();
    }

    @Transactional
    public List<ClientProduct> getClientProductsByClientId(Long clientId) {
        return clientProductRepository.findByClientId(clientId);
    }
}
