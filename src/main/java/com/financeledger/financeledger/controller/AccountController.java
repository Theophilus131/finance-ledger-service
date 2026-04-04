package com.financeledger.financeledger.controller;

import com.financeledger.financeledger.dto.request.AccountRequest;
import com.financeledger.financeledger.dto.response.AccountResponse;
import com.financeledger.financeledger.repository.UserRepository;
import com.financeledger.financeledger.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/accounts")
@RequiredArgsConstructor
@Tag(name = "Accounts", description = "Ledger account management")
public class AccountController {

    private final AccountService accountService;
    private final UserRepository userRepository;

    @PostMapping
    @Operation(summary = "Create a new account")
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody AccountRequest request,
            @AuthenticationPrincipal String email) {
        UUID userId = userRepository.findByEmail(email).get().getId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(accountService.createAccount(request, userId));
    }

    @GetMapping
    @Operation(summary = "Get all accounts for logged in user")
    public ResponseEntity<List<AccountResponse>> getMyAccounts(
            @AuthenticationPrincipal String email) {
        UUID userId = userRepository.findByEmail(email).get().getId();
        return ResponseEntity.ok(accountService.getUserAccounts(userId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a single account by ID")
    public ResponseEntity<AccountResponse> getAccount(
            @PathVariable UUID id) {
        return ResponseEntity.ok(accountService.getAccount(id));
    }
}
