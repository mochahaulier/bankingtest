package dev.mochahaulier.bankingtest.controller;

import dev.mochahaulier.bankingtest.model.ClientProduct;
import dev.mochahaulier.bankingtest.service.ClientProductService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/client-products")
public class ClientProductController {

    private final ClientProductService clientProductService;

    @PostMapping
    public ClientProduct createClientProduct(@RequestParam Long clientId,
            @RequestParam Long productId,
            @RequestParam BigDecimal initialBalance,
            @RequestParam BigDecimal loanAmount,
            @RequestParam @Valid LocalDate startDate,
            @RequestParam @Valid LocalDate endDate,
            @RequestParam BigDecimal fixedInstallment) {
        return clientProductService.createClientProduct(clientId, productId, initialBalance, loanAmount, startDate,
                endDate,
                fixedInstallment);
    }

    @GetMapping
    public List<ClientProduct> getAllClientProducts() {
        return clientProductService.getAllClientProducts();
    }

    @GetMapping("/client/{clientId}")
    public List<ClientProduct> getClientProductsByClientId(@PathVariable Long clientId) {
        return clientProductService.getClientProductsByClientId(clientId);
    }

}
