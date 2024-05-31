package dev.mochahaulier.bankingtest.controller;

import dev.mochahaulier.bankingtest.dto.ProductDefinitionRequest;
import dev.mochahaulier.bankingtest.service.ProductDefinitionService;
import dev.mochahaulier.bankingtest.validation.ValidationGroup;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

import java.util.Set;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product-definitions")
@Validated
public class ProductDefinitionController {

    private final ProductDefinitionService productDefinitionService;
    private final Validator validator;

    @PostMapping
    public void processProductDefinitions(
            @RequestBody @Valid ProductDefinitionRequest productDefinitionRequest) {
        for (ProductDefinitionRequest.DefinitionRequest definition : productDefinitionRequest.getDefinitions()) {
            if ("N".equals(definition.getOperation())) {
                validate(definition, ValidationGroup.NewOperation.class);
            } else if ("U".equals(definition.getOperation())) {
                validate(definition, ValidationGroup.UpdateOperation.class);
            }
        }
        productDefinitionService.processProductDefinitions(productDefinitionRequest.getDefinitions());
    }

    private <T> void validate(T object, Class<?>... groups) {
        Set<ConstraintViolation<T>> violations = validator.validate(object, groups);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
