package com.financeledger.financeledger.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder @NoArgsConstructor @AllArgsConstructor
public class BankStatementRequest {

    @NotBlank(message = "Bank name is required")
    private String bankName;

    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @NotNull(message = "Statement date is required")
    private LocalDate statementDate;

    @NotNull(message = "Opening balance is required")
    private BigDecimal openingBalance;

    @NotNull(message = "Closing balance is required")
    private BigDecimal closingBalance;

}
