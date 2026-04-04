package com.financeledger.financeledger.controller;


import com.financeledger.financeledger.dto.request.JournalEntryRequest;
import com.financeledger.financeledger.dto.response.JournalEntryResponse;
import com.financeledger.financeledger.dto.response.TrialBalanceResponse;
import com.financeledger.financeledger.service.JournalEntryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/journal")
@RequiredArgsConstructor
@Tag(name = "Journal Entries", description = "Double-entry ledger operations")
public class JournalEntryController {

    private final JournalEntryService journalEntryService;

    @PostMapping
    @Operation(summary = "Create a journal entry")
    public ResponseEntity<JournalEntryResponse> createEntry(
            @Valid @RequestBody JournalEntryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(journalEntryService.createEntry(request));
    }

    @GetMapping("/account/{accountId}")
    @Operation(summary = "Get all journal entries for an account")
    public ResponseEntity<List<JournalEntryResponse>> getEntries(
            @PathVariable UUID accountId) {
        return ResponseEntity.ok(
                journalEntryService.getEntriesByAccount(accountId));
    }

    @GetMapping("/trial-balance")
    @Operation(summary = "Get trial balance across all accounts")
    public ResponseEntity<TrialBalanceResponse> getTrialBalance() {
        return ResponseEntity.ok(journalEntryService.getTrialBalance());
    }

}
