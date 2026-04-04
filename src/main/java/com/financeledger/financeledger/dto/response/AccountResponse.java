package com.financeledger.financeledger.dto.response;

import com.financeledger.financeledger.entity.Account;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AccountResponse {
    private UUID id;
    private String code;
    private String name;
    private Account.AccountType type;
    private String currency;
    private BigDecimal balance;
    private LocalDateTime createdAt;
}
