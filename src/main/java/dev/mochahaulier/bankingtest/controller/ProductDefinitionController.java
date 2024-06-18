package dev.mochahaulier.bankingtest.controller;

import dev.mochahaulier.bankingtest.dto.ProductDefinitionRequest;
import dev.mochahaulier.bankingtest.dto.ProductDefinitionResponse;
import dev.mochahaulier.bankingtest.service.ProductDefinitionService;
import dev.mochahaulier.bankingtest.validation.ValidationGroup;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/product-definitions")
@Validated
public class ProductDefinitionController {

    private final ProductDefinitionService productDefinitionService;
    private final Validator validator;

    @PostMapping
    public ResponseEntity<ProductDefinitionResponse> processProductDefinitions(
            @RequestBody @Valid ProductDefinitionRequest productDefinitionRequest) {
        // This now allows the processing of all validated requests one by one
        // Returns all the errors and all the succesfully processed definitions
        Map<Integer, ProductDefinitionRequest.DefinitionRequest> validDefinitions = new LinkedHashMap<>();
        List<String> errors = new ArrayList<>();
        List<String> successes = new ArrayList<>();

        List<ProductDefinitionRequest.DefinitionRequest> definitions = productDefinitionRequest.getDefinitions();
        for (int i = 0; i < definitions.size(); i++) {
            final int index = i;
            ProductDefinitionRequest.DefinitionRequest definition = definitions.get(i);
            try {
                switch (definition.getOperation()) {
                    case NEW:
                        validate(definition, i, ValidationGroup.NewOperation.class);
                        break;
                    case UPDATE:
                        validate(definition, i, ValidationGroup.UpdateOperation.class);
                        break;
                    case INVALID:
                        throw new IllegalArgumentException(
                                "Invalid operation " + definition.getOperation());
                }
                validDefinitions.put(index, definition); // Add to valid definitions if no exception
            } catch (ConstraintViolationException e) {
                e.getConstraintViolations()
                        .forEach(violation -> errors
                                .add("[" + index + "]: [VALIDATION ERROR]: " + violation.getMessage()));
            } catch (IllegalArgumentException e) {
                errors.add("[" + index + "]: [VALIDATION ERROR]: " + e.getMessage());
            }
        }

        ProductDefinitionResponse processResponse;

        if (!validDefinitions.isEmpty()) {
            processResponse = productDefinitionService.processProductDefinitions(validDefinitions);
            errors.addAll(processResponse.getErrors());
            successes.addAll(processResponse.getSuccesses());
        }

        // Return a response containing both errors and successful indexes
        return ResponseEntity.ok(new ProductDefinitionResponse(errors, successes));
    }

    private <T> void validate(T object, int index, Class<?>... groups) {
        Set<ConstraintViolation<T>> violations = validator.validate(object, groups);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
