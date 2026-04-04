package com.financeledger.financeledger.dto.request;

import com.financeledger.financeledger.entity.Account;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
public class AccountRequest {
    @NotBlank(message = "Account code is required")
    private String code;

    @NotBlank(message = "Account name is required")
    private String name;

    @NotNull(message = "Account type is required")
    private Account.AccountType type;

    private String currency = "NGN";
}
