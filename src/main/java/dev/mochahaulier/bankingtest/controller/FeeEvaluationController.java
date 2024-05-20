package dev.mochahaulier.bankingtest.controller;

import dev.mochahaulier.bankingtest.service.FeeEvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fee-evaluation")
public class FeeEvaluationController {

    @Autowired
    private FeeEvaluationService feeEvaluationService;

    @PostMapping("/evaluate")
    public ResponseEntity<Void> evaluateFees() {
        feeEvaluationService.evaluateFees();
        return ResponseEntity.ok().build();
    }
}