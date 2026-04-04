package com.financeledger.financeledger.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor @NoArgsConstructor @Builder
public class TrialBalanceResponse {
    private List<TrialBalanceEntry> entries;
    private BigDecimal totalDebits;
    private BigDecimal totalCredits;
    private boolean balanced;

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class TrialBalanceEntry {
        private String accountCode;
        private String accountName;
        private BigDecimal totalDebits;
        private BigDecimal totalCredits;
        private BigDecimal netBalance;
    }
}
