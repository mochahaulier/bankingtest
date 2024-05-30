package dev.mochahaulier.bankingtest.controller;

import dev.mochahaulier.bankingtest.dto.ProductDefinitionRequest;
import dev.mochahaulier.bankingtest.service.ProductDefinitionService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product-definitions")
public class ProductDefinitionController {

    private final ProductDefinitionService productDefinitionService;

    @PostMapping
    public void processProductDefinitions(@RequestBody ProductDefinitionRequest productDefinitionRequest) {
        productDefinitionService.processProductDefinitions(productDefinitionRequest.getDefinitions());
    }
}
