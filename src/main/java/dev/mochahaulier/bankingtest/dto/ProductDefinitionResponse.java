package dev.mochahaulier.bankingtest.dto;

import java.util.List;

public class ProductDefinitionResponse {
    private List<String> errors;
    private List<String> successes;

    public ProductDefinitionResponse(List<String> errors, List<String> successes) {
        this.errors = errors;
        this.successes = successes;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public List<String> getSuccesses() {
        return successes;
    }

    public void setProcessedIndexes(List<String> successes) {
        this.successes = successes;
    }
}
