package com.financeledger.financeledger.controller;


import com.financeledger.financeledger.dto.request.BankStatementRequest;
import com.financeledger.financeledger.dto.response.BankStatementResponse;
import com.financeledger.financeledger.dto.response.ReconciliationResponse;
import com.financeledger.financeledger.service.ReconciliationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/reconciliation")
@RequiredArgsConstructor
@Tag(name = "Reconciliation", description = "Bank statement import and reconciliation")
public class ReconciliationController {

    private final ReconciliationService reconciliationService;

    @PostMapping("/import")
    @Operation(summary = "Import a bank statement")
    public ResponseEntity<BankStatementResponse> importStatement(
            @Valid @RequestBody BankStatementRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reconciliationService.importStatement(request));
    }

    @PostMapping("/{statementId}/reconcile")
    @Operation(summary = "Run reconciliation against a bank statement")
    public ResponseEntity<ReconciliationResponse> reconcile(
            @PathVariable UUID statementId) {
        return ResponseEntity.ok(
                reconciliationService.reconcile(statementId));
    }
}
