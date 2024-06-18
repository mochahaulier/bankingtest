package dev.mochahaulier.bankingtest.controller;

import dev.mochahaulier.bankingtest.dto.ClientProductRequest;
import dev.mochahaulier.bankingtest.model.ClientProduct;
import dev.mochahaulier.bankingtest.service.ClientProductService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.util.List;

import jakarta.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/client-products")
public class ClientProductController {

    private final ClientProductService clientProductService;

    @PostMapping
    public ClientProduct createClientProduct(@RequestBody @Valid ClientProductRequest request) {
        return clientProductService.createClientProduct(request);
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
