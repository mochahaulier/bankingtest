package dev.mochahaulier.bankingtest.controller;

import dev.mochahaulier.bankingtest.dto.ProductDefinitionRequest;
import dev.mochahaulier.bankingtest.service.ProductDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/product-definitions")
public class ProductDefinitionController {

    @Autowired
    private ProductDefinitionService productDefinitionService;

    @PostMapping
    public void processProductDefinitions(@RequestBody ProductDefinitionRequest productDefinitionRequest) {
        productDefinitionService.processProductDefinitions(productDefinitionRequest.getDefinitions());
    }
}
