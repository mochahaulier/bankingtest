package dev.mochahaulier.bankingtest.service;

import dev.mochahaulier.bankingtest.dto.ClientProductRequest;
import dev.mochahaulier.bankingtest.model.AccountProduct;
import dev.mochahaulier.bankingtest.model.Client;
import dev.mochahaulier.bankingtest.model.ClientProduct;
import dev.mochahaulier.bankingtest.model.LoanProduct;
import dev.mochahaulier.bankingtest.model.Product;
import dev.mochahaulier.bankingtest.repository.AccountProductRepository;
import dev.mochahaulier.bankingtest.repository.ClientProductRepository;
import dev.mochahaulier.bankingtest.repository.ClientRepository;
import dev.mochahaulier.bankingtest.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClientProductService {

    private final ClientProductRepository clientProductRepository;
    private final ClientRepository clientRepository;
    private final ProductRepository productRepository;
    private final TransactionService transactionService;
    private final AccountProductRepository accountProductRepository;

    @Transactional
    public ClientProduct createClientProduct(ClientProductRequest request) {
        Client client = clientRepository.findById(request.getClientId())
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        ClientProduct clientProduct;

        switch (product.getProductType()) {
            case ACCOUNT:
                AccountProduct accountProduct = new AccountProduct();
                accountProduct.setStartDate(request.getStartDate());
                // accountProduct.setAccountBalance(request.getInitialBalance());
                clientProduct = accountProduct;
                break;
            case LOAN:
                LoanProduct loanProduct = new LoanProduct();
                loanProduct.setStartDate(request.getStartDate());
                loanProduct.setEndDate(request.getEndDate());
                loanProduct.setFixedInstallment(request.getFixedInstallment());
                loanProduct.setOriginalAmount(request.getLoanAmount());
                clientProduct = loanProduct;
                break;
            default:
                throw new IllegalArgumentException("Unsupported product type: " + product.getProductType());
        }

        clientProduct.setClient(client);
        clientProduct.setProduct(product);
        clientProduct.setType(product.getProductType());
        clientProduct.setLastChargeDate(request.getStartDate());

        clientProduct = clientProductRepository.save(clientProduct);

        if (clientProduct instanceof AccountProduct) {
            AccountProduct accountProduct = (AccountProduct) clientProduct;
            transactionService.processAccountDeposit(accountProduct, request.getInitialBalance());
        }

        return clientProduct;
    }

    @Transactional(readOnly = true)
    public List<ClientProduct> getAllClientProducts() {
        return clientProductRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<ClientProduct> getClientProductsByClientId(Long clientId) {
        return clientProductRepository.findByClientId(clientId);
    }

    @Transactional(readOnly = true)
    public List<AccountProduct> getClientAccounts(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));

        return accountProductRepository.findByClient(client);
    }
}
