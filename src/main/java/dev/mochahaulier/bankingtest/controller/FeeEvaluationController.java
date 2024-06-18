package dev.mochahaulier.bankingtest.controller;

import dev.mochahaulier.bankingtest.service.FeeEvaluationService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fee-evaluation")
public class FeeEvaluationController {

    private final FeeEvaluationService feeEvaluationService;

    @PostMapping("/evaluate")
    public ResponseEntity<Void> evaluateFees() {
        feeEvaluationService.evaluateFees();
        return ResponseEntity.ok().build();
    }
}