package com.financeledger.financeledger.service;

import com.financeledger.financeledger.dto.request.AccountRequest;
import com.financeledger.financeledger.dto.response.AccountResponse;
import com.financeledger.financeledger.entity.Account;
import com.financeledger.financeledger.entity.User;
import com.financeledger.financeledger.exception.DuplicateResourceException;
import com.financeledger.financeledger.exception.ResourceNotFoundException;
import com.financeledger.financeledger.repository.AccountRepository;
import com.financeledger.financeledger.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final UserRepository userRepository;

    @Transactional
    public AccountResponse createAccount(AccountRequest request, UUID userId) {
        if (accountRepository.existsByCode(request.getCode())) {
            throw new DuplicateResourceException(
                    "Account with code " + request.getCode() + " already exists");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId.toString()));

        Account account = Account.builder()
                .user(user)
                .code(request.getCode())
                .name(request.getName())
                .type(request.getType())
                .currency(request.getCurrency())
                .build();

        return mapToResponse(accountRepository.save(account));
    }

    public List<AccountResponse> getUserAccounts(UUID userId) {
        return accountRepository.findByUserId(userId)
                .stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public AccountResponse getAccount(UUID id) {
        return accountRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Account", id.toString()));
    }

    private AccountResponse mapToResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .code(account.getCode())
                .name(account.getName())
                .type(account.getType())
                .currency(account.getCurrency())
                .balance(account.getBalance())
                .createdAt(account.getCreatedAt())
                .build();
    }
}
