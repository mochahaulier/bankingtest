package dev.mochahaulier.bankingtest.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;

import dev.mochahaulier.bankingtest.dto.ProductCreationRequest;
import dev.mochahaulier.bankingtest.dto.ProductUpdateRequest;
import dev.mochahaulier.bankingtest.model.Product;
import dev.mochahaulier.bankingtest.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public Product createProduct(@RequestBody @Valid ProductCreationRequest productRequest) {
        return productService.createProduct(productRequest);
    }

    @GetMapping
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/{id}")
    public Optional<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @PutMapping("/{id}")
    public Product updateProductRate(@RequestBody @Valid ProductUpdateRequest productRequest) {
        return productService.updateProductRate(productRequest);
    }

    @GetMapping("/by-definition/{definitionId}")
    public ResponseEntity<List<Product>> getProductsByDefinition(@PathVariable String definitionId) {
        List<Product> products = productService.getProductsByDefinition(definitionId);
        return ResponseEntity.ok(products);
    }
}
