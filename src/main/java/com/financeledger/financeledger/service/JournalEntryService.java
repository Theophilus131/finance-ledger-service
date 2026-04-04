package com.financeledger.financeledger.service;

import com.financeledger.financeledger.dto.request.JournalEntryRequest;
import com.financeledger.financeledger.dto.response.JournalEntryResponse;
import com.financeledger.financeledger.dto.response.TrialBalanceResponse;
import com.financeledger.financeledger.entity.Account;
import com.financeledger.financeledger.entity.JournalEntry;
import com.financeledger.financeledger.exception.ResourceNotFoundException;
import com.financeledger.financeledger.repository.AccountRepository;
import com.financeledger.financeledger.repository.JournalEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JournalEntryService {

    private final JournalEntryRepository journalEntryRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public JournalEntryResponse createEntry(JournalEntryRequest request) {
        Account account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Account", request.getAccountId().toString()));

        JournalEntry entry = JournalEntry.builder()
                .account(account)
                .type(request.getType())
                .amount(request.getAmount())
                .description(request.getDescription())
                .entryDate(java.time.LocalDateTime.now())
                .build();

        if (request.getType() == JournalEntry.EntryType.DEBIT) {
            account.setBalance(account.getBalance().add(request.getAmount()));
        } else {
            account.setBalance(account.getBalance().subtract(request.getAmount()));
        }

        accountRepository.save(account);
        return mapToResponse(journalEntryRepository.save(entry));
    }

    public List<JournalEntryResponse> getEntriesByAccount(UUID accountId) {
        return journalEntryRepository.findByAccountId(accountId)
                .stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public TrialBalanceResponse getTrialBalance() {
        List<Account> accounts = accountRepository.findAll();
        List<TrialBalanceResponse.TrialBalanceEntry> entries = new ArrayList<>();
        BigDecimal totalDebits = BigDecimal.ZERO;
        BigDecimal totalCredits = BigDecimal.ZERO;

        for (Account account : accounts) {
            List<JournalEntry> journalEntries =
                    journalEntryRepository.findByAccountId(account.getId());

            BigDecimal debits = journalEntries.stream()
                    .filter(e -> e.getType() == JournalEntry.EntryType.DEBIT)
                    .map(JournalEntry::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal credits = journalEntries.stream()
                    .filter(e -> e.getType() == JournalEntry.EntryType.CREDIT)
                    .map(JournalEntry::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            totalDebits = totalDebits.add(debits);
            totalCredits = totalCredits.add(credits);

            entries.add(TrialBalanceResponse.TrialBalanceEntry.builder()
                    .accountCode(account.getCode())
                    .accountName(account.getName())
                    .totalDebits(debits)
                    .totalCredits(credits)
                    .netBalance(debits.subtract(credits))
                    .build());
        }

        return TrialBalanceResponse.builder()
                .entries(entries)
                .totalDebits(totalDebits)
                .totalCredits(totalCredits)
                .balanced(totalDebits.compareTo(totalCredits) == 0)
                .build();
    }

    private JournalEntryResponse mapToResponse(JournalEntry entry) {
        return JournalEntryResponse.builder()
                .id(entry.getId())
                .accountId(entry.getAccount().getId())
                .type(entry.getType())
                .amount(entry.getAmount())
                .description(entry.getDescription())
                .entryDate(entry.getEntryDate())
                .createdAt(entry.getCreatedAt())
                .build();
    }
}
