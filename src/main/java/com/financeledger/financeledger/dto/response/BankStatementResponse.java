package com.financeledger.financeledger.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder @NoArgsConstructor @AllArgsConstructor
public class BankStatementResponse {
    private UUID id;
    private String bankName;
    private String accountNumber;
    private LocalDate statementDate;
    private BigDecimal openingBalance;
    private BigDecimal closingBalance;
    private LocalDateTime importedAt;
}
